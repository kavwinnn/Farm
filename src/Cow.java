import java.awt.Color;
import java.awt.Graphics2D;

public class Cow extends Animal {
    public Cow(int col, int row) {
        super(col, row);
    }

    @Override public String getKind() { return "Cow"; }
    @Override public int getBuyPrice() { return 600; }
    @Override public String getProductName() { return "Milk"; }
    @Override public int getDaysPerProduct() { return 4; }
    @Override public int getProductPrice() { return 180; }

    /** A white cow with brown patches, slowly swishing its tail. */
    @Override
    protected void drawBody(Graphics2D g, int x, int y, int frames) {
        int swish = (frames / 30) % 2 == 0 ? 0 : 3;
        Color brown = new Color(140, 85, 45);
        g.setColor(brown);                              // tail
        g.drawLine(x + 3, y + 12, x + 1, y + 21 - swish);
        g.fillOval(x, y + 20 - swish, 3, 4);
        g.setColor(new Color(80, 50, 35));              // legs
        g.fillRect(x + 6, y + 21, 3, 7);
        g.fillRect(x + 11, y + 21, 3, 7);
        g.fillRect(x + 18, y + 21, 3, 7);
        g.fillRect(x + 23, y + 21, 3, 7);
        g.setColor(new Color(250, 248, 240));           // body
        g.fillRoundRect(x + 3, y + 9, 25, 14, 8, 8);
        g.setColor(brown);                              // patches
        g.fillOval(x + 7, y + 10, 7, 6);
        g.fillOval(x + 15, y + 15, 6, 5);
        g.setColor(new Color(250, 248, 240));           // head
        g.fillRoundRect(x + 22, y + 4, 9, 11, 5, 5);
        g.setColor(brown);
        g.fillOval(x + 22, y + 4, 5, 5);
        g.setColor(new Color(240, 230, 200));           // short horns
        g.fillRect(x + 23, y + 1, 2, 4);
        g.fillRect(x + 28, y + 1, 2, 4);
        g.setColor(new Color(245, 170, 175));           // pink nose
        g.fillRoundRect(x + 23, y + 11, 9, 5, 3, 3);
        g.setColor(new Color(30, 25, 25));              // eye
        g.fillRect(x + 28, y + 7, 2, 2);
    }

    @Override
    protected void drawProductIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(250, 250, 255));           // bottle
        g.fillRoundRect(x, y + 3, 8, 9, 3, 3);
        g.fillRect(x + 2, y, 4, 4);
        g.setColor(new Color(80, 140, 220));            // cap and label
        g.fillRect(x + 2, y, 4, 2);
        g.fillRect(x, y + 6, 8, 3);
        g.setColor(new Color(140, 140, 160));
        g.drawRoundRect(x, y + 3, 8, 9, 3, 3);
    }
}
