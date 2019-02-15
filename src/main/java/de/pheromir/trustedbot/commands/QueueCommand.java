package de.pheromir.trustedbot.commands;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
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
	protected void exec(CommandEvent e) {
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
				e.reply(musicManager.scheduler.getRepeat() ? "The queue is now repeating."
						: "The queue is no longer repeating.");
				return;
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
	}
}
