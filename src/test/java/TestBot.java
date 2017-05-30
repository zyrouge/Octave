import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.Credentials;
import xyz.gnarbot.gnar.utils.Utils;

import java.io.File;

/**
 * Test bot instantiation class.
 *
 * @author Avarel
 */
public class TestBot {
    public static void main(String[] args) {
        Credentials credentials = new Credentials(new File(Utils.DATA_FOLDER, "credentials.conf"));
        BotConfiguration config = new BotConfiguration(new File(Utils.DATA_FOLDER, "bot.conf"));

        new Bot(config, credentials);
    }
}
