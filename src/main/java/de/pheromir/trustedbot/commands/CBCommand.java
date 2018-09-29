package de.pheromir.trustedbot.commands;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class CBCommand extends Command {

	public CBCommand() {
		this.name = "cb";
		this.help = "Chaturbatebenachrichtigungen für ein Benutzer im Channel de-/aktivieren.";
		this.arguments = "<Username>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.hidden = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			ArrayList<String> streams = new ArrayList<>();
			for (String str : GuildConfig.getCBList().keySet()) {
				if (GuildConfig.getCBList().get(str).contains(e.getChannel().getIdLong())) {
					streams.add(str);
				}
			}
			if (streams.isEmpty()) {
				e.reply("In diesem Channel sind momenten keine CB-Benachrichtigungen aktiv.");
				return;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : streams) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("In diesem Channel sind momentan CB-Benachrichtigung für folgende Benutzer aktiv: " + msg);
				return;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxfehler. Verwendung: `!" + this.name + " <Username>`");
			return;
		} else {
			if (GuildConfig.getCBList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getCBList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeCBStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("CB-Benachrichtigungen für " + e.getArgs().toLowerCase()
						+ " sind in diesem Channel nun deaktiviert.");
			} else {
				if (!Methods.doesCBUserExist(e.getArgs())) {
					e.reply("Es scheint keinen Benutzer mit diesem Namen zu geben (oder es ist ein Fehler aufgetreten).");
					return;
				}
				GuildConfig.addCBStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
				e.reply("CB-Benachrichtigungen für " + e.getArgs().toLowerCase()
						+ " sind in diesem Channel nun aktiviert.");
			}
			return;
		}
	}

}
