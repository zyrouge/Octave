package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import xyz.gnarbot.gnar.Bot;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties("expiresBy")
public class PremiumKey implements ManagedObject {
    public enum Type {
        PREMIUM, // GUILD
        USER
    }

    private final String id;
    private final long duration;
    private final Type type;

    private Redeemer redeemer;

    @ConstructorProperties({"id", "type", "duration"})
    public PremiumKey(String id, Type type, long duration) {
        this.id = id;
        this.type = type;
        this.duration = duration;
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
        System.out.println("saving key");
        Bot.db().savePremiumKey(this);
    }

    @Override
    public void delete() {
        Bot.db().deleteKey(id);
    }
}
