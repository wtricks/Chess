package ChessJava;

import java.util.ArrayList;
import java.util.List;

public class Chess {
    /**
     * Hold reference for {@code Pattern} object.
     */
    private Pattern pattern;

    /**
     * Hold reference for {@code Loader} object.
     */
    private Loader loader;
    
    /**
     * Hold piece moves.
     * Helpful for checking game status.
     */
    private List<Move> lastMove;

    /**
     * Hold piece moves.
     * Will be used in {@code movePiece}.
     */
    private List<Move> pieceMove;
    
    // use for performance reason.
    private boolean hasValue = true;

    // last result of 'InsufficiantMaterial' method
    private boolean IS_MA = false;

    // last result of 'ThreeFoldrepetition' method
    private boolean TH_RP = false;

    // is this first time
    private boolean initial = true;

    public Chess() {
        loader = new Loader();
    }

    /**
     * Load initial game
     * @return Empty string indicate not error, otherwise the error message
     */
    public String load() {
        return load(Constants.DEFAULT_FEN);
    }

    /**
     * Get piece positions in FEN string form.
     * @return FEN string
     */
    public String fen() {
        if (initial) return "";
        return loader.fen();
    }

    /**
     * Get piece positions in the form of string that you can print.
     * Visualize piec positions in the board using this string
     */
    public String print() {
        if (initial) return "";
        return loader.print();
    }

    /**
     * Load game from specific position by suing FEN.
     * @param fen FEN string.
     * @return Empty string indicate not error, otherwise the error message
     */
    public String load(String fen) {
        Variables var = loader.var;
        String result = loader.load(fen);
        if (!result.equalsIgnoreCase("")) {
            loader.var = var;
            return result;
        }

        // trigger listener
        onLoad(initial);
        
        if (initial) {
            // create pattern instance
            pattern = new Pattern(loader);
            initial = false;
        } 


        // before doing something, check if game is not over.
        hasValue = false;
        if (isGameOver()) {
            return "Game is Over";
        }
        hasValue = true;

        // remove old pieces from the board
        if (var != null) {
            for(byte i=0; i <64; i++) {
                if (var.position[i] != null)
                    onRemove(var.position[i]);
            }
        }

        // remove old pieces from the board
        for(byte i=0; i <64; i++) {
            if (loader.var.position[i] != null)
                onAdd(loader.var.position[i]);
        }
        
        // listen about turn change
        onSwap(loader.var.turn);

        // insert the piece positions
        loader.var.fen.push(loader.helperOfFen());
        return "";
    }

    /**
     * Move piece from one location to another
     * @param index
     */
    public boolean movePiece(String square) {
        return movePiece(Variables.strToNum(square));
    }

