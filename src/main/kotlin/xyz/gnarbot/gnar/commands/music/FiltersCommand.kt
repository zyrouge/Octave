package xyz.gnarbot.gnar.commands.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
    aliases = ["filter", "filters", "fx", "effects"],
    description = "Apply filters to the music"
)
@BotInfo(
    id = 666,
    category = Category.MUSIC,
    scope = Scope.VOICE,
    djLock = true
)

class FiltersCommand : MusicCommandExecutor(true, true, true) {
    private val filters = listOf("tremolo", "timescale", "karaoke")
    private val filterString = filters.joinToString("`, `", prefix = "`", postfix = "`")

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        when (args.firstOrNull()) {
            "timescale" -> modifyTimescale(context, label, args.drop(1), manager)
            "tremolo" -> modifyTremolo(context, label, args.drop(1), manager)
            "karaoke" -> modifyKaraoke(context, label, args.drop(1), manager)
            "status" -> context.send().info("https://serux.pro/355ec8e43e.png").queue()
            else -> context.send().info("Invalid filter. Pick one of $filterString, or `status` to view filter status.").queue()
        }

        // TODO: Provide min/max values for each property.
    }

    fun modifyTimescale(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            return ctx.send().info("`${ctx.data.command.prefix}${label} timescale <pitch/speed/rate> <value>").queue()
        }

        if (args.size < 2 || args[1].toDoubleOrNull() == null) {
            return ctx.send().info("You need to specify a number for `${args[0].toLowerCase()}`").queue()
        }

        when (args[0]) {
            //"pitch" -> manager.dspFilter.tsPitch = args[1].toDouble()
            //"speed" -> manager.dspFilter.tsSpeed = args[1].toDouble()
            //"rate" -> manager.dspFilter.tsRate = args[1].toDouble()
            else -> ctx.send().info("Invalid choice `${args[0]}`, pick one of `pitch`/`speed`/`rate`.")
        }

        // TO DO: MIN/MAX
    }

    fun modifyTremolo(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            return ctx.send().info("`${ctx.data.command.prefix}${label} tremolo <depth/frequency> <value>").queue()
        }

        if (args.size < 2 || args[1].toFloatOrNull() == null) {
            return ctx.send().info("You need to specify a number for `${args[0].toLowerCase()}`").queue()
        }

        when (args[0]) {
            //"depth" -> manager.dspFilter.tDepth = args[1].toFloat()
            //"frequency" -> manager.dspFilter.tFrequency = args[1].toFloat()
            else -> ctx.send().info("Invalid choice `${args[0]}`, pick one of `depth`/`frequency`.")
        }
    }

    fun modifyKaraoke(ctx: Context, label: String, args: List<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            return ctx.send().info("`${ctx.data.command.prefix}${label} karaoke <level/band/width> <value>").queue()
        }

        if (args.size < 2 || args[1].toFloatOrNull() == null) {
            return ctx.send().info("You need to specify a number for `${args[0].toLowerCase()}`").queue()
        }

        when (args[0]) {
            //"level" -> manager.dspFilter.kLevel = args[1].toFloat()
            //"band" -> manager.dspFilter.kFilterBand = args[1].toFloat()
            //"width" -> manager.dspFilter.kFilterWidth = args[1].toFloat()
            else -> ctx.send().info("Invalid choice `${args[0]}`, pick one of `level`/`band`/`width`.")
        }
    }
}