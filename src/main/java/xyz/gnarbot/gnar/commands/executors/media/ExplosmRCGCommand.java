package xyz.gnarbot.gnar.commands.executors.media;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "rcg",
        description = "Generate random Cyanide and Happiness comic."
)
@BotInfo(
        id = 26,
        category = Category.MEDIA
)
public class ExplosmRCGCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        try {
            Document document;

            document = Jsoup.connect("http://explosm.net/rcg").get();

            Element element = document.getElementById("rcg-comic").getElementsByTag("img").first();

            String url = element.absUrl("src");

            String logo = "http://explosm.net/img/logo.png";

            context.send().embed("Cyanide and Happiness")
                    .setDesc("**Random Comic Generator**")
                    .setImage(url)
                    .setThumbnail(logo)
                    .action().queue();

        } catch (Exception e) {
            context.send().error("Unable to grab random Cyanide and Happiness comic.").queue();
            e.printStackTrace();
        }
    }
}

