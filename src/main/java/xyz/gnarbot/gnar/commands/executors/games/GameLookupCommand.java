package xyz.gnarbot.gnar.commands.executors.games;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;

@Command(aliases = {"game", "gamelookup"},
        usage = "(Game name)",
        description = "Look up information about a game.")
public class GameLookupCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        try {
            String query = StringUtils.join(args, " ");

            String url = new URIBuilder("https://igdbcom-internet-game-database-v1.p.mashape.com/games/")
                    .addParameter("fields", "name,summary,rating,cover.url")
                    .addParameter("limit", String.valueOf(1))
                    .addParameter("search", query)
                    .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .header("X-Mashape-Key", context.getBot().getKeys().getMashape())
                    .header("Accept", "application/json")
                    .build();

            HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    context.send().error("Failure to query API.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONArray jsa = new JSONArray(new JSONTokener(response.body().byteStream()));

                    if (jsa.length() == 0) {
                        context.send().error("No game found with that title.").queue();
                        return;
                    }

                    JSONObject jso = jsa.getJSONObject(0);

                    String title = jso.optString("name");
                    //String publisher = jso.optString("publisher");
                    String score = jso.optString("rating");
                    String desc = jso.optString("summary");
                    String thumb = "https:" + jso.optJSONObject("cover").optString("url");

                    context.send().embed(title)
                            .setColor(context.getBot().getConfig().getAccentColor())
                            .setThumbnail(thumb)
                            //.field("Publisher", true, publisher)
                            .field("Score", true, score)
                            .field("Description", false, desc)
                            .action().queue();

                    response.close();
                }
            });

//            JSONArray jsa = response.getBody().getArray();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
