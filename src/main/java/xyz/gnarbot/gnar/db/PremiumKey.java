package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties("expiresBy")
public class PremiumKey extends ManagedObject {
    public enum Type {
        PREMIUM, // GUILD
        USER
    }

    private final long duration;
    private final Type type;
    private Redeemer redeemer;

    @ConstructorProperties({"id", "type", "duration"})
    public PremiumKey(String id, Type type, long duration) {
        super(id, "keys");
        this.type = type;
        this.duration = duration;
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
        return type + "(" + getId() + ", " + duration + ")";
    }
}
