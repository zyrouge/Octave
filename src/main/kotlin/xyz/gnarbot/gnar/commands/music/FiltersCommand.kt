package xyz.gnarbot.gnar.commands.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.BoostSetting
import xyz.gnarbot.gnar.music.MusicManager

@Command(
    aliases = ["filter", "filters", "fx", "effects"],
    description = "Apply audio filters to the music such as speed and pitch"
)
@BotInfo(
    id = 666,
    category = Category.MUSIC,
    scope = Scope.VOICE,
    djLock = true
)

class FiltersCommand : MusicCommandExecutor(true, true, true) {
    private val filters = mapOf(
        //"tremolo" to ::modifyTremolo,
        "timescale" to ::modifyTimescale
        //"karaoke" to ::modifyKaraoke
    )
    private val filterString = filters.keys.joinToString("`, `", prefix = "`", postfix = "`")

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val filter = args.firstOrNull()
            ?: return context.send().info("Invalid filter. Pick one of $filterString, or `status` to view filter status.").queue()

        if (filter == "status") {
            return status(context, manager)
        }

        filters[filter]?.invoke(context, label, args.drop(1), manager)
            ?: return context.send().info("Invalid filter. Pick one of $filterString, or `status` to view filter status.").queue()
    }

    fun status(ctx: Context, manager: MusicManager) {
        val karaokeStatus = "Disabled"
        val tremoloStatus = "Disabled"
        val timescaleStatus = if (manager.dspFilter.timescaleEnable) "Enabled" else "Disabled"

        ctx.send().embed("Music Effects") {
            field("Karaoke", true) { karaokeStatus }
            field("Timescale", true) { timescaleStatus }
            field("Tremolo", true) { tremoloStatus }
            setFooter("Due to how effects work, only one can be applied at a time.")
        }.action().queue()
    }

    fun modifyTimescale(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            return ctx.send().info("`${ctx.bot.configuration.prefix}${label} timescale <pitch/speed/rate> <value>`").queue()
        }

        val value = args.getOrNull(1)?.toDoubleOrNull()?.coerceIn(0.1, 3.0)
            ?: return ctx.send().info("`pitch`/`speed`/`rate` `<number>`").queue()

        when (args[0]) {
            "pitch" -> manager.dspFilter.tsPitch = value
            "speed" -> manager.dspFilter.tsSpeed = value
            "rate" -> manager.dspFilter.tsRate = value
            else -> return ctx.send().info("Invalid choice `${args[0]}`, pick one of `pitch`/`speed`/`rate`.").queue()
        }

        ctx.send().info("Timescale `${args[0].toLowerCase()}` set to `$value`").queue()
    }
//
//    fun modifyTremolo(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
//        if (args.isEmpty()) {
//            return ctx.send().info("`${ctx.bot.configuration.prefix}$label tremolo <depth/frequency> <value>`").queue()
//        }
//
//        if (args.size < 2 || args[1].toFloatOrNull() == null) {
//            return ctx.send().info("You need to specify a number for `${args[0].toLowerCase()}`").queue()
//        }
//
//        when (args[0]) {
//            "depth" -> manager.dspFilter.tDepth = args[1].toFloat()
//            "frequency" -> manager.dspFilter.tFrequency = args[1].toFloat()
//            else -> ctx.send().info("Invalid choice `${args[0]}`, pick one of `depth`/`frequency`.").queue()
//        }
//    }
//
//    fun modifyKaraoke(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
//        if (args.isEmpty()) {
//            return ctx.send().info("`${ctx.bot.configuration.prefix}${label} karaoke <level/band/width> <value>`").queue()
//        }
//
//        if (args.size < 2 || args[1].toFloatOrNull() == null) {
//            return ctx.send().info("You need to specify a number for `${args[0].toLowerCase()}`").queue()
//        }
//
//        when (args[0]) {
//            "level" -> manager.dspFilter.kLevel = args[1].toFloat()
//            "band" -> manager.dspFilter.kFilterBand = args[1].toFloat()
//            "width" -> manager.dspFilter.kFilterWidth = args[1].toFloat()
//            else -> ctx.send().info("Invalid choice `${args[0]}`, pick one of `level`/`band`/`width`.").queue()
//        }
//    }
}
