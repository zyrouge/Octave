package xyz.gnarbot.gnar.commands.executors.media;


import com.jagrosh.jdautilities.menu.PaginatorBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

@Command(
        id = 28,
        aliases = "meme",
        usage = "(meme name) | (top text) | (bottom text)",
        description = "Create the dankest memes ever.",
        category = Category.MEDIA
)
public class MemeCommand extends CommandExecutor {
    private static final Map<String, String> map = new TreeMap<>();

    static {
        Request request = new Request.Builder()
                .url("https://api.imgflip.com/get_memes")
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONArray memeList = new JSONObject(new JSONTokener(response.body().byteStream()))
                        .getJSONObject("data")
                        .getJSONArray("memes");

                response.close();

                for (int i = 0; i < memeList.length(); i++) {
                    JSONObject jso = memeList.optJSONObject(i);
                    map.put(jso.getString("name"), jso.getString("id"));
                }
            }
        });
    }

    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error(
                    "Example Usage:\n\n"
                            + "`_meme (meme name) | (top text) | (bottom text)`\n"
                            + "`_meme spongegar | what in | tarnation`\n\n"
                            + "For a list of memes, type:\n\n"
                            + "`_meme list`"
            ).queue();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            new PaginatorBuilder(Bot.getWaiter())
                    .setTitle("Meme list")
                    .setDescription("Use one of these like so: `_meme (meme name) | (top text) | (bottom text)`")
                    .addAll(map.keySet())
                    .build()
                    .display(context.getChannel());
            return;
        }

        String query = StringUtils.join(args, " ");
        String[] arguments = query.split("\\|");

        if (args.length < 3) {
            context.send().error("Insufficient arguments... `_meme (meme name) | (top text) | (bottom text)`").queue();
            return;
        }

        int ld = Integer.MAX_VALUE;
        String id = null;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            int _d = StringUtils.getLevenshteinDistance(entry.getKey(), arguments[0].trim());

            if (_d < ld) {
                ld = _d;
                id = entry.getValue();
            }
        }

        String url;
        try {
            url = new URIBuilder("https://api.imgflip.com/caption_image")
                    .addParameter("template_id", id)
                    .addParameter("username", "GNARBot")
                    .addParameter("password", Bot.KEYS.getImgFlip())
                    .addParameter("text0", arguments[1].trim())
                    .addParameter("text1", arguments[2].trim())
                    .toString();
        } catch (URISyntaxException e) {
            Bot.LOG.warn("Meme error", e);
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .header("X-Mashape-Key", Bot.KEYS.getMashape())
                .header("Accept", "application/json")
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
                context.send().error("Failure to query API.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject jso = new JSONObject(new JSONTokener(response.body().byteStream()))
                        .getJSONObject("data");

                context.send().embed("Meme Generator")
                        .setImage(jso.optString("url"))
                        .action().queue();

                response.close();
            }
        });
    }
}
