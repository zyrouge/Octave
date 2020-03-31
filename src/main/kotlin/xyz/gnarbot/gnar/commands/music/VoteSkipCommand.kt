package xyz.gnarbot.gnar.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
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
class VoteSkipCommand : MusicCommandExecutor(true, true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (context.member.voiceState!!.isDeafened) {
            context.send().issue("You actually have to be listening to the song to start a vote.").queue()
            return
        }
        if (manager.isVotingToSkip) {
            context.send().issue("There is already a vote going on!").queue()
            return
        }

        val voteSkipCooldown = if(context.data.music.voteSkipCooldown <= 0) {
            context.bot.configuration.voteSkipCooldown.toMillis()
        } else {
            context.data.music.voteSkipCooldown
        }

        if (System.currentTimeMillis() - manager.lastVoteTime < voteSkipCooldown) {
            context.send().issue("You must wait $voteSkipCooldown before starting a new vote.").queue()
            return
        }

        val voteSkipDuration = if(context.data.music.voteSkipDuration <= 0) {
            context.bot.configuration.voteSkipDuration.toMillis()
        } else {
            context.data.music.voteSkipDuration
        }

        val voteSkipDurationText = if(context.data.music.voteSkipDuration <= 0) {
            context.bot.configuration.voteSkipDuration.toMinutes().toString() + " minutes"
        } else {
            context.bot.configuration.voteSkipDurationText
        }

        if (manager.player.playingTrack.duration - manager.player.playingTrack.position <= voteSkipDuration) {
            context.send().issue("By the time the vote finishes in $voteSkipDurationText, the song will be over.").queue()
            return
        }

        manager.lastVoteTime = System.currentTimeMillis()
        manager.isVotingToSkip = true

        context.send().embed("Vote Skip") {
            desc {
                buildString {
                    append(context.message.author.asMention)
                    append(" has voted to **skip** the current track!")
                    append(" React with :thumbsup: or :thumbsdown:\n")
                    append("Whichever has the most votes in ${context.bot.configuration.voteSkipDurationText} will win!")
                }
            }
        }.action().queue {
            it.addReaction("👍").queue()
            it.addReaction("👎").queue()

            it.editMessage(EmbedBuilder(it.embeds[0]).apply {
                desc { "Voting has ended! Check the newer messages for results." }
                clearFields()
            }.build()).queueAfter(voteSkipDuration, TimeUnit.MILLISECONDS) {
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