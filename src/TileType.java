public enum TileType {
    GRASS('.', true),
    SOIL(',', true),
    FENCE('#', false),
    POND('~', false),
    HOUSE('H', false),
    SHOP('S', false),
    BIN('B', false),
    PET_SHOP('P', false),
    PEN_FENCE('f', false),
    GATE('g', true),
    CHICKEN_PEN('c', true),
    COW_PEN('w', true);

    private final char symbol;      // how the tile is written in Farm's map
    private final boolean walkable;

    TileType(char symbol, boolean walkable) {
        this.symbol = symbol;
        this.walkable = walkable;
    }

    public boolean isWalkable() { return walkable; }

    public static TileType fromSymbol(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) return type;
        }
        throw new IllegalArgumentException("no tile type for '" + symbol + "'");
    }
}
