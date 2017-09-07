package xyz.gnarbot.gnar.tests

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Executors.newSingleThreadScheduledExecutor().schedule({
        println("lol")
    }, 5000, TimeUnit.SECONDS)
}