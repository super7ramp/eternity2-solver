package re.belv.eternity2.solver;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * The Eternity II game.
 */
final class Game {

    private final Piece[] pieces;
    private final Piece[][] initialBoard;
    private final int rowCount;
    private final int columnCount;
    private final int colorCount;

    /**
     * Constructs an instance.
     *
     * @param pieces       the available pieces
     * @param initialBoard the initial board; Any non-{@code null} piece is considered as fixed and will not be moved
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if the number of pieces is inconsistent with the given row and column counts
     */
    Game(final Piece[] pieces, final Piece[][] initialBoard) {
        this.pieces = Objects.requireNonNull(pieces);
        this.initialBoard = Objects.requireNonNull(initialBoard);
        rowCount = initialBoard.length;
        columnCount = rowCount == 0 ? 0 : initialBoard[0].length;
        if (rowCount * columnCount != pieces.length) {
            throw new IllegalArgumentException("Inconsistent number of pieces: " + pieces.length + " != " + rowCount + " * " + columnCount);
        }
        colorCount = (int) Arrays.stream(pieces)
                .flatMapToInt(piece -> IntStream.of(piece.northColor(), piece.eastColor(), piece.southColor(), piece.westColor()))
                .distinct()
                .count();
    }

    Piece piece(final int pieceNumber) {
        return pieces[pieceNumber];
    }

    Optional<Piece> initialBoardPiece(final int rowIndex, final int columnIndex) {
        return Optional.ofNullable(initialBoard[rowIndex][columnIndex]);
    }

    int rowCount() {
        return rowCount;
    }

    int columnCount() {
        return columnCount;
    }

    int piecesCount() {
        return pieces.length;
    }

    int borderCount() {
        return Piece.Border.count() * piecesCount();
    }

    int colorCount() {
        return colorCount;
    }
}
