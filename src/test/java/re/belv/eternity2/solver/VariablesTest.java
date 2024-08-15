package re.belv.eternity2.solver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class VariablesTest {

    @Test
    void representingPiece() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);

        assertThat(variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_0)).isEqualTo(1);
        assertThat(variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_90)).isEqualTo(2);
        assertThat(variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_180)).isEqualTo(3);
        assertThat(variables.representingPiece(0, 0, 0, Piece.Rotation.PLUS_270)).isEqualTo(4);

        assertThat(variables.representingPiece(0, 0, 8, Piece.Rotation.PLUS_180)).isEqualTo(35);
        assertThat(variables.representingPiece(0, 0, 8, Piece.Rotation.PLUS_270)).isEqualTo(36);
        assertThat(variables.representingPiece(0, 1, 0, Piece.Rotation.PLUS_0)).isEqualTo(37);
        assertThat(variables.representingPiece(0, 1, 0, Piece.Rotation.PLUS_90)).isEqualTo(38);

        assertThat(variables.representingPiece(2, 2, 8, Piece.Rotation.PLUS_270)).isEqualTo(324);
    }

    @Test
    void representingPieceCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertThat(variables.representingPieceCount()).isEqualTo(324);
    }

    @Test
    void representingBorder() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);

        assertThat(variables.representingBorder(0, 0, Piece.Border.NORTH, 0)).isEqualTo(325);
        assertThat(variables.representingBorder(0, 0, Piece.Border.NORTH, 1)).isEqualTo(326);
        assertThat(variables.representingBorder(0, 0, Piece.Border.NORTH, 2)).isEqualTo(327);
        assertThat(variables.representingBorder(0, 0, Piece.Border.NORTH, 3)).isEqualTo(328);
        assertThat(variables.representingBorder(0, 0, Piece.Border.EAST, 0)).isEqualTo(329);


        assertThat(variables.representingBorder(0, 0, Piece.Border.WEST, 2)).isEqualTo(339);
        assertThat(variables.representingBorder(0, 0, Piece.Border.WEST, 3)).isEqualTo(340);
        assertThat(variables.representingBorder(0, 1, Piece.Border.NORTH, 0)).isEqualTo(341);

        assertThat(variables.representingBorder(2, 2, Piece.Border.WEST, 3)).isEqualTo(468);
    }

    @Test
    void representingBorderCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertThat(variables.representingBorderCount()).isEqualTo(144);
    }

    @Test
    void variableCount() {
        final var board = new Board(new Piece[][]{
                {new Piece(0, 0, 1, 2, 3), new Piece(1, 0, 1, 2, 3), new Piece(2, 0, 1, 2, 3)},
                {new Piece(3, 0, 1, 2, 3), new Piece(4, 0, 1, 2, 3), new Piece(5, 0, 1, 2, 3)},
                {new Piece(6, 0, 1, 2, 3), new Piece(7, 0, 1, 2, 3), new Piece(8, 0, 1, 2, 3)},

        });
        final var variables = new Variables(board);
        assertThat(variables.count()).isEqualTo(144 + 324);
    }
}
