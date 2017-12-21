package xyz.gnarbot.gnar.commands.executors.media;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@Command(
        aliases = {"urbandict", "ub", "urbandictionary"},
        usage = "(word)",
        description = "Crowd-sourced definitions of the internet."
)
@BotInfo(
        id = 30,
        category = Category.MEDIA
)
public class UrbanDictionaryCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String mashape = context.getBot().getCredentials().getMashape();
        if (mashape == null) {
            context.send().error("Mashape key is null").queue();
            return;
        }

        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
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
                .header("X-Mashape-Key", mashape)
                .header("Accept", "text/plain")
                .get()
                .build();

        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                context.send().error("Failed to find that word.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) return;

                JSONArray words = new JSONObject(new JSONTokener(body.byteStream())).getJSONArray("list");

                if (words.length() < 1) {
                    context.send().error("Could not find that word.").queue();
                    return;
                }

                JSONObject word = words.getJSONObject(0);

                context.send().embed()
                        .setTitle(word.getString("word"), word.getString("permalink"))
                        .setThumbnail("https://s3.amazonaws.com/mashape-production-logos/apis/53aa4f67e4b0a9b1348da532_medium")
                        .setDesc("Definition by " + word.optString("author"))
                        .field("\uD83D\uDC4D Upvotes", true, word.optInt("thumbs_up"))
                        .field("\uD83D\uDC4E Downvotes", true, word.optInt("thumbs_down"))
                        .field("Definition", false, word.optString("definition"))
                        .field("Example", false, word.optString("example"))
                        .action().queue();
            }
        });
    }

}
