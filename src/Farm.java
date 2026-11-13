import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Farm {
    public static final int TILE = 32;   // pixels
    public static final int COLS = 20;
    public static final int ROWS = 15;

    // . grass   , tilled soil   # fence   ~ pond   H house   S shop   B shipping bin   V where the villager stands
    // P pet shop   f pen fence   g pen gate   c chicken pen floor   w cow pen floor
    private static final String[] MAP = {
        "####################",
        "#HHHH.....PPPPSSSS.#",
        "#HHHH.....PPPPSSSS.#",
        "#HHHH.....PPPPSSSS.#",
        "#..................#",
        "#..................#",
        "#...,,,,,..V.......#",
        "#...,,,,,..........#",
        "#..................#",
        "#..................#",
        "#..fgffffffgff~~~..#",
        "#.Bfccccfwwwwf~~~~.#",
        "#..fccccfwwwwf~~~..#",
        "#..fccccfwwwwf.....#",
        "####################",
    };

    private static final Color GRASS = new Color(110, 190, 75);
    private static final Color GRASS_DARK = new Color(80, 155, 55);
    private static final Color SOIL = new Color(150, 100, 62);
    private static final Color SOIL_WET = new Color(95, 62, 40);
    private static final Color WOOD = new Color(150, 105, 65);

    private final Tile[][] tiles = new Tile[ROWS][COLS];
    private Villager villager;

    public Farm() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                char symbol = MAP[r].charAt(c);
                if (symbol == 'V') {
                    villager = new Villager("Mrs. Daeng", c, r);
                    symbol = '.';   // she stands on grass
                }
                tiles[r][c] = new Tile(TileType.fromSymbol(symbol));
            }
        }
    }

    public Villager getVillager() { return villager; }

    /** @return the villager standing on (col, row), or null */
    public Villager villagerAt(int col, int row) {
        return villager != null && villager.getCol() == col && villager.getRow() == row ? villager : null;
    }

    /** @return the tile, or null if (col, row) is off the map */
    public Tile tileAt(int col, int row) {
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return null;
        return tiles[row][col];
    }

    public boolean isWalkable(int col, int row) {
        Tile tile = tileAt(col, row);
        return tile != null && tile.getType().isWalkable() && villagerAt(col, row) == null;
    }

    /** Growing crops that still need water today. */
    public int countThirstyCrops() {
        int count = 0;
        for (Tile[] line : tiles) {
            for (Tile tile : line) {
                if (tile.hasCrop() && !tile.isWatered() && !tile.getCrop().isMature()) count++;
            }
        }
        return count;
    }

    /** Crops grow overnight, then the new day's weather may water everything. */
    public void newDay(Weather weather) {
        for (Tile[] line : tiles) {
            for (Tile tile : line) {
                tile.newDay();
                if (weather.watersCrops()) tile.soak();
            }
        }
    }

    // ---- drawing --------------------------------------------------------------

    public void draw(Graphics2D g) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) drawTile(g, tiles[r][c], c, r);
        }
        drawHouse(g, boundsOf(TileType.HOUSE));
        drawShop(g, boundsOf(TileType.SHOP));
        drawPetShop(g, boundsOf(TileType.PET_SHOP));
        drawBin(g, boundsOf(TileType.BIN));
    }

    private void drawTile(Graphics2D g, Tile tile, int col, int row) {
        int x = col * TILE, y = row * TILE;
        int seed = col * 7 + row * 13;   // so each grass tile looks a little different, but never flickers
        switch (tile.getType()) {
            case SOIL:
                g.setColor(tile.isWatered() ? SOIL_WET : SOIL);
                g.fillRect(x, y, TILE, TILE);
                g.setColor(tile.isWatered() ? SOIL_WET.darker() : SOIL.darker());
                for (int i = 6; i < TILE; i += 9) g.drawLine(x + 3, y + i, x + TILE - 4, y + i);
                if (tile.hasCrop()) {
                    tile.getCrop().draw(g, x, y);
                    if (!tile.isWatered() && !tile.getCrop().isMature()) drawDroplet(g, x + 23, y + 2);
                }
                break;
            case POND:
                g.setColor(new Color(70, 140, 220));
                g.fillRect(x, y, TILE, TILE);
                g.setColor(new Color(150, 200, 250));
                g.drawArc(x + 6, y + 8 + seed % 10, 12, 6, 0, 180);
                break;
            case FENCE:
                drawGrass(g, x, y, seed);
                g.setColor(WOOD);
                if (col == 0 || col == COLS - 1) {
                    g.fillRect(x + 10, y, 3, TILE);
                    g.fillRect(x + 19, y, 3, TILE);
                    g.fillRect(x + 8, y + 12, 16, 8);
                } else {
                    g.fillRect(x, y + 10, TILE, 3);
                    g.fillRect(x, y + 19, TILE, 3);
                    g.fillRect(x + 12, y + 6, 8, 20);
                }
                break;
            case PEN_FENCE:   // a low wooden pen fence, thinner than the outer one
                drawGrass(g, x, y, seed);
                g.setColor(WOOD);
                if (tileAt(col, row - 1).getType() != TileType.PEN_FENCE) {   // top rail of the pen
                    g.fillRect(x, y + 14, TILE, 2);
                    g.fillRect(x, y + 21, TILE, 2);
                    g.fillRect(x + 14, y + 10, 4, 16);
                } else {
                    g.fillRect(x + 14, y, 4, TILE);
                    g.fillRect(x + 11, y + 8, 10, 2);
                    g.fillRect(x + 11, y + 20, 10, 2);
                }
                break;
            case GATE:   // open gate: grass with a wooden post on each side
                drawGrass(g, x, y, seed);
                g.setColor(WOOD);
                g.fillRect(x, y + 10, 4, 16);
                g.fillRect(x + TILE - 4, y + 10, 4, 16);
                break;
            case CHICKEN_PEN:   // straw
                g.setColor(new Color(235, 215, 140));
                g.fillRect(x, y, TILE, TILE);
                g.setColor(new Color(215, 190, 110));
                g.drawLine(x + 4 + seed % 9, y + 8, x + 10 + seed % 9, y + 6);
                g.drawLine(x + 14 + seed % 7, y + 22, x + 20 + seed % 7, y + 24);
                break;
            case COW_PEN:   // grassy dirt
                g.setColor(new Color(150, 170, 90));
                g.fillRect(x, y, TILE, TILE);
                g.setColor(new Color(125, 145, 70));
                g.drawLine(x + 5 + seed % 11, y + 12, x + 7 + seed % 11, y + 8);
                g.drawLine(x + 18 + seed % 8, y + 25, x + 20 + seed % 8, y + 21);
                break;
            default:   // grass, and the ground under buildings
                drawGrass(g, x, y, seed);
        }
    }

    private void drawGrass(Graphics2D g, int x, int y, int seed) {
        g.setColor(GRASS);
        g.fillRect(x, y, TILE, TILE);
        g.setColor(GRASS_DARK);
        int tx = x + 5 + seed * 5 % 20, ty = y + 9 + seed * 3 % 16;
        g.drawLine(tx, ty, tx - 2, ty - 5);
        g.drawLine(tx + 3, ty, tx + 4, ty - 6);
    }

    /** The little "needs water" sign on a dry plot. */
    private void drawDroplet(Graphics2D g, int x, int y) {
        g.setColor(new Color(120, 190, 255));
        g.fillPolygon(new int[] {x + 3, x + 6, x}, new int[] {y, y + 5, y + 5}, 3);
        g.fillOval(x, y + 3, 7, 7);
        g.setColor(new Color(30, 80, 160));
        g.drawOval(x, y + 3, 7, 7);
    }

    /** The pixel rectangle covered by every tile of one type, e.g. the whole house. */
    private Rectangle boundsOf(TileType type) {
        int minC = COLS, minR = ROWS, maxC = -1, maxR = -1;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (tiles[r][c].getType() != type) continue;
                minC = Math.min(minC, c);
                minR = Math.min(minR, r);
                maxC = Math.max(maxC, c);
                maxR = Math.max(maxR, r);
            }
        }
        return new Rectangle(minC * TILE, minR * TILE, (maxC - minC + 1) * TILE, (maxR - minR + 1) * TILE);
    }

    private void drawHouse(Graphics2D g, Rectangle b) {
        int wallTop = b.y + b.height / 3;
        g.setColor(new Color(240, 225, 190));
        g.fillRect(b.x + 4, wallTop, b.width - 8, b.y + b.height - wallTop);
        g.setColor(new Color(185, 65, 50));
        g.fillPolygon(new int[] {b.x - 2, b.x + b.width / 2, b.x + b.width + 2},
                      new int[] {wallTop + 8, b.y + 2, wallTop + 8}, 3);
        g.setColor(new Color(120, 80, 45));             // door, above the tile the farmer wakes on
        g.fillRect(b.x + TILE + 6, b.y + b.height - 30, 20, 30);
        g.setColor(new Color(150, 200, 240));
        g.fillRect(b.x + b.width - 46, wallTop + 16, 24, 18);
        g.setColor(WOOD);
        g.drawRect(b.x + b.width - 46, wallTop + 16, 24, 18);
    }

    private void drawShop(Graphics2D g, Rectangle b) {
        g.setColor(WOOD);
        g.fillRect(b.x + 6, b.y + 30, 4, b.height - 30);
        g.fillRect(b.x + b.width - 10, b.y + 30, 4, b.height - 30);
        g.fillRect(b.x + 2, b.y + b.height / 2 + 6, b.width - 4, b.height / 2 - 8);
        for (int i = 0; i < 8; i++) {   // striped awning
            g.setColor(i % 2 == 0 ? new Color(220, 60, 60) : new Color(250, 245, 235));
            g.fillRect(b.x + i * b.width / 8, b.y + 6, b.width / 8, 24);
        }
        g.setColor(new Color(250, 240, 200));
        g.fillRect(b.x + b.width / 2 - 32, b.y + b.height / 2 + 14, 64, 20);
        g.setColor(new Color(60, 40, 30));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.drawString("SEEDS", b.x + b.width / 2 - 21, b.y + b.height / 2 + 29);
    }

    /** Same look as the seed shop, with a green-and-white awning and a "PETS" sign. */
    private void drawPetShop(Graphics2D g, Rectangle b) {
        g.setColor(WOOD);
        g.fillRect(b.x + 6, b.y + 30, 4, b.height - 30);
        g.fillRect(b.x + b.width - 10, b.y + 30, 4, b.height - 30);
        g.fillRect(b.x + 2, b.y + b.height / 2 + 6, b.width - 4, b.height / 2 - 8);
        for (int i = 0; i < 8; i++) {   // striped awning
            g.setColor(i % 2 == 0 ? new Color(60, 160, 80) : new Color(250, 245, 235));
            g.fillRect(b.x + i * b.width / 8, b.y + 6, b.width / 8, 24);
        }
        g.setColor(new Color(250, 240, 200));
        g.fillRect(b.x + b.width / 2 - 32, b.y + b.height / 2 + 14, 64, 20);
        g.setColor(new Color(60, 40, 30));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.drawString("PETS", b.x + b.width / 2 - 17, b.y + b.height / 2 + 29);
    }

    private void drawBin(Graphics2D g, Rectangle b) {
        g.setColor(WOOD);
        g.fillRect(b.x + 3, b.y + 9, 26, 21);
        g.setColor(WOOD.darker());
        g.fillRect(b.x + 1, b.y + 4, 30, 7);
        g.setColor(new Color(250, 240, 200));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 9));
        g.drawString("SHIP", b.x + 6, b.y + 24);
    }
}
