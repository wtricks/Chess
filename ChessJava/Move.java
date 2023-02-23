package ChessJava;

public final class Move {
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
     * Same as {@code flag}, but will be true if move can capture a piece.
     */
    public boolean capture;

    public Move(int from, int to,  char flag, boolean capture) {
        this.from = from;
        this.to = to;
        this.flag = flag;
        this.capture = capture;
    }

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
        this.flag = Constants.NO_FLAG;
        this.capture = false;
    }
}
