package xyz.gnarbot.gnar.tests

import java.util.*

fun main(args: Array<String>) {
    val set = emptySet<Nothing>()
    val set2 = emptySet<Nothing>()
    println(Collections.disjoint(set, set2))

}