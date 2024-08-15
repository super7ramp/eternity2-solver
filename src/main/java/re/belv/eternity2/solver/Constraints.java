package re.belv.eternity2.solver;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.GateTranslator;

/**
 * Where constraints are built and added to the solver.
 * <p>
 * In an ideal world, this class would only be a factory of constraints but given there may be a lot of them it is more
 * memory efficient to build them and add them to the solver in one go.
 */
final class Constraints {

    private final Variables variables;
    private final Problem problem;

    /**
     * Constructs an instance.
     *
     * @param variables the problem variables
     * @param problem   the problem
     */
    Constraints(final Variables variables, final Problem problem) {
        this.variables = variables;
        this.problem = problem;
    }

    void addAllConstraintsTo(final ISolver solver) throws ContradictionException {
        addExactlyOnePiecePerPositionTo(solver);
        addExactlyOnePositionPerPieceTo(solver);
        addExactlyOneColorPerBorderTo(solver);
        addAdjacentBordersMustHaveSameColorTo(solver);
        addBorderColorsMatchPiecesTo(solver);
        addMiddlePieceDoesNotMoveTo(solver);
    }

    void addExactlyOnePiecePerPositionTo(final ISolver solver) throws ContradictionException {
        final var gator = new GateTranslator(solver);
        final var positionPieces = new VecInt(problem.piecesCount());
        for (int rowIndex = 0; rowIndex < problem.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                for (int pieceIndex = 0; pieceIndex < problem.piecesCount(); pieceIndex++) {
                    final var positionPieceRotations = new VecInt(Piece.Rotation.count());
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        positionPieceRotations.push(variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation));
                    }
                    final int positionPiece = solver.nextFreeVarId(true);
                    gator.xor(positionPiece, positionPieceRotations);
                    positionPieces.push(positionPiece);
                }
                solver.addExactly(positionPieces, 1);
                positionPieces.clear();
            }
        }
    }

    void addExactlyOnePositionPerPieceTo(final ISolver solver) throws ContradictionException {
        final var piecePositions = new VecInt(problem.rowCount() * problem.columnCount() * Piece.Rotation.count());
        for (int pieceIndex = 0; pieceIndex < problem.piecesCount(); pieceIndex++) {
            for (int rowIndex = 0; rowIndex < problem.rowCount(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        piecePositions.push(variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation));
                    }
                }
            }
            solver.addExactly(piecePositions, 1);
            piecePositions.clear();
        }
    }

    void addExactlyOneColorPerBorderTo(final ISolver solver) throws ContradictionException {
        final var borderColors = new VecInt(problem.colorCount());
        for (int rowIndex = 0; rowIndex < problem.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                for (final Piece.Border border : Piece.Border.all()) {
                    for (int colorIndex = 0; colorIndex < problem.colorCount(); colorIndex++) {
                        borderColors.push(variables.representingBorder(rowIndex, columnIndex, border, colorIndex));
                    }
                    solver.addExactly(borderColors, 1);
                    borderColors.clear();
                }
            }
        }
    }

    void addAdjacentBordersMustHaveSameColorTo(final ISolver solver) throws ContradictionException {
        // east-west
        for (int row = 0; row < problem.rowCount(); row++) {
            for (int column = 0; column < problem.columnCount() - 1; column++) {
                for (int color = 0; color < problem.colorCount(); color++) {
                    final int eastBorder = variables.representingBorder(row, column, Piece.Border.EAST, color);
                    final int neighborWestBorder = variables.representingBorder(row, column + 1, Piece.Border.WEST, color);
                    // eastBorder <=> neighborWestBorder
                    solver.addClause(new VecInt(new int[]{-eastBorder, neighborWestBorder}));
                    solver.addClause(new VecInt(new int[]{eastBorder, -neighborWestBorder}));
                }
            }
        }
        // north-south
        for (int rowIndex = 0; rowIndex < problem.rowCount() - 1; rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                for (int colorIndex = 0; colorIndex < problem.colorCount(); colorIndex++) {
                    final int southBorder = variables.representingBorder(rowIndex, columnIndex, Piece.Border.SOUTH, colorIndex);
                    final int neighborNorthBorder = variables.representingBorder(rowIndex + 1, columnIndex, Piece.Border.NORTH, colorIndex);
                    // southBorder <=> neighborNorthBorder
                    solver.addClause(new VecInt(new int[]{-southBorder, neighborNorthBorder}));
                    solver.addClause(new VecInt(new int[]{southBorder, -neighborNorthBorder}));
                }
            }
        }
    }

    void addBorderColorsMatchPiecesTo(final ISolver solver) throws ContradictionException {
        final var gator = new GateTranslator(solver);
        final var pieceBorders = new VecInt(Piece.Border.count());
        for (int rowIndex = 0; rowIndex < problem.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                for (int pieceIndex = 0; pieceIndex < problem.piecesCount(); pieceIndex++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        final int pieceLit = variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation);
                        final Piece piece = problem.piece(pieceIndex).rotate(rotation);
                        for (final Piece.Border border : Piece.Border.all()) {
                            final int color = piece.colorTo(border);
                            final int pieceBorder = variables.representingBorder(rowIndex, columnIndex, border, color);
                            pieceBorders.push(pieceBorder);
                        }
                        final int pieceBordersLit = solver.nextFreeVarId(true);
                        gator.and(pieceBordersLit, pieceBorders);
                        // pieceLit => pieceBordersLit
                        solver.addClause(new VecInt(new int[]{-pieceLit, pieceBordersLit}));
                        pieceBorders.clear();
                    }
                }
            }
        }
    }

    // TODO implement this
    void addMiddlePieceDoesNotMoveTo(final ISolver solver) {
        // the middle piece shall not move
    }
}
