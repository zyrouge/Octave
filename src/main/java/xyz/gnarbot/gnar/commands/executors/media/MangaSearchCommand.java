package xyz.gnarbot.gnar.commands.executors.media;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandDispatcher;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.EmbedMaker;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;

@Command(
        id = 27,
        aliases = {"manga", "mangasearch", "malmanga"},
        usage = "(manga name)",
        description = "Search for an manga via MymangaList.",
        category = Category.MEDIA
)
public class MangaSearchCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            CommandDispatcher.INSTANCE.sendHelp(context, getInfo());
            return;
        }

        if (!Bot.getMALAPI().isLogin()) {
            context.send().error("I've been disabled by our developers for now.").queue();
            return;
        }

        String query = StringUtils.join(args, " ");
        JSONObject jso = Bot.getMALAPI().makeRequest(MyAnimeListAPI.SEARCH_MANGA, query);
        if (jso == null) {
            context.send().error("API timed out. Try again in a bit.").queue();
            return;
        }
        if (!jso.has("manga")) {
            context.send().error("Nothing found with that search term!").queue();
            return;
        }

        if (jso.has("manga") && jso.getJSONObject("manga").has("entry")) {
            Object obj = jso.getJSONObject("manga").get("entry");

            if (obj instanceof JSONObject) {
                obj = new JSONArray().put(obj);
            }

            EmbedMaker eb = new EmbedMaker();

            if (obj instanceof JSONArray) {
                JSONArray jsa = (JSONArray) obj;

                for (int i = 0; i < Math.min(jsa.length(), 3); i++) {
                    JSONObject entry = jsa.getJSONObject(i);

                    if (i == 0) {
                        eb.setThumbnail(entry.getString("image"));
                    }

                    eb.field(StringUtils.truncate(entry.getString("english"), 40), false,
                            "**Chapters:** `" + entry.getInt("chapters") + "`\n"
                                    + "**Score:** `" + entry.getDouble("score") + "/10`\n"
                                    + "**Status:** `" + entry.getString("status") + "`\n"
                                    + "**Start Date:** `" +  entry.getString("start_date") + "`\n"
                                    + "**End Date:** `" + entry.getString("end_date") + "`");

                    String synopsis = Jsoup.parse(entry.getString("synopsis")).text().replaceAll("\\[i]", "").replaceAll("\\[/i]", "");

                    eb.field("Synopsis", false, StringUtils.truncate(synopsis, 200));
                }
            } else {
                context.send().error("Internal error.").queue();
            }
            context.getChannel().sendMessage(eb.build()).queue();
        } else {
            context.send().error("I could not find anything by that title.").queue();
        }
    }
}
