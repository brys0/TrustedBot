package de.pheromir.discordmusicbot.helper;

import de.pheromir.discordmusicbot.Methods;

public class YouTubeTitleCache {

	private String id;
	private String title;
	private long duration;
	private String description;
	private String channel;
	private String thumbnailUrl;

	public YouTubeTitleCache(String id, String title, long duration, String description, String channel, String thumbnailUrl) {
		this.id = id;
		this.title = title;
		this.duration = duration;
		this.description = description;
		this.channel = channel;
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getID() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public long getDuration() {
		return duration;
	}

	public String getDescription() {
		return description;
	}

	public String getChannel() {
		return channel;
	}

	public String getDurationString() {
		return Methods.getTimeString(duration);
	}

	public String getThumbnailURL() {
		return thumbnailUrl;
	}

}
