package xyz.gnarbot.gnar.commands

enum class Category(val title: String, val show: Boolean = true) {
    SETTINGS("Settings"),
    MUSIC("Music"),
    FUN("Shitpost"),
    MEDIA("Media"),
    GENERAL("General"),
    NONE("", show = false),
}