/*package xyz.gnarbot.gnar.commands.executors.media

import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.SubredditReference
import xyz.gnarbot.gnar.commands.*
import java.util.*

@Command(aliases = ["keto", "gross", "ketogenic"], description = "Grab random ketogenic posts on Reddit for you.")
@BotInfo(id = 103, category = Category.MEDIA)
class KetoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        try {
            val ref : SubredditReference = context.bot.redditClient.subreddit("ketorecipes")
            val post = ref.posts().limit(100).build()
            val submission = post.elementAt(6)[0]
            val title = submission?.title
            context.send().embed().setTitle(title, submission.url).action().queue()
        } catch (e: Exception) {
            context.send().error(e.toString()).queue()
        }

    }
}
*/