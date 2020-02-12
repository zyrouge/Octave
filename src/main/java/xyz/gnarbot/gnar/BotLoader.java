package xyz.gnarbot.gnar;

import javax.security.auth.login.LoginException;
import java.io.File;

/**
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class BotLoader {
    public static Bot BOT;

    public static void main(String[] args) throws LoginException, InterruptedException {
        BOT = new Bot(new BotCredentials(new File("credentials.conf")), () -> new Configuration(new File("bot.conf")));
    }
}
