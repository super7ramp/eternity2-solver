package re.belv.eternity2.solver;

import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SolutionIterator implements Iterator<Piece[][]> {

    private final Variables variables;
    private final ModelIterator modelIterator;
    private int[] nextModel;

    SolutionIterator(final Variables variables, final ISolver backend) {
        this.variables = variables;
        this.modelIterator = new ModelIterator(backend);
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
            final boolean isSatisfiable = modelIterator.isSatisfiable();
            if (isSatisfiable) {
                nextModel = modelIterator.model();
            }
        } catch (final TimeoutException e) {
            // ignore
        }
        return nextModel;
    }

}
