package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class PrefixCommand extends Command {

	public PrefixCommand() {
		this.name = "prefix";
		this.aliases = new String[] { "präfix" };
		this.help = "Edit the prefix of the commands for this guild.";
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.guildOnly = true;
		this.category = new Category("Settings");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		if (e.getArgs().isEmpty()) {
			e.reply("Current prefix: " + Main.getGuildConfig(e.getGuild()).getPrefix());
			return;
		}

		Main.getGuildConfig(e.getGuild()).setPrefix(e.getArgs());
		e.reply("The prefix has been set to `" + e.getArgs() + "`.");

	}

}
