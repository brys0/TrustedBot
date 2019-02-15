package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;

public class ForwardCommand extends TrustedCommand {

	public ForwardCommand() {
		this.name = "forward";
		this.arguments = "<HH:mm:ss>";
		this.help = "Fast-forward the current track by the specified time.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void exec(CommandEvent e) {
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("You need DJ privileges to fast-forward the track.");
			return;
		}
		long time;
		try {
			time = Methods.parseTimeString(e.getArgs());
		} catch (NumberFormatException ex) {
			e.reply("Syntaxerror. Usage: `HH:mm:ss` or `mm:ss`.");
			return;
		}
		if (Main.getGuildConfig(e.getGuild()).player.getPlayingTrack() == null) {
			e.reactError();
			return;
		}
		
		time += Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration();
		Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().setPosition(Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration() < time
			? Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration()
			: (time < 0) ? 0 : time);
		e.reactSuccess();

	}

}
