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

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.music.QueueTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

public class QueueCommand extends TrustedCommand {

	public QueueCommand() {
		this.name = "queue";
		this.aliases = new String[] { "q", "playlist" };
		this.help = "Shows current queue.";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		GuildConfig musicManager = gc;
		ArrayList<QueueTrack> titles = musicManager.scheduler.getRequestedTitles();

		if (e.getArgs().equalsIgnoreCase("clear")) {
			if (gc.getDJs().contains(e.getAuthor().getIdLong())
					|| e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				musicManager.scheduler.clearQueue();
				e.reactSuccess();
				return true;
			}
		}

		if (e.getArgs().equalsIgnoreCase("repeat")) {
			if (gc.getDJs().contains(e.getAuthor().getIdLong())
					|| e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				musicManager.scheduler.setRepeat(!musicManager.scheduler.getRepeat());
				e.reply(musicManager.scheduler.getRepeat() ? "The queue is now repeating."
						: "The queue is no longer repeating.");
				return true;
			}
		}

		MessageBuilder mes = new MessageBuilder();
		int amnt = titles.size() > 10 ? 10 : titles.size();

		mes.append("**Queue** (" + (amnt > 10 ? "Next 10 tracks)" : amnt + " tracks)"));
		EmbedBuilder m = new EmbedBuilder();
		m.setTitle("Queue repeating: " + (musicManager.scheduler.getRepeat() ? "on" : "off"));
		m.setColor(e.getGuild().getSelfMember().getColor());
		m.appendDescription("Currently playing: " + (musicManager.player.getPlayingTrack() == null ? "nothing"
				: musicManager.player.getPlayingTrack().getInfo().title + " ["
						+ Methods.getTimeString(musicManager.player.getPlayingTrack().getInfo().length)
						+ "], *requested by "
						+ (e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()) != null
								? (e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()).getNickname() != null
										? e.getGuild().getMember(musicManager.scheduler.getCurrentRequester()).getNickname()
										: musicManager.scheduler.getCurrentRequester().getName())
								: musicManager.scheduler.getCurrentRequester().getName())
						+ "*")
				+ "\n\n");

		if (titles.size() == 0) {
			m.appendDescription("The queue is empty.");
		}
		for (int i = 0; i < amnt; i++) {
			m.appendDescription("**[" + (i + 1) + "]** " + titles.get(i).getTrack().getInfo().title + " ["
					+ Methods.getTimeString(titles.get(i).getTrack().getDuration()) + "], *requested by "
					+ (e.getGuild().getMember(titles.get(i).getRequestor()) != null
							? (e.getGuild().getMember(titles.get(i).getRequestor()).getNickname() != null
									? e.getGuild().getMember(titles.get(i).getRequestor()).getNickname()
									: titles.get(i).getRequestor().getName())
							: titles.get(i).getRequestor().getName())
					+ "*\n\n");
		}
		m.setFooter("Skip specified track: !skip [id]", e.getJDA().getSelfUser().getAvatarUrl());
		mes.setEmbed(m.build());
		e.reply(mes.build());
		return true;
	}
}
