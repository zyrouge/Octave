package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.MessageEmbed

class KEmbedBuilder : AbstractEmbedBuilder<KEmbedBuilder> {
    constructor() : super()
    constructor(embed: MessageEmbed) : super(embed)
}
