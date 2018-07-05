package de.pheromir.discordmusicbot.listener;

import de.pheromir.discordmusicbot.Methods;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelListener extends ListenerAdapter {

	@Override
	public void onVoiceChannelCreate(VoiceChannelCreateEvent e) {
		Methods.createTextPerVoice(e.getJDA(), e.getChannel());
	}

	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
		Methods.deleteTextPerVoice(e.getJDA(), e.getChannel());
	}

	@Override
	public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent e) {
		Methods.renameTextPerVoice(e.getJDA(), e.getOldName(), e.getNewName());
	}

}
