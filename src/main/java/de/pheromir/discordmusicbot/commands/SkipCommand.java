package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.handler.GuildMusicManager;

public class SkipCommand extends Command {

	public SkipCommand() {
		this.name = "skip";
		this.help = "Musiktrack überspringen";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		GuildMusicManager m = Main.getGuildAudioPlayer(e.getGuild());
		
		if(e.getArgs().isEmpty()) {
			m.scheduler.nextTrack();
			e.reactSuccess();
		} else {
			int index = Integer.MAX_VALUE;
			try {
				index = Integer.parseInt(e.getArgs())-1;
			} catch (NumberFormatException ex) {
				e.reply("Bitte eine gültige Zahl angeben.");
				return;
			}
			if(index >= m.scheduler.getRequestedTitles().size()) {
				e.reply("Bitte eine gültige Zahl angeben.");
			}
			if (!Main.djs.contains(e.getAuthor().getId()) && !e.isOwner() && m.scheduler.getRequestedTitles().get(index).getRequestor() != e.getAuthor()) {
				e.reply("Du kannst nur Songs skippen, die du selbst hinzugefügt hast.");
				return;
			}
			String title = m.scheduler.getRequestedTitles().get(index).getTrack().getInfo().title;
			if(m.scheduler.skipTrackNr(index)) {
				e.reply("`"+title+"` wurde übersprungen.");
			} else {
				e.reply("Hoppala, da ist wohl etwas schiefgegangen");
			}
		}
	}

}
