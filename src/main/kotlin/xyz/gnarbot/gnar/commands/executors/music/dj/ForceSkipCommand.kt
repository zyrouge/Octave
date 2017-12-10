//package xyz.gnarbot.gnar.commands.executors.music.dj
//
//import xyz.gnarbot.gnar.commands.*
//import xyz.gnarbot.gnar.commands.executors.music.MusicCommandExecutor
//import xyz.gnarbot.gnar.music.MusicManager
//
//@Command(
//        aliases = ["forceskip"],
//        description = "Skip the current music track forcefully."
//)
//@BotInfo(
//        id = 60,
//        category = Category.MUSIC,
//        scope = Scope.VOICE,
//        roleRequirement = "DJ"
//)
//class ForceSkipCommand : MusicCommandExecutor(false, false) {
//    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
//        context.send().error("Deprecated, use `_skip` instead.").queue()
//        return
////        manager.scheduler.nextTrack()
////
////        context.send().embed("Skip Current Track") {
////            desc { "The track was skipped." }
////        }.action().queue()
//    }
//}
