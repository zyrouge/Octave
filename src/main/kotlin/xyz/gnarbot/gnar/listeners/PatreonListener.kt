package xyz.gnarbot.gnar.listeners

import com.patreon.resources.Pledge
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import xyz.gnarbot.gnar.Bot

class PatreonListener(private val bot: Bot) : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        launch {
            if (!event.message.contentRaw.startsWith("_patreon ")) {
                return@launch
            }

            val user = event.author
            val name = event.message.contentRaw.substring(9)

            val campaign = bot.patreon.fetchCampaigns().get().first()
            val pledges = mutableListOf<Pledge>()

            var cursor: String? = null
            do {
                val document = bot.patreon.fetchPageOfPledges(campaign.id, 100, cursor)
                pledges += document.get()
                cursor = bot.patreon.getNextCursorFromDocument(document)
            } while (cursor != null)

            val pledge = pledges.find {
                it.patron.fullName == name || it.patron.discordId == user.id
            }

            if (pledge != null) {
                event.channel.sendMessage("Valid patreon!").queue()
            } else {
                event.channel.sendMessage("ur not a patron! but donate to us YEET").queue()
            }
        }
    }
}
