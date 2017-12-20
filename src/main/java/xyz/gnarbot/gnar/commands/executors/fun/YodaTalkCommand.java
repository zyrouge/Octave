package xyz.gnarbot.gnar.commands.executors.fun;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@Command(
        aliases = {"yodatalk"},
        usage = "(words...)",
        description = "Learn to speak like Yoda, you will."
)
@BotInfo(
        id = 14,
        category = Category.FUN
)
public class YodaTalkCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String mashape = Bot.KEYS.getMashape();
        if (mashape == null) {
            context.send().error("Mashape key is null").queue();
            return;
        }

        if (args.length == 0) {
            Bot.getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        try {
            String query = StringUtils.join(args, " ");

            String url = new URIBuilder("https://yoda.p.mashape.com/yoda")
                    .addParameter("sentence", query)
                    .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .header("X-Mashape-Key", mashape)
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
                    ResponseBody body = response.body();
                    if (body == null) return;

                    context.send().embed()
                            .setDesc(body.string())
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