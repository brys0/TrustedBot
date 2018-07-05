package de.pheromir.discordmusicbot.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
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

	/**
	 * Creates a player and a track scheduler.
	 * 
	 * @param manager
	 *            Audio player manager to use for creating the player.
	 */
	public GuildMusicManager(AudioPlayerManager manager, int volume, Guild g) {
		player = manager.createPlayer();
		scheduler = new TrackScheduler(player, g);
		player.addListener(scheduler);
		suggestions = new HashMap<>();
		setVolume(volume);
	}

	public void setVolume(int vol) {
		player.setVolume(vol);
		File configFile = new File("config.yml");
		YamlConfiguration yaml = new YamlConfiguration();
		try {
			Configuration cfg = yaml.load(configFile);
			cfg.set("Music.Volume", vol);
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
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