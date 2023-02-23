package ChessJava;

class History {
    /**
     * Sqaure index (0-63) of {@code piece}.
     * @example 0 | 23 | 61 ...
     */
    public int from;

    /**
     * Sqaure index (0-63) of {@code piece}.
     * @example 0 | 23 | 61 ...
     */
    public int to;

    /**
     * Flags for actions.
     * @example K | Q | P | B | C
     */
    public char flag;

    /**
     * Is allowed castling
     */
    public int castle;

    /**
     * Epassant move
     */
    public int ePassant;

    /**
     * Piece information which was captured
     */
    public Piece capture;

    History(int from, int to, char flag, int castle, int e, Piece capture) {
        this.from = from;
        this.to = to;
        this.flag = flag;
        this.castle = castle;
        this.capture = capture;
        this.ePassant = e;
    }
}
