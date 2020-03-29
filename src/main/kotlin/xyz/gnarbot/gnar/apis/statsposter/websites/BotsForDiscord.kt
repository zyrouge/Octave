package xyz.gnarbot.gnar.apis.statsposter.websites

class BotsForDiscord(botId: String, auth: String) : Website(
    "BotsForDiscord",
    "https://botsfordiscord.com/api/bot/$botId",
    "server_count",
    auth
)
