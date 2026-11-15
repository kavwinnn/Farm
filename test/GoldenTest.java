import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Golden output for refactoring 1.4 (Duplicated Code in FarmScreen.ship() / sleep()).
 * Calls the game's own classes and prints what shipping and sleeping produce.
 * Run it before and after the refactoring: the two outputs must be identical, line by line.
 *
 *   javac -d out src/*.java test/GoldenTest.java
 *   java -cp out GoldenTest > after.txt
 *   (compare after.txt with test/golden_output.txt)
 *
 * Only the lines that don't depend on chance are printed (weather and Mrs. Daeng's news are random).
 * sleep() saves the game, so the player's save file is put back afterwards.
 */
public class GoldenTest {
    private static FarmScreen screen;
    private static GamePanel game;

    public static void main(String[] args) throws Exception {
        Path save = Paths.get("littlefarm_save.txt");
        byte[] backup = Files.exists(save) ? Files.readAllBytes(save) : null;
        try {
            run();
        } finally {
            if (backup != null) Files.write(save, backup);
            else Files.deleteIfExists(save);
        }
    }

    private static void run() throws Exception {
        game = new GamePanel();
        screen = FarmScreen.newGame(game);
        Farmer farmer = screen.getFarmer();

        System.out.println("== 1. ship an empty basket");
        ship();

        System.out.println("== 2. ship one of each crop");
        farmer.getBasket().add("Turnip", 3);
        farmer.getBasket().add("Potato", 1);
        farmer.getBasket().add("Strawberry", 2);
        ship();
        sleep();

        System.out.println("== 3. a day with nothing shipped");
        sleep();

        System.out.println("== 4. ship twice in one day, the same crop both times");
        farmer.getBasket().add("Turnip", 1);
        ship();
        farmer.getBasket().add("Turnip", 4);
        farmer.getBasket().add("Strawberry", 1);
        ship();
        sleep();

        System.out.println("== 5. animal products");
        farmer.getBasket().add("Egg", 2);
        farmer.getBasket().add("Milk", 1);
        ship();
        sleep();

        System.out.println("== 6. a big basket");
        farmer.getBasket().add("Potato", 99);
        farmer.getBasket().add("Milk", 12);
        ship();
        sleep();
    }

    private static void ship() throws Exception {
        Method ship = FarmScreen.class.getDeclaredMethod("ship");
        ship.setAccessible(true);
        try {
            ship.invoke(screen);
            List<?> texts = (List<?>) field(FarmScreen.class, "floatingTexts").get(screen);
            Object last = texts.get(texts.size() - 1);
            System.out.println("ship -> " + field(FloatingText.class, "text").get(last)
                    + " | basket " + screen.getFarmer().getBasket());
        } catch (java.lang.reflect.InvocationTargetException e) {
            System.out.println("ship -> refused: " + e.getCause().getMessage());
        }
    }

    private static void sleep() throws Exception {
        int before = screen.getFarmer().getGold();
        Method sleep = FarmScreen.class.getDeclaredMethod("sleep", boolean.class);
        sleep.setAccessible(true);
        sleep.invoke(screen, false);
        Object summary = field(GamePanel.class, "screen").get(game);
        for (Object line : (List<?>) field(SummaryScreen.class, "lines").get(summary)) {
            String text = (String) line;
            if (text.startsWith("Shipped") || text.startsWith("Nothing was shipped")) System.out.println("summary: " + text);
        }
        System.out.println("gold " + before + " -> " + screen.getFarmer().getGold()
                + " | earned total " + screen.getFarmer().getTotalEarned());
        screen.beginDay();
    }

    private static Field field(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
