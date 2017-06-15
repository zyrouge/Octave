package xyz.gnarbot.gnar.commands.executors.fun;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@Command(
        aliases = {"urbandict", "ub", "urbandictionary"},
        category = Category.FUN
)
public class UrbanDictionaryCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (Bot.KEYS.getMashape() == null) {
            context.send().error("Mashape key is null").queue();
            return;
        }

        String query = StringUtils.join(args, " ");

        String url;
        try {
            url = new URIBuilder("https://mashape-community-urban-dictionary.p.mashape.com/define")
                    .addParameter("term", query)
                    .toString();
        } catch (URISyntaxException e) {
            Bot.LOG.error("Urban dictionary error", e);
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .header("X-Mashape-Key", Bot.KEYS.getMashape())
                .header("Accept", "text/plain")
                .get()
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.send().error("Failed to find that word.").queue();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONArray words = new JSONObject(response.body().string()).getJSONArray("list");

                if (words.length() < 1) {
                    context.send().error("Could not find that word.").queue();
                    return;
                }

                JSONObject word = words.getJSONObject(0);

                context.send().embed("Urban Dictionary")
                        .setThumbnail("https://s3.amazonaws.com/mashape-production-logos/apis/53aa4f67e4b0a9b1348da532_medium")
                        .field("Word", true, "[" + word.getString("word") + "](" + word.getString("permalink") + ")")
                        .field("Definition", true, word.optString("definition"))
                        .field("Example", true, word.optString("example"))
                        .action().queue();
            }
        });
    }

}
