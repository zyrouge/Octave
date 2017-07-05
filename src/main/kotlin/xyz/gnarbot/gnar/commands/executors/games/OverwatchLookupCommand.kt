package xyz.gnarbot.gnar.commands.executors.games

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.HttpUtils
import java.awt.Color
import java.io.IOException

@Command(
        aliases = arrayOf("overwatch", "ow"),
        usage = "(BattleTag#0000) [region]",
        description = "Look up Overwatch information about a player.",
        category = Category.GAMES
)
class OverwatchLookupCommand : CommandExecutor() {
    private val regions = arrayOf("us", "eu", "kr")

    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Insufficient arguments. `${info.usage}`.").queue()
            return
        }

        if (!args[0].matches(Regex("""[a-zA-Z1-9]+[#-]\d+"""))) {
            context.send().error("You did not enter a valid BattleTag `[BattleTag#0000]`.").queue()
            return
        }

        val tag = args[0].replace('#', '-')

        var region: String? = null
        if (args.size > 1) {
            for (r in regions) {
                if (args[1].equals(r, ignoreCase = true)) {
                    region = r
                    break
                }
            }
            if (region == null) {
                context.send().error("Invalid region provided. `[us, eu, kr]`").queue()
                return
            }
        }

        val request = Request.Builder()
                .url("https://owapi.net/api/v3/u/$tag/stats")
                .build()

        HttpUtils.CLIENT.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                context.send().error("Failure to query API.").queue()
            }

            override fun onResponse(call: Call, response: Response) {
                val rjso = JSONObject(JSONTokener(response.body()!!.byteStream()))

                response.close()

                var jso: JSONObject? = null

                // Region arg provided.
                if (region != null) {
                    if (!rjso.has(region)) {
                        context.send().error("Unable to find Overwatch player `" + tag + "` in region `" + region!!.toUpperCase() + "`.").queue()
                        return
                    }

                    jso = rjso.optJSONObject(region)
                } else {
                    for (r in regions) {
                        if (!rjso.has(r)) {
                            continue
                        }

                        jso = rjso.optJSONObject(r)
                        region = r
                        break
                    }

                    if (jso == null || region == null) {
                        context.send().error("Unable to find Overwatch player `$tag`.").queue()
                        return
                    }
                }// Region arg not provided. Search for first non-null region.

                context.send().embed("Overwatch Stats: $tag") {
                    desc {
                        buildString {
                            appendln("Battle Tag: **__[$tag](https://playoverwatch.com/en-gb/career/pc/$region/$tag)__**")
                            appendln("Region: **__[${region!!.toUpperCase()}](http://masteroverwatch.com/leaderboards/pc/$region/mode/ranked/category/skillrating)__**")
                        }
                    }

                    val overall = jso?.optJSONObject("stats")

                    if (overall == null) {
                        context.send().error("Unable to find statistics for Overwatch player`$tag`.").queue()
                        return
                    }

                    var avatar: String? = null
                    var eliminations = 0
                    var medals = 0
                    var dmg_done = 0
                    var cards = 0

                    overall.optJSONObject("quickplay")?.let {
                        it.optJSONObject("overall_stats")?.let {
                            field("General") {
                                buildString {
                                    append("Level: **[${it.optInt("prestige") * 100 + it.optInt("level")}]()**")
                                    avatar = it.optString("avatar")
                                }
                            }
                        }
                        field("Quick Play", true) {
                            it.optJSONObject("average_stats")?.let {
                                buildString {
                                    appendln("Avg. Elims: **[${it.optDouble("eliminations_avg")}]()**")
                                    appendln("Avg. Deaths: **[${it.optDouble("deaths_avg")}]()**")
                                }
                            }

                            it.optJSONObject("overall_stats")?.let {
                                buildString {
                                    appendln("Wins: **[${it.optInt("wins")}]()**")
                                }
                            }

                            it.optJSONObject("game_stats")?.let {
                                eliminations += it.optDouble("eliminations").toInt()
                                medals += it.optDouble("medals").toInt()
                                dmg_done += it.optDouble("damage_done").toInt()
                                cards += it.optDouble("cards").toInt()

                                buildString {
                                    appendln("K/D Ratio: **[${it.optDouble("kpd")}]()**")
                                    appendln("Played for: **[${it.optInt("time_played")} hours]()**")
                                }
                            }
                        }
                    }

                    var rankName: String? = null

                    overall.optJSONObject("competitive")?.let {
                        field("Competitive", true) {

                            buildString {
                                it.optJSONObject("average_stats")?.let {
                                    appendln("Avg. Elims: **[" + it.optDouble("eliminations_avg") + "]()**")
                                    appendln("Avg. Deaths: **[" + it.optDouble("deaths_avg") + "]()**")
                                }

                                it.optJSONObject("game_stats").let {
                                    appendln("Wins/Draws/Loses: **["
                                            + it.optInt("games_won") + "]()** | **["
                                            + it.optInt("games_tied") + "]()** | **["
                                            + it.optInt("games_lost") + "]()**")
                                    appendln("K/D Ratio: **[" + it.optDouble("kpd") + "]()**")
                                    appendln("Played for: **[" + it.optInt("time_played") + " hours]()**")

                                    eliminations += it.optDouble("eliminations").toInt()
                                    medals += it.optDouble("medals").toInt()
                                    dmg_done += it.optDouble("damage_done").toInt()
                                    cards += it.optDouble("cards").toInt()
                                }

                                it.optJSONObject("overall_stats")?.let {
                                    val rank = it.optInt("comprank")

                                    when {
                                        rank < 1500 -> {
                                            color { Color(150, 90, 56) }
                                            rankName = "Bronze"
                                        }
                                        rank in 1500..1999 -> {
                                            color { Color(168, 168, 168) }
                                            rankName = "Silver"
                                        }
                                        rank in 2000..2499 -> {
                                            color { Color(201, 137, 16) }
                                            rankName = "Gold"
                                        }
                                        rank in 2500..2999 -> {
                                            color { Color(229, 228, 226) }
                                            rankName = "Platinum"
                                        }
                                        rank in 3000..3499 -> {
                                            color { Color(63, 125, 255) }
                                            rankName = "Diamond"
                                        }
                                        rank in 3500..3999 -> {
                                            color { Color(255, 184, 12) }
                                            rankName = "Master"
                                        }
                                        rank >= 4000 -> {
                                            color { Color(238, 180, 255) }
                                            rankName = "Grand Master"
                                        }
                                    }

                                    appendln("Comp. Rank: **[:beginner: $rank]() ($rankName)**")
                                }
                            }
                        }
                    }

                    field("Overall", false) {
                        buildString {
                            appendln("Eliminations: **[$eliminations]()**")
                            appendln("Medals: **[$medals]()**")
                            appendln("Total Damage: **[$dmg_done]()**")
                            appendln("Cards: **[$cards]()**")
                        }
                    }

                    thumbnail { avatar }
                }.action().queue()
            }
        })
    }
}
