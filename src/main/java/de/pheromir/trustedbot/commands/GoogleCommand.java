package de.pheromir.trustedbot.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class GoogleCommand extends Command {

	private final String BASE_URL = "https://www.google.com/search?q=%s";

	public GoogleCommand() {
		this.name = "google";
		this.aliases = new String[] { "g" };
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Create a google search url with the specified keywords.";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		String url;
		try {
			url = String.format(BASE_URL, URLEncoder.encode(e.getArgs(), "UTF-8"));
			e.reply(url);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			e.reply("Oops, looks like something went wrong.");
		}
	}

}
