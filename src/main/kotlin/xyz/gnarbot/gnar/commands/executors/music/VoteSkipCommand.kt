package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.EmbedBuilder
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.desc
import java.util.concurrent.TimeUnit

@Command(
        aliases = ["voteskip"],
        description = "Vote to skip the current music track."
)
@BotInfo(
        id = 75,
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class VoteSkipCommand : MusicCommandExecutor(true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (context.member.voiceState.isDeafened) {
            context.send().error("You actually have to be listening to the song to start a vote.").queue()
            return
        }
        if (manager.isVotingToSkip) {
            context.send().error("There is already a vote going on!").queue()
            return
        }
        if (System.currentTimeMillis() - manager.lastVoteTime < Bot.CONFIG.voteSkipCooldown.toMillis()) {
            context.send().error("You must wait ${Bot.CONFIG.voteSkipCooldownText} before starting a new vote.").queue()
            return
        }
        if (manager.player.playingTrack.duration - manager.player.playingTrack.position <= Bot.CONFIG.voteSkipDuration.toMillis()) {
            context.send().error("By the time the vote finishes in ${Bot.CONFIG.voteSkipDurationText}, the song will be over.").queue()
            return
        }

        manager.lastVoteTime = System.currentTimeMillis()
        manager.isVotingToSkip = true

        context.send().embed("Vote Skip") {
            desc {
                buildString {
                    append(b(context.message.author.name))
                    append(" has voted to **skip** the current track!")
                    append(" React with :thumbsup: or :thumbsdown:\n")
                    append("Whichever has the most votes in ${Bot.CONFIG.voteSkipDurationText} will win!")
                }
            }
        }.action().queue {
            it.addReaction("👍").queue()
            it.addReaction("👎").queue()

            it.editMessage(EmbedBuilder(it.embeds[0]).apply {
                desc { "Voting has ended! Check the newer messages for results." }
                clearFields()
            }.build()).queueAfter(Bot.CONFIG.voteSkipDuration.seconds, TimeUnit.SECONDS) {
                var skip = 0
                var stay = 0

                it.reactions.forEach {
                    if (it.reactionEmote.name == "👍") skip = it.count - 1
                    if (it.reactionEmote.name == "👎") stay = it.count - 1
                }

                context.send().embed("Vote Skip") {
                    desc {
                        buildString {
                            if (skip > stay) {
                                appendln("The vote has passed! The song has been skipped.")
                                manager.scheduler.nextTrack()
                            } else {
                                appendln("The vote has failed! The song will stay.")
                            }
                        }
                    }
                    field("Results") {
                        "__$skip Skip Votes__ — __$stay Stay Votes__"
                    }
                }.action().queue()
                manager.isVotingToSkip = false
            }
        }
    }
}