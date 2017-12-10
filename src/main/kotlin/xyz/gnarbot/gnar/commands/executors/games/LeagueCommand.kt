package xyz.gnarbot.gnar.commands.executors.games

import net.rithms.riot.api.ApiConfig
import net.rithms.riot.api.RiotApi
import net.rithms.riot.constant.Platform
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.commands.template.annotations.Name

@Command(
        aliases = ["lol", "league", "summoner", "summonerinfo", "si"],
        usage = "_summoner (summoner name) (region)",
        description = "Show information on the given summoner. **NOTICE:** This is a work in progress."
)
@BotInfo(
        id = 84
)
class LeagueCommand : CommandTemplate() {
    private var riotAPI = RiotApi(ApiConfig().apply { Bot.KEYS.riotAPIKey?.let(this::setKey) })

    @Description("List general account information")
    fun account(context: Context,
                @[Name("summoner name") Description("Leauge of Legends username.")] name: String,
                region: Platform) {
        val summoner = riotAPI.getSummonerByName(region, name)

        context.send().embed("Summoner Info") {
            thumbnail {
                "https://vignette.wikia.nocookie.net/leagueoflegends/images/1/12/League_of_Legends_Icon.png"
            }
            desc {
                buildString {
                    append("**Name:** ").append(summoner.name).append('\n')
                    append("**Summoner ID:** ").append(summoner.id).append('\n')
                    append("**Account ID:** ").append(summoner.accountId).append('\n')
                    append("**Level:** ").append(summoner.summonerLevel).append('\n')
                    append("**Icon ID:** ").append(summoner.profileIconId)
                }
            }
        }.action().queue()
    }

    @Description("Show everyone who you main and how good you are.")
    fun main(context: Context,
             @[Name("summoner name") Description("Leauge of Legends username.")] name: String,
             region: Platform) {
        val summoner = riotAPI.getSummonerByName(region, name)

        val summonerMasteries = riotAPI.getChampionMasteriesBySummoner(region, summoner.id)

        val championId = summonerMasteries[0].championId

        context.send().embed("$name's Mastery Info") {
            image {
                "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/${riotAPI.getDataChampion(region, championId).name}_0.jpg"
            }
            desc {
                buildString {
                    append("**Champion Name:** ").append(riotAPI.getDataChampion(region, championId).name).append('\n')
                    append("**Mastery Level:** ").append(summonerMasteries.get(0).championLevel).append('\n')
                    append("**Mastery Points:** ").append(summonerMasteries.get(0).championPoints).append('\n')
                    append("**Chest Acquired:** ").append(summonerMasteries.get(0).isChestGranted).append('\n')
                    append("**Points To Next Level:** ").append(summonerMasteries.get(0).championPointsUntilNextLevel).append('\n')
                }
            }
        }.action().queue()
    }

    @Description("Get information on their last game.")
    fun last(context: Context,
             @[Name("summoner name") Description("Leauge of Legends username.")] name: String,
             region: Platform) {
        val summoner = riotAPI.getSummonerByName(region, name)

        val game = riotAPI.getMatchListByAccountId(region,summoner.accountId)

        val games = game.matches
        if (games == null || games.isEmpty()) {
            context.send().error("API Exception: Games not found.").queue()
            return
        }

        context.send().embed("$name's Last Game") {
            thumbnail{ "http://ddragon.leagueoflegends.com/cdn/6.8.1/img/map/map1.png" }
            desc {
                buildString {
                    append("**Champion Played:** ").append(riotAPI.getDataChampion(region, games[0].champion).name).append('\n')
                    append("**Lane Played:** ").append(games[0].lane).append('\n')
                    append("**Role:** ").append(games[0].role).append('\n')
                    append("**Region:** ").append(games[0].platformId).append('\n')
                    append("**Season:** ").append(games[0].season).append('\n')
                    append("**Queue:** ").append(games[0].queue).append('\n')
                }
            }
        }.action().queue()
    }
}
