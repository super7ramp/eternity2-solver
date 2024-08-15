package re.belv.eternity2.solver;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import java.util.Collections;
import java.util.Iterator;

/**
 * A solver for the eternity2 problem.
 * <p>
 * This class is <em>not</em> thread-safe.
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
     *
     * @param pieces the pieces representing the initial board
     * @return an iterator on the solutions (the pieces representing the solved board)
     * @throws IllegalArgumentException if given pieces are invalid (e.g. {@code null}, with inconsistent row lengths,
     *                                  etc.)
     */
    public Iterator<Piece[][]> solve(final Piece[][] pieces) {
        final var problem = new Problem(pieces);
        final var variables = new Variables(problem);
        final var constraints = new Constraints(variables, problem);

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
