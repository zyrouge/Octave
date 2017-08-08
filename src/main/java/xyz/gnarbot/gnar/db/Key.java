package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import xyz.gnarbot.gnar.Bot;

import java.beans.ConstructorProperties;

public class Key implements ManagedObject {
    public enum Type {
        PREMIUM
    }

    private final String id;
    private final long duration;
    private final Type type;
    private Redeemer redeemer;

    @JsonIgnoreProperties("expiresBy")
    @ConstructorProperties({"id", "type", "duration"})
    public Key(String id, Type type, long duration) {
        this(id, type, duration, null);
    }

    @JsonIgnoreProperties("expiresBy")
    @ConstructorProperties({"id", "type", "duration", "redeemer"})
    public Key(String id, Type type, long duration, Redeemer redeemer) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.redeemer = redeemer;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public void setRedeemer(Redeemer redeemer) {
        this.redeemer = redeemer;
    }

    public Redeemer getRedeemer() {
        return redeemer;
    }

    @Override
    public String toString() {
        return type + "(" + id + ", " + duration + ")";
    }

    @Override
    public void save() {
        Bot.db().savePremiumKey(this);
    }

    @Override
    public void delete() {
        Bot.db().deleteKey(id);
    }
}
