/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot.commands;

import java.awt.Color;
import java.time.Instant;

import javax.naming.NameNotFoundException;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
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
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}
		try {
			e.getChannel().sendTyping().complete();
			RainbowSixStats stats = new RainbowSixStats(e.getArgs().replaceAll("[^\\w\\d_\\.-]", ""));

			EmbedBuilder eb = new EmbedBuilder();
			
			eb.setAuthor(stats.getUsername(), stats.getProfileURL(), stats.getAvatarURL());
			eb.setColor(e.getChannelType() == ChannelType.TEXT ? e.getSelfMember().getColor() : Color.BLUE);
			eb.setDescription("Ranked stats on Uplay for region " + stats.getMainRegion() + "\n"
					+ "Current Season: " + RainbowSixStats.translateSeason(RainbowSixStats.currentSeason));
			eb.setTitle("View __" + stats.getUsername() + "__'s Full Stats on R6Tab", stats.getProfileURL());
			

			eb.addField("Rank", 
				"**Now:** " + RainbowSixStats.translateRank(stats.getCurrentRank()) + "\n"
			  + "**Max:** "	+ RainbowSixStats.translateRank(stats.getMaxRank()) + "\n"
			  		+ "~~-----~~", true);
			
			eb.addField("MMR", 
				"**Now:** " + stats.getCurrentMMR() + "\n"
			  + "**Max:** " + stats.getMaxMMR()
					+ "\n~~-----~~", true);

			eb.addField("General", 
				"**Level:** " + stats.getLevel() + "\n"
			  + "**Playtime:** " + stats.getPlaytime()
					+ " hours\n~~-----~~", true);
			
			eb.addField("Ranked Seasonal", 
				"**Kills:** " + stats.getRankedKillsSeasonal() + " | **Deaths:** " + stats.getRankedDeathsSeasonal() + "\n"
			  + "**Wins:** " + stats.getRankedWinsSeasonal() + " | **Lost:** " + stats.getRankedLossesSeasonal() + "\n"
			  + "**KD:** " + String.format("%.1f", stats.getRankedKDRSeasonal()) + " | **WLR:** " + String.format("%.1f", stats.getRankedWinLoseRateSeasonal()) + "%\n"
			  		+ "~~-----~~", true);

			eb.addField("Ranked Matches", 
				"**Wins:** " + stats.getRankedWins() + "\n"
			  + "**Losses:** " + stats.getRankedLosses() + "\n"
			  + "**WLR:** " + String.format("%.1f", stats.getRankedWinLoseRate()) + "%"
					+ "\n~~-----~~", true);
			
			eb.addField("Ranked Stats", 
				"**Kills:** " + stats.getRankedKills() + "\n"
			  + "**Deaths:** " + stats.getRankedDeaths() + "\n"
			  + "**Overall KD:** " + String.format("%.1f", stats.getRankedKDR()) + "\n"
			  		+ "~~-----~~", true);
			
			eb.addField("Casual Seasonal", 
				"**Kills:** " + stats.getCasualKillsSeasonal() + " | **Deaths:** " + stats.getCasualDeathsSeasonal() + "\n"
			  + "**Wins:** " + stats.getCasualWinsSeasonal() + " | **Lost:** " + stats.getCasualLossesSeasonal() + "\n"
			  + "**KD:** " + String.format("%.1f", stats.getCasualKDRSeasonal()) + " | **WLR:** " + String.format("%.1f", stats.getCasualWinLoseRateSeasonal()) + "%\n"
						  		+ "~~-----~~", true);

			eb.addField("Casual Matches", 
				"**Wins:** " + stats.getCasualWins() + "\n"
			  + "**Losses:** " + stats.getCasualLosses() + "\n"
			  + "**WLR:** " + String.format("%.1f", stats.getCasualWinLoseRate()) + "%"
					+ "\n~~-----~~", true);
			
			eb.addField("Casual Stats", 
				"**Kills:** " + stats.getCasualKills() + "\n"
			  + "**Deaths:** " + stats.getCasualDeaths() + "\n"
			  + "**Overall KD:** " + String.format("%.1f", stats.getCasualKDR()) + "\n"
			  		+ "~~-----~~", true);
			
			eb.addField("Fav. Operators", 
				"**Attacker:** " + stats.getFavAttacker() + "\n"
			  + "**Defender:** " + stats.getFavDefender() + "\n"
			  		+ "~~-----~~", true);	

			eb.addField(
				RainbowSixStats.translateSeason(RainbowSixStats.currentSeason - 1), 
				"**Rank**: " + RainbowSixStats.translateRank(stats.getLastThreeSeasonRanks()[1][0]) + "\n"
			  + "**MMR:** " + stats.getLastThreeSeasonRanks()[1][1], true);
			
			eb.addField(
				RainbowSixStats.translateSeason(RainbowSixStats.currentSeason - 2), 
				"**Rank**: " + RainbowSixStats.translateRank(stats.getLastThreeSeasonRanks()[2][0]) + "\n"
						+ "**MMR:** " + stats.getLastThreeSeasonRanks()[2][1], true);
			
			String aliases;
			if(stats.getAliases().size() == 0) {
				aliases = "No previous Names";
			} else {
				StringBuilder sb = new StringBuilder();
				stats.getAliases().forEach(str -> sb.append(str + "\n"));
				aliases = sb.toString().substring(0, sb.length()-1);
			}
			
			eb.addField("Previous Names", 
				aliases, false);
			
			eb.setThumbnail(String.format("https://r6tab.com/images/pngranks/%d.png", stats.getCurrentRank()));
			
			eb.setFooter("Stats provided by R6Tab.com, Updated: ", "https://r6tab.com/images/logoimg.png");
			eb.setTimestamp(Instant.ofEpochMilli(stats.getUpdatedMillis()));

			e.reply(eb.build());
			return true;
		} catch (Exception e1) {
			if (e1 instanceof NameNotFoundException) {
				e.reply("I'm sorry, but I couldn't find the requested user.");
				return false;
			} else {
				e.reply("An error occurred. Sorry.");
				Main.LOG.error("", e1);
				return false;
			}
		}

	}

}
