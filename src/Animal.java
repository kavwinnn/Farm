import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Every farm animal is cared for the same way; each subclass says what it costs, what it gives,
 * how often, and what it looks like. Love and the fed-day count are kept per animal.
 */
public abstract class Animal {
    public static final int MAX_LOVE = 5;

    private int col, row;
    private int love = 0;
    private int fedDays = 0;          // fed days counted towards the next product
    private boolean fedToday = false;
    private boolean productReady = false;

    protected Animal(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public abstract String getKind();
    public abstract int getBuyPrice();
    public abstract String getProductName();
    public abstract int getDaysPerProduct();
    public abstract int getProductPrice();

    /** Draws the animal itself; (x, y) is the top-left corner of its tile. */
    protected abstract void drawBody(Graphics2D g, int x, int y, int frames);

    /** Draws the small "ready to collect" icon (an egg, a bottle of milk...) with its top-left at (x, y). */
    protected abstract void drawProductIcon(Graphics2D g, int x, int y);

    public void feed() throws FarmException {
        if (fedToday) throw new FarmException("Already fed today");
        fedToday = true;
    }

    /** @return the name of what was collected */
    public String collect() throws FarmException {
        if (!productReady) throw new FarmException("Nothing to collect yet");
        productReady = false;
        return getProductName();
    }

    /**
     * Overnight: a fed animal gains love and counts one more fed day (only while nothing is waiting
     * to be collected); a hungry one loses love. Then tomorrow it is hungry again.
     * @return news for the end-of-day summary, e.g. "was hungry (love 2 -> 1)", or null
     */
    public String newDay() {
        String news = null;
        if (fedToday) {
            love = Math.min(MAX_LOVE, love + 1);
            if (!productReady) {
                fedDays++;
                if (fedDays >= getDaysPerProduct()) {
                    productReady = true;
                    fedDays = 0;
                    news = "has " + getProductName().toLowerCase() + " ready!";
                }
            }
        } else {
            int before = love;
            love = Math.max(0, love - 1);
            news = "was hungry (love " + before + " -> " + love + ")";
        }
        fedToday = false;
        return news;
    }

    /** Puts back the state read from a save file. */
    public void restore(int love, int fedDays, boolean ready) {
        if (love < 0 || love > MAX_LOVE || fedDays < 0 || fedDays >= getDaysPerProduct()) {
            throw new IllegalArgumentException("bad " + getKind() + " state: love " + love + ", fed days " + fedDays);
        }
        this.love = love;
        this.fedDays = fedDays;
        this.productReady = ready;
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
    public int getLove() { return love; }
    public int getFedDays() { return fedDays; }
    public boolean isFedToday() { return fedToday; }
    public boolean isProductReady() { return productReady; }

    public void draw(Graphics2D g, int frames, boolean showHearts) {
        int x = col * Farm.TILE, y = row * Farm.TILE;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 5, y + 25, 22, 6);
        drawBody(g, x, y, frames);
        if (!fedToday) drawBowl(g, x + 22, y + 22);
        if (productReady) drawProductIcon(g, x + 22, y - 6);
        if (showHearts) drawHearts(g, x + 1, y - 12);
    }

    /** The "hasn't eaten today" sign: a small wooden bowl. */
    private void drawBowl(Graphics2D g, int x, int y) {
        g.setColor(new Color(130, 85, 50));
        g.fillArc(x, y - 2, 10, 10, 180, 180);
        g.setColor(new Color(90, 55, 30));
        g.drawArc(x, y - 2, 10, 10, 180, 180);
        g.drawLine(x, y + 3, x + 10, y + 3);
    }

    private void drawHearts(Graphics2D g, int x, int y) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(x - 2, y - 2, 34, 10, 6, 6);
        for (int i = 0; i < MAX_LOVE; i++) {
            int hx = x + i * 6, hy = y;
            g.setColor(i < love ? new Color(235, 70, 90) : new Color(120, 110, 110));
            g.fillOval(hx, hy, 3, 3);
            g.fillOval(hx + 2, hy, 3, 3);
            g.fillPolygon(new int[] {hx, hx + 5, hx + 2}, new int[] {hy + 2, hy + 2, hy + 5}, 3);
        }
    }
}
