package re.belv.eternity2.solver;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import java.util.Optional;

/**
 * Where the game constraints are built and added to the solver.
 *
 * @apiNote In an ideal world, this class would only be a factory of clauses but given there may be a lot of them it is
 * more memory efficient to build them and add them to the solver in one go.
 */
final class Constraints {

    /** The problem variables. */
    private final Variables variables;

    /** The game. */
    private final Game game;

    /**
     * Constructs an instance.
     *
     * @param variables the problem variables
     * @param game      the game
     */
    Constraints(final Variables variables, final Game game) {
        this.variables = variables;
        this.game = game;
    }

    /**
     * Adds all constraints to the given solver.
     *
     * @param solver the solver
     * @throws ContradictionException if a constraint is trivially unsatisfiable
     */
    void addAllConstraintsTo(final ISolver solver) throws ContradictionException {
        addExactlyOnePiecePerPositionTo(solver);
        addExactlyOnePositionPerPieceTo(solver);
        addExactlyOneColorPerBorderTo(solver);
        addAdjacentBordersMustHaveSameColorTo(solver);
        addBorderColorsMatchPiecesTo(solver);
        addInitialBoardPiecesAreFixedTo(solver);
    }

    /**
     * Constrains the given solver so that there is exactly one piece with exactly one rotation, in each position.
     *
     * @param solver the solver
     * @throws ContradictionException when a constraint is trivially unsatisfiable
     */
    void addExactlyOnePiecePerPositionTo(final ISolver solver) throws ContradictionException {
        final var positionPieces = new VecInt(game.piecesCount() * Piece.Rotation.count());
        for (int rowIndex = 0; rowIndex < game.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                for (int pieceIndex = 0; pieceIndex < game.piecesCount(); pieceIndex++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        positionPieces.push(variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation));
                    }
                }
                solver.addExactly(positionPieces, 1);
                positionPieces.clear();
            }
        }
    }

    /**
     * Constrains the given solver so that there is exactly one position for each piece (i.e. a piece cannot be in two
     * positions in the same time).
     *
     * @param solver the solver
     * @throws ContradictionException when a constraint is trivially unsatisfiable
     */
    void addExactlyOnePositionPerPieceTo(final ISolver solver) throws ContradictionException {
        final var piecePositions = new VecInt(game.rowCount() * game.columnCount() * Piece.Rotation.count());
        for (int pieceIndex = 0; pieceIndex < game.piecesCount(); pieceIndex++) {
            for (int rowIndex = 0; rowIndex < game.rowCount(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        piecePositions.push(variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation));
                    }
                }
            }
            solver.addExactly(piecePositions, 1);
            piecePositions.clear();
        }
    }

    /**
     * Constrains the given solver so that there is exactly one color per border (i.e. the same border cannot have two
     * colors at the same time).
     *
     * @param solver the solver
     * @throws ContradictionException when a constraint is trivially unsatisfiable
     */
    void addExactlyOneColorPerBorderTo(final ISolver solver) throws ContradictionException {
        final var borderColors = new VecInt(game.colorCount());
        for (int rowIndex = 0; rowIndex < game.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                for (final Piece.Border border : Piece.Border.all()) {
                    for (int colorIndex = 0; colorIndex < game.colorCount(); colorIndex++) {
                        borderColors.push(variables.representingBorder(rowIndex, columnIndex, border, colorIndex));
                    }
                    solver.addExactly(borderColors, 1);
                    borderColors.clear();
                }
            }
        }
    }

    /**
     * Constrains the solver so that adjacent borders has the same color.
     *
     * @param solver the solver
     * @throws ContradictionException when a constraint is trivially unsatisfiable
     */
    void addAdjacentBordersMustHaveSameColorTo(final ISolver solver) throws ContradictionException {
        // east-west
        for (int row = 0; row < game.rowCount(); row++) {
            for (int column = 0; column < game.columnCount() - 1; column++) {
                for (int color = 0; color < game.colorCount(); color++) {
                    final int eastBorder = variables.representingBorder(row, column, Piece.Border.EAST, color);
                    final int neighborWestBorder = variables.representingBorder(row, column + 1, Piece.Border.WEST, color);
                    // eastBorder <=> neighborWestBorder
                    solver.addClause(new VecInt(new int[]{-eastBorder, neighborWestBorder}));
                    solver.addClause(new VecInt(new int[]{eastBorder, -neighborWestBorder}));
                }
            }
        }
        // north-south
        for (int rowIndex = 0; rowIndex < game.rowCount() - 1; rowIndex++) {
            for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                for (int colorIndex = 0; colorIndex < game.colorCount(); colorIndex++) {
                    final int southBorder = variables.representingBorder(rowIndex, columnIndex, Piece.Border.SOUTH, colorIndex);
                    final int neighborNorthBorder = variables.representingBorder(rowIndex + 1, columnIndex, Piece.Border.NORTH, colorIndex);
                    // southBorder <=> neighborNorthBorder
                    solver.addClause(new VecInt(new int[]{-southBorder, neighborNorthBorder}));
                    solver.addClause(new VecInt(new int[]{southBorder, -neighborNorthBorder}));
                }
            }
        }
    }

    /**
     * Constrains the solver so that the colors of the borders match the colors of the pieces.
     * <p>
     * This is here that the two kinds of {@link Variables} (pieces and borders) are linked.
     *
     * @param solver the solver
     * @throws ContradictionException when a constraint is trivially unsatisfiable
     */
    void addBorderColorsMatchPiecesTo(final ISolver solver) throws ContradictionException {
        for (int rowIndex = 0; rowIndex < game.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                for (int pieceIndex = 0; pieceIndex < game.piecesCount(); pieceIndex++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        final int pieceLit = variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation);
                        final Piece piece = game.piece(pieceIndex).rotate(rotation);
                        for (final Piece.Border border : Piece.Border.all()) {
                            final int color = piece.colorTo(border);
                            final int pieceBorder = variables.representingBorder(rowIndex, columnIndex, border, color);
                            solver.addClause(new VecInt(new int[]{-pieceLit, pieceBorder}));
                        }
                    }
                }
            }
        }
    }

    /**
     * Constrains the solver so that the pieces on the initial board are fixed.
     *
     * @param solver the solver
     * @throws ContradictionException if a constraint is trivially unsatisfiable
     */
    void addInitialBoardPiecesAreFixedTo(final ISolver solver) throws ContradictionException {
        for (int rowIndex = 0; rowIndex < game.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < game.columnCount(); columnIndex++) {
                final Optional<Piece> fixedPiece = game.initialBoardPiece(rowIndex, columnIndex);
                if (fixedPiece.isPresent()) {
                    final int pieceIndex = fixedPiece.get().id();
                    final Piece originalPiece = game.piece(pieceIndex);
                    final Piece.Rotation rotation = originalPiece.rotationTo(fixedPiece.get());
                    final int pieceLit = variables.representingPiece(rowIndex, columnIndex, pieceIndex, rotation);
                    solver.addClause(new VecInt(new int[]{pieceLit}));
                }
            }
        }
    }
}
