/** How the market turns an item's normal price into today's selling price (Strategy pattern). */
public interface PriceStrategy {
    /** The word that picks this strategy on the command line, e.g. "festival". */
    String getWord();

    /** One line for the player, e.g. "Festival: everything sells for 150%". */
    String getDescription();

    int sellPrice(int basePrice);
}
