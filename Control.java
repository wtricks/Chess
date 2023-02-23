import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import ChessJava.Chess;
import ChessJava.Constants;

import java.awt.Font;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Control extends JPanel {
    private JTextField text;
    private Chess chess;
    private JLabel status;
    private JLabel turn;
    private Border border = BorderFactory.createDashedBorder(Color.RED, 3, 2);
    
    public void changeTurn(String str) {
        turn.setText(str);
    }

     public void changeStatus(String str) {
        status.setText(str);
    }

    public void AddPiece(int index, int color) {
        //
    }

    public void RemovePiece(int index, int color) {
        //
    }

    Control(int height, int width, Chess chess) {
        this.chess = chess;
        setBounds(height, 0, width, height);
        setLayout(null);

        createText(10,10,width,50,Color.GREEN,new Font("Bauhaus 93", Font.BOLD, 40), "PinakaChess");

        text = new JTextField();
        text.setBounds(6, 70, 250, 30);
        text.setText(Constants.DEFAULT_FEN);
        // text.setText("rnb1kbnr/pppqpppp/8/1B6/5P2/4p2N/PPPP2PP/RNBQK2R w KQkq - 0 4");
        add(text);

        createText(6,100,width-30,10,Color.BLACK,new Font("Arial", Font.ITALIC, 10), "For custom position, Change FEN, Then clik on Reset.");

        createButton("Start", 135);
        createButton("Undo", 185);
        createButton("Redo", 235);
        createButton("Copy piece position", 285);

        createText(10,345,width,15,Color.BLACK,new Font("Calibri", Font.BOLD, 15), "TURN: ");
        turn = createText(60,345,width,13,Color.BLUE,new Font("Calibri", Font.BOLD, 13), "Not started Yet!");
        createText(10,360,width,15,Color.BLACK,new Font("Calibri", Font.BOLD, 15), "STATUS: ");
        status = createText(70,360,width,28,Color.BLUE,new Font("Calibri", Font.BOLD, 13), "Not started Yet!");
    }

    private JLabel createText(int x, int y, int w, int h, Color c, Font font, String str) {
        JLabel status = new JLabel(str);
        status.setBounds(x, y, w, h);
        status.setForeground(c);
        status.setFont(font);
        add(status);
        return status;
    }

    private void createButton(String name, int y) {
        JButton button = new JButton(name);
        button.setBounds(40, y, 180,45);
        button.setFocusable(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.addMouseListener(new Mouse(button));
        add(button);
    }

    class Mouse extends MouseAdapter {
        JButton button;

        Mouse(JButton b) {
            this.button = b;
        }

        public void mouseEntered(MouseEvent e) {
            button.setBorder(border);
        }

        public void mouseExited(MouseEvent e) {
            button.setBorder(null);
        }

        public void mouseClicked(MouseEvent e) {
            switch (button.getText()) {
                case "Start": 
                    chess.load(text.getText());
                    button.setText("Reset");
                    break;
                 
                case "Reset": 
                    chess.load(text.getText());
                    break;  

                case "Redo": 
                    chess.redo();
                    break;    

                case "Undo": 
                    chess.undo();
                    break;   
                    
                default:
                    String s = text.getText();
                    text.setText(chess.fen());
                    text.selectAll();
                    text.copy();
                    text.setText(s);
                    System.out.println("\nPiece Position: \n---------------\n" +chess.print());
            }
        }
    }
}
