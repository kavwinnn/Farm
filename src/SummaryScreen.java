import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

/** The end-of-day report: what was shipped, the gold counting up, and tomorrow's weather. */
public class SummaryScreen implements Screen {
    private static final int COUNT_UP_TICKS = 60;   // also stops a held SPACE from skipping the report

    private final GamePanel game;
    private final FarmScreen farm;
    private final List<String> lines;
    private final int goldBefore, goldAfter;
    private int ticks = 0;

    public SummaryScreen(GamePanel game, FarmScreen farm, List<String> lines, int goldBefore, int goldAfter) {
        this.game = game;
        this.farm = farm;
        this.lines = lines;
        this.goldBefore = goldBefore;
        this.goldAfter = goldAfter;
    }

    @Override
    public void update() {
        ticks++;
    }

    @Override
    public void keyPressed(int code) {
        if (ticks < COUNT_UP_TICKS) return;
        if (code != KeyEvent.VK_SPACE && code != KeyEvent.VK_ENTER) return;
        if (farm.isMonthOver()) game.setScreen(new EndingScreen(game, farm));
        else farm.beginDay();
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 200);
        // 6 lines fit the normal box; with more (e.g. news from many animals) the box grows and everything below moves down
        int extra = Math.max(0, lines.size() - 6) * 22;
        int width = 480, height = 350 + extra, left = (Ui.WIDTH - width) / 2;
        int top = Math.max(0, Math.min(80, (Ui.HEIGHT - height) / 2));
        Ui.paper(g, left, top, width, height);
        GameClock clock = farm.getClock();

        Ui.centered(g, "End of Day " + (clock.getDay() - 1), 24, top + 40, Ui.WOOD);
        g.setFont(Ui.font(13));
        g.setColor(Ui.INK);
        for (int i = 0; i < lines.size(); i++) g.drawString(lines.get(i), left + 24, top + 78 + i * 22);

        double progress = Math.min(1.0, ticks / (double) COUNT_UP_TICKS);
        int shown = goldBefore + (int) Math.round((goldAfter - goldBefore) * progress);
        Ui.centered(g, "Gold  " + shown + "G", 26, top + 220 + extra, new Color(190, 140, 20));

        if (!farm.isMonthOver()) {
            g.setFont(Ui.font(15));
            g.setColor(Ui.INK);
            String weather = "Tomorrow: " + clock.getWeather().getLabel();
            int textX = (Ui.WIDTH - g.getFontMetrics().stringWidth(weather)) / 2;
            g.drawString(weather, textX, top + 262 + extra);
            clock.getWeather().drawIcon(g, textX - 28, top + 246 + extra);
            int daysLeft = FarmScreen.LAST_DAY - clock.getDay() + 1;
            int stillNeeded = Math.max(0, FarmScreen.GOAL_GOLD - goldAfter);
            Ui.centered(g, daysLeft + " days left, " + stillNeeded + "G still needed", 15, top + 288 + extra, Ui.INK);
        } else {
            Ui.centered(g, "That was the last day of the month...", 15, top + 275 + extra, Ui.INK);
        }
        if (ticks >= COUNT_UP_TICKS && (game.getFrames() / 30) % 2 == 0) {
            Ui.centered(g, "press SPACE", 14, top + height - 18, Ui.WOOD);
        }
    }
}
