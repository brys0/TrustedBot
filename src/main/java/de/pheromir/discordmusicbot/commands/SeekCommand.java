package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import net.dv8tion.jda.core.Permission;

public class SeekCommand extends Command {

	public SeekCommand() {
		this.name = "seek";
		this.help = "Musikwiedergabe stoppen";
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
		Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().setPosition(Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration() < time
				? Main.getGuildConfig(e.getGuild()).player.getPlayingTrack().getDuration()
				: time);
		e.reactSuccess();

	}

}
