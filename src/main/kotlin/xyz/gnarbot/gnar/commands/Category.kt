package xyz.gnarbot.gnar.commands

import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.commands.template.annotations.Name

@Name("category")
@Description("Settings|Music|Fun|Media|General")
enum class Category(val title: String, val show: Boolean = true) {
    SETTINGS("Settings"),
    MUSIC("Music"),
    FUN("Fun"),
    MEDIA("Media"),
    GENERAL("General"),
    NONE("", show = false);

    override fun toString() = title
}