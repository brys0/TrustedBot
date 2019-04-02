package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;

public class SeekCommand extends TrustedCommand {

	public SeekCommand() {
		this.name = "seek";
		this.arguments = "<HH:mm:ss>";
		this.aliases = new String[] { "goto" };
		this.help = "Seek the playback time of the current track.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("You need DJ privileges to seek the track.");
			return false;
		}
		long time;
		try {
			time = Methods.parseTimeString(e.getArgs());
		} catch (NumberFormatException ex) {
			e.reply("Syntaxerror. Usage: `HH:mm:ss` or `mm:ss`.");
			return false;
		}
		if (Main.getGuildConfig(e.getGuild()).player.getPlayingTrack() == null) {
			e.reactError();
			return false;
		}
		Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().setPosition(Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration() < time
				? Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration()
				: (time < 0) ? 0 : time);
		e.reactSuccess();
		return true;
	}

}
