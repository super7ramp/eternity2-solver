package re.belv.eternity2.solver;

import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

/**
 * An iterator of solver solutions.
 */
final class SolutionIterator implements Iterator<Piece[][]> {

    /** The interval at which to print statistics. */
    private static final int PRINT_STATS_INTERVAL_IN_SECONDS = 5;

    /** The problem variables. */
    private final Variables variables;

    /** The solver backend decorated with {@link ModelIterator}. */
    private final ISolver backend;

    /** Printer for statistics. */
    private final PrintWriter printer;

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
        printer = new PrintWriter(System.out, true);
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

        try (final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2)) {
            executor.scheduleAtFixedRate(this::printStats, PRINT_STATS_INTERVAL_IN_SECONDS,
                    PRINT_STATS_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
            final Future<int[]> futureNextModel = executor.submit(this::lookForSolution);
            nextModel = futureNextModel.get();
        } catch (final InterruptedException e) {
            // This forces the solver to stop.
            backend.expireTimeout();
            Thread.currentThread().interrupt();
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }

        return nextModel;
    }

    private void printStats() {
        printer.println(PRINT_STATS_INTERVAL_IN_SECONDS + "s elapsed, here are some statistics:");
        backend.printStat(printer);
        printer.println("---------------------");
    }

    private int[] lookForSolution() throws TimeoutException {
        return backend.isSatisfiable() ? backend.model() : null;
    }
}
