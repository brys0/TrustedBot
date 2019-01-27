package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;

public class DJRemoveCommand extends Command {

	public DJRemoveCommand() {
		this.name = "djremove";
		this.help = "Remove DJ permissions of the specified user.";
		this.arguments = "<User-Mention>";
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		Pattern p = Pattern.compile("(\\d+)");
		for (String arg : args) {
			Matcher m = p.matcher(arg);
			if (m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if (mem == null) {
					e.reply("The specified user couldn't be found.");
					continue;
				}
				if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " is not a DJ.");
					continue;
				} else {
					e.reply(mem.getAsMention() + " is no longer a DJ.");
					Main.getGuildConfig(e.getGuild()).removeDJ(Long.parseLong(id));
					continue;
				}
			} else {
				e.reply("The specified user couldn't be found.");
				continue;
			}
		}
	}
}
