package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties("expiresBy")
public class PremiumKey extends ManagedObject {
    public enum Type {
        PREMIUM, // GUILD
        PREMIUM_OVERRIDE, //Eventual user overrides
        USER
    }

    private final long duration;
    private final Type type;
    private final boolean override;
    private Redeemer redeemer;

    @ConstructorProperties({"id", "type", "duration", "override"})
    public PremiumKey(String id, Type type, long duration, boolean override) {
        super(id, "keys");
        this.type = type;
        this.duration = duration;
        this.override = override;
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isOverride() {
        return override;
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
