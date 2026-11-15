/** The one place that turns a word into a price strategy (Factory pattern). */
public class PriceStrategyFactory {
    public static final String[] WORDS = {"normal", "festival", "slump"};

    public static PriceStrategy create(String word) {
        switch (word.toLowerCase()) {
            case "normal": return new NormalPrices();
            case "festival": return new FestivalPrices();
            case "slump": return new SlumpPrices();
            default: throw new IllegalArgumentException("unknown price word: " + word);
        }
    }
}
