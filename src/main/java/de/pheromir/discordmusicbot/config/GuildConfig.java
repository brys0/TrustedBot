package de.pheromir.discordmusicbot.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.MySQL;
import de.pheromir.discordmusicbot.music.AudioPlayerSendHandler;
import de.pheromir.discordmusicbot.music.Suggestion;
import de.pheromir.discordmusicbot.music.TrackScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class GuildConfig {

	private Guild g;
	
	private int volume;
	public final AudioPlayer player;
	public final TrackScheduler scheduler;
	private List<Long> djs;
	private HashMap<User, ArrayList<Suggestion>> suggestions;

	private static HashMap<String, List<Long>> twitch = new HashMap<>();
	private static HashMap<String, List<Long>> reddit = new HashMap<>();
	private static HashMap<String, List<Long>> cb = new HashMap<>();
	
	static {
		downloadTwitchUsers();
		downloadSubreddits();
		downloadCBUsers();
	}

	public GuildConfig(Guild g) {
		this.g = g;
		djs = new ArrayList<>();
		volume = 100;
		suggestions = new HashMap<>();
		player = Main.playerManager.createPlayer();
		scheduler = new TrackScheduler(player, g);
		player.addListener(scheduler);
		this.initialize();
		this.downloadGuildVolume();
		this.downloadDJs();
		g.getAudioManager().setSendingHandler(getSendHandler());
	}
	
	public void initialize() {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Guilds (GuildId, Volume) VALUES (?, 100)");
			prep.setString(1, g.getId());
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Initialization failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void addDJ(Long longID) {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO DJs (GuildId, UserId) VALUES (?, ?)");
			prep.setString(1, g.getId());
			prep.setString(2, longID.toString());
			if (!djs.contains(longID)) {
				djs.add(longID);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("DJ add failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void removeDJ(Long longID) {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM DJs WHERE GuildId = ? AND UserId = ?");
			prep.setString(1, g.getId());
			prep.setString(2, longID.toString());
		if (djs.contains(longID)) {
			djs.remove(longID);
			prep.execute();
		}
		} catch (SQLException e) {
			System.out.println("DJ remove failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Long> getDJs() {
		return djs;
	}
	
	public void setVolume(int vol) {
		volume = vol;
		player.setVolume(volume);
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("UPDATE Guilds SET Volume = ? WHERE GuildId = ?");
			prep.setInt(1, volume);
			prep.setString(2, g.getId());
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Volume set failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public int getVolume() {
		return volume;
	}

	public HashMap<User, ArrayList<Suggestion>> getSuggestions() {
		return suggestions;
	}

	public Guild getGuild() {
		return g;
	}

	public void downloadGuildVolume() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT Volume FROM Guilds WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				setVolume(res.getInt("Volume"));
			}
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void downloadDJs() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT UserId FROM DJs WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				djs.add(Long.parseLong(res.getString("UserId")));
			}
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addTwitchStream(String twitchname, Long channelID) {
		twitchname = twitchname.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Twitch (ChannelId, Username) VALUES (?, ?)");
			prep.setString(1, channelID.toString());
			prep.setString(2, twitchname);
			List<Long> list = new ArrayList<>();
			if (twitch.containsKey(twitchname)) {
				list = twitch.get(twitchname);
				if (!list.contains(channelID)) {
					list.add(channelID);
					twitch.put(twitchname, list);
					prep.execute();
				}
			} else {
				list.add(channelID);
				twitch.put(twitchname, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Twitch add failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void removeTwitchStream(String twitchname, Long channelID) {
		twitchname = twitchname.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM Twitch WHERE ChannelId = ? AND Username = ?");
			prep.setString(1, channelID.toString());
			prep.setString(2, twitchname);
			List<Long> list = new ArrayList<>();
			if (twitch.containsKey(twitchname)) {
				list = twitch.get(twitchname);
				if (list.contains(channelID))
					list.remove(channelID);
				twitch.put(twitchname, list);
				prep.execute();
			} else {
				list.add(channelID);
				twitch.put(twitchname, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Twitch remove failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, List<Long>> getTwitchList() {
		return twitch;
	}

	public static void addSubreddit(String subreddit, Long channelID) {
		subreddit = subreddit.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Reddit (ChannelId, Subreddit) VALUES (?, ?)");
			prep.setString(1, channelID.toString());
			prep.setString(2, subreddit);
			List<Long> list = new ArrayList<>();
			if (reddit.containsKey(subreddit)) {
				list = reddit.get(subreddit);
				if (!list.contains(channelID)) {
					list.add(channelID);
					reddit.put(subreddit, list);
					prep.execute();
				}
			} else {
				list.add(channelID);
				reddit.put(subreddit, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Reddit add failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void removeSubreddit(String subreddit, Long channelID) {
		subreddit = subreddit.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM Reddit WHERE ChannelId = ? AND Subreddit = ?");
			prep.setString(1, channelID.toString());
			prep.setString(2, subreddit);
			List<Long> list = new ArrayList<>();
			if (reddit.containsKey(subreddit)) {
				list = reddit.get(subreddit);
				if (list.contains(channelID))
					list.remove(channelID);
				reddit.put(subreddit, list);
				prep.execute();
			} else {
				list.add(channelID);
				reddit.put(subreddit, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Reddit remove failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static HashMap<String, List<Long>> getRedditList() {
		return reddit;
	}

	public static void addCBStream(String username, Long channelID) {
		username = username.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Chaturbate (ChannelId, Username) VALUES (?, ?)");
			prep.setString(1, channelID.toString());
			prep.setString(2, username);
			List<Long> list = new ArrayList<>();
			if (cb.containsKey(username)) {
				list = cb.get(username);
				if (!list.contains(channelID)) {
					list.add(channelID);
					cb.put(username, list);
					prep.execute();
				}
			} else {
				list.add(channelID);
				cb.put(username, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Chaturbate add failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void removeCBStream(String username, Long channelID) {
		username = username.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM Chaturbate WHERE ChannelId = ? AND Username = ?");
			prep.setString(1, channelID.toString());
			prep.setString(2, username);
			List<Long> list = new ArrayList<>();
			if (cb.containsKey(username)) {
				list = cb.get(username);
				if (list.contains(channelID))
					list.remove(channelID);
				cb.put(username, list);
				prep.execute();
			} else {
				list.add(channelID);
				cb.put(username, list);
				prep.execute();
			}
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Chaturbate remove failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static HashMap<String, List<Long>> getCBList() {
		return cb;
	}

	public static void downloadTwitchUsers() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT ChannelId, Username FROM Twitch");
			res = state.executeQuery();
			while (res.next()) {
				String twitchname = res.getString("Username");
				Long channelId = Long.parseLong(res.getString("ChannelId"));
				twitchname = twitchname.toLowerCase();
				List<Long> list = new ArrayList<>();
				if (twitch.containsKey(twitchname)) {
					list = twitch.get(twitchname);
					if (!list.contains(channelId)) {
						list.add(channelId);
						twitch.put(twitchname, list);
					}
				} else {
					list.add(channelId);
					twitch.put(twitchname, list);
				}
			}
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void downloadSubreddits() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT ChannelId, Subreddit FROM Reddit");
			res = state.executeQuery();
			while (res.next()) {
				String subreddit = res.getString("Subreddit");
				Long channelId = Long.parseLong(res.getString("ChannelId"));
				subreddit = subreddit.toLowerCase();
				List<Long> list = new ArrayList<>();
				if (reddit.containsKey(subreddit)) {
					list = reddit.get(subreddit);
					if (!list.contains(channelId)) {
						list.add(channelId);
						reddit.put(subreddit, list);
					}
				} else {
					list.add(channelId);
					reddit.put(subreddit, list);
				}
			}
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void downloadCBUsers() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT ChannelId, Username FROM Chaturbate");
			res = state.executeQuery();
			while (res.next()) {
				String username = res.getString("Username");
				Long channelId = Long.parseLong(res.getString("ChannelId"));
				username = username.toLowerCase();
				List<Long> list = new ArrayList<>();
				if (cb.containsKey(username)) {
					list = cb.get(username);
					if (!list.contains(channelId)) {
						list.add(channelId);
						cb.put(username, list);
					}
				} else {
					list.add(channelId);
					cb.put(username, list);
				}
			}
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}
	
	public static void addSubredditPostHistory(String post) {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Reddit_Posts (Url) VALUES (?)");
			prep.setString(1, post);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			System.out.println("Reddit add history failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean RedditPosthistoryContains(String post) {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("SELECT * FROM Reddit_Posts WHERE Url = ?");
			prep.setString(1, post);
			ResultSet res = prep.executeQuery();
			if (res.next()) {
				sq.closeConnection();
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("Checking Reddit history failed: " + e.getMessage());
			return true;
		}
	}

	public static void clearSubredditPostHistory() {
		Methods.mySQLQuery("TRUNCATE TABLE Reddit_Posts");
	}

}
