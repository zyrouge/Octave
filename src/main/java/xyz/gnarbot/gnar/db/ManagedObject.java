package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.gnarbot.gnar.BotLoader;

import javax.annotation.Nullable;

public abstract class ManagedObject<T> {
    private static final Database db = BotLoader.BOT.db();
    private final String id;
    @JsonIgnore
    private final String table;

    @Nullable
    private final String userID;

    @JsonIgnore
    public ManagedObject(String id, String table) {
        this.id = id;
        this.table = table;
        this.userID = null;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void delete() {
        if (db.isOpen()) db.getGuildData(id, "")
                .delete();
    }
}