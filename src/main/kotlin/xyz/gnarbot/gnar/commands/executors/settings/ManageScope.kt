package xyz.gnarbot.gnar.commands.executors.settings

import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions

enum class ManageScope {
    USER {
        override fun empty(): String {
            return "The allowed members list is empty, all members are able to use the command."
        }

        override fun notIn(name: String, target: String): String {
            return "$name is not in the allowed users list for `$target`."
        }

        override fun disallowSuccess(name: String, target: String): String {
            return "Removed $name from the allowed users list for `$target`."
        }

        override fun alreadyAdded(name: String, target: String): String {
            return "$name is already in the allowed users list for `$target`."
        }

        override fun allowSuccess(name: String, target: String): String {
            return "Added $name to the allowed users list for `$target`."
        }

        override fun transform(map: Map<Int, CommandOptions>, id: Int): MutableSet<String> {
            return map[id]!!.allowedUsers
        }
    },
    ROLE {
        override fun empty(): String {
            return "The allowed roles list is empty, all roles are able to use the command."
        }

        override fun notIn(name: String, target: String): String {
            return "$name is not in the allowed roles list for `$target`."
        }

        override fun disallowSuccess(name: String, target: String): String {
            return "Removed $name from the allowed roles list for `$target`."
        }

        override fun alreadyAdded(name: String, target: String): String {
            return "$name is already in the allowed roles list for `$target`."
        }

        override fun allowSuccess(name: String, target: String): String {
            return "Added $name to the allowed roles list for `$target`."
        }

        override fun transform(map: Map<Int, CommandOptions>, id: Int): MutableSet<String> {
            return map[id]!!.allowedRoles
        }
    },
    CHANNEL {
        override fun empty(): String {
            return "The allowed channels list is empty, all channels are able to use the command."
        }

        override fun notIn(name: String, target: String): String {
            return "$name is not in the allowed channels list for `$target`."
        }

        override fun disallowSuccess(name: String, target: String): String {
            return "Removed $name from the allowed channels list for `$target`."
        }

        override fun alreadyAdded(name: String, target: String): String {
            return "$name is already in the allowed channels list for `$target`."
        }

        override fun allowSuccess(name: String, target: String): String {
            return "Added $name to the allowed channels list for `$target`."
        }

        override fun transform(map: Map<Int, CommandOptions>, id: Int): MutableSet<String> {
            return map[id]!!.allowedChannels
        }
    };

    abstract fun empty(): String

    abstract fun notIn(name: String, target: String): String

    abstract fun disallowSuccess(name: String, target: String): String

    abstract fun alreadyAdded(name: String, target: String): String

    abstract fun allowSuccess(name: String, target: String): String

    abstract fun transform(map: Map<Int, CommandOptions>, id: Int): MutableSet<String>
}