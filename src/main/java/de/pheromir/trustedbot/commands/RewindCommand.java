package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import net.dv8tion.jda.core.Permission;

public class RewindCommand extends Command {

	public RewindCommand() {
		this.name = "rewind";
		this.help = "Aktuellen Titel zurückspulen";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du musst mind. DJ sein um die Wiedergabestelle ändern zu können.");
			return;
		}
		long time;
		try {
			time = Methods.parseTimeString(e.getArgs());
		} catch (NumberFormatException ex) {
			e.reply("Ungültige Angabe. Syntax: `HH:mm:ss` oder `mm:ss`.");
			return;
		}
		if (Main.getGuildConfig(e.getGuild()).player.getPlayingTrack() == null) {
			e.reactError();
			return;
		}
		time -= Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration();
		
		Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().setPosition(Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration() < time
			? Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration()
			: (time < 0) ? 0 : time);
		e.reactSuccess();

	}

}
