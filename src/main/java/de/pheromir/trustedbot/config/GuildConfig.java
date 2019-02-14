package de.pheromir.trustedbot.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.MySQL;
import de.pheromir.trustedbot.commands.custom.AliasCommand;
import de.pheromir.trustedbot.commands.custom.CustomCommand;
import de.pheromir.trustedbot.music.AudioPlayerSendHandler;
import de.pheromir.trustedbot.music.Suggestion;
import de.pheromir.trustedbot.music.TrackScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class GuildConfig implements GuildSettingsProvider {

	private Guild g;
	private Long guildId;
	private int volume;
	public final AudioPlayer player;
	public final TrackScheduler scheduler;
	private List<Long> djs;
	private List<String> disabledCommands;
	private HashMap<User, ArrayList<Suggestion>> suggestions;
	private HashMap<String, CustomCommand> customCommands;
	private HashMap<String, AliasCommand> aliasCommands;
	private String cmdPrefix;

	private static HashMap<String, List<Long>> twitch = new HashMap<>();
	private static HashMap<String, List<Long>> reddit = new HashMap<>();

	static {
		downloadTwitchUsers();
		downloadSubreddits();
	}

	public GuildConfig(Guild g) {
		this.g = g;
		cmdPrefix = "!";
		guildId = g.getIdLong();
		djs = new ArrayList<>();
		volume = 100;
		disabledCommands = new ArrayList<>();
		suggestions = new HashMap<>();
		customCommands = new HashMap<>();
		aliasCommands = new HashMap<>();
		player = Main.playerManager.createPlayer();
		scheduler = new TrackScheduler(player, g);
		player.addListener(scheduler);
		initialize();
		downloadGeneralGuild();
		downloadDJs();
		downloadDisabledCommands();
		downloadAliasCommands();
		downloadCustomCommands();
		g.getAudioManager().setSendingHandler(getSendHandler());
	}

	public void initialize() {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Guilds (GuildId, Volume, Prefix) VALUES (?, 100, ?)");
			prep.setString(1, g.getId());
			prep.setString(2, "!");
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("Initialization failed: ", e);
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
			Main.LOG.error("DJ add failed: ", e);
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
			Main.LOG.error("DJ remove failed: ", e);
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
			Main.LOG.error("Volume set failed: ", e);
		}
	}

	public int getVolume() {
		return volume;
	}

	public void setPrefix(String prefix) {
		cmdPrefix = prefix;
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("UPDATE Guilds SET Prefix = ? WHERE GuildId = ?");
			prep.setString(1, prefix);
			prep.setString(2, g.getId());
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("Prefix set failed: ", e);
		}
	}

	@Override
	public ArrayList<String> getPrefixes() {
		ArrayList<String> l = new ArrayList<>();
		l.add(cmdPrefix);
		return l;

	}

	public String getPrefix() {
		return cmdPrefix;

	}

	public HashMap<User, ArrayList<Suggestion>> getSuggestions() {
		return suggestions;
	}

	public Guild getGuild() {
		return g;
	}

	public void downloadGeneralGuild() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT Volume, Prefix FROM Guilds WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				volume = res.getInt("Volume");
				player.setVolume(volume);
				cmdPrefix = res.getString("Prefix");
			}
			sql.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
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
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
	}

	public void downloadCustomCommands() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT Name, Text FROM CustomCommands WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				customCommands.put(res.getString("Name"), new CustomCommand(res.getString("Name"),
						res.getString("Text"), g.getId()));
			}
			sql.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
	}

	public void addCustomCommand(String name, String response) {
		CustomCommand cc = new CustomCommand(name, response, g.getId());
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("REPLACE INTO CustomCommands (Name, Text, GuildId) VALUES (?, ?, ?)");
			prep.setString(1, cc.getName());
			prep.setString(2, cc.getResponse());
			prep.setString(3, guildId.toString());
			customCommands.put(cc.getName(), cc);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("CustomCommand add failed: ", e);
		}
	}

	public void removeCustomCommand(String name) {
		name = name.toLowerCase();
		if (!customCommands.containsKey(name))
			return;
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM CustomCommands WHERE Name = ? AND GuildId = ?");
			prep.setString(1, name);
			prep.setString(2, guildId.toString());
			customCommands.remove(name);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("CustomCommand remove failed: ", e);
		}

	}

	public HashMap<String, CustomCommand> getCustomCommands() {
		return customCommands;
	}

	public void downloadAliasCommands() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT Name, Command, Arguments FROM AliasCommands WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				aliasCommands.put(res.getString("Name"), new AliasCommand(res.getString("Name"),
						res.getString("Command"), res.getString("Arguments"), g.getId()));
			}
			sql.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
	}

	public void addAliasCommand(String name, String command, String args) {
		AliasCommand cc = new AliasCommand(name, command, args, g.getId());
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("REPLACE INTO AliasCommands (Name, Command, Arguments, GuildId) VALUES (?, ?, ?, ?)");
			prep.setString(1, cc.getName());
			prep.setString(2, cc.getCommand());
			prep.setString(3, cc.getArgs());
			prep.setString(4, guildId.toString());
			aliasCommands.put(cc.getName(), cc);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("AliasCommand add failed: " + e);
		}
	}

	public void removeAliasCommand(String name) {
		name = name.toLowerCase();
		if (!aliasCommands.containsKey(name))
			return;
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM AliasCommands WHERE Name = ? AND GuildId = ?");
			prep.setString(1, name);
			prep.setString(2, guildId.toString());
			aliasCommands.remove(name);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("AliasCommand remove failed: " + e);
		}

	}

	public HashMap<String, AliasCommand> getAliasCommands() {
		return aliasCommands;
	}

	public static void addTwitchStream(String twitchname, Long channelID, Long guildId) {
		twitchname = twitchname.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Twitch (ChannelId, Username, GuildId) VALUES (?, ?, ?)");
			prep.setString(1, channelID.toString());
			prep.setString(2, twitchname);
			prep.setString(3, guildId.toString());
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
			Main.LOG.error("Twitch add failed: " + e);
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
			Main.LOG.error("Twitch remove failed: " + e);
		}
	}

	public static HashMap<String, List<Long>> getTwitchList() {
		return twitch;
	}

	public static void addSubreddit(String subreddit, Long channelID, Long guildId) {
		subreddit = subreddit.toLowerCase();
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Reddit (ChannelId, Subreddit, GuildId) VALUES (?, ?, ?)");
			prep.setString(1, channelID.toString());
			prep.setString(2, subreddit);
			prep.setString(3, guildId.toString());
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
			Main.LOG.error("Reddit add failed: " + e);
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
			Main.LOG.error("Reddit remove failed: " + e);
		}
	}

	public static HashMap<String, List<Long>> getRedditList() {
		return reddit;
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
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
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
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
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
			prep.setString(1, post.length()>190?post.substring(0, 190):post);
			prep.execute();
		} catch (SQLException e) {
			Main.LOG.error("Reddit add history failed: " + e);
		} finally {
			sq.closeConnection();
		}
	}

	public static boolean RedditPosthistoryContains(String post) {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("SELECT * FROM Reddit_Posts WHERE Url = ?");
			prep.setString(1, post.length()>190?post.substring(0, 190):post);
			ResultSet res = prep.executeQuery();
			if (res.next()) {
				sq.closeConnection();
				return true;
			} else {
				sq.closeConnection();
				return false;
			}
		} catch (SQLException e) {
			Main.LOG.error("Checking Reddit history failed: " + e);
			return false;
		}
	}

	public static void clearSubredditPostHistory() {
		Methods.mySQLQuery("TRUNCATE TABLE Reddit_Posts");
	}
	
	public void disableCommand(String cmd) {
		cmd = cmd.toLowerCase();
		if(!isCommandDisabled(cmd)) {
			disabledCommands.add(cmd);
		}
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO DisabledCommands (GuildId, Command) VALUES (?, ?)");
			prep.setString(1, guildId.toString());
			prep.setString(2, cmd);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("DisabledCommand add failed: " + e);
		}
		
	}
	
	public void enableCommand(String cmd) {
		cmd = cmd.toLowerCase();
		if(isCommandDisabled(cmd)) {
			disabledCommands.remove(cmd);
		}
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM DisabledCommands WHERE GuildId = ? AND Command = ?");
			prep.setString(1, guildId.toString());
			prep.setString(2, cmd);
			prep.execute();
			sq.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("DisabledCommand remove failed: " + e);
		}
		
	}
	
	public boolean isCommandDisabled(String cmd) {
		return disabledCommands.contains(cmd.toLowerCase());
	}
	
	public List<String> getDisabledCommands() {
		return disabledCommands;
	}
	
	public void downloadDisabledCommands() {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		PreparedStatement state;
		ResultSet res = null;
		try {
			state = sql.getConnection().prepareStatement("SELECT Command FROM DisabledCommands WHERE GuildId = ?");
			state.setString(1, g.getId());
			res = state.executeQuery();
			while (res.next()) {
				disabledCommands.add(res.getString("Command"));
			}
			sql.closeConnection();
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
	}
	
	public void delete() {
		MySQL sq = Main.getMySQL();
		sq.openConnection();
		try {
			PreparedStatement cb = sq.getConnection().prepareStatement("DELETE IGNORE FROM Guilds WHERE GuildId = ?");
			cb.setString(1, guildId.toString());
			cb.execute();
			sq.closeConnection();
			SettingsManager.guildConfigs.remove(this.guildId);
		} catch (SQLException e) {
			Main.LOG.error("General delete failed: " + e);
		}
	}

}
