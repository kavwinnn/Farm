/** Festival month: visitors pay half as much again for everything. */
public class FestivalPrices implements PriceStrategy {
    @Override public String getWord() { return "festival"; }
    @Override public String getDescription() { return "Festival: everything sells for 150%"; }
    @Override public int sellPrice(int basePrice) { return basePrice * 3 / 2; }
}
