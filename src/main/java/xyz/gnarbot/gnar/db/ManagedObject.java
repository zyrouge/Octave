package xyz.gnarbot.gnar.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ManagedObject {
    @JsonIgnore
    void delete();

    @JsonIgnore
	void save();
}