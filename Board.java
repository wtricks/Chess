import java.awt.Color;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.event.MouseAdapter;
import javax.swing.border.Border;

import ChessJava.Chess;
import ChessJava.Constants;
import ChessJava.Move;
import ChessJava.Piece;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import java.awt.Font;

public class Board extends JLayeredPane {
    private Chess chess;
    private int BOX_SIZE;
    private int from = 0;
    private int to = 0;
    private int check = -1;
    private Control sidebar;
    private List<Move> lastMove;
    private List<Integer> checker;
    protected boolean capture = false;
    protected char checkerColor = ' ';
    private JPanel[] box = new JPanel[64];

    // BOARD COLORS
    // BELOW ARE SOME COLORS, CHOOSE ONE OF THEM
    private Color WHITE = new Color(112,162,163); 
    private Color BLACK = new Color(117,228,185);

    // private Color WHITE = new Color(112,102,119); 
    // private Color BLACK = new Color(204,183,174); 

    // private Color WHITE = new Color(111,115,210); 
    // private Color BLACK = new Color(157,172,255);

    // private Color WHITE = new Color(187,190,100); 
    // private Color BLACK = new Color(234,240,206);

    // private Color WHITE = new Color(111,143,114); 
    // private Color BLACK = new Color(173,189,143);

    // private Color WHITE = new Color(184,139,74); 
    // private Color BLACK = new Color(227,193,111);
    

    // borders
    private final Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
    private final Border lastStep = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
    private final Border hoverBorder = BorderFactory.createLineBorder(Color.GREEN, 2);
    private final Border yellowBorder = BorderFactory.createLineBorder(Color.YELLOW, 2);
    private final Border stepBorder = BorderFactory.createLineBorder(Color.ORANGE, 2);
   
