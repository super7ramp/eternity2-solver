package re.belv.eternity2.solver;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import java.util.Collections;
import java.util.Iterator;

/**
 * A solver for the <a href="https://en.wikipedia.org/wiki/Eternity_II_puzzle">Eternity II</a> problem.
 * <p>
 * This class is <em>not</em> thread-safe.
 * <p>
 * Example of usage:
 * <pre>{@code
 * // The list of pieces (id, north color, east color, south color, west color).
 * final var pieces = new Piece[]{
 *     new Piece(0, 0, 1, 2, 3),
 *     new Piece(1, 0, 1, 2, 3),
 *     new Piece(2, 0, 1, 2, 3),
 *     new Piece(3, 0, 1, 2, 3),
 * };
 *
 * // The board is a 2x2 grid. The bottom right piece is fixed.
 * final var initialBoard = new Piece[][]{
 *     {null, null},
 *     {null, pieces[1].rotate(Piece.Rotation.PLUS_90)}
 * };
 *
 * // Instantiate the solver and solve the game.
 * final var solver = new Solver();
 * final Iterator<Piece[][]> solutions = solver.solve(pieces, initialBoard);
 * while (solutions.hasNext()) {
 *     final Piece[][] solution = solutions.next();
 *     System.out.println(Arrays.deepToString(solution));
 * }
 * </pre>
 */
public final class Solver {

    /** The actual solver. */
    private final ISolver backend;

    /**
     * Creates an instance.
     */
    public Solver() {
        backend = SolverFactory.newLight();
    }

    /**
     * Solves the given game.
     * <p>
     * The search for solution is performed lazily, upon call to the {@link Iterator#hasNext() hasNext} or
     * {@link Iterator#next() next} method of the returned solution {@link Iterator}.
     * <p>
     * A second call to this method will reset the solver and make the iterator returned on first call invalid.
     *
     * @param pieces       the available pieces
     * @param initialBoard the initial board; Any non-{@code null} piece is considered as fixed and will not be moved
     * @return an iterator on the solutions (the pieces representing the solved board)
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if given game is invalid (e.g. number of pieces inconsistent with board dimensions)
     */
    public Iterator<Piece[][]> solve(final Piece[] pieces, final Piece[][] initialBoard) {
        final var game = new Game(pieces, initialBoard);
        final var variables = new Variables(game);
        final var constraints = new Constraints(variables, game);

        backend.reset();
        backend.newVar(variables.count());
        try {
            constraints.addAllConstraintsTo(backend);
        } catch (final ContradictionException e) {
            return Collections.emptyIterator();
        }

        return new Solutions(variables, backend);
    }
}
