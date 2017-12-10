package xyz.gnarbot.gnar.commands.executors.media;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Command(
        aliases = {
                "action", "cry", "cuddle", "hug", "kiss", "lewd", "lick",
                "nom", "nyan", "owo", "pat", "potato", "pout", "rem",
                "smug", "stare", "tickle", "triggered", "slap"
        },
        description = "Unleash your unrelenting yet suppressed emotions.",
        usage = "(action)"
)
@BotInfo(
        id = 78,
        category = Category.MEDIA
)
public class ActionCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String action;

        if (!label.equals("action")) {
            action = label;
        } else if (args.length > 0) {
            action = args[0];
        } else {
            context.send().error("Try one of these actions: `" + Arrays.toString(getInfo().aliases()) + "`").queue();
            return;
        }

        if (!Arrays.asList(getInfo().aliases()).contains(action)) {
            context.send().error("This isn't one of the available actions.").queue();
            return;
        }

        context.send().embed().setImage(getRamMoeImage(action)).action().queue();
    }

    private static String getRamMoeImage(String type) {
        String url;
        try {
            url = new URIBuilder("https://rra.ram.moe/i/r").addParameter("type", type).toString();
        } catch (URISyntaxException e) {
            return null;
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response r = HttpUtils.CLIENT.newCall(request).execute()) {
            ResponseBody body = r.body();
            if (body == null) {
                return null;
            }

            return "https://rra.ram.moe" + new JSONObject(body.string()).getString("path");
        } catch (IOException e) {
            return null;
        }
    }
}
