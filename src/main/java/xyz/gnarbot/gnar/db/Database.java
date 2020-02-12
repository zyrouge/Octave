package xyz.gnarbot.gnar.db;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.db.guilds.GuildData;
import xyz.gnarbot.gnar.db.guilds.UserData;

import javax.annotation.Nullable;
import java.util.List;

import static com.rethinkdb.RethinkDB.r;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger("Database");
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

    public Connection getConn() {
        return conn;
    }

    public boolean isOpen() {
        return conn != null && conn.isOpen();
    }

    public void close() {
        conn.close();
    }

    @Nullable
    public GuildData getGuildData(String id) {
        return get("guilds_v2", id, GuildData.class);
    }

    @Nullable
    public PremiumKey getPremiumKey(String id) {
        return get("keys", id, PremiumKey.class);
    }

    public UserData getUserData(String id) {
        return get("users", id, UserData.class);
    }

    @Nullable
    public PatreonEntry getPatreonEntry(String id) {
        return get("patreon", id, PatreonEntry.class);
    }

    @Nullable
    public <T> T get(String table, String id, Class<T> cls) {
        return isOpen() ? r.table(table).get(id).run(conn, cls) : null;
    }
}
