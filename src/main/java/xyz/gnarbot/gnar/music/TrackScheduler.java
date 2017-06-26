package xyz.gnarbot.gnar.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.executors.music.RepeatOption;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    private final MusicManager musicManager;
    private final AudioPlayer player;

    private final Queue<AudioTrack> queue;
    private AudioTrack lastTrack;
    private RepeatOption repeatOption = RepeatOption.NONE;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(MusicManager manager, AudioPlayer player) {
        this.musicManager = manager;
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        if (queue.isEmpty()) {
            Bot.getPlayers().destroy(musicManager.getId());
            return;
        }

        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.mayStartNext) {
            switch (repeatOption) {
                case SONG: {
                    AudioTrack newTrack = lastTrack.makeClone();
                    newTrack.setUserData(lastTrack.getUserData());
                    player.startTrack(newTrack, false);
                    break;
                }
                case QUEUE: {
                    AudioTrack newTrack = lastTrack.makeClone();
                    newTrack.setUserData(lastTrack.getUserData());
                    queue.offer(newTrack);
                }
                case NONE:
                    nextTrack();
            }
        }
    }

    public RepeatOption getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(RepeatOption repeatOption) {
        this.repeatOption = repeatOption;
    }

    public void shuffle() {
        Collections.shuffle((List<?>) queue);
    }

    public AudioTrack getLastTrack() {
        return lastTrack;
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }
}
