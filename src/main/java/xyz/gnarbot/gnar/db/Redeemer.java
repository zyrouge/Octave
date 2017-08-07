package xyz.gnarbot.gnar.db;

public class Redeemer {
    public enum Type {
        GUILD,
        USER
    }

    private final Type type;
    private final String id;

    public Redeemer(Type type, String id) {
        this.type = type;
        this.id = id;
    }
}
