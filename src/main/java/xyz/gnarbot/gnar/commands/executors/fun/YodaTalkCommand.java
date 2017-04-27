package xyz.gnarbot.gnar.commands.executors.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = {"yodatalk"},
        usage = "(sentence)",
        description = "Learn to speak like Yoda, you will.",
        category = Category.FUN
)
public class YodaTalkCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("At least put something. `:[`").queue();
            return;
        }

        try {
            String query = StringUtils.join(args, "+");

            HttpResponse<String> response = Unirest.get("https://yoda.p.mashape.com/yoda?sentence=" + query)
                    //.queryString("sentence", query)
                    .header("X-Mashape-Key", "dw1mYrC2ssmsh2WkFGHaherCtl48p1wtuHWjsnYbP3Y7q8y6M5")
                    .header("Accept", "text/plain")
                    .asString();

            String result = response.getBody();

            context.send().embed("Yoda-Speak")
                    .setColor(BotConfiguration.ACCENT_COLOR)
                    .setDescription(result)
                    .setThumbnail("https://upload.wikimedia.org/wikipedia/en/9/9b/Yoda_Empire_Strikes_Back.png")
                    .action().queue();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}