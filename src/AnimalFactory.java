/** The one place that turns an animal's name into an animal object (Factory pattern). */
public class AnimalFactory {
    public static final String[] NAMES = {"Chicken", "Cow"};

    public static Animal create(String kind, int col, int row) {
        switch (kind) {
            case "Chicken": return new Chicken(col, row);
            case "Cow": return new Cow(col, row);
            default: throw new IllegalArgumentException("unknown animal: " + kind);
        }
    }

    /** @return what an animal product (Egg, Milk) sells for, or -1 if the name is not an animal product */
    public static int productPrice(String productName) {
        for (String kind : NAMES) {
            Animal sample = create(kind, 0, 0);
            if (sample.getProductName().equals(productName)) return sample.getProductPrice();
        }
        return -1;
    }
}
