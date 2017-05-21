package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

import java.io.File;
import java.io.FileReader;

@Command(
        aliases = "champdata",
        category = Category.FUN)
public class ChampDataCommand extends CommandExecutor {
    private static final String[] names = ChampQuoteCommand.names;
    private static JSONObject information;

    static {
        try {
            File file = new File(Utils.DATA_FOLDER, "league/League.txt");
            information = new JSONObject(new JSONTokener(new FileReader(file)));
        } catch (Exception ignore) { }
    }

    @Override
    public void execute(Context context, String[] args) {
        int maybeDistance = 20;
        String maybe = "";

        for (String s : names) {
            int distance = StringUtils.getLevenshteinDistance(s, StringUtils.join(args, "").replaceAll("'", ""));
            if (maybeDistance > distance) {
                maybeDistance = distance;
                maybe = s;
            }
        }

        JSONObject jso = information.getJSONObject(maybe);

        JSONArray spells = jso.getJSONArray("spells");


        StringBuilder spellInfo = new StringBuilder("**" + maybe + "**: " + jso.get("title") + "\n");
        String key = "";


        for (int i = 0; i < spells.length(); i++) {
            JSONObject spellOne = spells.getJSONObject(i);

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

            spellInfo.append("    **").append(key).append("** - ").append(spellOne.get("name")).append(": \n         ").append(spellOne.get
                    ("description")).append("\n");
        }

        spellInfo.append("\n**Skins:**");


        JSONArray skins = jso.getJSONArray("skins");
        for (int i = 0; i < skins.length(); i++) {
            JSONObject j = skins.getJSONObject(i);

            int fuckTits = i + 1;

            spellInfo.append("\n    **").append(fuckTits).append("**: ").append(j.get("name"));
        }

        context.send().text(spellInfo.toString()).queue();
    }

}
