package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.KEmbedBuilder
import xyz.gnarbot.gnar.utils.b
import java.util.concurrent.TimeUnit

@Command(
        aliases = arrayOf("voteskip"),
        description = "Vote to skip the current music track.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class VoteSkipCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = context.guildData.musicManager

        val member = context.guild.getMember(context.message.author)

        if (manager.player.playingTrack == null) {
            context.send().error("There isn't a song playing.").queue()
            return
        }

        if (member.voiceState.isDeafened) {
            context.send().error("You actually have to be listening to the song to start a vote... Tsk tsk...").queue {
                it.delete().queueAfter(5, TimeUnit.SECONDS)
            }
            return
        }
        if (manager.isVotingToSkip) {
            context.send().error("There is already a vote going on!").queue { msg ->
                msg.delete().queueAfter(5, TimeUnit.SECONDS)
            }
            return
        }
        if (System.currentTimeMillis() - manager.lastVoteTime < BotConfiguration.VOTE_SKIP_COOLDOWN.toMillis()) {
            context.send().error("You must wait ${BotConfiguration.VOTE_SKIP_COOLDOWN_TEXT} before starting a new vote.").queue()
            return
        }
        if (manager.player.playingTrack.duration - manager.player.playingTrack.position <= BotConfiguration.VOTE_SKIP_DURATION.toMillis()) {
            context.send().error("By the time the vote finishes in ${BotConfiguration.VOTE_SKIP_DURATION_TEXT}, the song will be over.").queue()
            return
        }

        manager.lastVoteTime = System.currentTimeMillis()
        manager.isVotingToSkip = true

        context.send().embed("Vote Skip") {
            color = BotConfiguration.MUSIC_COLOR
            description {
                buildString {
                    append(b(context.message.author.name))
                    append(" has voted to **skip** the current track!")
                    appendln(" React with :thumbsup: or :thumbsdown:")
                    append("Whichever has the most votes in ${BotConfiguration.VOTE_SKIP_DURATION_TEXT} will win!")
                }
            }
        }.action().queue {
            it.addReaction("👍").queue()
            it.addReaction("👎").queue()

            it.editMessage(KEmbedBuilder(it.embeds[0]).apply {
                description = "Voting has ended! Check the newer messages for results."
                clearFields()
            }.build()).queueAfter(BotConfiguration.VOTE_SKIP_DURATION.seconds, TimeUnit.SECONDS) {

                var skip = 0
                var stay = 0

                it.reactions.forEach {
                    if (it.emote.name == "👍") skip = it.count - 1
                    if (it.emote.name == "👎") stay = it.count - 1
                }

                context.send().embed("Vote Skip") {
                    color = BotConfiguration.MUSIC_COLOR
                    description {
                        buildString {
                            if (skip > stay) {
                                appendln("The vote has passed! The song has been skipped.")
                                if (manager.scheduler.queue.isEmpty()) {
                                    context.guildData.musicManager.reset()
                                } else {
                                    manager.scheduler.nextTrack()
                                }
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