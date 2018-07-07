package de.pheromir.discordmusicbot.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
import de.pheromir.discordmusicbot.handler.AudioPlayerSendHandler;
import de.pheromir.discordmusicbot.handler.TrackScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class GuildConfig {

	private Guild g;
	private File configFile;
	private YamlConfiguration yaml;
	private Configuration cfg;
	private List<Long> djs;
	private HashMap<String, List<Long>> twitch;
	private int volume;
	public final AudioPlayer player;
	public final TrackScheduler scheduler;
	public boolean autoPause = false;
	public HashMap<User, ArrayList<Suggestion>> suggestions;

	public GuildConfig(Guild g) {
		this.g = g;
		djs = new ArrayList<>();
		volume = 30;
		yaml = new YamlConfiguration();
		twitch = new HashMap<>();
		configFile = new File("config//" + this.g.getId() + ".yml");
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
				cfg = yaml.load(configFile);
				cfg.set("Music.DJs", djs);
				cfg.set("Music.Volume", volume);
				yaml.save(cfg, configFile);
			} else {
				cfg = yaml.load(configFile);
				djs = cfg.getLongList("Music.DJs");
				volume = cfg.getInt("Music.Volume");
				if (cfg.getSection("Twitch") != null) {
					for (String key : cfg.getSection("Twitch").getKeys()) {
						twitch.put(key, cfg.getLongList("Twitch." + key));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			cfg = null;
		}
		player = Main.playerManager.createPlayer();
		scheduler = new TrackScheduler(player, g);
		player.addListener(scheduler);
		suggestions = new HashMap<>();
		player.setVolume(volume);
	}

	public void removeDJ(Long longID) {
		if (djs.contains(longID)) {
			djs.remove(longID);
		}
		cfg.set("Music.DJs", djs);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addDJ(Long longID) {
		if (!djs.contains(longID)) {
			djs.add(longID);
		}
		cfg.set("Music.DJs", djs);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Long> getDJs() {
		return djs;
	}

	public void setVolume(int vol) {
		volume = vol;
		player.setVolume(volume);
		cfg.set("Music.Volume", vol);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getVolume() {
		return volume;
	}

	public void addTwitchStream(String twitchname, Long channelID) {
		twitchname = twitchname.toLowerCase();
		List<Long> list = new ArrayList<>();
		if (twitch.containsKey(twitchname)) {
			list = twitch.get(twitchname);
			if (!list.contains(channelID)) {
				list.add(channelID);
				twitch.put(twitchname, list);
			}
		} else {
			list.add(channelID);
			twitch.put(twitchname, list);
		}
		cfg.set("Twitch." + twitchname, list);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.renewGeneralTwitchList();
	}

	public void removeTwitchStream(String twitchname, Long channelID) {
		twitchname = twitchname.toLowerCase();
		List<Long> list = new ArrayList<>();
		if (twitch.containsKey(twitchname)) {
			list = twitch.get(twitchname);
			if (list.contains(channelID))
				list.remove(channelID);
			twitch.put(twitchname, list);
		} else {
			list.add(channelID);
			twitch.put(twitchname, list);
		}
		if (list.isEmpty()) {
			cfg.set("Twitch." + twitchname, null);
		} else {
			cfg.set("Twitch." + twitchname, list);
		}
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.renewGeneralTwitchList();
	}

	public HashMap<String, List<Long>> getTwitchList() {
		return twitch;
	}
	
	public void setAutoPause(boolean pause) {
		this.autoPause = pause;
	}
	
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}

}
