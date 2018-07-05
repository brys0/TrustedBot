package de.pheromir.discordmusicbot.handler;

import java.util.ArrayList;
import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.helper.Suggestion;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {

	/**
	 * Audio player for the guild.
	 */
	public final AudioPlayer player;
	/**
	 * Track scheduler for the player.
	 */
	public final TrackScheduler scheduler;
	
	public boolean autoPause = false;
	
	public HashMap<User, ArrayList<Suggestion>> suggestions;
	
	private Guild guild;

	/**
	 * Creates a player and a track scheduler.
	 * 
	 * @param manager
	 *            Audio player manager to use for creating the player.
	 */
	public GuildMusicManager(AudioPlayerManager manager, int volume, Guild g) {
		guild = g;
		player = manager.createPlayer();
		scheduler = new TrackScheduler(player, g);
		player.addListener(scheduler);
		suggestions = new HashMap<>();
		setVolume(volume);
	}

	public void setVolume(int vol) {
		player.setVolume(vol);
		Main.getGuildConfig(guild).setVolume(vol);
	}
	
	public void setAutoPause(boolean pause) {
		this.autoPause = pause;
	}

	/**
	 * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
	 */
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}
}