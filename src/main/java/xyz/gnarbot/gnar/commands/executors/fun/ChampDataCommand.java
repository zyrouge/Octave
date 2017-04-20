package xyz.gnarbot.gnar.commands.executors.fun;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

import java.io.File;
import java.util.List;

@Command(
        aliases = "champdata",
        category = Category.FUN)
public class ChampDataCommand extends CommandExecutor {
    private static final String[] names = ChampQuoteCommand.names;
    private static final Config information = ConfigFactory.parseFile(new File(BotConfiguration.DATA_FOLDER,"league/League.txt"));

    @Override
    public void execute(Message message, String[] args) {
        int maybeDistance = 20;
        String maybe = "";

        for (String s : names) {
            int distance = StringUtils.getLevenshteinDistance(s, StringUtils.join(args, "").replaceAll("'", ""));
            if (maybeDistance > distance) {
                maybeDistance = distance;
                maybe = s;
            }
        }

        Config jso = information.getConfig(maybe);

        List<? extends Config> spells = jso.getConfigList("spells");


        StringBuilder spellInfo = new StringBuilder("**" + maybe + "**: " + jso.getString("title") + "\n");
        String key = "";


        for (int i = 0; i < spells.size(); i++) {
            Config spellOne = spells.get(i);

            switch (i) {
                case 0:
                    key = "Q";
                    break;
                case 1:
                    key = "W";
                    break;
                case 2:
                    key = "E";
                    break;
                case 3:
                    key = "R";
                    break;
            }

            spellInfo.append("    **")
                    .append(key)
                    .append("** - ")
                    .append(spellOne.getString("name"))
                    .append(": \n         ")
                    .append(spellOne.getString("description"))
                    .append("\n");
        }

        spellInfo.append("\n**Skins:**");


        List<? extends Config> skins = jso.getConfigList("skins");
        for (int i = 0; i < skins.size(); i++) {
            Config j = skins.get(i);

            int fuckTits = i + 1;

            spellInfo.append("\n    **").append(fuckTits).append("**: ").append(j.getString("name"));
        }

        message.send().text(spellInfo.toString()).queue();
    }

}
