package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.RandomImageCommand;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;

public class LewdCommand extends RandomImageCommand {

	public LewdCommand() {
		this.name = "lewd";
		this.help = "Shows a random lewd neko picture. (NSFW-Channels only!)";
		this.BASE_URL = "https://nekos.life/api/" + "lewd/" + "neko";
		this.jsonKey = "neko";
		this.creditsCost = 5L;
	}
	
	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		if(e.getChannelType() == ChannelType.TEXT && !((TextChannel)e.getChannel()).isNSFW()) {
			e.reply("This command can be used in NSFW-channels only.");
			return;
		}
		super.execute(e);
	}

}
