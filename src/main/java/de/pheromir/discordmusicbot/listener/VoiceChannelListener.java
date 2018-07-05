package de.pheromir.discordmusicbot.listener;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.handler.GuildMusicManager;
import de.pheromir.discordmusicbot.helper.QueueTrack;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceDeafen(GuildVoiceDeafenEvent e) {		if (!e.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;
		if (Main.getGuildAudioPlayer(e.getGuild()).player.getPlayingTrack() == null)
			return;
		if (!e.getMember().getVoiceState().inVoiceChannel() && !(e.getMember().getVoiceState().getChannel() == e.getGuild().getSelfMember().getVoiceState().getChannel()))
			return;
		checkVoiceChannelForListeners(e.getGuild().getSelfMember().getVoiceState().getChannel());
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if (!e.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;
		if (Main.getGuildAudioPlayer(e.getGuild()).player.getPlayingTrack() == null)
			return;
		if (!(e.getChannelJoined() == e.getGuild().getSelfMember().getVoiceState().getChannel()))
			return;
		checkVoiceChannelForListeners(e.getGuild().getSelfMember().getVoiceState().getChannel());
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if (!e.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return;
		if (Main.getGuildAudioPlayer(e.getGuild()).player.getPlayingTrack() == null)
			return;
		if (!(e.getChannelLeft() == e.getGuild().getSelfMember().getVoiceState().getChannel()))
			return;
		checkVoiceChannelForListeners(e.getGuild().getSelfMember().getVoiceState().getChannel());
	}
	
	public void checkVoiceChannelForListeners(VoiceChannel vc) {
		GuildMusicManager musicManager = Main.getGuildAudioPlayer(vc.getGuild());
		boolean unmutedPresent = false;
		for (Member mem : vc.getMembers()) {
			if (mem == vc.getGuild().getSelfMember())
				continue;
			if (!mem.getVoiceState().isDeafened()) {
				unmutedPresent = true;
			}
		}
		
		if (!unmutedPresent) {
			musicManager.player.setPaused(true);
			musicManager.setAutoPause(true);
		} else {
			if (musicManager.autoPause) {
				if (musicManager.player.getPlayingTrack().getDuration() == Long.MAX_VALUE) {
					musicManager.scheduler.setCurrentTrack(new QueueTrack(musicManager.player.getPlayingTrack().makeClone(), musicManager.scheduler.getCurrentRequester()));
					musicManager.player.startTrack(musicManager.player.getPlayingTrack().makeClone(), false);
				}
				musicManager.player.setPaused(false);
				musicManager.setAutoPause(false);
			}
		}
	}

}
