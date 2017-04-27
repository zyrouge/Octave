package xyz.gnarbot.gnar;

/**
 * Main bot instantiation class.
 * <p>
 * To run a test instance, use TestBot.java found in
 * test/java/xyz/gnarbot/gnar/tests folder.
 * <p>
 * Do not modify.
 *
 * @author Avarel, Xevryll
 */
public class MainBot {
    public static void main(String[] args) {
        new Bot(Credentials.PRODUCTION, 32);
    }
}
