//package xyz.gnarbot.gnar.guilds
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties
//import xyz.gnarbot.gnar.Bot
//import xyz.gnarbot.gnar.db.ManagedObject
//import java.beans.ConstructorProperties
//import java.lang.System.currentTimeMillis
//
//@JsonIgnoreProperties("disabledCommands")
//data class GuildOptions @ConstructorProperties("id") constructor(val id: String): ManagedObject {
//    /** Server bot prefix. */
//    var prefix: String = Bot.CONFIG.prefix
//
////    /** Aliases of disabled commands. */
////    var disabledCommands: MutableSet<String> = hashSetOf()
//
//    /** String IDs of ignored channels. */
//    var ignoredChannels: MutableSet<String> = hashSetOf()
//
//    /** String IDs of ignored users. */
//    var ignoredUsers: MutableSet<String> = hashSetOf()
//
//    /** String IDs of ignored roles. */
//    var ignoredRoles: MutableSet<String> = hashSetOf()
//
//    /** String ID of the auto-role. */
//    var autoRole: String? = null
//
//    /** String IDs of the self-assignable roles. */
//    var selfRoles: MutableSet<String> = hashSetOf()
//
//    /** DJ role will be the only ones who can use music and
//     *  bypass all permission requirements for music automatically. */
//    var djRole: String? = null
//
//    /** String ID of the text channel music can be requested in. */
//    var requestChannel: String? = null
//
//    /** String IDs of voice channels that music can be used in. */
//    var musicChannels: MutableSet<String> = hashSetOf()
//
//    var premiumUntil: Long = 0
//
//
//
//    @JsonIgnore
//    fun isPremium() = currentTimeMillis() < premiumUntil
//
//    @JsonIgnore
//    fun remainingPremium(): Long {
//        return if (isPremium()) this.premiumUntil - currentTimeMillis() else 0
//    }
//
//    @JsonIgnore
//    fun addPremium(ms: Long) {
//        when {
//            isPremium() -> this.premiumUntil += ms
//            else -> this.premiumUntil = currentTimeMillis() + ms
//        }
//    }
//
//    override fun save() = Bot.DATABASE.saveGuildOptions(this)
//    override fun delete() = Bot.DATABASE.deleteGuildOptions(id)
//}