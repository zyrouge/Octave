package xyz.gnarbot.gnar.commands.executors.media

import xyz.gnarbot.gnar.commands.*

@Command(aliases = ["goodshit", "cat"], description = "That's some good shit right there.")
@BotInfo(id = 1337, category = Category.MEDIA)

class GoodShitCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().text("\uD83D\uDC4C\uD83D\uDC40\uD83D\uDC4C\uD83D\uDC40\uD83D\uDC4C\uD83D\uDC40\uD83D\uDC4C\uD83D" +
                "\uDC40\uD83D\uDC4C\uD83D\uDC40 good shit go౦ԁ sHit\uD83D\uDC4C thats ✔ some " +
                "good\uD83D\uDC4C\uD83D\uDC4Cshit " +
                "right\uD83D\uDC4C\uD83D\uDC4Cthere\uD83D\uDC4C\uD83D\uDC4C\uD83D\uDC4C right✔there ✔✔if i do ƽaү so " +
                "" + "my self \uD83D\uDCAF i say so \uD83D\uDCAF thats what im talking about right there right there " +
                "" + "(chorus: ʳᶦᵍʰᵗ ᵗʰᵉʳᵉ) mMMMMᎷМ\uD83D\uDCAF \uD83D\uDC4C\uD83D\uDC4C " +
                "\uD83D\uDC4CНO0ОଠOOOOOОଠଠOoooᵒᵒᵒᵒᵒᵒᵒᵒᵒ\uD83D\uDC4C \uD83D\uDC4C\uD83D\uDC4C \uD83D\uDC4C " +
                "\uD83D\uDCAF \uD83D\uDC4C \uD83D\uDC40 \uD83D\uDC40 \uD83D\uDC40 \uD83D\uDC4C\uD83D\uDC4CGood shit")
                .queue();
    }
}