package ChessJava;

import java.util.ArrayList;
import java.util.List;

class Pattern {
    /**
     * {@code kingPattern} Pattern, In which king can move.
     */
    private static int[][] kingPattern = new int[][]{ 
        {-1, 0}, {1,0}, {0,-1}, {0,1}, 
        {-1, 1}, {1,1}, {1,-1}, {-1,-1} 
    };

    /**
     * {@code queenPattern} Pattern, In which queen can move.
     */
    private static int[][] queenPattern = new int[][]{ 
        {-1, 0}, {1,0}, {0,-1}, {0,1}, 
        {-1, 1}, {1,1}, {1,-1}, {-1,-1} 
    };

    /**
     * {@code bishopPattern} Pattern, In which Bishop can move.
     */
    private static int[][] bishopPattern = new int[][]{ 
        {-1, 1}, {1,1}, {1,-1}, {-1,-1} 
    };

    /**
     * {@code knightPattern} Pattern, In which Knight can move.
     */
    private static int[][] knightPattern = new int[][]{ 
        {-1,2}, {1,2}, {-1,-2}, {1,-2}, 
        {-2,1}, {-2,-1}, {2,1}, {2,-1}
    };

    /**
     * {@code rookPattern} Pattern, In which Rook can move.
     */
    private static int[][] rookPattern = new int[][]{ 
        {-1, 0}, {1,0}, {0,-1}, {0,1},
    };

    /**
     * {@code whitePawnPattern} Pattern, In which White pawn can move.
     */
    private static int[][] whitePawnPattern = new int[][]{ 
        {0,1}, {-1,1}, {1,1}
    };

    /**
     * {@code blackPawnPattern} Pattern, In which Black pawn can move.
     */
    private static int[][] blackPawnPattern = new int[][]{ 
        {0,-1}, {-1,-1}, {1,-1}
    };

    /**
     * Will hold all sqaures index which are checking {@code king}.
     */
    private List<Integer> unsafePosition = new ArrayList<>();

    /**
     * Hold reference for {@code Variables} object.
     */
    private Loader var;

    Pattern(Loader var) {
        this.var = var;
    }

    /**
     * All the unsafe positions
     */
    public List<Integer> getUnsafeBy() {
        return unsafePosition;
    }

    /**
     * Evaluate a pattern depending on piece information.
     * @param piece Piece information. 
     * @return List of all moves.
     */
    public List<Move> evaluatePattern(Piece piece) {
        List<Move> moves = new ArrayList<>();
        int[][] pattern = getPattern(piece);
        byte maxPath = getMaxPath(piece.name);

        boolean found;
        char firstChar;
        int firstNum, secondNum, nextSqr;
        Piece current;

        // evaluate each step in pattern
        for(byte i = 0; i < pattern.length; i++) {

            // for the current direction, do this
            // convert index into String square.
            // 36 -> E5, This is useful for boundry checking.
            firstChar = (char)(piece.square % 8 + Constants.SQUARE);
            secondNum = piece.square / 8 + 1;
            
            // small part of patterns for current direction.
            int[] part = pattern[i];

            for (byte j = 0; j < maxPath; j++) {
                firstNum = firstChar - Constants.SQUARE + part[0];
                secondNum += part[1];

                // now we can check boundry.
                if (firstNum < 0 || firstNum > 7 || secondNum < 1 || secondNum > 8)
                    break;
                
                // square in current direction.     
                nextSqr = firstNum + (secondNum-1)*8;
                current = var.var.position[nextSqr];

                // for the next itration, do this
                firstChar = (char)(firstNum + Constants.SQUARE);

                // we can not got more in this direction.
                // we found a piece of same color.
                if (current != null && current.color == piece.color)
                    break;
                 
                // if the piece is king
                // we also need to check, if new square is safe or not.
                if (piece.name == Constants.KING && getUnsafeBy(nextSqr).size() > 0)
                    break;  

                if (piece.name == Constants.PAWN) {
                    // check if pawn is going to promoted
                    found = secondNum == Constants.PROMOTED_COL[piece.color==Constants.WHITE?0:1];

                    // check if pawn can capture a piece or epasant pawn.
                    // i != 0 means, according to pattern when pawn is running in cross direction.
                    if (i != 0 && ((current == null && nextSqr == var.var.epasant) || (current != null && current.color != piece.color))) {
                        moves.add(new Move(piece.square, nextSqr, nextSqr==var.var.epasant?Constants.E_PASSANT:found?Constants.BIG_PAWN:Constants.NO_FLAG, true));
                    }
                    // rnbqkbnr/pppppp2/7p/6P1/8/8/PPPPPPP1/RNBQKBNR w KQkq - 0 2
                    // check for valid moves
                    // also check big pawn move.
                    // i != 0 means, according to pattern when pawn is running in vertical direction.
                    else if (i == 0 && current == null) {
                        // also check, if pawn is its initial position.
                        // we can run two steps here.
                        if (var.var.position[nextSqr+(var.var.turn==Constants.WHITE?8:-8)] == null && Constants.BIG_PAWN_COL[piece.color==Constants.WHITE?0:1] == secondNum) {
                            moves.add(new Move(piece.square, nextSqr+(piece.color==Constants.WHITE?8:-8), Constants.BIG_PAWN, false));
                        }
                        moves.add(new Move(piece.square, nextSqr, found?Constants.BIG_PAWN:Constants.NO_FLAG,false));
                    }
                } 

                // we can capture a piece, if color is opposite
                else if (current != null && current.color != piece.color) {
                    moves.add(new Move(piece.square, nextSqr, Constants.NO_FLAG, true));
                    break;
                }

                // other all the moves are valid moves
                else moves.add(new Move(piece.square, nextSqr));
            }
        }

        return moves;
    }