    Board(int size) {

        setBounds(0, 0, size, size);
        setBackground(Color.CYAN);
        setLayout(null);
        BOX_SIZE = size / 8;

        // SHOW OFF ONLY
        ImageIcon image = new ImageIcon(Board.class.getResource("logo.jpg"));
        JLabel jb = new JLabel(new ImageIcon(image.getImage().getScaledInstance(size, size, 0)));
        jb.setBounds(0, 0, size, size);
        add(jb);

        // for naming
        JLabel name = new JLabel("Pinaka Chess");
        name.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 80));
        name.setForeground(Color.ORANGE);
        name.setBounds(14, 350, size, 80);
        add(name, 2, 0);

        JLabel author = new JLabel("Anuj Kumar");
        author.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
        author.setForeground(Color.WHITE);
        author.setBounds(415, 420, size, 20);
        add(author, 2, 0);

        JLabel link = new JLabel("github.com/wtricks/PinakaChess");
        link.setFont(new Font("Calibri", Font.PLAIN, 14));
        link.setForeground(Color.BLACK);
        link.setBounds(10, size - 25, size, 20);
        add(link, 2, 0);

        boolean isWhite = false;
        for(int i=0; i<8; i++) { // row -> y
            isWhite = !isWhite;
            for(int j=0; j<8; j++) { // col -> x
                createBoard(i, j, isWhite);
                isWhite = !isWhite;
            }
        }

        // Creating main logic
        chess = new Chess() {

            protected void onSwap(char color) {
                sidebar.changeTurn(color == Constants.WHITE ? "White" : "Black");
            }

            protected void onLoad(boolean initial) {
                if (initial) {
                    remove(link);
                    remove(author);
                    remove(name);
                    remove(jb);
                    repaint();
                } else {
                    box[from].setBorder(null);
                    box[to].setBorder(null);
                    if (check != -1) {
                        box[check].setBorder(null);
                        check = -1;
                    }
                    removeBorder(); // remove borders

                    if (checker != null)
                        for(int i : checker) {
                            box[i].setBorder(null);
                        }
                }
                sidebar.changeStatus("Game is started!");
            }
            
            protected void onStatusChange(char reason) {
                String res;
                switch(reason) {
                    case Constants.CHECKMATE:
                        checker = chess.getChecker();

                        for(int i : checker) {
                            box[i].setBorder(yellowBorder);
                        }

                        res = "Checkmate, " + (chess.getTurn()==Constants.WHITE?"Black":"White") + " won.";
                        break;
                    case Constants.STALEMATE:
                        res = "Draw, " + (chess.getTurn()!=Constants.WHITE?"Black":"White") + " has no moves.";   
                        break;

                    case Constants.THREE_FOLD_REPETITION:
                        res = "Draw, By ThreeFoldRepetition.";  
                        break;
                    
                    case Constants.FIFTY_MOVE:
                        res = "Draw, By FiftyMove.";  
                        break;
                   
                    case Constants.IS_MATERIAL:
                        res = "Draw, By InsufficiantMaterial.";  
                        break;    

                    default:
                        if (checker != null) {
                            for(int i : checker) {
                                box[i].setBorder(null);
                            }
                        }
                        res = "running...";
                        break;
                }
                
                sidebar.changeStatus(res);
            }

            protected void onMove(int from, int to) {
                Pieces.images[to] = Pieces.images[from];
                Pieces.images[to].setBounds((to%8)*BOX_SIZE, (7-to/8)*BOX_SIZE, BOX_SIZE, BOX_SIZE);

                if (capture) {
                    return;
                }
                sidebar.changeStatus(
                    (chess.getTurn()!=Constants.WHITE?"Black ":"White ")+ 
                    Pieces.getPieceName(chess.pieceAt(to))+ 
                    " moved from "+ Chess.getSquare(from) + " to " + Chess.getSquare(to)
                );
            }

            protected void onCheck(Piece pieceBy, Piece king) {
                check = pieceBy.square;
                checkerColor = pieceBy.color;
                sidebar.changeStatus("<html>"+
                    (pieceBy.color==Constants.WHITE?"Black ":"White ")+ 
                    Pieces.getPieceName(pieceBy.name) + " from "+ Chess.getSquare(pieceBy.square) +" check " + 
                    (pieceBy.color!=Constants.WHITE?"<br/>Black ":"<br/>White ")+
                    " king at " + Chess.getSquare(king.square)+"</html>"
                );
            }

            protected void onCapture(Piece captured, Piece capturedBy, boolean isEpassant) {
                sidebar.changeStatus("<html>"+
                    (chess.getTurn()!=Constants.WHITE?"Black ":"White ")+
                    Pieces.getPieceName(capturedBy.name)+ " from "+
                    Chess.getSquare(capturedBy.square) + " captured <br/>" + 
                    (chess.getTurn()==Constants.WHITE?"Black ":"White ")+
                    Pieces.getPieceName(captured.name)+ " at " +Chess.getSquare(captured.square)+
                    (isEpassant?" by en-passant rule":"") +"</html>"
                );
                capture = true;
            }

            protected Piece onPromoted(Piece piece) { 
                return null; 
            }

            protected void onAdd(Piece piece) {
                JLabel l = Pieces.getPiece(piece, BOX_SIZE);
                l.setBounds((piece.square%8)*BOX_SIZE, (7-piece.square/8)*BOX_SIZE, BOX_SIZE, BOX_SIZE);
                add(l, 4, 0);
            }

            protected void onRemove(Piece piece) {
                remove(Pieces.images[piece.square]);
                Pieces.images[piece.square] = null;
                repaint();
            }
        };
    }

    public void addSidebar(Control c) {
        sidebar = c;
    }

    public Chess getChessInstance() {
        return chess;
    }

    private void createBoard(int i, int j, boolean isWhite) {
        int x = BOX_SIZE * j;
        int y = BOX_SIZE * (7-i);

        JPanel p = new JPanel();
        p.addMouseListener(new Mouse(i * 8 + j));
        p.setBackground(!isWhite ? BLACK : WHITE);
        p.setLayout(null);
        p.setBounds(x, y, BOX_SIZE, BOX_SIZE);
        box[i * 8 + j] = p;
        
        // add it to the frame
        add(p);

        // for numbering on square
        if (i == 0 || j == 0) {
            if (i == 0) {
                JLabel sqr = new JLabel(""+((char)('A'+j)));
                sqr.setBounds(5, BOX_SIZE-16, 10, 10);
                p.add(sqr);
            }

            if (j == 0) {
                JLabel sqr = new JLabel(""+(i+1));
                sqr.setBounds(5, 5, 10, 10);
                p.add(sqr);
            }
        }
    }

    public void removeBorder() {
        if (lastMove == null) return;
        for(int i=0;i<lastMove.size(); i++) {
            if (lastMove.get(i).to != check)
                box[lastMove.get(i).to].setBorder(null);
            else {
                box[check].setBorder(yellowBorder);
            }    
        }
    }

    class Mouse extends MouseAdapter {
        private boolean has;
        private int index;
        private Border border= null;

        Mouse(int index) {
            this.index = index;
        }

        public void mouseEntered(MouseEvent e) {
            if (has == true) return;
            border = box[index].getBorder();
            box[index].setBorder(hoverBorder);   
            has = true;
        }

        public void mouseExited(MouseEvent e) {
            has = false;
            box[index].setBorder(border);
        }

        public void mouseClicked(MouseEvent e) {
            if ((chess.colorAt(index) == Constants.NO_FLAG || lastMove != null) && chess.movePiece(index)) {
                removeBorder(); // remove step borders
                if (check != index || (capture && chess.colorAt(index) != checkerColor)) {
                    if (check != -1) box[check].setBorder(null);
                    box[to = index].setBorder(border = lastStep);
                    check = -1;
                    checkerColor = ' ';
                } else {
                    box[to = index].setBorder(border = yellowBorder);
                }
                box[from].setBorder(lastStep);
                lastMove = null;
                capture = false;
            } else if (chess.getTurn() == chess.colorAt(index)) {
                box[from].setBorder(null);
                if (check == -1) box[to].setBorder(null);
                removeBorder(); // remove borders

                lastMove = chess.getMove(index);
                if (lastMove == null) return;
                for(int i=0;i<lastMove.size(); i++) {
                    if (lastMove.get(i).capture)
                        box[lastMove.get(i).to].setBorder(redBorder);
                    else box[lastMove.get(i).to].setBorder(stepBorder);    
                }

                border = stepBorder;
                box[from=index].setBorder(stepBorder);
            }
        }
    }
}
