package de.pheromir.discordmusicbot.tasks;

import java.util.TimerTask;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.entities.Guild;

public class LeaveChannel extends TimerTask {

	private Guild g;

	public LeaveChannel(Guild g) {
		this.g = g;
	}

	@Override
	public void run() {
		if (Main.getGuildConfig(g).player.getPlayingTrack() != null)
			return;
		g.getAudioManager().closeAudioConnection();
		Main.getGuildConfig(g).player.stopTrack();
		Main.getGuildConfig(g).player.setPaused(false);
	}

}
