package de.pheromir.discordmusicbot.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
import net.dv8tion.jda.core.entities.Guild;

public class GuildConfig {

	private Guild g;
	private File configFile;
	private YamlConfiguration yaml;
	private Configuration cfg;
	private List<Long> djs;
	private int volume;

	public GuildConfig(Guild g) {
		this.g = g;
		djs = new ArrayList<>();
		volume = 30;
		yaml = new YamlConfiguration();
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
			}
		} catch (IOException e) {
			e.printStackTrace();
			cfg = null;
		}
	}

	public void removeDJ(Long longID) {
		if(djs.contains(longID)) {
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
		if(!djs.contains(longID)) {
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

}
