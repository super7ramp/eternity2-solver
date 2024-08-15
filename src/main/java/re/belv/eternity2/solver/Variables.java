package re.belv.eternity2.solver;

/**
 * Where the translation between the problem and the variables occurs.
 */
final class Variables {

    /** The problem to solve. */
    private final Problem problem;

    /**
     * Constructs an instance.
     *
     * @param problem the problem to solve
     */
    Variables(final Problem problem) {
        this.problem = problem;
    }

    /**
     * Returns the variable representing the given piece with the given rotation at the given row and column.
     * <table>
     *     <caption>Variable representation for a 3x3 grid</caption>
     *   <tr>
     *     <th>Slot variable</th>
     *     <td>1</td>
     *     <td>2</td>
     *     <td>3</td>
     *     <td>4</td>
     *     <td>5</td>
     *     <td>...</td>
     *     <td>35</td>
     *     <td>36</td>
     *     <td>37</td>
     *     <td>38</td>
     *     <td>...</td>
     *     <td>144</td>
     *   </tr>
     *   <tr>
     *     <th>Represented value</th>
     *     <td>(0,0): Piece #0, +0°</td>
     *     <td>(0,0): Piece #0, +90°</td>
     *     <td>(0,0): Piece #0, +180°</td>
     *     <td>(0,0): Piece #0, +270°</td>
     *     <td>(0,0): Piece #1, +0°</td>
     *     <td>...</td>
     *     <td>(0,0): Piece #8, +180°</td>
     *     <td>(0,0): Piece #8, +270°</td>
     *     <td>(0,1): Piece #0, +0°</td>
     *     <td>(0,1): Piece #0, +90°</td>
     *     <td>...</td>
     *     <td>(3,3): Piece #8, +270°</td>
     *   </tr>
     * </table>
     *
     * @param pieceIndex  the piece
     * @param rotation    the piece rotation
     * @param rowIndex    the row of the piece
     * @param columnIndex the column of the piece
     * @return the variable
     */
    int representingPiece(final int rowIndex, final int columnIndex, final int pieceIndex, final Piece.Rotation rotation) {
        if (rowIndex >= problem.rowCount()) {
            throw new IllegalArgumentException("Row index out of bounds: " + rowIndex);
        }
        if (columnIndex >= problem.columnCount()) {
            throw new IllegalArgumentException("Column index out of bounds: " + columnIndex);
        }
        if (pieceIndex >= problem.piecesCount()) {
            throw new IllegalArgumentException("Piece index out of bounds: " + pieceIndex);
        }
        return rowIndex * problem.columnCount() * problem.piecesCount() * Piece.Rotation.count()
                + columnIndex * problem.piecesCount() * Piece.Rotation.count()
                + pieceIndex * Piece.Rotation.count()
                + rotation.ordinal()
                + 1; // variables start at 1
    }

    /**
     * Returns the number variables representing pieces.
     *
     * @return the  number variables representing pieces
     */
    int representingPieceCount() {
        return problem.rowCount() * problem.columnCount() * problem.piecesCount() * Piece.Rotation.count();
    }

    /**
     * Returns the variable representing the given border with the given color at the given row and column.
     * <table>
     *     <caption>Variable representation for a 3x3 grid with 4 colors</caption>
     *   <tr>
     *     <th>Color variable</th>
     *     <td>325</td>
     *     <td>326</td>
     *     <td>327</td>
     *     <td>328</td>
     *     <td>329</td>
     *     <td>...</td>
     *     <td>339</td>
     *     <td>340</td>
     *     <td>341</td>
     *     <td>...</td>
     *     <td>468</td>
     *   </tr>
     *   <tr>
     *     <th>Represented value</th>
     *     <td>(0,0), North: Color #0</td>
     *     <td>(0,0), North: Color #1</td>
     *     <td>(0,0), North: Color #2</td>
     *     <td>(0,0), North: Color #3</td>
     *     <td>(0,0), East: Color #0</td>
     *     <td>...</td>
     *     <td>(0,0), West: Color #2</td>
     *     <td>(0,0), West: Color #3</td>
     *     <td>(0,1), North: Color #0</td>
     *     <td>...</td>
     *     <td>(3,3), West: Color #9</td>
     *   </tr>
     * </table>
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @param border      the border
     * @param colorIndex  the color index
     * @return the variable of the given color at the given border at the given row and column.
     */
    int representingBorder(final int rowIndex, final int columnIndex, final Piece.Border border, final int colorIndex) {
        if (rowIndex >= problem.rowCount()) {
            throw new IllegalArgumentException("Row index out of bounds: " + rowIndex);
        }
        if (columnIndex >= problem.columnCount()) {
            throw new IllegalArgumentException("Column index out of bounds: " + columnIndex);
        }
        if (colorIndex >= problem.colorCount()) {
            throw new IllegalArgumentException("Color index out of bounds: " + colorIndex);
        }
        return representingPieceCount() + 1
                + rowIndex * problem.columnCount() * Piece.Border.count() * problem.colorCount()
                + columnIndex * Piece.Border.count() * problem.colorCount()
                + border.ordinal() * problem.colorCount()
                + colorIndex;
    }

    /**
     * Returns the number of variables representing borders.
     *
     * @return the number of variables representing borders
     */
    int representingBorderCount() {
        return problem.colorCount() * problem.borderCount();
    }

    /**
     * Returns the total number of variables.
     *
     * @return the total number of variables
     */
    int count() {
        return representingPieceCount() + representingBorderCount();
    }

    /**
     * Translates model back to pieces.
     *
     * @param model the model
     * @return the pieces
     */
    public Piece[][] backToPieces(final int[] model) {
        final var pieces = new Piece[problem.rowCount()][problem.columnCount()];
        for (int rowIndex = 0; rowIndex < problem.rowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem.columnCount(); columnIndex++) {
                for (int pieceNumber = 0; pieceNumber < problem.piecesCount(); pieceNumber++) {
                    for (final Piece.Rotation rotation : Piece.Rotation.all()) {
                        final int slotVariable = representingPiece(rowIndex, columnIndex, pieceNumber, rotation);
                        if (model[slotVariable - 1] > 0) {
                            final Piece piece = problem.piece(pieceNumber).rotate(rotation);
                            pieces[rowIndex][columnIndex] = piece;
                        }
                    }
                }
            }
        }
        return pieces;
    }
}
