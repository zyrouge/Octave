package xyz.gnarbot.gnar.commands.executors.media;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
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
        id = 31,
        aliases = "xkcd",
        usage = "[comic #]",
        description = "Grab some XKCD comics.",
        category = Category.MEDIA
)
public class XKCDCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        Request request = new Request.Builder()
                .url("http://xkcd.com/info.0.json")
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
                context.send().error("Unable to grab xkcd comic.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) return;

                JSONObject latestJso = new JSONObject(new JSONTokener(body.byteStream()));

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
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        call.cancel();
                        context.send().error("Unable to grab xkcd comic.").queue();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body == null) return;

                        JSONObject jso = new JSONObject(new JSONTokener(body.byteStream()));

                        String title = jso.getString("title");

                        int num = jso.getInt("num");

                        String url = jso.getString("img").replaceAll("\\\\/", "/");

                        String logo = "http://imgs.xkcd.com/static/terrible_small_logo.png";

                        context.send().embed(title)
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