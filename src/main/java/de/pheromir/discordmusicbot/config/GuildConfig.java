package de.pheromir.discordmusicbot.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.music.AudioPlayerSendHandler;
import de.pheromir.discordmusicbot.music.Suggestion;
import de.pheromir.discordmusicbot.music.TrackScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class GuildConfig {

	private Guild g;

	private File configFile;
	private YamlConfiguration yaml;
	private Configuration cfg;

	private int volume;
	public final AudioPlayer player;
	public final TrackScheduler scheduler;
	public boolean autoPause = false;
	private List<Long> djs;
	private HashMap<User, ArrayList<Suggestion>> suggestions;

	private HashMap<String, List<Long>> twitch;
	private HashMap<String, List<Long>> reddit;
	private HashMap<String, List<String>> redditPosts;
	private List<Long> longTitlesUsers;

	public GuildConfig(Guild g) {
		this.g = g;
		djs = new ArrayList<>();
		volume = 30;
		yaml = new YamlConfiguration();
		twitch = new HashMap<>();
		reddit = new HashMap<>();
		redditPosts = new HashMap<>();
		longTitlesUsers = new ArrayList<>();
		configFile = new File("config//" + this.g.getId() + ".yml");
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
				cfg = yaml.load(configFile);
				cfg.set("Music.DJs", djs);
				cfg.set("Music.Volume", volume);
				cfg.set("Music.LongTitlesUsers", longTitlesUsers);
				cfg.set("Twitch", new ArrayList<>());
				cfg.set("Reddit", new ArrayList<>());
				yaml.save(cfg, configFile);
			} else {
				cfg = yaml.load(configFile);
				djs = cfg.getLongList("Music.DJs");
				volume = cfg.getInt("Music.Volume");
				longTitlesUsers = cfg.getLongList("Music.LongTitlesUsers");
				if (cfg.getSection("Twitch") != null) {
					for (String key : cfg.getSection("Twitch").getKeys()) {
						twitch.put(key, cfg.getLongList("Twitch." + key));
					}
				}
				if (cfg.getSection("Reddit") != null) {
					for (String key : cfg.getSection("Reddit").getKeys()) {
						reddit.put(key, cfg.getLongList("Reddit." + key));
					}
				}
				if (cfg.getSection("RedditPosts") != null) {
					for (String key : cfg.getSection("RedditPosts").getKeys()) {
						redditPosts.put(key, cfg.getStringList("RedditPosts." + key));
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
	
	public void addSubredditPostHistory(String subreddit, String post) {
		subreddit = subreddit.toLowerCase();
		List<String> list = new ArrayList<>();
		if(redditPosts.containsKey(subreddit)) {
			list = redditPosts.get(subreddit);
			if(!list.contains(post)) {
				list.add(post);
				redditPosts.put(subreddit, list);
			}
		} else {
			list.add(post);
			redditPosts.put(subreddit, list);
		}
		cfg.set("RedditPosts." + subreddit, list);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getSubredditPostHistory(String subreddit) {
		subreddit = subreddit.toLowerCase();
		if(this.redditPosts.containsKey(subreddit)) {
			return redditPosts.get(subreddit);
		} else {
			return new ArrayList<>();
		}
	}
	
	public void clearSubredditPostHistory(String subreddit) {
		subreddit = subreddit.toLowerCase();
		cfg.set("RedditPosts." + subreddit, null);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearSubredditPostHistory() {
		cfg.set("RedditPosts", null);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addSubreddit(String subreddit, Long channelID) {
		subreddit = subreddit.toLowerCase();
		List<Long> list = new ArrayList<>();
		if (reddit.containsKey(subreddit)) {
			list = reddit.get(subreddit);
			if (!list.contains(channelID)) {
				list.add(channelID);
				reddit.put(subreddit, list);
			}
		} else {
			list.add(channelID);
			reddit.put(subreddit, list);
		}
		cfg.set("Reddit." + subreddit, list);
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.renewGeneralRedditList();
	}

	public void removeSubreddit(String subreddit, Long channelID) {
		subreddit = subreddit.toLowerCase();
		List<Long> list = new ArrayList<>();
		if (reddit.containsKey(subreddit)) {
			list = reddit.get(subreddit);
			if (list.contains(channelID))
				list.remove(channelID);
			reddit.put(subreddit, list);
		} else {
			list.add(channelID);
			reddit.put(subreddit, list);
		}
		if (list.isEmpty()) {
			cfg.set("Reddit." + subreddit, null);
		} else {
			cfg.set("Reddit." + subreddit, list);
		}
		try {
			yaml.save(cfg, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.renewGeneralRedditList();
	}

	public HashMap<String, List<Long>> getTwitchList() {
		return twitch;
	}
	
	public HashMap<String, List<Long>> getRedditList() {
		return reddit;
	}

	public List<Long> getLongTitlesUsers() {
		return longTitlesUsers;
	}

	public void addLongTitlesUser(Long id) {
		if (!longTitlesUsers.contains(id)) {
			longTitlesUsers.add(id);
			cfg.set("Music.LongTitlesUsers", longTitlesUsers);
			try {
				yaml.save(cfg, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void removeLongTitlesUser(Long id) {
		if (longTitlesUsers.contains(id)) {
			longTitlesUsers.remove(id);
			cfg.set("Music.LongTitlesUsers", longTitlesUsers);
			try {
				yaml.save(cfg, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setAutoPause(boolean pause) {
		this.autoPause = pause;
	}

	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}

	public HashMap<User, ArrayList<Suggestion>> getSuggestions() {
		return suggestions;
	}

}
