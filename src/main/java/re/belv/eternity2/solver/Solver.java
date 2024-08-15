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
 * final var pieces = new Piece[][]{
 *      {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3)},
 *      {new Piece(2, 0, 1, 2, 3), new Piece(3, 0, 1, 2, 3)},
 * };
 * final var solver = new Solver();
 * final Iterator<Piece[][]> solutionIterator = solver.solve(pieces);
 * while (solutionIterator.hasNext()) {
 *     System.out.println(solutionIterator.next());
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
        this.backend = SolverFactory.newLight();
    }

    /**
     * Solves the given pieces.
     * <p>
     * The search for solution is performed lazily, upon call to the {@link Iterator#hasNext() hasNext} or
     * {@link Iterator#next() next} method of the returned solution {@link Iterator}.
     * <p>
     * A second call to this method will reset the solver and make the iterator returned on first call invalid.
     *
     * @param pieces the pieces representing the initial board
     * @return an iterator on the solutions (the pieces representing the solved board)
     * @throws NullPointerException     if any of the given pieces is {@code null}
     * @throws IllegalArgumentException if given pieces are invalid (e.g. with inconsistent row lengths)
     */
    public Iterator<Piece[][]> solve(final Piece[][] pieces) {
        final var board = new Board(pieces);
        final var variables = new Variables(board);
        final var constraints = new Constraints(variables, board);

        backend.reset();
        backend.newVar(variables.count());
        try {
            constraints.addAllConstraintsTo(backend);
        } catch (final ContradictionException e) {
            return Collections.emptyIterator();
        }

        return new SolutionIterator(variables, backend);
    }
}
