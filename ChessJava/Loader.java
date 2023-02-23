package ChessJava;


class Loader {
    /**
     * Hold reference for {@code Variables} object.
     */
    public Variables var;

    public String load(String fen) {
        String[] parts = fen.split("\\s+");
        String[] pieces;
        byte index, col, row;
        char curr, color;

        if (parts.length != 6) {
            return "Invalid Fen String given of length " + parts.length;
        }

        var = new Variables();

        try {
            pieces = parts[0].split("/");
            
            // each row must have 8 columns positions.
            if (pieces.length != 8) throw new Error("Invalid Fen String given!");
            
            index = 0;
            
            // check each row
            for(row = 0; row < pieces.length; row++) {
                if (index != 8 && index != 0) {
                    throw new Error(row + "th row must have 8 squares.");
                }

                index = 0;
                for(col = 0; col < pieces[row].length(); col++) {
                    curr = pieces[row].charAt(col);
                    if (Character.isDigit(curr)) {
                        index += curr - '0';
                        continue;
                    } else index++;

                    // each row has only 8 columns
                    if (index > 8) {
                        throw new Error("Maximum 8 pieces are allowed in " + (row+1) + "th row.");
                    }

                    // 0 -> white, 1 -> black, -1 -> invalid character
                    if ((color = ("kqbnrp".indexOf(curr) != -1) ? 'B' : (("KQBNRP".indexOf(curr) != -1) ? 'W' : '$')) == '$') {
                        throw new Error("Invalid piece character '"+ curr +"' given in " + (row+1) +"th row, " + (col+1) + "th column.");
                    }
                    
                    // we will use uppercase charater.
                    curr = Character.toUpperCase(curr);

                    // storing piece and the piece color
                    var.position[(7-row)*8+index-1] = new Piece((7-row)*8+index-1, color, curr);

                    if (curr == 'K') {
                        if (var.kingsPosition[color=='W'?0:1] != null)
                            throw new Error("Only one king is allowed both side.");
                        
                        var.kingsPosition[color=='W'?0:1] = var.position[(7-row)*8+index-1];
                    }  
                }
            }

            if (var.kingsPosition[0] == null || var.kingsPosition[1] == null) {
                throw new Error("Both side must have one king.");
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        // this is for fifty move
        var.countMove[0] = Integer.valueOf(parts[4]);
        var.countMove[1] = Integer.valueOf(parts[5]);

        // set turn 
        var.turn = parts[1].equals("w") ? Constants.WHITE : Constants.BLACK;
        var.epasant = (parts[3].length() != 2) ? -1 : Variables.strToNum(parts[3].toUpperCase());

        if (parts[2].indexOf("K") != -1) var.castles[0]++;
        if (parts[2].indexOf("Q") != -1) var.castles[0]+=2;
        if (parts[2].indexOf("k") != -1) var.castles[1]++;
        if (parts[2].indexOf("q") != -1) var.castles[1]+=2;
        return "";
    }

    /**
     * Print complete board and piece position using String
     * @return
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        Piece piece;
        String temp;

        for(byte i = 7; i >= 0; i--) {
            temp = (i + 1) + " |";

            for(byte j = 0; j < 8; j++) {
                piece = var.position[i * 8 + j];
                if (piece != null) {
                    temp += " " + (
                        piece.color == Constants.BLACK ? 
                            Character.toLowerCase(piece.name) 
                            : piece.name
                        ) + " ";
                } else temp += " - ";
            }

            sb.append(temp+"\n");
        }

        sb.append("- - -  -  -  -  -  -  -  -\n");
        sb.append("  | a  b  c  d  e  f  g  h");

        return sb.toString();
    }
    
    public String fen() {
        String fen = helperOfFen();
        String str = "";
        System.out.println(var.castles[0] + " " + var.castles[1]);
        if (var.castles[0] == 1 || var.castles[0] == 3) str += Character.toUpperCase(Constants.KING);
        if (var.castles[0] >= 2) str += Character.toUpperCase(Constants.QUEEN);
        if (var.castles[1] == 1 || var.castles[1] == 3) str += Character.toLowerCase(Constants.KING);
        if (var.castles[1] >= 2) str += Character.toLowerCase(Constants.QUEEN);

        return fen + " " + Character.toLowerCase(var.turn) + " " 
            + (str.equals("")?"-":str) + " " + (var.epasant == -1 ? "-" 
            : Variables.getSquare(var.epasant).toLowerCase()) + " " + var.countMove[0] 
            + " " + var.countMove[1];
    }

    /**
     * Create fen which will denote the pieces position.
     * @return fen string
     */
    public String helperOfFen() {
        StringBuffer sb = new StringBuffer();
        byte row, col, count = 0;
        Piece piece;
        
        // we have 8 rows and 8 columns
        for(row = 7; row >= 0; row--) {
            for(col = 0; col < 8; col++) {
                // get the piece at perticular index.
                piece = var.position[row*8+col];

                // if piece is not found
                if (piece == null) {
                    count++;
                    continue;
                }

                // if we found some empty squares
                if (count != 0) {
                    sb.append(count);
                    count = 0;
                }

                sb.append(piece.color == Constants.BLACK ? Character.toLowerCase(piece.name) : piece.name);
            }

            // if we found some empty square.
            if (count != 0) {
                sb.append(count);
                count = 0;
            }

            // we don't need to add '/' after last column
            if (row != 0) sb.append("/");
        }

        return sb.toString();
    }
}
