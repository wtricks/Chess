package ChessJava;

import java.util.Stack;

class Variables {
    /**
     * Hold {@code ePassant} square
     */
    int epasant;

    /**
     * Hold {@code History} of game moves.
     */
    Stack<History> forwardHistory = new Stack<>();

    /**
     * Hold {@code History} of game moves.
     */
    Stack<History> backwardHistory = new Stack<>();

    /**
     * Hold {@code FEN} of piece position.
     */
    Stack<String> fen = new Stack<>();

    /**
     * Indicate if castle is possible or not.
     */
    int[] castles = new int[] { 0, 0 };

    /**
     * This indicate, how many turn are passed without capturing or moving a pawn.
     */
    int[] countMove = new int[] { 0, 0 };

    /**
     * This indicate the game status
     */
    char gameStatus = '\u0000';

    /**
     * Hold position of kings of both side.
     */
    Piece[] kingsPosition = new Piece[2];

    /**
     * Hold board position of square.
     * If square does not have any piece, null value will be there.
     */
    Piece[] position = new Piece[64];

    /**
     * Hold {@code Turn}
     */
    char turn;

    /**
     * Convert numeric index into string code from A1 to H8
     * @param index sqaure index from 0 to 63.
     * @return square code from A1 to H8.
     */
    static String getSquare(int index) {
        return ((char)(index % 8 + Constants.SQUARE)) +""+ (index / 8 + 1);
    }

    /**
     * Convert string square code into numeric index.
     * @param square Index from A1 to A8 ... H1 to H8
     */
    static int strToNum(String square) {
        return (square.charAt(0) - Constants.SQUARE) * 8 
        + (square.charAt(1) - '0') - 1;
    }
}