    /**
     * Find the matching pattern.
     * @param piece Piece information
     * @return Pattern array
     */
    private int[][] getPattern(Piece piece) {
        switch(piece.name) {
            case Constants.KING: return kingPattern;
            case Constants.QUEEN: return queenPattern;
            case Constants.BISHOP: return bishopPattern;
            case Constants.ROOK: return rookPattern;
            case Constants.KNIGHT: return knightPattern;
            default: return (piece.color == Constants.WHITE) ? 
                whitePawnPattern : blackPawnPattern;
        }
    }

    /**
     * Maximum path that can a piece run.
     * @param curr piece name.
     * @return maximu path in number.
     */
    private byte getMaxPath(char curr) {
        switch(curr) {
            case Constants.KING: return 1;
            case Constants.QUEEN: return 7;
            case Constants.PAWN: return 1;
            case Constants.KNIGHT: return 1;
            case Constants.ROOK: return 7;
            default: return 7;
        }
    }

    /**
     * Check if the square is safe for the king.
     * @param index Index of square from 0 - 63
     * @return A list of all square which are checking {@code king}.
     *   Use it as readable, do not make any changes in this list.
     */
    public List<Integer> getUnsafeBy(int index) {
        // clear all previous indexs.
        unsafePosition.clear();

        int distance, i, j;
        char direction, currentPiece;
        String from, to;
        Piece king = var.var.kingsPosition[var.var.turn == Constants.WHITE ? 0 : 1];

        // remove king piece from the board.
        var.var.position[king.square] = null;

        // one way to check if any piece can capture king on perticular index is to make same move.
        // if we run pawn move from perticular and if we can capture a pawn, it means that pawn can also capture current piece.
        // same works for others pieces. This way we can check if the king is safe on perticular index or not.
        Piece[] pieces = new Piece[] { 
            new Piece(index, king.color, Constants.QUEEN),
            new Piece(index, king.color, Constants.PAWN),
            new Piece(index, king.color, Constants.KNIGHT)
        };

        for(i = 0; i < 3; i++) {
            // get all posible moves according to perticular piece.
            List<Move> moves = evaluatePattern(pieces[i]);
        
            for(j = 0; j < moves.size(); j++) {
                // we will check only those piece which can be captured.
                if (moves.get(j).capture == false) continue;
                
                // piece information on current square
                currentPiece = var.var.position[moves.get(j).to].name;

                // if piece is pawn or knight
                if (pieces[i].name != Constants.QUEEN) {
                    
                    // piece must be same.
                    if (currentPiece == pieces[i].name) 
                        unsafePosition.add(moves.get(j).to);
                    continue;
                }

                // get sqaure code, ex. A6...H3
                from = new String(Variables.getSquare(index));
                to = new String(Variables.getSquare(moves.get(j).to));
                
                // get of two squares.
                direction = getDirection(from, to);

                // get the distance between two sqaures.
                distance = (distance = Math.abs(from.charAt(1) - to.charAt(1))) != 0 ? distance : Math.abs(from.charAt(0) - to.charAt(0));
            
                // At distance one, only bishop, queen, pawn, rook and king can capture.
                if (distance == 1 && (currentPiece == Constants.QUEEN || currentPiece == Constants.KING || (currentPiece == Constants.BISHOP && direction == 'C') || (currentPiece == Constants.ROOK && direction == 'P'))) {
                    unsafePosition.add(moves.get(j).to);
                }

                // At distance more than 1, only queen & rook can capture in plus+ direction and queen & bishop can capture in cross direction.
                else if (currentPiece == Constants.QUEEN || (currentPiece == Constants.BISHOP && direction == 'C') || (currentPiece == Constants.ROOK && direction == 'P')) {
                    unsafePosition.add(moves.get(j).to);
                }   
            }
        }

        // add king piece to the board again.
        var.var.position[king.square] = king;

        return unsafePosition;
    }

    /**
     * Get the direction, In which two sqaures are placed.
     * @param from First square index from 0 to 63
     * @param to Second square index from 0 to 63
     * @return C -> Cross direction, P -> Horizontal or vertical direction
     */
    public static char getDirection(String from, String to) {
        // direction will be horizontal if first character is same of both square code, ex. A==A | H==H ...
        // direction will be vertical if second character is same of both square code, ex. 1==1 | 8==8 ...
        if (from.charAt(0) == to.charAt(0) || from.charAt(1) == to.charAt(1))
            return 'P';

        // direction will be 'top left' to 'bottom right' or 'top right' to 'bottom bottom'.
        // if absolute difference of first and last character of both square code is same.
        if (Math.abs(from.charAt(0) - to.charAt(0)) == Math.abs(from.charAt(1) - to.charAt(1)))
            return 'C';
        
        return Constants.NO_FLAG;
    }
}
