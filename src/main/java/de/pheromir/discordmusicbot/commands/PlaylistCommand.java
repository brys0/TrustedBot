package de.pheromir.discordmusicbot.commands;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.helper.GuildConfig;
import de.pheromir.discordmusicbot.helper.QueueTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

public class PlaylistCommand extends Command {

	public PlaylistCommand() {
		this.name = "playlist";
		this.aliases = new String[] { "q", "queue" };
		this.help = "";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;

		GuildConfig musicManager = Main.getGuildConfig(e.getGuild());
		ArrayList<QueueTrack> titles = musicManager.scheduler.getRequestedTitles();

		if (e.getArgs().equalsIgnoreCase("clear")) {
			if (Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
					|| e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				musicManager.scheduler.clearQueue();
				e.reactSuccess();
				return;
			}
		}
		
		if (e.getArgs().equalsIgnoreCase("repeat")) {
			if (Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
					|| e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				musicManager.scheduler.setRepeat(!musicManager.scheduler.getRepeat());
				e.reply(musicManager.scheduler.getRepeat() ? "Die Warteschlange wiederholt sich nun."
						: "Die Warteschlange wiederholt sich nicht mehr.");
				return;
			}
		}

		MessageBuilder mes = new MessageBuilder();
		int amnt = titles.size()>10?10:titles.size();
		
		mes.append("**Warteschlange** ("+(amnt>9?"Nächste 10 Titel)":amnt+" Titel)"));
		EmbedBuilder m = new EmbedBuilder();
		m.setTitle("Wiederholung der Warteschlange: " + (musicManager.scheduler.getRepeat() ? "an" : "aus"));
		m.setColor(e.getGuild().getSelfMember().getColor());
		m.appendDescription("Derzeit läuft: " + (musicManager.player.getPlayingTrack() == null ? "nichts"
				: musicManager.player.getPlayingTrack().getInfo().title + " ["
						+ Methods.getTimeString(musicManager.player.getPlayingTrack().getInfo().length)
						+ "], *hinzugefügt von "
						+ (e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()) != null
								? (e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()).getNickname() != null
										? e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()).getNickname()
										: musicManager.scheduler.getCurrentRequester().getName())
								: musicManager.scheduler.getCurrentRequester().getName())
						+ "*")
				+ "\n\n");

		if (titles.size() == 0) {
			m.appendDescription("Es sind keine weiteren Titel eingereiht.");
		}
		for (int i = 0; i < amnt; i++) {
			m.appendDescription("**[" + (i + 1) + "]** " + titles.get(i).getTrack().getInfo().title
				+ " [" + Methods.getTimeString(titles.get(i).getTrack().getDuration())
				+ "], *hinzugefügt von "
					+ (e.getGuild().getMember(titles.get(i).getRequestor()) != null
							? (e.getGuild().getMember(titles.get(i).getRequestor()).getNickname() != null
									? e.getGuild().getMember(titles.get(i).getRequestor()).getNickname()
									: titles.get(i).getRequestor().getName())
							: titles.get(i).getRequestor().getName())
					+ "*\n\n");
		}
		m.setFooter("Bestimmten Titel überspringen: !skip [Nr]", e.getJDA().getSelfUser().getAvatarUrl());
		mes.setEmbed(m.build());
		e.reply(mes.build());
	}
}
