/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot.music;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	private ScheduledExecutorService exServ;
	private Future<?> future;
	private Guild g;

	/**
	 * @param player
	 *            The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player, Guild g) {
		this.player = player;
		this.queue = new ArrayList<>();
		this.repeat = false;
		this.currentTrack = null;
		this.exServ = Executors.newScheduledThreadPool(1);
		this.g = g;
	}

	public void queue(AudioTrack track, User requestedByMember) {
		if (!player.startTrack(track, true)) {
			queue.add(new QueueTrack(track, requestedByMember));
		} else {
			currentTrack = new QueueTrack(track, requestedByMember);
			if (future != null) {
				future.cancel(true);
				future = null;
			}
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (queue.isEmpty() && future == null) {
			future = exServ.schedule(() -> {
				if (player.getPlayingTrack() == null) {
					g.getAudioManager().closeAudioConnection();
				}
			}, 10, TimeUnit.SECONDS);
		}
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
			if (future != null) {
				future.cancel(true);
				future = null;
			}

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
		if (index >= queue.size() || index < 0)
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