    /**
     * Move piece from one location to another
     * @param index
     */
    public boolean movePiece(Move move) {
        if (initial) return false;

        Variables v = loader.var;   
        Piece[] position = v.position;
        Piece piece = position[move.from];

        // piece must be same as 'TURN' and has some moves
        if (piece == null || getTurn() != piece.color || pieceMove == null || pieceMove.size() == 0 || v.gameStatus != Constants.NO_FLAG) 
            return false;

        int color = Constants.WHITE == piece.color ? 0 : 1;
        int castle = v.castles[color], ep = v.epasant;
        Piece capture = position[move.to];

        // count turns
        if (Constants.BLACK == piece.color) v.countMove[1]++;

        // if piece is pawn, we can sure that same position will not arive
        if (piece.name == Constants.PAWN) v.fen.clear();

        // turn of castle if we move rook or king
        // also if we are capturing rook.
        if (position[move.from].square == Constants.KING ||
            position[move.from].square == Constants.ROOK || 
            (position[move.to] != null && position[move.to].square == Constants.ROOK)
        ) v.castles[color] = -1;

        if (move.flag != Constants.E_PASSANT) {
            v.epasant = -1;
        }

        // if the move is ePassant capture.
        if (move.flag == Constants.E_PASSANT) {
            int nextSqr = v.epasant + (piece.color == Constants.WHITE ? -8 : 8);
            v.countMove[0] = 0;
            capture = position[nextSqr]; // store piece information so we can use it to store in history 
            onRemove(position[nextSqr]); // trigger listener
            onCapture(position[nextSqr], piece, true); // trigger listener
            position[nextSqr] = null;
            v.epasant = -1;
        } 

        // capture a piece by piece other than the pawn
        else if (move.capture) {
            v.countMove[0] = 0; 
            onRemove(position[move.to]); // trigger listener
            onCapture(position[move.to], piece, false); // trigger listener
            position[move.to] = null;
            v.fen.clear();
        } 

        // only a valid move, no capture
        else if (piece.name == Constants.PAWN) {
            if (move.flag == Constants.BIG_PAWN) {
                v.epasant = move.to + (piece.color == Constants.WHITE ? -8 : 8);
            } 
            v.countMove[0] = 0;
        } else v.countMove[0]++;

        // now move piece from 'from' to 'to'.
        position[move.to] = position[move.from];
        position[move.to].square = move.to;
        position[move.from] = null;
        onMove(move.from, move.to); // trigger listener

        // check if pawn is promoted
        if (move.flag == Constants.PROMOTE_PAWN) {
            onRemove(piece); // trigger listener
            Piece newPiece = onPromoted(piece);
            if (newPiece == null) 
                newPiece = new Piece(piece.square, piece.color, Constants.QUEEN);
        
            // Add it to the board
            position[move.to] = newPiece;
            onAdd(newPiece);  // trigger listener
        }

        // queen side castling
        else if (move.flag == Constants.QUEEN_CASTLE) {
            int from = color == 0 ? 0 : 56;
            int to = color == 0 ? 3 : 59;
            onMove(from, to);

            position[to] = position[from];
            position[to].square = to;
            position[from] = null;
        }

        // queen side castling
        else if (move.flag == Constants.KING_CASTLE) {
            int from = color == 0 ? 7 : 63;
            int to = color == 0 ? 5 : 61;
            onMove(from, to);

            position[to] = position[from];
            position[to].square = to;
            position[from] = null;
        }

        // trigger listener
        onSwap(v.turn = color == 0 ? 'B' : 'W');

        // remove all move information
        lastMove = pieceMove = null;

        // store move info in history
        // we are also storing piece position so we can check ThreeFoldRepetition.
        v.forwardHistory.add(new History(move.from, move.to, move.flag, castle, ep, capture));
        v.fen.push(loader.helperOfFen());
        
        // if we have some moves for 'REDO'
        if (v.backwardHistory.size() > 0)
            v.backwardHistory.empty();

        // check game status
        hasValue = false;
        if (isGameOver()) {
            return false;
        }

        hasValue = true;

        // check if opponent king is in check
        if (inCheck()) {
            onCheck(piece, loader.var.kingsPosition[color==0?1:0]);
        }

        return true;
    }

    /**
     * Move piece from one location to another
     * @param index
     */
    public boolean movePiece(int to) {
        if (to > 63 || to < 0 || pieceMove == null || initial) 
            return false;
        
        for(byte i=0; i<pieceMove.size(); i++) {
            if (pieceMove.get(i).to == to) {
                return movePiece(pieceMove.get(i)); 
            }
        } 

        return false;
    }

    /**
     * Get all the legal moves for piece
     * @param square
     * @return
     */
    public List<Move> getMove(String square) {
        return getMove(Variables.strToNum(square));
    }

    /**
     * Get all the legal moves for piece
     * @param square square index from 0 - 63
     * @return
     */
    public List<Move> getMove(int index) {
        if (index < 0 || index > 63 || initial) return null;

        Variables var = loader.var;
        Piece piece = var.position[index];

        if (piece == null || piece.color != loader.var.turn || var.gameStatus != Constants.NO_FLAG) return null;

        // get all legal moves.
        List<Move> moves = pattern.evaluatePattern(piece);
    
        // we need to check if castling is possible
        if (Constants.KING == piece.name) {
            return pieceMove=getKingMoves(moves);
        }

        // no other piece can safe king.
        // if more than 1 piece is checking king
        if (pattern.getUnsafeBy(loader.var.kingsPosition[getTurn()==Constants.WHITE?0:1].square).size() > 1) {
            moves.clear();
            return pieceMove=moves;
        }

        // if only 1 piece is checking king,
        // check current piece can capture that opponent piece or can come between them.
        if (pattern.getUnsafeBy().size() == 1 && moves.size() > 0) {
            return pieceMove=canPieceMove(moves, index);
        }

        // we also need to check, what if we remove this piece.
        // our king is safe or not.
        var.position[index] = null;
        if (pattern.getUnsafeBy(var.kingsPosition[piece.color==Constants.WHITE?0:1].square).size() > 0) {
            moves.clear();
        }

        var.position[index] = piece;
        return pieceMove=moves;
    }

