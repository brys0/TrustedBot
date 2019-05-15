package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

public class StatusCommand extends TrustedCommand {

	public StatusCommand() {
		this.name = "status";
		this.help = "Set the status of the bot. (is playing/listening..)";
		this.arguments = "<play|watch|listen|[stream]> [stream-url] <Text>";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		JDA api = e.getJDA();
		if (args.length < 2) {
			e.reply(usage);
			return false;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = args[0].equalsIgnoreCase("stream") ? 2 : 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			String msg = sb.toString().trim();
			switch (args[0].toLowerCase()) {
				case "play":
					api.getPresence().setGame(Game.playing(msg));
					break;
				case "watch":
					api.getPresence().setGame(Game.watching(msg));
					break;
				case "listen":
					api.getPresence().setGame(Game.listening(msg));
					break;
				case "stream":
					api.getPresence().setGame(Game.streaming(msg, args[1]));
					break;
				default:
					e.reply(usage);
					return false;
			}
			e.reactSuccess();
			return true;
		}
	}

}
