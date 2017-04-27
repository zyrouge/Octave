package xyz.gnarbot.gnar.commands.executors.media;

import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.util.Random;

@Command(
        aliases = "xkcd",
        description = "Grab some XKCD comics.",
        category = Category.FUN
)
public class XKCDCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        try {
            JSONObject latestJso = Unirest.get("http://xkcd.com/info.0.json").asJson().getBody().getObject();

            if (latestJso != null) {
                int min = 500;
                int max = latestJso.getInt("num");

                int rand;
                if (args.length >= 1) {
                    int input;
                    try {
                        input = Integer.valueOf(args[0]);

                        if (input > max || input < 1) {
                            context.send().error("xkcd does not have a comic for that number.").queue();
                        }

                        rand = input;
                    } catch (NumberFormatException e) {
                        if (args[0].equalsIgnoreCase("latest")) {
                            rand = max;
                        } else {
                            context.send().error("You didn't enter a proper number.").queue();
                            return;
                        }
                    }
                } else {
                    rand = min + new Random().nextInt(max - min);
                }

                JSONObject jso = Unirest.get("http://xkcd.com/" + rand + "/info.0.json").asJson().getBody().getObject();

                if (jso != null) {
                    String title = jso.getString("title");

                    int num = jso.getInt("num");

                    String url = jso.getString("img").replaceAll("\\\\/", "/");

                    String logo = "http://imgs.xkcd.com/static/terrible_small_logo.png";

                    context.send().embed(title)
                            .setColor(BotConfiguration.ACCENT_COLOR)
                            .setDescription("No: " + num)
                            .setThumbnail(logo)
                            .setImage(url)
                            .action().queue();

                    return;
                }
            }

            context.send().error("Unable to grab xkcd comic.").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}