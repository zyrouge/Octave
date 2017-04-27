package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.utils.Context;

public enum Scope {
    GUILD {
        @Override
        public boolean checkPermission(Context context, Permission... permissions) {
            return context.getMember().hasPermission(permissions);
        }
    },
    TEXT {
        @Override
        public boolean checkPermission(Context context, Permission... permissions) {
            return context.getMember().hasPermission(context.getMessage().getTextChannel(), permissions);
        }
    },
    VOICE {
        @Override
        public boolean checkPermission(Context context, Permission... permissions) {
            return context.getMember().hasPermission(context.getMember().getVoiceState().getChannel(), permissions);
        }
    };

    abstract public boolean checkPermission(Context context, Permission... permissions);
}
