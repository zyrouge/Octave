package xyz.gnarbot.gnar.commands

enum class Category(val title: String, val show: Boolean = true) {
    MODERATION("Moderation"),
    MUSIC("Music"),
    GAMES("Games"),
    FUN("Shitpost"),
    MEDIA("Media"),
    GENERAL("General"),
    NONE("", show = false),
}