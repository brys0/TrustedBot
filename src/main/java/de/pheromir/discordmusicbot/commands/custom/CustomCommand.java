package de.pheromir.discordmusicbot.commands.custom;


public class CustomCommand {
	
	private String name;
	private String response;
	private Long guildId;
	
	public CustomCommand(String name, String response, Long guildId) {
		this.name = name.toLowerCase();
		this.response = response;
		this.guildId = guildId;
	}
	
	public CustomCommand(String name, String response, String guildId) {
		this.name = name.toLowerCase();
		this.response = response;
		this.guildId = Long.parseLong(guildId);
	}
	
	public String getName() {
		return name;
	}
	public String getResponse() {
		return response;
	}
	
	public Long getGuildId() {
		return guildId;
	}

}
