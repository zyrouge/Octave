package xyz.gnarbot.gnar.apis.statsposter.websites

class BotsGg(botId: String, auth: String) : Website(
    "BotGG",
    "https://discord.bots.gg/bots/$botId/stats",
    "guildCount",
    auth
)
