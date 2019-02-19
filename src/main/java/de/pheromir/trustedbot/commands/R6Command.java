package de.pheromir.trustedbot.commands;

import java.awt.Color;

import javax.naming.NameNotFoundException;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.r6stats.RainbowSixStats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;

public class R6Command extends TrustedCommand {

	public R6Command() {
		this.name = "r6";
		this.help = "Show the Rainbow Six Stats of a player on PC";
		this.arguments = "<Username>";
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected void exec(CommandEvent e) {
		try {
			RainbowSixStats stats = new RainbowSixStats(e.getArgs());

			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(stats.getUsername(), stats.getProfileURL(), stats.getAvatarURL());
			eb.setFooter("Stats provided by R6Tab.com (" + stats.getUpdatedAgo()
					+ ")", "https://r6tab.com/images/logoimg.png");
			eb.setColor(e.getChannelType() == ChannelType.TEXT ? e.getSelfMember().getColor() : Color.BLUE);
			eb.setDescription("Ranked stats on Uplay for region " + stats.getMainRegion());
			eb.setTitle("View __" + stats.getUsername() + "__'s Full Stats on R6Tab", stats.getProfileURL());
			eb.setThumbnail(String.format("https://r6tab.com/images/discordranks/%d.png?width=80&height=80", stats.getCurrentRank()));

			eb.addField("Rank", "**Now:** "+RainbowSixStats.translateRank(stats.getCurrentRank()) + "\n**Max:** "
					+ RainbowSixStats.translateRank(stats.getCurrentRank())+ "\n~~-----~~", true);
			eb.addField("MMR", "**Now:** "+stats.getCurrentMMR() + "\n**Max:** " + stats.getMaxMMR() + "\n~~-----~~", true);

			eb.addField("Level", stats.getLevel() + "\n\n~~-----~~", true);
			eb.addField("Fav. Operators", "**Attacker:** " + stats.getFavAttacker() + "\n**Defender:** "
					+ stats.getFavDefender() + "\n~~-----~~", true);

			eb.addField("Matches", "**Wins:** " + stats.getWins() + "\n**Losses:** " + stats.getLosses()
					+ "\n**WLR:** " + stats.getWinLoseRate() + "%" + "\n~~-----~~", true);
			eb.addField("Stats", "**Kills:** " + stats.getKills() + "\n**Deaths:** " + stats.getDeaths()
					+ "\n**Overall KD:** " + stats.getKillDeathRate() + "\n~~-----~~", true);

			eb.addField(RainbowSixStats.translateSeason(RainbowSixStats.currentSeason - 1), "**Rank**: "
					+ RainbowSixStats.translateRank(stats.getLastThreeSeasonRanks()[1][0]) + "\n**MMR:** "
					+ stats.getLastThreeSeasonRanks()[1][1], true);
			eb.addField(RainbowSixStats.translateSeason(RainbowSixStats.currentSeason - 2), "**Rank**: "
					+ RainbowSixStats.translateRank(stats.getLastThreeSeasonRanks()[2][0]) + "\n**MMR:** "
					+ stats.getLastThreeSeasonRanks()[2][1], true);

			e.reply(eb.build());

		} catch (Exception e1) {
			if (e1 instanceof NameNotFoundException) {
				e.reply("I'm sorry, but I couldn't find the requested user.");
				return;
			} else {
				Main.LOG.error("", e1);
			}
		}

	}

}