    /**
     * Check if piece other than king can move if the king is in check.
     * @param moves 
     * @param index
     * @return
     */
    private List<Move> canPieceMove(List<Move> moves, int index) {
        int unsafeIndex = pattern.getUnsafeBy().get(0); 
        Piece piece = loader.var.position[unsafeIndex];

        // check if we can capture opponent piece
        Move move = null;
        for(byte i = 0; i < moves.size(); i++) {
            if (moves.get(i).capture && moves.get(i).to == unsafeIndex) {
                move = moves.get(i);
                break;
            }
        }

        // check checker is knight or pawn.
        // we can only capture theme, otherwise not wasy to protect king.
        if (piece.name == Constants.KNIGHT || piece.name == Constants.PAWN) {
            moves.clear();
            if (move != null)
                moves.add(move);
            return moves;
        }

        // new list for storing moves.
        List<Move> newList = new ArrayList<>();
        if (move != null) newList.add(move);

        // check all sqaures between two squares
        squareBetween(
            loader.var.position[index], moves, newList, 
            Variables.getSquare(unsafeIndex),
            Variables.getSquare(
                loader.var.kingsPosition[piece.color == Constants.BLACK?0:1].square
            )
        );
        
        return newList;
    }

    /**
     * Get all sqaures between two sqaures in board.
     * @param sqr1 source sqaure
     * @param sqr2 destination square
     * @return
     */
    private void squareBetween(Piece piece, List<Move> moves, List<Move> newList, String sqr1, String sqr2) {
        int first = sqr2.charAt(0) - sqr1.charAt(0);
        int second = sqr2.charAt(1) - sqr1.charAt(1);
        
        first = first == 0 ? 0 : (first > 0 ? 1 : -1);
        second = second == 0 ? 0 : (second > 0 ? 1 : -1);
        String curr = sqr1;
        int index = 0, f, s;
        while(!curr.equals(sqr2) && index < 8) {
            f = (curr.charAt(0) - Constants.SQUARE) + first;
            s = (curr.charAt(1) - '0') + second;

            if (f < 0 || f > 7 || s > 8 || s < 1)
                break;
            
            f = (s-1)*8+f;
            for(Move m : moves) {
                if (m.to == f) {
                    newList.add(new Move(piece.square, f, piece.color, false));
                }
            }
            curr = Variables.getSquare(f);  
            index++;   
        }
    }

    /**
     * Check for castling
     * @param moves
     * @return
     */
    private List<Move> getKingMoves(List<Move> moves) {
        int color = loader.var.turn == Constants.WHITE ? 0 : 1;
        int num = color == 0 ? 1 : 8;
        Piece piece = loader.var.kingsPosition[color];

        // for castling king must not be in check
        if (pattern.getUnsafeBy(piece.square).size() > 0)
            return moves;

        // check king side castle
        if ((loader.var.castles[color] == 1 || loader.var.castles[color] == 3) && isValid(0, color, num)) {
            moves.add(new Move(piece.square, (color == 0 ? 6 : 62), Constants.KING_CASTLE, false));
        }

        // check queen side castle
        if (loader.var.castles[color] >= 2 && isValid(1, color, num)) {
            moves.add(new Move(piece.square, (color == 0 ? 2 : 58), Constants.QUEEN_CASTLE, false));
        }

        return moves;
    }

