package xyz.gnarbot.gnar.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.guilds.GuildData;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger("Database");
    private final Connection conn;
    private final Bot bot; //Simply here for referencing data we may need

    public Database(Connection conn, Bot bot) {
        this.conn = conn;
        this.bot = bot;
    }

    public Connection getConn() {
        return conn;
    }

    public boolean isOpen() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public GuildData getGuildData(String id, String dataType) {
        return (GuildData) get(id, dataType);
    }

    @Nullable
    public Object get(String id, String columnName) {
        try {
            return isOpen() ? bot.getDb().selectGuildData(Integer.valueOf(id)).getObject("") : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
