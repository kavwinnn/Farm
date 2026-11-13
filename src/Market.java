/** What anything in the basket sells for: animal products (Egg, Milk) or crops. */
public class Market {
    private Market() {}   // only static helpers here

    public static int sellPriceOf(String name) {
        int price = AnimalFactory.productPrice(name);
        if (price >= 0) return price;
        return CropFactory.create(name).getSellPrice();
    }
}