    /**
     * Check all the sqaures between rook and king.
     * All sqaure must be safe and should not have no piece.
     * @param piece
     * @param color
     * @param num
     * @return
     */
    private boolean isValid(int piece, int color, int num) {
        int index, i;
        Variables var = loader.var;
        String square = Variables.getSquare(var.kingsPosition[color].square);

        if (square.equals("E"+num)) {
            String[] sqr = piece != 1 ? new String[] { ("C" + num), ("D" + num) } : new String[] { ("F" + num), ("G" + num) };

            for(i = 0; i < 2; i++) {
                index = Variables.strToNum(sqr[i]);
                if (var.position[index] != null || pattern.getUnsafeBy(index).size() > 0) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Check if game is over.
     * @return
     */
    public boolean isGameOver() {
        if (isCheckmate() || isDraw()) {
            onStatusChange(loader.var.gameStatus);
            return true;
        }

        return false;
    }

    /**
     * Check if game is draw.
     * @return
     */
    public boolean isDraw() {
        // game is alredy over
        if (loader.var.gameStatus != Constants.NO_FLAG)
            return false;

        char res = Constants.NO_FLAG;

        if (loader.var.countMove[0] >= 100) {
            res = Constants.FIFTY_MOVE;
        } else if (isStalemate()) {
            res = Constants.STALEMATE;
        } else if (isInsufficiantMaterial()) {
            res = Constants.IS_MATERIAL;
        } else if (isThreeFoldRepetition()) {
            res = Constants.THREE_FOLD_REPETITION;
        }

        if (res != Constants.NO_FLAG) {
            loader.var.gameStatus = res;
            return true;
        }

        return false;
    }
    
    /**
     * Is current the king in check
     * @return
     */
    public boolean inCheck() {
        return pattern.getUnsafeBy().size() > 0;
    }

    /**
     * Check if game is in checkmate situation.
     * @return
     */
    private boolean isCheckmate() {
        if (!hasMoves() && inCheck()) {
            loader.var.gameStatus = Constants.CHECKMATE;
            return true;
        }

        return false;
    }

    /**
     * Check if game is draw by stalemate situation.
     * @return
     */
    public boolean isStalemate() {
        return !hasMoves() && !inCheck();
    }

    /**
     * Check game has sufficiant material or not.
     * @return
     */
    public boolean isInsufficiantMaterial() {
        if (hasValue) return IS_MA;
        short count = 0;

        Piece piece;
        byte blackKnight = -1;
        byte whiteKnight = -1;
        for(byte i=0; i<64; i++) {
            piece = loader.var.position[i];

            // we can ignore queen, pown and rook.
            // beacuse if they are in the game then game has sufficant piece.
            if (piece != null && (piece.name == Constants.QUEEN || 
                piece.name == Constants.PAWN || piece.name == Constants.ROOK)
            ) return (IS_MA = false);

            if (piece.color == Constants.BLACK) {
                if (piece.name == Constants.BIG_PAWN)
                    blackKnight = i;
                count += Character.toLowerCase(piece.name);   
            } else {
                if (piece.name == Constants.BIG_PAWN)
                    whiteKnight = i;
                count += piece.name; 
            }
        }

        if (count == 66 || count == 78 || count == 156 || count == 98 || count == 110 || count == 220) {
            return IS_MA = true;
        }

        // We have one condition whwre both side has only one bishop and a king.
        // but if both bishop are on diiferent color box, then they will never meet.
        // this is also a condition, where game will never over.
        if (count == 156 && whiteKnight != -1 && whiteKnight%8 != blackKnight%8) {
            return IS_MA = true;
        }

        return IS_MA = false;
    }

    /**
     * Check conditoin where same piece position arise more than two times.
     * @return
     */
    public boolean isThreeFoldRepetition() {
        if (hasValue) // returning previously calculated value
            return TH_RP;

        if (loader.var.fen.size() == 0) 
            return TH_RP = false;  

        int count = 0;
        String current = loader.var.fen.peek();
        for(String fen : loader.var.fen) {
            if (fen.equals(current))
                count++;    
        }

        return TH_RP = count > 2;
    }

    /**
     * Check if we have legals moves
     * @return
     */
    private boolean hasMoves() {
        if (hasValue) 
            return lastMove == null || lastMove.size() > 0;
            
        Variables v = loader.var;

        int color = (getTurn() == Constants.WHITE) ? 0 : 1;
        // check if king has safest moves or not.
        lastMove = pattern.evaluatePattern(v.kingsPosition[color]);
        
        // also check if king is safe or not
        pattern.getUnsafeBy(loader.var.kingsPosition[color].square);

        // if king has moves
        if (lastMove.size() > 0) return true;

        // if king has not moves
        // check for pieces.
        for(byte i = 0; i < 64; i++) {
            if (v.position[i] != null && v.position[i].color == getTurn()) {
                lastMove = pattern.evaluatePattern(v.position[i]);

                if (lastMove.size() > 0) return true;
            }
        }

        return false;
    }

    /**
     * Get the turn of player.
     * @return
     */
    public char getTurn() {
        if (initial) return ' ';
        return loader.var.turn;
    }

    /**
     * Undo piece position one step
     * @return
     */
    public boolean undo() {
        Variables v = loader.var;
        if (initial || v.forwardHistory.isEmpty())
            return false;

        // get last move information    
        History his = v.forwardHistory.pop();
        v.backwardHistory.push(his);

        boolean isWhite = getTurn() == Constants.WHITE;
        v.castles[isWhite ? 1: 0] = his.castle;
        v.epasant = his.ePassant;
        
        // decrease turns
        if (isWhite) 
            v.countMove[1]--;

        // decrease fifty move.    
        if (v.countMove[0] < 2) 
            v.countMove[0] = 0;
        else v.countMove[0]--;       

        if (his.flag == Constants.KING_CASTLE) {
            int from = !isWhite ? 5 : 61;
            int to = !isWhite ? 7 : 63;

            v.position[to] = v.position[from];
            v.position[to].square = to;
            v.position[from] = null;
            onMove(from, to); // trigger listener
        } else if (his.flag == Constants.QUEEN_CASTLE) {
            int from = !isWhite ? 3 : 59;
            int to = !isWhite ? 0 : 56;
            
            v.position[to] = v.position[from];
            v.position[to].square = to;
            v.position[from] = null;
            onMove(from, to); // trigger listener
        } else if (his.flag == Constants.PROMOTE_PAWN) {
            onRemove(v.position[his.to]);
            onAdd(v.position[his.to] = new Piece(his.to, isWhite?Constants.BLACK:Constants.WHITE, Constants.PAWN));
        }

        // return piece its previous position.
        v.position[his.from] = v.position[his.to];
        v.position[his.from].square = his.from;
        v.position[his.to] = null;
        onMove(his.to, his.from);

        if (his.capture != null) {
            v.position[his.to] = his.capture;
            onAdd(his.capture);
        }

        // change turn
        v.turn = v.turn == Constants.WHITE ? Constants.BLACK : Constants.WHITE;
        onSwap(getTurn()); // trigger listener

        // remove last board position
        v.fen.pop();

        // if game over, do it back
        if (v.gameStatus != Constants.NO_FLAG) {
            onStatusChange(v.gameStatus = Constants.NO_FLAG);
        }
        
        // check game status
        hasValue = false;
        isGameOver();
        hasValue = true;

        return true;
    }

    /**
     * Redo piece position one step
     * @return
     */
    public boolean redo() {
        if (initial || loader.var.backwardHistory.size() == 0)
            return false;
        
        History his = loader.var.backwardHistory.pop();    
        getMove(his.from);
        movePiece(his.to);
        return true;
    }

    /**
     * Get piece color at specific index (if has)
     * @param index
     * @return
     */
    public char colorAt(String sqaure) {
        return colorAt(Variables.strToNum(sqaure));
    }

    /**
     * Get piece color at specific index (if has)
     * @param index
     * @return
     */
    public char colorAt(int index) {
        if (index < 0 || index > 63 || initial || loader.var.position[index] == null)
            return Constants.NO_FLAG;

        return loader.var.position[index].color;
    }

    /**
     * Get piece name at specific index (if has)
     * @param index
     * @return
     */
    public char pieceAt(String sqaure) {
        return colorAt(Variables.strToNum(sqaure));
    }

    /**
     * Get piece name at specific index (if has)
     * @param index
     * @return
     */
    public char pieceAt(int index) {
        if (index < 0 || index > 63 || initial || loader.var.position[index] == null)
            return Constants.NO_FLAG;

        return loader.var.position[index].name;
    }

    /**
     * Convert numeric index into string code
     * @param index
     * @return
     */
    public static String getSquare(int index) {
        if (index < 0 || index > 63) return "";
        return Variables.getSquare(index);
    }

    /**
     * List in which all index will be stored which are checking king.
     * @return
     */
    public List<Integer> getChecker() {
        List<Integer> in = new ArrayList<>();
        for(int index : pattern.getUnsafeBy())
            in.add(index);
        return in;
    }

    /**
     * {@code onSwap } will be called many times, whenver the turn change.
     * @param color color of piece.
     * @example 'W' | 'B'
     */
    protected void onSwap(char color) {}

    /**
     * Will be called when the game will over
     * @param reason
     */
    protected void onStatusChange(char reason) {}

    /**
     * {@code onMove} will be called whenever the a {@code piece} move from one square another square.
     * @param from index from 0-63, from where a piece is going to be move.
     * @param to index from 0-63, to where a piece is going to be move.
     */
    protected void onMove(int from, int to) {}

    /**
     * {@code onCheck} Will be called whenver the 'King' of one side will be in check.
     * @param piece 'King' square information.
     * @param checkBy List of 'Pieces', who are checking.
     */
    protected void onCheck(Piece pieceBy, Piece kingAt) {}

    /**
     * Whenver a piece will be captured, {@code onCapture} will be called.
     * @param captured Piece which is captured.
     * @param capturedBy Piece which has captured.
     */
    protected void onCapture(Piece captured, Piece capturedBy, boolean epasant) {}

    /**
     * Whenever the pawn will promoted, {@code onPromoted} will be called.
     * @param piece Pawn information.
     * @return Piece For replacing pawn.
     */
    protected Piece onPromoted(Piece piece) { return null; }

    /**
     * Whenever a new piece will going to be add, {@code onAdd} will be called.
     * @param piece Piece information
     */
    protected void onAdd(Piece piece) {}

    /**
     * Whenever a piece will going to be remove, {@code onRemove} will be called.
     * @param piece Piece information
     */
    protected void onRemove(Piece piece) {}

    /**
     * Whenever a load method called {@code onLoad} will be called.
     * @param piece Piece information
     */
    protected void onLoad(boolean initial) {}
}
