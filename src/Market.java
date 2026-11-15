/** What anything in the basket sells for: animal products (Egg, Milk) or crops, adjusted by the price strategy. */
public class Market {
    private static PriceStrategy strategy = new NormalPrices();

    private Market() {}   // only static helpers here

    public static PriceStrategy getStrategy() { return strategy; }

    public static void setStrategy(PriceStrategy newStrategy) {
        if (newStrategy == null) throw new IllegalArgumentException("price strategy can't be null");
        strategy = newStrategy;
    }

    public static int sellPriceOf(String name) {
        int price = AnimalFactory.productPrice(name);
        if (price < 0) price = CropFactory.create(name).getSellPrice();
        return strategy.sellPrice(price);
    }
}
