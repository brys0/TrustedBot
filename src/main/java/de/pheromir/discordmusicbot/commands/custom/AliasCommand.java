package de.pheromir.discordmusicbot.commands.custom;

public class AliasCommand {

	private String name;
	private String command;
	private String args;
	private Long guildId;

	public AliasCommand(String name, String command, String args, Long guildId) {
		this.name = name.toLowerCase();
		this.args = args;
		this.command = command;
		this.guildId = guildId;
	}

	public AliasCommand(String name, String command, String args, String guildId) {
		this.name = name.toLowerCase();
		this.command = command;
		this.args = args;
		this.guildId = Long.parseLong(guildId);
	}

	public String getName() {
		return name;
	}

	public String getArgs() {
		return args;
	}

	public Long getGuildId() {
		return guildId;
	}

	public String getCommand() {
		return command;
	}
}
