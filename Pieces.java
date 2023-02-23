import javax.swing.JLabel;

import ChessJava.Constants;
import ChessJava.Piece;

import javax.swing.ImageIcon;
import java.awt.Dimension;

public class Pieces {
    private static final String imageFolder = "./pieces/";
    public static JLabel[] images = new JLabel[64];

    public static String getPieceName(char c) {
        switch(c) {
            case 'K': return "King";
            case 'Q': return "Queen";
            case 'B': return "Bishop";
            case 'N': return "Knight";
            case 'R': return "Rook";
            default: return "Pawn";
        }
    }

    public static JLabel getPiece(Piece piece, int size) {
        JLabel l = images[piece.square];
        if (l == null) l = images[piece.square] = new JLabel();

        if (l.getSize().getWidth() != size) {
            ImageIcon img = new ImageIcon(Pieces.class.getResource(imageFolder + ((piece.color==Constants.WHITE)?"w_":"b_")+ 
                        getPieceName(piece.name).toLowerCase() + ".png"));

            l.setSize(new Dimension(size, size));
            l.setIcon(new ImageIcon(img.getImage().getScaledInstance(size, size, 0)));
        }

        return l;
    }

    public static void AddPiece(int piece, int color) {
        //
    }
}