package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.BotLoader;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "lenny",
        usage = "[integer]",
        description = "( ͡° ͜ʖ ͡° )"
)
@BotInfo(
        id = 6,
        category = Category.FUN
)
public class LennyCommand extends CommandExecutor {
    private static final String[] mouths = {
            "v", "ᴥ", "ᗝ", "Ѡ", "ᗜ", "Ꮂ", "ᨓ", "ᨎ", "ヮ", "╭͜ʖ╮", " ͟ل͜", " ͜ʖ", " ͟ʖ", " ʖ̯", "ω", " ³", " ε ", "﹏",
            "□", "ل͜", "‿", "╭╮", "‿‿", "▾", "‸", "Д", "∀", "!", "人", ".", "ロ", "_", "෴", "ꔢ", "ѽ", "ഌ", "⏠", "⏏",
            "⍊", "⍘", "ツ", "益", "╭∩╮", "Ĺ̯", "◡", " ͜つ", "◞ ", "ヘ"
    };

    private static final String[] eyes = {
            "⌐■|■", " ͠° | °", "⇀|↼", "´• | •`", "´|`", "`|´", "ó|ò", "ò|ó", "⸌|⸍", ">|<", "<|>", "Ƹ̵̡|Ʒ", "ᗒ|ᗕ", "⟃|⟄",
            "⪧|⪦", "⪦|⪧", "⪩|⪨", "⪨|⪩", "⪰|⪯", "⫑|⫒", "⨴|⨵", "⩿|⪀", "⩾|⩽", "⩺|⩹", "⩹|⩺", "◥▶|◀◤", "◍|◎",
            "/͠-| ͝-\\", "\u2323|\u2323\u201d", " ͡⎚| ͡⎚", "≋|≋", "૦ઁ|૦ઁ", "  ͯ|  ͯ", "  ͌|  ͌", "ꗞ|ꗞ", "ꔸ|ꔸ", "꘠|꘠", "ꖘ|ꖘ",
            "ළ|ළ", "◉|◉", "☉|☉", "・|・", "▰|▰", "ᵔ|ᵔ", " ﾟ| ﾟ", "□|□", "☼|☼", "*|*", "`|`", "⚆|⚆", "⊜|⊜", "❍|❍",
            "￣|￣", "─|─", "✿|✿", "•|•", "T|T", "^|^", "ⱺ|ⱺ", "@|@", "ȍ|ȍ", "  |  ", "  |  ", "x|x", "-|-",
            "$|$", "Ȍ|Ȍ", "ʘ|ʘ", "Ꝋ|Ꝋ", "|", "|", "⸟|⸟", "๏|๏", "ⴲ|ⴲ", "■|■", " ﾟ| ﾟ", "◕|◕", "◔|◔", "✧|✧",
            "■|■", "♥|♥", " ͡° |͡°", "¬|¬", " º | º ", "⨶|⨶", "⨱|⨱", "⏓|⏓", "⏒|⏒", "⍜|⍜", "⍤|⍤", "ᚖ|ᚖ", "ᴗ|ᴗ", "ಠ|ಠ",
            "σ|σ", "☯|☯", "の|の", "￢ |￢ ", "э|э", "\u070d|\u070d"
    };

    private static final String[] ears = {
            "q|p", "ʢ|ʡ", "⸮|?", "ʕ|ʔ", "ᖗ|ᖘ", "ᕦ|ᕥ", "ᕦ(|)ᕥ", "ᕙ(|)ᕗ", "ᘳ|ᘰ", "ᕮ|ᕭ", "ᕳ|ᕲ", "(|)", "[|]",
            "¯\\_|_/¯", "୧|୨", "୨|୧", "⤜(|)⤏", "☞|☞", "(╭☞|)╭☞", "ᑫ|ᑷ", "ᑴ|ᑷ", "ヽ(|)ﾉ", "\\(|)/", "乁(|)ㄏ", "└[|]┘",
            "(づ|)づ", "(ง|)ง", "⎝|⎠", "ლ(|ლ)", "ᕕ(|)ᕗ", "(∩|)⊃━☆ﾟ.*", "【|】", "﴾|﴿", "(╯|）╯︵ ┻━┻"
    };

    @Override
    public void execute(Context context, String label, String[] args) {
        int count;

        if (args.length == 0) {
            count = 1;
        } else {
            try {
                count = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                context.send().error("That's not a number. `" + BotLoader.BOT.getConfiguration().getPrefix() +"lenny 10`").queue();
                return;
            }
            count = Math.min(Math.max(count, 1), 15);
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            String mouth = mouths[(int) (Math.random() * mouths.length)];
            String[] eye = eyes[(int) (Math.random() * eyes.length)].split("\\|");
            String[] ear = ears[(int) (Math.random() * ears.length)].split("\\|");

            builder.append('`').append(ear[0]).append(eye[0]).append(mouth).append(eye[1]).append(ear[1]).append("`   ");
        }
        context.send().text(builder.toString()).queue();
    }
}