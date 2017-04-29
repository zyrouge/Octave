package xyz.gnarbot.gnar;

import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.SimpleLog;

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
        RestAction.DEFAULT_FAILURE = t -> {
            if (!(t instanceof PermissionException)) {
                if (RestAction.LOG.getEffectiveLevel().getPriority() <= SimpleLog.Level.DEBUG.getPriority()) {
                    RestAction.LOG.log(t);
                } else {
                    RestAction.LOG.fatal("RestAction queue returned failure: [" + t.getClass().getSimpleName() + "] " + t.getMessage());
                }
            }
        };

        new Bot(Credentials.PRODUCTION, 36);
    }
}
