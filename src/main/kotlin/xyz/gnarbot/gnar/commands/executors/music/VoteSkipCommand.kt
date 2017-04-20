package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.b
import net.dv8tion.jda.core.entities.Message
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import java.util.concurrent.TimeUnit

@Command(
        aliases = arrayOf("voteskip"),
        description = "Vote to skip the current music track.",
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class VoteSkipCommand : CommandExecutor() {
    override fun execute(message: Message, args: Array<String>) {
        val manager = guildData.musicManager

        val member = guild.getMember(message.author)

        if (manager.player.playingTrack == null) {
            message.send().error("There isn't a song playing.").queue()
            return
        }

        if (member.voiceState.isDeafened) {
            message.send().error("You actually have to be listening to the song to start a vote... Tsk tsk...").queue {
                it.delete().queueAfter(5, TimeUnit.SECONDS)
            }
            return
        }
        if (manager.isVotingToSkip) {
            message.send().error("There is already a vote going on!").queue { msg ->
                msg.delete().queueAfter(5, TimeUnit.SECONDS)
            }
            return
        }
        if (System.currentTimeMillis() - manager.lastVoteTime < BotConfiguration.VOTE_SKIP_COOLDOWN.toMillis()) {
            message.send().error("You must wait ${BotConfiguration.VOTE_SKIP_COOLDOWN_TEXT} before starting a new vote.").queue()
            return
        }
        if (manager.player.playingTrack.duration - manager.player.playingTrack.position <= 30) {
            message.send().error("By the time the vote finishes, the song will be over.").queue()
            return
        }

        manager.lastVoteTime = System.currentTimeMillis()
        manager.isVotingToSkip = true

        message.send().embed("Vote Skip") {
            color = BotConfiguration.MUSIC_COLOR
            description {
                buildString {
                    append(b(message.author.name))
                    append(" has voted to **skip** the current track!")
                    appendln(" React with :thumbsup: or :thumbsdown:")
                    append("Whichever has the most votes in 30 seconds will win!")
                }
            }
        }.rest().queue {
            it.addReaction("👍").queue()
            it.addReaction("👎").queue()

            it.editMessage(it.embeds[0].edit().apply {
                description = "Voting has ended! Check the newer messages for results."
                clearFields()
            }.build()).queueAfter(30, TimeUnit.SECONDS) {

                var skip = 0
                var stay = 0

                it.reactions.forEach {
                    if (it.emote.name == "👍") skip = it.count - 1
                    if (it.emote.name == "👎") stay = it.count - 1
                }

                it.send().embed("Vote Skip") {
                    color = BotConfiguration.MUSIC_COLOR
                    description {
                        buildString {
                            if (skip > stay) {
                                appendln("The vote has passed! The song has been skipped.")
                                if (manager.scheduler.queue.isEmpty()) {
                                    guildData.resetMusicManager()
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
                }.rest().queue()
                manager.isVotingToSkip = false
            }
        }
    }
}