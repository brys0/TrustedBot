package de.pheromir.discordmusicbot.helper;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

import de.pheromir.discordmusicbot.commands.R6Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public class R6StatsSender extends TimerTask {

	private MessageChannel c;
	private String args;

	public R6StatsSender(MessageChannel c, String args) {
		this.c = c;
		this.args = args;
	}

	@Override
	public void run() {
		RainbowSixStats stats = null;
		if (!R6Command.statsCache.containsKey(args.toLowerCase())
				|| R6Command.statsCache.get(args.toLowerCase()).getValidUntil() < System.currentTimeMillis()) {
			c.sendMessage("Frage Statistik ab..\nDies kann einen Moment dauern.").complete();
			try {
				stats = new RainbowSixStats(args);
			} catch (NullPointerException | IOException e) {
				if (e instanceof FileNotFoundException) {
					c.sendMessage("Der Spieler scheint nicht zu existieren.").complete();
				} else {
					e.printStackTrace();
					c.sendMessage("Die Statistik für den Spieler " + args
							+ " konnte nicht abgerufen werden.").complete();
				}
				return;
			}
			R6Command.statsCache.put(args.toLowerCase(), stats);
		} else {
			LocalDateTime now = LocalDateTime.now();
			now.minusHours(3);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
			c.sendMessage("Zwischengespeicherte Statistik vom " + formatter.format(now)
					+ " Uhr.\nDie Statistiken werden spätestens nach drei Stunden neu abgefragt.").complete();
			stats = R6Command.statsCache.get(args.toLowerCase());
		}

		EmbedBuilder emb = new EmbedBuilder();
		emb.setAuthor("Rainbow Six Siege Statistik für:");
		emb.setTitle(stats.getUsername(), "https://r6db.com/player/" + stats.getUUID());
		emb.setColor(c.getType() == ChannelType.TEXT ? ((TextChannel) c).getGuild().getSelfMember().getColor()
				: Color.BLUE);
		emb.addField("~~-------------------~~", "**RANKED**", false);
		emb.addField("Wins", stats.getRankedWins() + "", true);
		emb.addField("Losses", stats.getRankedLosses() + "", true);
		emb.addField("W/L-R", stats.getRankedWinLoseRate() + "", true);
		emb.addField("Kills", stats.getRankedKills() + "", true);
		emb.addField("Deaths", stats.getRankedDeaths() + "", true);
		emb.addField("K/D-R", stats.getRankedKillDeathRate() + "", true);

		emb.addField("~~-------------------~~", "**CASUAL**", false);
		emb.addField("Wins", stats.getCasualWins() + "", true);
		emb.addField("Losses", stats.getCasualLosses() + "", true);
		emb.addField("W/L-R", stats.getCasualWinLoseRate() + "", true);
		emb.addField("Kills", stats.getCasualKills() + "", true);
		emb.addField("Deaths", stats.getCasualDeaths() + "", true);
		emb.addField("K/D-R", stats.getCasualKillDeathRate() + "", true);

		c.sendMessage(emb.build()).complete();

		int i = 0;
		for (RainbowSixStatsSeason season : stats.getSeasons()) {
			if (i++ > 3)
				break;
			EmbedBuilder semb = new EmbedBuilder();
			semb.setColor(c.getType() == ChannelType.TEXT ? ((TextChannel) c).getGuild().getSelfMember().getColor()
					: Color.BLUE);
			semb.setAuthor("Season-Statistik von " + stats.getUsername());
			semb.setTitle("**Operation: " + season.getSeason() + "** (" + season.getId()
					+ ")", "https://r6db.com/player/" + stats.getUUID());
			semb.setDescription("Region: " + season.getRegion());
			semb.addField("Wins", season.getWins() + "", true);
			semb.addField("Losses", season.getLosses() + "", true);
			semb.addField("Abandons", season.getAbandons() + "", true);
			semb.addField("Vorherige Rang-MMR", season.getPreviousRating() + "", true);
			semb.addField("MMR", season.getRating() + "", true);
			semb.addField("Nächste Rang-MMR", season.getNextRating() + "", true);
			semb.addField("Rang", season.getRank(), true);

			c.sendMessage(semb.build()).complete();
		}
	}

}
