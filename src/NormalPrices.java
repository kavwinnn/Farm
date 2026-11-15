/** Prices as written in each crop and animal class. */
public class NormalPrices implements PriceStrategy {
    @Override public String getWord() { return "normal"; }
    @Override public String getDescription() { return "Normal market: everything sells at its usual price"; }
    @Override public int sellPrice(int basePrice) { return basePrice; }
}
