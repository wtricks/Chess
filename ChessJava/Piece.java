package ChessJava;

public class Piece {
    /**
     * Will be the color of current piece
     * {@example} W | B
     */
    public char color;

    /**
     * Square index (0-63) where this piece is placed.
     * @example 0 | 23 | 54 ...
     */
    public int square;

    /**
     * Name of the piece.
     * @example K | Q | B | R | N | P
     */
    public char name;

    public Piece(int square, char color, char name) {
        this.square = square;
        this.color = color;
        this.name = name;
    }
}
