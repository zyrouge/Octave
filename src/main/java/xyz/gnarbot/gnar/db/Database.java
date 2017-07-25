package xyz.gnarbot.gnar.db;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.guilds.GuildOptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.rethinkdb.RethinkDB.r;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger("Database");
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final Connection conn;

    public Database(String name) {
        Connection conn = null;
        try {
            Connection.Builder builder = r.connection().hostname("localhost").port(28015);
            // potential spot for authentication
            conn = builder.connect();
            if (r.dbList().<List<String>>run(conn).contains(name)) {
                LOG.info("Connected to database.");
                conn.use(name);
            } else {
                LOG.info("Rethink Database `" + name + "` is not present. Closing connection.");
                close();
                System.exit(0);
            }
        } catch (ReqlDriverError e) {
            LOG.error("Rethink Database connection failed.", e);
            System.exit(0);
        }
        this.conn = conn;
    }

    public boolean isOpen() {
        return conn != null && conn.isOpen();
    }

    public void close() {
        conn.close();
    }

//    public void save() {
//        LOG.info("Saving to database.");
//        TLongObjectIterator<GuildData> iter = Bot.getGuildDataMap().iterator();
//        while (iter.hasNext()) {
//            iter.advance();
//            GuildData gd = iter.value();
//            gd.save();
//            if (gd.getMusicManager().getPlayer().getPlayingTrack() == null) {
//                iter.remove();
//            }
//        }
//    }

    @Nullable
    public GuildOptions getGuildOptions(String id) {
        return isOpen() ? r.table("guilds").get(id).run(conn, GuildOptions.class) : null;
    }

    public void saveGuildOptions(GuildOptions guildData) {
        if (isOpen()) r.table("guilds").insert(guildData)
                .optArg("conflict", "replace")
                .runNoReply(conn);
    }

    public void deleteGuildOptions(String id) {
        if (isOpen()) r.table("guilds").get(id)
                .delete()
                .runNoReply(conn);
    }

    @Nullable
    public Key getPremiumKey(String id) {
        return isOpen() ? r.table("keys").get(id).run(conn, Key.class) : null;
    }

    public void savePremiumKey(Key key) {
        if (isOpen()) r.table("keys").insert(key)
                .runNoReply(conn);
    }

    public void deleteKey(String id) {
        if (isOpen()) r.table("keys").get(id)
                .delete()
                .runNoReply(conn);
    }

    public ScheduledExecutorService getExecutor() {
        return exec;
    }
}
