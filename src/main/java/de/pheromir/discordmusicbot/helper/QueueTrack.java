package de.pheromir.discordmusicbot.helper;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.User;

public class QueueTrack {

	private AudioTrack track;
	private User requestor;
	
	public QueueTrack(AudioTrack track, User requestor) {
		this.track = track;
		this.requestor = requestor;
	}
	
	public AudioTrack getTrack() {
		return this.track;
	}
	
	public User getRequestor() {
		return this.requestor;
	}
	
}
