package xyz.gnarbot.gnar.commands.executors.media;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Command(
        aliases = "meme",
        usage = "-meme_name | _top | _bottom",
        description = "Create the dankest memes ever.",
        category = Category.FUN
)
public class MemeCommand extends CommandExecutor {
    private static final Map<String, String> map = new TreeMap<>();

    static {
        Request request = new Request.Builder()
                .url("https://api.imgflip.com/get_memes")
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
        try {
            if (args[0].equalsIgnoreCase("list")) {
                int page = 1;

                try {
                    if (args[1] != null) {
                        page = Integer.valueOf(args[1]);
                        if (page <= 0) { page = 1; }
                    }
                } catch (Exception ignore) {}

                Set<String> names = map.keySet();

                int pages;

                if (map.keySet().size() % 10 == 0) {
                    pages = names.size() / 10;
                } else {
                    pages = names.size() / 10 + 1;
                }

                if (page > pages) page = pages;

                int _page = page;
                context.send().embed("Meme List")
                        .description(() -> {
                            StringBuilder sb = new StringBuilder();
                            int i = 0;
                            for (String g : names) {
                                i++;
                                if (i < 10 * _page + 1 && i > 10 * _page - 10) {
                                    sb.append("**#").append(i).append("** [").append(g).append("]()").append('\n');
                                }
                            }
                            return sb.toString();
                        })
                        .setFooter("Page [" + page + "/" + pages + "]", null)
                        .action().queue();
                return;
            }
            String query = StringUtils.join(args, " ");
            String[] arguments = query.split("\\|");

            int ld = 999;
            String id = null;

            for (Map.Entry<String, String> entry : map.entrySet()) {
                int _d = StringUtils.getLevenshteinDistance(entry.getKey(), arguments[0].trim());

                if (_d < ld) {
                    ld = _d;
                    id = entry.getValue();
                }
            }

            String url = new URIBuilder("https://api.imgflip.com/caption_image")
                    .addParameter("template_id", id)
                    .addParameter("username", "GNARBot")
                    .addParameter("password", context.getKeys().getImgFlip())
                    .addParameter("text0", arguments[1].trim())
                    .addParameter("text1", arguments[2].trim())
                    .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .header("X-Mashape-Key", context.getKeys().getMashape())
                    .header("Accept", "application/json")
                    .build();

            HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    context.send().error("Failure to query API.").queue();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jso = new JSONObject(new JSONTokener(response.body().byteStream()))
                            .getJSONObject("data");

                    context.send().embed("Meme Generator")
                            .setColor(context.getConfig().getAccentColor())
                            .setImage(jso.optString("url"))
                            .action().queue();

                    response.close();
                }
            });
        } catch (Exception e) {
            context.send().error(
                    "*Arguments invalid. Example Usage:**\n\n" +
                    "[_meme Spongegar | Top Text | Bottom Text]()\n\n" +
                    "**For a list of memes, type:**\n\n" +
                    "[_meme list (page #)]()").queue();
        }
    }

}
