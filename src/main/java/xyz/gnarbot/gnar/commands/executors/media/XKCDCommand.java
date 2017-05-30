package xyz.gnarbot.gnar.commands.executors.media;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.util.Random;

@Command(
        aliases = "xkcd",
        description = "Grab some XKCD comics.",
        category = Category.FUN
)
public class XKCDCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {

        Request request = new Request.Builder()
                .url("http://xkcd.com/info.0.json")
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                context.send().error("Unable to grab xkcd comic.").queue();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject latestJso = new JSONObject(new JSONTokener(response.body().byteStream()));

                int min = 500;
                int max = latestJso.getInt("num");

                int rand;
                if (args.length >= 1) {
                    int input;
                    try {
                        input = Integer.valueOf(args[0]);

                        if (input > max || input < 1) {
                            context.send().error("xkcd does not have a comic for that number.").queue();
                            return;
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

                Request request = new Request.Builder()
                        .url("http://xkcd.com/" + rand + "/info.0.json")
                        .build();

                HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        context.send().error("Unable to grab xkcd comic.").queue();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject jso = new JSONObject(new JSONTokener(response.body().byteStream()));

                        String title = jso.getString("title");

                        int num = jso.getInt("num");

                        String url = jso.getString("img").replaceAll("\\\\/", "/");

                        String logo = "http://imgs.xkcd.com/static/terrible_small_logo.png";

                        context.send().embed(title)
                                .setColor(context.getBot().getConfig().getAccentColor())
                                .setDescription("No: " + num)
                                .setThumbnail(logo)
                                .setImage(url)
                                .action().queue();
                    }
                });
            }
        });
    }
}