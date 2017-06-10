package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.EmbedMaker
import xyz.gnarbot.gnar.utils.Utils
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
            context.send().error("You actually have to be listening to the song to start a vote.").queue()
            return
        }
        if (manager.isVotingToSkip) {
            context.send().error("There is already a vote going on!").queue()
            return
        }
        if (System.currentTimeMillis() - manager.lastVoteTime < context.bot.config.voteSkipCooldown.toMillis()) {
            context.send().error("You must wait ${context.bot.config.voteSkipCooldownText} before starting a new vote.").queue()
            return
        }
        if (manager.player.playingTrack.duration - manager.player.playingTrack.position <= context.bot.config.voteSkipDuration.toMillis()) {
            context.send().error("By the time the vote finishes in ${context.bot.config.voteSkipDurationText}, the song will be over.").queue()
            return
        }

        manager.lastVoteTime = System.currentTimeMillis()
        manager.isVotingToSkip = true

        context.send().embed("Vote Skip") {
            color = context.bot.config.musicColor
            description {
                buildString {
                    append(b(context.message.author.name))
                    append(" has voted to **skip** the current track!")
                    appendln(" React with :thumbsup: or :thumbsdown:")
                    append("Whichever has the most votes in ${context.bot.config.voteSkipDurationText} will win!")
                }
            }
        }.action().queue {
            it.addReaction("👍").queue()
            it.addReaction("👎").queue()

            it.editMessage(EmbedMaker(it.embeds[0]).apply {
                description = "Voting has ended! Check the newer messages for results."
                clearFields()
            }.build()).queueAfter(context.bot.config.voteSkipDuration.seconds, TimeUnit.SECONDS) {
                var skip = 0
                var stay = 0

                it.reactions.forEach {
                    if (it.emote.name == "👍") skip = it.count - 1
                    if (it.emote.name == "👎") stay = it.count - 1
                }

                context.send().embed("Vote Skip") {
                    color = context.bot.config.musicColor
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
                }.action().queue(Utils.deleteMessage(30))
                manager.isVotingToSkip = false
            }
        }
    }
}