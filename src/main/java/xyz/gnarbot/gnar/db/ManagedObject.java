package xyz.gnarbot.gnar.db;

import xyz.gnarbot.gnar.Bot;

public interface ManagedObject {
    void delete();

	void save();

	default void deleteAsync() {
		Bot.DATABASE.queue(this::delete);
	}

	default void saveAsync() {
		Bot.DATABASE.queue(this::save);
	}
}