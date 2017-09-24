package xyz.gnarbot.gnar.commands.executors.games

import net.rithms.riot.api.RiotApiException
import net.rithms.riot.constant.Platform
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.utils.Context
import java.util.*
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description


@Command(
        id = 84,
        aliases = arrayOf("lol", "league", "summoner", "summonerinfo", "si"),
        usage = "_summoner [Region] [Summoner Name]",
        description = "Show information on the given summoner. **NOTICE:** This is a work in progress."
)
class LeagueCommand : CommandTemplate() {
    @Description("List general account information")
    fun account(context: Context, region: Platform, name: String) {
        try {
            val summoner = Bot.getRiotAPI().getSummonerByName(region, name)

            context.send().embed("Summoner Info") {
                thumbnail{ "https://vignette.wikia.nocookie.net/leagueoflegends/images/1/12/League_of_Legends_Icon.png" }
                desc {
                    buildString {
                        append("**Name:** " + summoner.name + "\n")
                        append("**Summoner ID:** " + summoner.id + "\n")
                        append("**Account ID:** " + summoner.accountId + "\n")
                        append("**Level:** " + summoner.summonerLevel + "\n")
                        append("**Icon ID:**" + summoner.profileIconId)
                    }
                }
            }.action().queue()
        } catch (e : NoSuchElementException) {
            e.printStackTrace()
        } catch (e : RiotApiException) {
            e.printStackTrace()
        }
    }

    @Description("Show everyone who you main and how good you are")
    fun main(context: Context, region: Platform, name: String) {
        try {

            val summoner = Bot.getRiotAPI().getSummonerByName(region, name)

            val summonerMasteries = Bot.getRiotAPI().getChampionMasteriesBySummoner(region, summoner.id)

            val championId = summonerMasteries[0].championId

            context.send().embed("$name's Mastery Info") {
                image{ "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + Bot.riotAPI.getDataChampion(region, championId).name + "_0.jpg" }
                desc {
                    buildString {
                        append("**Champion Name:** " + Bot.riotAPI.getDataChampion(region, championId).name + "\n")
                        append("**Mastery Level:** " + summonerMasteries.get(0).championLevel + "\n")
                        append("**Mastery Points:** " + summonerMasteries.get(0).championPoints + "\n")
                        append("**Chest Acquired:** " + summonerMasteries.get(0).isChestGranted + "\n")
                        append("**Points To Next Level:** " + summonerMasteries.get(0).championPointsUntilNextLevel + "\n")
                    }
                }
            }.action().queue()

        } catch (e : Exception) { e.printStackTrace() }
    }

    @Description("Get information on their last game")
    fun last(context: Context, region: Platform, name: String) {
        try {

            val summoner = Bot.getRiotAPI().getSummonerByName(region, name)

            val game = Bot.getRiotAPI().getMatchListByAccountId(region,summoner.accountId)

            val games = game.matches

            if(games == null) { context.send().error("API Exception: Games not found").queue(); }

            context.send().embed("$name's Last Game") {
                thumbnail{ "http://ddragon.leagueoflegends.com/cdn/6.8.1/img/map/map1.png" }
                image { "" }
                desc {
                    buildString {
                        append("**Champion Played:** " + Bot.riotAPI.getDataChampion(region, games[0].champion).name + "\n")
                        append("**Lane Played:** " + games[0].lane + "\n")
                        append("**Role:** " + games[0].role + "\n")
                        append("**Region:** " + games[0].platformId + "\n")
                        append("**Season:** " + games[0].season + "\n")
                        append("**Queue:** " + games[0].queue + "\n")
                    }
                }
            }.action().queue()

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}
