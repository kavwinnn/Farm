/** A slump: too much produce in the valley, so buyers pay only 70%. */
public class SlumpPrices implements PriceStrategy {
    @Override public String getWord() { return "slump"; }
    @Override public String getDescription() { return "Market slump: everything sells for 70%"; }
    @Override public int sellPrice(int basePrice) { return basePrice * 7 / 10; }
}
