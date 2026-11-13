import java.awt.Color;
import java.awt.Graphics2D;

public class Chicken extends Animal {
    public Chicken(int col, int row) {
        super(col, row);
    }

    @Override public String getKind() { return "Chicken"; }
    @Override public int getBuyPrice() { return 200; }
    @Override public String getProductName() { return "Egg"; }
    @Override public int getDaysPerProduct() { return 2; }
    @Override public int getProductPrice() { return 50; }

    /** A round white hen that pecks at the ground now and then. */
    @Override
    protected void drawBody(Graphics2D g, int x, int y, int frames) {
        int peck = (frames / 20) % 6 == 0 ? 3 : 0;   // head dips for a moment
        g.setColor(new Color(240, 140, 40));            // legs
        g.fillRect(x + 12, y + 22, 2, 5);
        g.fillRect(x + 18, y + 22, 2, 5);
        g.fillRect(x + 10, y + 26, 5, 1);
        g.fillRect(x + 17, y + 26, 5, 1);
        g.setColor(new Color(250, 248, 240));           // body and tail
        g.fillOval(x + 6, y + 10, 20, 15);
        g.fillPolygon(new int[] {x + 6, x + 3, x + 10}, new int[] {y + 16, y + 8, y + 12}, 3);
        g.setColor(new Color(225, 220, 210));           // wing
        g.fillOval(x + 9, y + 15, 10, 6);
        g.setColor(new Color(250, 248, 240));           // head
        g.fillOval(x + 17, y + 4 + peck, 10, 10);
        g.setColor(new Color(220, 40, 40));             // comb and wattle
        g.fillOval(x + 19, y + 1 + peck, 4, 5);
        g.fillOval(x + 22, y + 2 + peck, 3, 4);
        g.fillOval(x + 25, y + 11 + peck, 2, 3);
        g.setColor(new Color(245, 190, 40));            // beak
        g.fillPolygon(new int[] {x + 26, x + 30, x + 26}, new int[] {y + 7 + peck, y + 9 + peck, y + 11 + peck}, 3);
        g.setColor(new Color(30, 25, 25));              // eye
        g.fillRect(x + 22, y + 7 + peck, 2, 2);
    }

    @Override
    protected void drawProductIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(250, 245, 225));
        g.fillOval(x, y, 8, 10);
        g.setColor(new Color(170, 150, 120));
        g.drawOval(x, y, 8, 10);
    }
}
