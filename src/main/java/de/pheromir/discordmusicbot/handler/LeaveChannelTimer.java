package de.pheromir.discordmusicbot.handler;

import java.util.TimerTask;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.entities.Guild;

public class LeaveChannelTimer extends TimerTask {
	
	private Guild g;
	
	public LeaveChannelTimer(Guild g) {
		this.g = g;
	}

	@Override
	public void run() {
		g.getAudioManager().closeAudioConnection();
		Main.getGuildAudioPlayer(g).setAutoPause(false);
		Main.getGuildAudioPlayer(g).player.stopTrack();
		Main.getGuildAudioPlayer(g).player.setPaused(false);
		System.out.println("VoiceVerbindung in Guild "+g.getName()+" ("+g.getId()+") getrennt.");
	}

}
