package re.belv.eternity2.solver;

import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator of solver solutions.
 */
final class SolutionIterator implements Iterator<Piece[][]> {

    /** The problem variables. */
    private final Variables variables;

    /** The solver backend decorated with {@link ModelIterator}. */
    private final ISolver backend;

    /** The model to return on call to {@link #next()}. */
    private int[] nextModel;

    /**
     * Constructs an instance.
     *
     * @param variables the problem variables
     * @param backend   the solver backend
     */
    SolutionIterator(final Variables variables, final ISolver backend) {
        this.variables = variables;
        this.backend = new ModelIterator(backend);
    }

    @Override
    public boolean hasNext() {
        return nextModel() != null;
    }

    @Override
    public Piece[][] next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more solution.");
        }
        final Piece[][] pieces = variables.backToPieces(nextModel);
        nextModel = null;
        return pieces;
    }

    private int[] nextModel() {
        if (nextModel != null) {
            return nextModel;
        }
        try {
            final boolean isSatisfiable = backend.isSatisfiable();
            if (isSatisfiable) {
                nextModel = backend.model();
            }
        } catch (final TimeoutException e) {
            // ignore
        }
        return nextModel;
    }

}
