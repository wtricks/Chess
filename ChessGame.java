import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Dimension;

public class ChessGame {
    private final int SCREEN_SIZE = 560;
    private final int BOX_SIZE = SCREEN_SIZE / 8;

    public ChessGame() {
        JFrame frame = new JFrame("Pinaka Chess");

        // extra 38 pixel is adding for fitting boxes
        frame.setSize(new Dimension(SCREEN_SIZE + BOX_SIZE * 4, SCREEN_SIZE + 38));
        frame.setIconImage(new ImageIcon(ChessGame.class.getResource("logo.jpg")).getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        Board board = new Board(SCREEN_SIZE);
        Control sidebar = new Control(SCREEN_SIZE, BOX_SIZE * 4, board.getChessInstance());
        board.addSidebar(sidebar);
        frame.add(sidebar);
        frame.add(board);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // rnbqkbnr/ppp1pppp/8/8/4pP2/8/PPPP2PP/RNBQKBNR w KQkq - 0 2
        new ChessGame();
    }
}
