package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.MessageEmbed

class EmbedMaker : AbstractEmbedMaker<EmbedMaker> {
    constructor() : super()
    constructor(embed: MessageEmbed) : super(embed)
}
