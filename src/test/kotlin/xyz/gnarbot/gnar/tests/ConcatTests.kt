package xyz.gnarbot.gnar.tests

fun main(args: Array<String>) {
    val set = setOf(1, 2, 3, 4, null)
    val dank: Int? = null
    println(dank in set)
}