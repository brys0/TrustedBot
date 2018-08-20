package de.pheromir.discordmusicbot.music;

public class Suggestion {

	private String title;
	private String id;

	public Suggestion(String title, String id) {
		this.title = title;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

}
