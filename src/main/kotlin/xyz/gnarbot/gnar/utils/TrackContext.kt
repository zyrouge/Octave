package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel

data class TrackContext(val requester: Member, val requestedChannel: TextChannel)