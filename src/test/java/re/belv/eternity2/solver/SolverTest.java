package re.belv.eternity2.solver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static re.belv.eternity2.solver.Piece.Rotation.*;

/**
 * Tests for {@link Solver}.
 */
final class SolverTest {

    private Solver solver;

    @BeforeEach
    void setUp() {
        solver = new Solver();
    }

    @Test
    void solve_1x2() {
        final var pieces = new Piece[]{new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3)};
        final var initialBoard = new Piece[][]{{null, null}};

        final Iterator<Piece[][]> solutionIterator = solver.solve(pieces, initialBoard);

        assertThat(solutionIterator).toIterable()
                .contains(new Piece[][]{{new Piece(0, 2, 3, 0, 1), new Piece(1, 0, 1, 2, 3)},})
                .hasSize(8);
    }

    @Test
    void solve_2x1() {
        final var pieces = new Piece[]{new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3),
        };
        final var initialBoard = new Piece[][]{
                {null},
                {null},
        };

        final Iterator<Piece[][]> solutionIterator = solver.solve(pieces, initialBoard);

        assertThat(solutionIterator).toIterable()
                .contains(new Piece[][]{
                        {new Piece(0, 2, 3, 0, 1)},
                        {new Piece(1, 0, 1, 2, 3)},
                })
                .hasSize(8);
    }

    @Test
    void solve_2x2() {
        final var pieces = new Piece[]{
                new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3),
                new Piece(2, 0, 1, 2, 3), new Piece(3, 0, 1, 2, 3),
        };
        final var initialBoard = new Piece[][]{
                {null, null},
                {null, null},
        };

        final Iterator<Piece[][]> solutionIterator = solver.solve(pieces, initialBoard);

        assertThat(solutionIterator).toIterable()
                .contains(new Piece[][]{
                        {new Piece(1, 0, 1, 2, 3), new Piece(0, 2, 3, 0, 1)},
                        {new Piece(2, 2, 3, 0, 1), new Piece(3, 0, 1, 2, 3)},
                })
                .hasSize(96);
    }

    @Test
    void solve_5x5() {
        final var piece19 = new Piece(19, 4, 8, 6, 8);
        final var pieces = new Piece[]{
                new Piece(0, 1, 0, 2, 1), new Piece(1, 1, 2, 4, 1), new Piece(2, 1, 2, 0, 1), new Piece(3, 1, 2, 0, 1), new Piece(4, 1, 3, 5, 4),
                new Piece(5, 1, 0, 8, 2), new Piece(6, 1, 3, 8, 2), new Piece(7, 1, 4, 5, 0), new Piece(8, 1, 3, 6, 0), new Piece(9, 1, 2, 5, 2),
                new Piece(10, 1, 3, 6, 2), new Piece(11, 1, 0, 7, 3), new Piece(12, 1, 4, 6, 3), new Piece(13, 1, 2, 7, 2), new Piece(14, 1, 0, 6, 3),
                new Piece(15, 1, 2, 5, 3), new Piece(16, 6, 8, 4, 5), new Piece(17, 4, 8, 5, 5), new Piece(18, 6, 8, 7, 6), piece19,
                new Piece(20, 6, 7, 6, 7), new Piece(21, 6, 5, 8, 4), new Piece(22, 5, 8, 5, 8), new Piece(23, 5, 7, 7, 7), new Piece(24, 6, 6, 6, 5),
        };
        final var initialBoard = new Piece[][]{
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, piece19.rotate(PLUS_90), null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
        };

        final Iterator<Piece[][]> solutionIterator = solver.solve(pieces, initialBoard);

        assertThat(solutionIterator).toIterable()
                .contains(new Piece[][]{
                        {pieces[2], pieces[13], pieces[6], pieces[12], pieces[1].rotate(PLUS_90)},
                        {pieces[11].rotate(PLUS_270), pieces[23].rotate(PLUS_90), pieces[22].rotate(PLUS_90), pieces[24], pieces[10].rotate(PLUS_90)},
                        {pieces[8].rotate(PLUS_270), pieces[20].rotate(PLUS_90), pieces[19].rotate(PLUS_90), pieces[21], pieces[15].rotate(PLUS_90)},
                        {pieces[5].rotate(PLUS_270), pieces[18].rotate(PLUS_180), pieces[16].rotate(PLUS_270), pieces[17].rotate(PLUS_270), pieces[9].rotate(PLUS_90)},
                        {pieces[3].rotate(PLUS_270), pieces[14].rotate(PLUS_180), pieces[4].rotate(PLUS_180), pieces[7].rotate(PLUS_180), pieces[0].rotate(PLUS_180)},
                })
                .hasSize(16);
    }
}
