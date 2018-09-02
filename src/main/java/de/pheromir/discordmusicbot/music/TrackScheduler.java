package de.pheromir.discordmusicbot.music;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

	private final AudioPlayer player;
	private ArrayList<QueueTrack> queue;
	private boolean repeat;
	private QueueTrack currentTrack;

	/**
	 * @param player
	 *            The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player, Guild g) {
		this.player = player;
		this.queue = new ArrayList<>();
		this.repeat = false;
		this.currentTrack = null;
	}

	public void queue(AudioTrack track, User requestedByMember) {
		if (!player.startTrack(track, true)) {
			queue.add(new QueueTrack(track, requestedByMember));
		} else {
			currentTrack = new QueueTrack(track, requestedByMember);
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			nextTrack();
		}
	}

	public void nextTrack() {
		if (repeat) {
			queue.add(new QueueTrack(currentTrack.getTrack().makeClone(), currentTrack.getRequestor()));
		}
		if (queue.isEmpty()) {
			player.startTrack(null, false);
		} else {
			currentTrack = queue.remove(0);
			player.startTrack(currentTrack.getTrack(), false);
		}
	}

	public User getCurrentRequester() {
		return currentTrack.getRequestor();
	}

	public void setCurrentTrack(QueueTrack track) {
		currentTrack = track;
	}

	public ArrayList<QueueTrack> getRequestedTitles() {
		return queue;
	}

	public boolean skipTrackNr(int index) {
		if (index >= queue.size())
			return false;
		queue.remove(index);
		return true;
	}

	public void clearQueue() {
		this.queue.clear();
	}

	public boolean getRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

}