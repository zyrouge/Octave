package xyz.gnarbot.gnar.commands.executors.`fun`

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.util.*

@Command(aliases = arrayOf("dialog"),
        usage = "(words...)",
        description = "Make some of that Windows ASCII art!")
class DialogCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val lines = WordUtils
                .wrap(StringUtils.join(args, ' ').replace("```", ""), 25, null, true)
                .split("\n")

        context.send().embed {
            color = context.bot.config.accentColor
            description = buildString {
                appendln("```")
                appendln("﻿ ___________________________ ")
                appendln("| Window          [_][☐][✕]|")
                appendln("|‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾|")

                lines.map(String::trim)
                        .map {
                            it + buildString {
                                kotlin.repeat(25 - it.length) { append(' ') }
                            }
                        }
                        .map { "| $it |" }
                        .forEach { appendln(it) }

                when (Random().nextInt(5)) {
                    0 -> {
                        appendln("|   _________    ________   |")
                        appendln("|  |   Yes   |  |   No   |  |")
                        appendln("|   ‾‾‾‾‾‾‾‾‾    ‾‾‾‾‾‾‾‾   |")
                    }
                    1 -> {
                        appendln("|  _____    ______    ____  |")
                        appendln("| | Yes |  | Help |  | No | |")
                        appendln("|  ‾‾‾‾‾    ‾‾‾‾‾‾    ‾‾‾‾  |")
                    }
                    2 -> {
                        appendln("|   _________    ________   |")
                        appendln("|  |  Maybe  |  |( ͡° ͜ʖ ͡°)|  |")
                        appendln("|   ‾‾‾‾‾‾‾‾‾    ‾‾‾‾‾‾‾‾   |")
                    }
                    3 -> {
                        appendln("|   _____________________   |")
                        appendln("|  |     Confirm     | X |  |")
                        appendln("|   ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾   |")
                    }
                    4 -> {
                        appendln("|   ___________   _______   |")
                        appendln("|  | HELLA YES | | PUSSY |  |")
                        appendln("|   ‾‾‾‾‾‾‾‾‾‾‾   ‾‾‾‾‾‾‾   |")
                    }
                }

                appendln(" ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾ ")
                appendln("```")
            }
        }.action().queue()
    }
}