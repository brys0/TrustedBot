package de.pheromir.discordmusicbot.handler;

import java.util.ArrayList;
import java.util.Timer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import de.pheromir.discordmusicbot.music.QueueTrack;
import de.pheromir.discordmusicbot.tasks.LeaveChannelTimer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

	private final AudioPlayer player;
	// private ArrayList<AudioTrack> queue;
	// private ArrayList<User> requestedBy;
	private ArrayList<QueueTrack> queue;
	// private User currentRequestor;
	private Timer timer;
	private Guild g;
	private boolean repeat;
	private QueueTrack currentTrack;

	/**
	 * @param player
	 *            The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player, Guild g) {
		this.player = player;
		this.queue = new ArrayList<>();
		// this.requestedBy = new ArrayList<>();
		// this.currentRequestor = null;
		this.timer = new Timer();
		this.g = g;
		this.repeat = false;
		this.currentTrack = null;
	}

	public void queue(AudioTrack track, User requestedByMember) {
		if (!player.startTrack(track, true)) {
			queue.add(new QueueTrack(track, requestedByMember));
		} else {
			currentTrack = new QueueTrack(track, requestedByMember);
			cancelAndRenewTimer();
		}
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		timer.schedule(new LeaveChannelTimer(this.g), (15 * 60 * 1000));
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		cancelAndRenewTimer();
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		timer.schedule(new LeaveChannelTimer(this.g), (15 * 60 * 1000));
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
			cancelAndRenewTimer();
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

	public void cancelAndRenewTimer() {
		this.timer.cancel();
		this.timer = new Timer();
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