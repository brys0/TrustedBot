package de.pheromir.trustedbot.events;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.custom.AliasCommand;
import de.pheromir.trustedbot.commands.custom.CustomCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdListener implements CommandListener {

	@Override
	public void onNonCommandMessage(MessageReceivedEvent e) {
		Message msg = e.getMessage();
		String content = msg.getContentDisplay();
		String contentRaw = msg.getContentRaw();
		if (!(e.getChannelType() == ChannelType.TEXT))
			return;
		GuildConfig gc = Main.getGuildConfig(e.getGuild());
		if (content.startsWith(Main.commandClient.getTextualPrefix())
				|| gc.getPrefixes().stream().anyMatch(s -> content.toLowerCase().startsWith(s.toLowerCase()))) {
			final String cmd;
			if (content.startsWith(Main.commandClient.getTextualPrefix())) {
				cmd = content.substring(Main.commandClient.getTextualPrefix().length(), content.length()).trim().split(" ")[0];
			} else {
				cmd = contentRaw.substring(Main.getGuildConfig(e.getGuild()).getPrefix().length(), contentRaw.length()).split(" ")[0];
			}
			if (gc.getAliasCommands().containsKey(cmd)) {
				if(Main.getGuildConfig(e.getGuild()).isCommandDisabled(cmd)) {
					e.getChannel().sendMessage(Main.COMMAND_DISABLED).complete();
					return;
				}
				AliasCommand ac = gc.getAliasCommands().get(cmd);
				Command command = Main.commandClient.getCommands().stream().filter(cmd1 -> cmd1.isCommandFor(ac.getCommand())).findAny().orElse(null);
				if (command != null) {
					CommandEvent cevent = new CommandEvent(e, ac.getArgs(), Main.commandClient);

					if (Main.commandClient.getListener() != null)
						Main.commandClient.getListener().onCommand(cevent, command);
					command.run(cevent);
					return; // Command is done
				}

			} else if (gc.getCustomCommands().containsKey(cmd)) {
				if(Main.getGuildConfig(e.getGuild()).isCommandDisabled(cmd)) {
					e.getChannel().sendMessage(Main.COMMAND_DISABLED).complete();;
					return;
				}
				CustomCommand cc = gc.getCustomCommands().get(cmd);
				e.getChannel().sendMessage(cc.getResponse()).complete();
				return;
			}

		}

	}

}
