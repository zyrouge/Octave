package xyz.gnarbot.gnar.tests

import xyz.gnarbot.gnar.commands.template.Parser

fun main(args: Array<String>) {
    println(Parser.DURATION.parse(null, "3:20:15")?.seconds)
}