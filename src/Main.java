import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    /** java -cp out Main [normal|festival|slump] picks how the market prices things (1.3). */
    public static void main(String[] args) {
        PriceStrategy prices = new NormalPrices();
        if (args.length > 0) {
            try {
                prices = PriceStrategyFactory.create(args[0]);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " - use one of " + String.join(", ", PriceStrategyFactory.WORDS)
                        + ". Playing with normal prices.");
            }
        }
        Market.setStrategy(prices);
        System.out.println(prices.getDescription());
        String title = "Little Farm - CN201" + (prices instanceof NormalPrices ? "" : " (" + prices.getDescription() + ")");

        // Swing is single-threaded: build the window on its event thread
        SwingUtilities.invokeLater(() -> {
            GamePanel panel = new GamePanel();
            JFrame frame = new JFrame(title);
            frame.add(panel);
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            panel.requestFocusInWindow();
            panel.start();
        });
    }
}
