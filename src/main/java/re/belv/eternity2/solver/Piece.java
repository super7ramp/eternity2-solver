package re.belv.eternity2.solver;

import java.util.List;

/**
 * A board piece.
 *
 * @param id         the piece id
 * @param northColor the color of the north border
 * @param eastColor  the color of the east border
 * @param southColor the color of the south border
 * @param westColor  the color of the west border
 */
public record Piece(int id, int northColor, int eastColor, int southColor, int westColor) {

    /**
     * A piece border.
     */
    enum Border {
        NORTH, EAST, SOUTH, WEST;

        private static final List<Border> CACHED_VALUES = List.of(values());

        static Iterable<Border> all() {
            return CACHED_VALUES;
        }

        static int count() {
            return CACHED_VALUES.size();
        }
    }

    /**
     * A piece rotation.
     */
    enum Rotation {
        PLUS_0, PLUS_90, PLUS_180, PLUS_270;

        private static final List<Rotation> CACHED_VALUES = List.of(values());

        static Iterable<Rotation> all() {
            return CACHED_VALUES;
        }

        static int count() {
            return CACHED_VALUES.size();
        }
    }

    /**
     * Returns the color of the given border.
     *
     * @param border the border
     * @return the color of the given border
     */
    int colorTo(final Border border) {
        return switch (border) {
            case NORTH -> northColor;
            case EAST -> eastColor;
            case SOUTH -> southColor;
            case WEST -> westColor;
        };
    }

    /**
     * Returns a new rotated piece.
     *
     * @param rotation the rotation to apply
     * @return the rotated piece
     */
    Piece rotate(final Rotation rotation) {
        return switch (rotation) {
            case PLUS_0 -> this;
            case PLUS_90 -> new Piece(id, westColor, northColor, eastColor, southColor);
            case PLUS_180 -> new Piece(id, southColor, westColor, northColor, eastColor);
            case PLUS_270 -> new Piece(id, eastColor, southColor, westColor, northColor);
        };
    }
}