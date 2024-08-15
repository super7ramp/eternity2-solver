package re.belv.eternity2.solver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class VariablesTest {

    @Test
    void representingPiece() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);

        assertEquals(1, variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_0));
        assertEquals(2, variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_90));
        assertEquals(3, variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_180));
        assertEquals(4, variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_270));

        assertEquals(35, variables.representingPiece(0, 0, 8, Piece.Rotation.PLUS_180));
        assertEquals(36, variables.representingPiece(0, 0, 8, Piece.Rotation.PLUS_270));
        assertEquals(37, variables.representingPiece(0, 1, 0, Piece.Rotation.PLUS_0));
        assertEquals(38, variables.representingPiece(0, 1, 0, Piece.Rotation.PLUS_90));

        assertEquals(324, variables.representingPiece(2, 2, 8, Piece.Rotation.PLUS_270));
    }

    @Test
    void representingPieceCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertEquals(324, variables.representingPieceCount());
    }

    @Test
    void representingBorder() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);

        assertEquals(325, variables.representingBorder(0, 0, Piece.Border.NORTH, 0));
        assertEquals(326, variables.representingBorder(0, 0, Piece.Border.NORTH, 1));
        assertEquals(327, variables.representingBorder(0, 0, Piece.Border.NORTH, 2));
        assertEquals(328, variables.representingBorder(0, 0, Piece.Border.NORTH, 3));
        assertEquals(329, variables.representingBorder(0, 0, Piece.Border.EAST, 0));

        assertEquals(339, variables.representingBorder(0, 0, Piece.Border.WEST, 2));
        assertEquals(340, variables.representingBorder(0, 0, Piece.Border.WEST, 3));
        assertEquals(341, variables.representingBorder(0, 1, Piece.Border.NORTH, 0));

        assertEquals(468, variables.representingBorder(2, 2, Piece.Border.WEST, 3));
    }

    @Test
    void representingBorderCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertEquals(144, variables.representingBorderCount());
    }

    @Test
    void variableCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertEquals(144 + 324, variables.count());
    }
}
