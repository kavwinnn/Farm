import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** The Pets shop: chickens, cows (each pen holds 3) and animal feed. */
public class PetShopScreen implements Screen {
    private static final int FEED_PRICE = 10;
    private static final int MAX_FEED = 99;

    private final GamePanel game;
    private final FarmScreen farm;
    private final Animal[] animals = new Animal[AnimalFactory.NAMES.length];   // one sample of each, to read prices from
    private final int rows = AnimalFactory.NAMES.length + 1;                   // every animal, then the feed
    private int row = 0;
    private int quantity = 1;
    private String status = "";

    public PetShopScreen(GamePanel game, FarmScreen farm) {
        this.game = game;
        this.farm = farm;
        for (int i = 0; i < animals.length; i++) animals[i] = AnimalFactory.create(AnimalFactory.NAMES[i], 0, 0);
    }

    private boolean isFeedRow() { return row == animals.length; }

    /** Animals: no more than the pen has room for (but at least 1, so a full pen gives an error). Feed: 99. */
    private int maxQuantity() {
        if (isFeedRow()) return MAX_FEED;
        Pen pen = farm.getFarm().penFor(animals[row].getKind());
        return Math.max(1, Pen.MAX - pen.size());
    }

    private int price() {
        return isFeedRow() ? FEED_PRICE : animals[row].getBuyPrice();
    }

    @Override
    public void keyPressed(int code) {
        switch (code) {
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: row = (row - 1 + rows) % rows; break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: row = (row + 1) % rows; break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: quantity = Math.max(1, quantity - 1); break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: quantity = quantity + 1; break;
            case KeyEvent.VK_ENTER: case KeyEvent.VK_SPACE: buy(); break;
            case KeyEvent.VK_ESCAPE: game.setScreen(farm); break;
            default: break;
        }
        quantity = Math.min(quantity, maxQuantity());
    }

    private void buy() {
        int total = price() * quantity;
        Farmer farmer = farm.getFarmer();
        try {
            if (isFeedRow()) {
                farmer.spendGold(total);
                farmer.addFeed(quantity);
                status = "Bought " + quantity + " animal feed. You have " + farmer.getFeed() + ".";
            } else {
                String kind = animals[row].getKind();
                Pen pen = farm.getFarm().penFor(kind);
                // check the pen first, so the gold is never taken for an animal that has nowhere to go
                if (pen.size() + quantity > Pen.MAX) throw new PenFullException(kind, pen.size(), Pen.MAX);
                farmer.spendGold(total);
                for (int i = 0; i < quantity; i++) pen.buy();
                status = "Bought " + quantity + " " + kind + ". Find " + (quantity == 1 ? "it" : "them") + " in the pen.";
            }
            farm.floatAtFacing("-" + total + "G", new Color(255, 140, 120));
        } catch (FarmException e) {
            status = e.getMessage();
        }
        quantity = Math.min(quantity, maxQuantity());
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 150);
        int left = 40, top = 50, width = Ui.WIDTH - 80;
        Ui.paper(g, left, top, width, 400);
        Ui.centered(g, "PET SHOP", 24, top + 38, Ui.WOOD);
        g.setFont(Ui.font(14));
        g.setColor(new Color(170, 120, 20));
        String gold = "Your gold " + farm.getFarmer().getGold() + "G";
        g.drawString(gold, left + width - 20 - g.getFontMetrics().stringWidth(gold), top + 38);

        g.setFont(Ui.font(13));
        g.setColor(Ui.WOOD);
        g.drawString(String.format("%-13s %6s   %s", "Item", "Price", "You have"), left + 30, top + 80);
        for (int i = 0; i < rows; i++) {
            int y = top + 110 + i * 30;
            if (i == row) {
                g.setColor(Ui.HIGHLIGHT);
                g.fillRoundRect(left + 18, y - 19, width - 36, 27, 8, 8);
            }
            g.setColor(Ui.INK);
            String line;
            if (i < animals.length) {
                Animal animal = animals[i];
                int owned = farm.getFarm().penFor(animal.getKind()).size();
                line = String.format("%-13s %5dG   owned %d/%d", animal.getKind(), animal.getBuyPrice(), owned, Pen.MAX);
            } else {
                line = String.format("%-13s %5dG   x%d", "Animal Feed", FEED_PRICE, farm.getFarmer().getFeed());
            }
            g.drawString(line, left + 30, y);
        }

        g.setColor(Ui.WOOD);
        String note = isFeedRow() ? "Animal Feed: 1 feed = 1 meal for one animal."
                : animals[row].getKind() + ": gives " + animals[row].getProductName() + " (" + Market.sellPriceOf(animals[row].getProductName())
                  + "G) after " + animals[row].getDaysPerProduct() + " fed days.";
        g.drawString(note, left + 30, top + 220);
        Ui.centered(g, "Quantity  < " + quantity + " >    Total " + price() * quantity + "G", 17, top + 268, Ui.INK);
        Ui.centered(g, status, 13, top + 305, status.startsWith("Bought") ? new Color(40, 130, 50) : Ui.WARNING);
        Ui.centered(g, "Feed is shared by chickens and cows. 1 feed = 1 meal", 12, top + 345, Ui.WOOD);
        Ui.centered(g, "UP/DOWN item   LEFT/RIGHT amount   ENTER buy   ESC leave", 12, top + 380, Ui.WOOD);
    }
}
