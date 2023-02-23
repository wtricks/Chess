package ChessJava;

public class Constants {
    /**
     * Reprensent {@code white color piece} in the game.
     */
    public static final char WHITE = 'W';

    /**
     * Reprensent {@code black color piece} in the game.
     */
    public static final char BLACK = 'B';

    /**
     * Reprensent {@code king} in the game.
     */
    public static final char KING = 'K';

    /**
     * Reprensent {@code queen} in the game.
     */
    public static final char QUEEN = 'Q';

    /**
     * Reprensent {@code bishop} in the game.
     */
    public static final char BISHOP = 'B';

    /**
     * Reprensent {@code knight} in the game.
     */
    public static final char KNIGHT = 'N';

    /**
     * Reprensent {@code rook} in the game.
     */
    public static final char ROOK = 'R';

    /**
     * Reprensent {@code pawn} in the game.
     */
    public static final char PAWN = 'P';
    
    /**
     * Reprensent {@code sqaure} in the game.
     */
    public static final char SQUARE = 'A';

    /**
     * {@code Flag} for indicating castling in king side. 
     */
    public static final char KING_CASTLE = 'K';

    /**
     * {@code Flag} for indicating castling in queen side.
     */
    public static final char QUEEN_CASTLE = 'Q';

    /**
     * {@code Flag} for indicating pawn promote. 
     */
    public static final char PROMOTE_PAWN = 'P';

    /**
     * {@code Flag} for indicating a e-passant capture.  
     */
    public static final char E_PASSANT = 'E';

    /**
     * {@code Flag} for indicating big pawn move. 
     */
    public static final char BIG_PAWN = 'B';

    /**
     * {@code Flag} for indicating no flag. 
     */
    public static final char NO_FLAG = '\u0000';

    /**
     * Column number from which a pawn can move two square.
     */
    public static final byte[] BIG_PAWN_COL = new byte[] { 3, 6 };

    /**
     * Column number from which a pawn can be promoted.
     */
    public static final byte[] PROMOTED_COL = new byte[] { 8, 1 };

    /**
     * Default position for pieces on the board
     */
    public static final String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0";

    /**
     * Reprensent {@code checkmate}.
     */
    public static final char CHECKMATE = 'C';

    /**
     * Reprensent {@code stalemate}.
     * If king is not in check and also has not legal moves.
     */
    public static final char STALEMATE = 'S';

    /**
     * Reprensent {@code ThreeFoldRepetition}.
     * If the same piece position comes three times.
     */
    public static final char THREE_FOLD_REPETITION = 'T';

    /**
     * Reprensent {@code FiftyMove}.
     * If both player complete 100 moves without running a pawn or capturing.
     */
    public static final char FIFTY_MOVE = 'F';

     /**
     * Reprensent {@code InsufficiantMaterial}.
     * If one of player has no sufficiant piecs to win the game.
     */
    public static final char IS_MATERIAL = 'I';
}
