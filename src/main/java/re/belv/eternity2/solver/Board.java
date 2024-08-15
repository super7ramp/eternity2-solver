package re.belv.eternity2.solver;

import java.util.HashSet;
import java.util.Objects;

final class Board {

    private final Piece[][] pieces;
    private final int[] colors;

    Board(final Piece[][] pieces) {
        Objects.requireNonNull(pieces);
        final var colorSet = new HashSet<Integer>();
        for (final Piece[] row : pieces) {
            Objects.requireNonNull(row);
            for (final Piece piece : row) {
                Objects.requireNonNull(piece);
                colorSet.add(piece.northColor());
                colorSet.add(piece.eastColor());
                colorSet.add(piece.southColor());
                colorSet.add(piece.westColor());
            }
        }
        this.pieces = pieces;
        this.colors = colorSet.stream().mapToInt(Integer::intValue).toArray();
    }

    Piece piece(final int pieceNumber) {
        return pieces[pieceNumber / columnCount()][pieceNumber % columnCount()];
    }

    int rowCount() {
        return pieces.length;
    }

    int columnCount() {
        return pieces.length == 0 ? 0 : pieces[0].length;
    }

    int piecesCount() {
        return rowCount() * columnCount();
    }

    int borderCount() {
        return Piece.Border.count() * piecesCount();
    }

    int colorCount() {
        return colors.length;
    }
}
