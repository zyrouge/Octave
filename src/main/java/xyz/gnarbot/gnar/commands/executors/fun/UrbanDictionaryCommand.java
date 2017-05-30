package xyz.gnarbot.gnar.commands.executors.fun;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gnarbot.gnar.utils.HttpUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.io.IOException;

@Command(
        aliases = {"urbandict", "ub", "urbandictionary"},
        category = Category.FUN
)
public class UrbanDictionaryCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        String query = StringUtils.join(args, "+");

        Request request = new Request.Builder()
                .url("https://mashape-community-urban-dictionary.p.mashape.com/define?term=" + query)
                .header("X-Mashape-Key", context.getBot().getKeys().getMashape())
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
                        .setColor(context.getBot().getConfig().getAccentColor())
                        .setThumbnail("https://s3.amazonaws.com/mashape-production-logos/apis/53aa4f67e4b0a9b1348da532_medium")
                        .field("Word", true, "[" + word.getString("word") + "](" + word.getString("permalink") + ")")
                        .field("Definition", true, word.optString("definition"))
                        .field("Example", true, word.optString("example"))
                        .action().queue();
            }
        });
    }

}
