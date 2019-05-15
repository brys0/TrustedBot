package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class RewindCommand extends TrustedCommand {

	public RewindCommand() {
		this.name = "rewind";
		this.arguments = "<HH:mm:ss>";
		this.help = "Rewind the current track by the specified time.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (!gc.getDJs().contains(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("You need DJ privileges to rewind the track.");
			return false;
		}

		if (args.length == 0) {
			e.reply(usage);
			return false;
		}

		long time;
		try {
			time = Methods.parseTimeString(e.getArgs());
		} catch (Exception ex) {
			e.reply(usage);
			return false;
		}
		if (gc.player.getPlayingTrack() == null) {
			e.reactError();
			return false;
		}
		time -= gc.player.getPlayingTrack().getDuration();

		gc.player.getPlayingTrack().setPosition(gc.player.getPlayingTrack().getDuration() < time
				? gc.player.getPlayingTrack().getDuration()
				: (time < 0) ? 0 : time);
		e.reactSuccess();
		return true;
	}

}
