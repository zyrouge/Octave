package xyz.gnarbot.gnar.commands.executors.fun;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@Command(
        aliases = {"yodatalk"},
        usage = "(sentence)",
        description = "Learn to speak like Yoda, you will.",
        category = Category.FUN
)
public class YodaTalkCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (context.getKeys().getMashape() == null) {
            context.send().error("Mashape key is null").queue();
            return;
        }

        if (args.length == 0) {
            context.send().error("At least put something. `:[`").queue();
            return;
        }

        try {
            String query = StringUtils.join(args, " ");

            String url = new URIBuilder("https://yoda.p.mashape.com/yoda")
                    .addParameter("sentence", query)
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
                    context.send().embed("Yoda-Speak")
                            .setColor(context.getConfig().getAccentColor())
                            .setDescription(response.body().string())
                            .setThumbnail("https://upload.wikimedia.org/wikipedia/en/9/9b/Yoda_Empire_Strikes_Back.png")
                            .action().queue();

                    response.close();
                }
            });
        } catch (URISyntaxException e) {
            context.send().error("Failure to query API.").queue();
        }
    }
}