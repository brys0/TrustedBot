/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot.config;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.MySQL;
import de.pheromir.trustedbot.commands.base.AliasCommand;
import de.pheromir.trustedbot.commands.base.CustomCommand;
import de.pheromir.trustedbot.misc.RedditSubscription;
import de.pheromir.trustedbot.music.AudioPlayerSendHandler;
import de.pheromir.trustedbot.music.Suggestion;
import de.pheromir.trustedbot.music.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuildConfig implements GuildSettingsProvider {

    private final Guild g;
    private final Long guildId;
    private int volume;
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    private final List<Long> djs;
    private final List<String> disabledCommands;
    private final List<Long> blacklist;
    private final HashMap<User, ArrayList<Suggestion>> suggestions;
    private final HashMap<Long, Long> credits;
    private final ArrayList<Long> rewardClaimed;
    private final HashMap<String, CustomCommand> customCommands;
    private final HashMap<String, AliasCommand> aliasCommands;
    private String cmdPrefix;
    private boolean randomServerIcon;

    private static final HashMap<String, List<Long>> twitch = new HashMap<>();
    private static final HashMap<String, RedditSubscription> reddit = new HashMap<>();

    static {
        downloadTwitchUsers();
        downloadSubreddits();
    }

    public GuildConfig(Guild g) {
        this.g = g;
        cmdPrefix = "!";
        guildId = g.getIdLong();
        randomServerIcon = false;
        djs = new ArrayList<>();
        volume = 100;
        credits = new HashMap<>();
        rewardClaimed = new ArrayList<>();
        disabledCommands = new ArrayList<>();
        suggestions = new HashMap<>();
        customCommands = new HashMap<>();
        aliasCommands = new HashMap<>();
        blacklist = new ArrayList<>();
        player = Main.playerManager.createPlayer();
        scheduler = new TrackScheduler(player, g);
        player.addListener(scheduler);
        initialize();
        downloadGeneralGuild();
        downloadCredits();
        downloadDJs();
        downloadDisabledCommands();
        downloadAliasCommands();
        downloadCustomCommands();
        g.getAudioManager().setSendingHandler(getSendHandler());
    }

    public void addRewardClaimed(long userId) {
        if (!rewardClaimed.contains(userId)) {
            rewardClaimed.add(userId);
        }
    }

    public boolean getRewardClaimed(long userId) {
        return rewardClaimed.contains(userId);
    }

    public void resetDailyRewards() {
        rewardClaimed.clear();
    }

    public void initialize() {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Guilds (GuildId, Volume, Prefix) VALUES (?, 100, ?)");
            prep.setString(1, g.getId());
            prep.setString(2, "!");
            prep.execute();
        } catch (SQLException e) {
            Main.LOG.error("Initialization failed: ", e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("DJ add failed: ", e);
        } finally {
            sq.closeConnection();
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
        } finally {
            sq.closeConnection();
        }
    }

    public List<Long> getDJs() {
        return djs;
    }

    public void addToBlacklist(Long longID) {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Blacklist (GuildId, UserId) VALUES (?, ?)");
            prep.setString(1, g.getId());
            prep.setString(2, longID.toString());
            if (!blacklist.contains(longID)) {
                blacklist.add(longID);
                prep.execute();
            }
        } catch (SQLException e) {
            Main.LOG.error("Blacklist add failed: ", e);
        } finally {
            sq.closeConnection();
        }
    }

    public void removeFromBlacklist(Long longID) {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM Blacklist WHERE GuildId = ? AND UserId = ?");
            prep.setString(1, g.getId());
            prep.setString(2, longID.toString());
            if (blacklist.contains(longID)) {
                blacklist.remove(longID);
                prep.execute();
            }
        } catch (SQLException e) {
            Main.LOG.error("Blacklist remove failed: ", e);
        } finally {
            sq.closeConnection();
        }
    }

    public List<Long> getBlacklist() {
        return blacklist;
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
        } catch (SQLException e) {
            Main.LOG.error("Volume set failed: ", e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("Prefix set failed: ", e);
        } finally {
            sq.closeConnection();
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

    public void setRandomServerIcon(boolean enabled) {
        randomServerIcon = enabled;
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("UPDATE Guilds SET RandomServerIcon = ? WHERE GuildId = ?");
            prep.setBoolean(1, enabled);
            prep.setString(2, g.getId());
            prep.execute();
        } catch (SQLException e) {
            Main.LOG.error("RandomServerIcon set failed: ", e);
        } finally {
            sq.closeConnection();
        }
    }

    public boolean getRandomServerIcon() {
        return randomServerIcon;
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
            state = sql.getConnection().prepareStatement("SELECT Volume, Prefix, RandomServerIcon FROM Guilds WHERE GuildId = ?");
            state.setString(1, g.getId());
            res = state.executeQuery();
            while (res.next()) {
                volume = res.getInt("Volume");
                player.setVolume(volume);
                cmdPrefix = res.getString("Prefix");
                randomServerIcon = res.getBoolean("RandomServerIcon");
            }
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("Error downloading DJs: ", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("Error downloading DJs: ", e);
            }
            sql.closeConnection();
        }
    }

    public void downloadBlacklist() {
        MySQL sql = Main.getMySQL();
        sql.openConnection();
        PreparedStatement state;
        ResultSet res = null;
        try {
            state = sql.getConnection().prepareStatement("SELECT UserId FROM Blacklist WHERE GuildId = ?");
            state.setString(1, g.getId());
            res = state.executeQuery();
            while (res.next()) {
                blacklist.add(Long.parseLong(res.getString("UserId")));
            }
        } catch (SQLException e) {
            Main.LOG.error("Error downloading Blacklist: ", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("Error downloading Blacklist: ", e);
            }
            sql.closeConnection();
        }
    }

    public void downloadCredits() {
        MySQL sql = Main.getMySQL();
        sql.openConnection();
        PreparedStatement state;
        ResultSet res = null;
        try {
            state = sql.getConnection().prepareStatement("SELECT UserId, Amount FROM Credits WHERE GuildId = ?");
            state.setString(1, g.getId());
            res = state.executeQuery();
            while (res.next()) {
                credits.put(Long.parseLong(res.getString("UserId")), res.getLong("Amount"));
            }
        } catch (SQLException e) {
            Main.LOG.error("Error downloading DJs: ", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("Error downloading DJs: ", e);
            }
            sql.closeConnection();
        }
    }

    public void setUserCredits(Long userId, Long amount) {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep;
            if (userCreditsExist(userId)) {
                prep = sq.getConnection().prepareStatement("UPDATE Credits SET Amount = ? WHERE GuildId = ? AND UserId = ?");
            } else {
                prep = sq.getConnection().prepareStatement("INSERT INTO Credits (Amount, GuildId, UserId) VALUES (?, ?, ?)");
            }
            prep.setLong(1, amount);
            prep.setString(2, g.getId());
            prep.setString(3, userId.toString());
            prep.execute();
            credits.put(userId, amount);
        } catch (SQLException e) {
            Main.LOG.error("Set UserCredits failed: ", e);
        } finally {
            sq.closeConnection();
        }
    }

    public void deleteUserCredits(Long userId) {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM Credits WHERE GuildId = ? AND UserId = ?");
            prep.setString(1, g.getId());
            prep.setString(2, userId.toString());
            prep.execute();
        } catch (SQLException e) {
            Main.LOG.error("Delete UserCredits failed: ", e);
        } finally {
            sq.closeConnection();
        }
    }

    public boolean userCreditsExist(Long userId) {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("SELECT Amount FROM Credits WHERE UserId = ? AND GuildId = ?");
            prep.setString(1, userId.toString());
            prep.setString(2, guildId.toString());
            ResultSet res = prep.executeQuery();
            boolean exists = res.next();
            return exists;
        } catch (SQLException e) {
            Main.LOG.error("Credit existance check failed: " + e);
            return false;
        } finally {
            sq.closeConnection();
        }
    }

    public Long getUserCredits(Long userId) {
        if (credits.containsKey(userId)) {
            return credits.get(userId);
        } else {
            if (!userCreditsExist(userId)) {
                credits.put(userId, 0L);
                setUserCredits(userId, 0L);
                return 0L;
            } else {
                MySQL sql = Main.getMySQL();
                sql.openConnection();
                PreparedStatement state;
                ResultSet res = null;
                try {
                    state = sql.getConnection().prepareStatement("SELECT Amount FROM Credits WHERE UserId = ? AND GuildId = ?");
                    state.setString(1, userId.toString());
                    state.setString(2, g.getId());
                    res = state.executeQuery();
                    while (res.next()) {
                        credits.put(userId, res.getLong("Amount"));
                        break;
                    }
                    return credits.get(userId);
                } catch (SQLException e) {
                    Main.LOG.error("", e);
                } finally {
                    try {
                        if (res != null)
                            res.close();
                    } catch (SQLException e) {
                        Main.LOG.error("", e);
                    }
                    sql.closeConnection();
                }
            }
        }
        return 0L;
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
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("CustomCommand add failed: ", e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("CustomCommand remove failed: ", e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("AliasCommand add failed: " + e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("AliasCommand remove failed: " + e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("Twitch add failed: " + e);
        } finally {
            sq.closeConnection();
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
                list.remove(channelID);
                twitch.put(twitchname, list);
                prep.execute();
            } else {
                list.add(channelID);
                twitch.put(twitchname, list);
                prep.execute();
            }
        } catch (SQLException e) {
            Main.LOG.error("Twitch remove failed: " + e);
        } finally {
            sq.closeConnection();
        }
    }

    public static HashMap<String, List<Long>> getTwitchList() {
        return twitch;
    }

    public static void addSubreddit(String subreddit, Long channelID, Long guildId, RedditSubscription.SortType sortType) {
        subreddit = subreddit.toLowerCase();
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO Reddit (ChannelId, Subreddit, GuildId, SortType) VALUES (?, ?, ?, ?)");
            prep.setString(1, channelID.toString());
            prep.setString(2, subreddit);
            prep.setString(3, guildId.toString());
            prep.setString(4, sortType.name());
            if (reddit.containsKey(subreddit)) {
                reddit.get(subreddit).addChannel(channelID, sortType);
                prep.execute();
            } else {
                RedditSubscription rs = new RedditSubscription(subreddit);
                rs.addChannel(channelID, sortType);
                reddit.put(subreddit, rs);
                prep.execute();
            }
        } catch (SQLException e) {
            Main.LOG.error("Reddit add failed: " + e);
        } finally {
            sq.closeConnection();
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
            if (reddit.containsKey(subreddit)) {
                reddit.get(subreddit).removeChannel(channelID);
                prep.execute();
            }
        } catch (SQLException e) {
            Main.LOG.error("Reddit remove failed: " + e);
        } finally {
            sq.closeConnection();
        }
    }

    public static HashMap<String, RedditSubscription> getRedditList() {
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
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
        }
    }

    public static void downloadSubreddits() {
        MySQL sql = Main.getMySQL();
        sql.openConnection();
        PreparedStatement state;
        ResultSet res = null;
        try {
            state = sql.getConnection().prepareStatement("SELECT ChannelId, Subreddit, SortType FROM Reddit");
            res = state.executeQuery();
            while (res.next()) {
                String subreddit = res.getString("Subreddit");
                Long channelId = Long.parseLong(res.getString("ChannelId"));
                RedditSubscription.SortType sortType = RedditSubscription.SortType.valueOf(res.getString("SortType"));
                subreddit = subreddit.toLowerCase();
                if (reddit.containsKey(subreddit)) {
                    reddit.get(subreddit).addChannel(channelId, sortType);
                } else {
                    RedditSubscription rs = new RedditSubscription(subreddit);
                    rs.addChannel(channelId, sortType);
                    reddit.put(subreddit, rs);
                }
            }
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
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
            prep.setString(1, post.length() > 150 ? post.substring(0, 150) : post);
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
            prep.setString(1, post.length() > 150 ? post.substring(0, 150) : post);
            ResultSet res = prep.executeQuery();
            boolean exists = res.next();
            return exists;
        } catch (SQLException e) {
            Main.LOG.error("Checking Reddit history failed: " + e);
            return false;
        } finally {
            sq.closeConnection();
        }
    }

    public static void clearSubredditPostHistory() {
        Methods.mySQLQuery("TRUNCATE TABLE Reddit_Posts");
    }

    public void disableCommand(String cmd) {
        cmd = cmd.toLowerCase();
        if (!isCommandDisabled(cmd)) {
            disabledCommands.add(cmd);
        }
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("INSERT IGNORE INTO DisabledCommands (GuildId, Command) VALUES (?, ?)");
            prep.setString(1, guildId.toString());
            prep.setString(2, cmd);
            prep.execute();
        } catch (SQLException e) {
            Main.LOG.error("DisabledCommand add failed: " + e);
        } finally {
            sq.closeConnection();
        }

    }

    public void enableCommand(String cmd) {
        cmd = cmd.toLowerCase();
        if (isCommandDisabled(cmd)) {
            disabledCommands.remove(cmd);
        }
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement prep = sq.getConnection().prepareStatement("DELETE IGNORE FROM DisabledCommands WHERE GuildId = ? AND Command = ?");
            prep.setString(1, guildId.toString());
            prep.setString(2, cmd);
            prep.execute();
        } catch (SQLException e) {
            Main.LOG.error("DisabledCommand remove failed: " + e);
        } finally {
            sq.closeConnection();
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
        } catch (SQLException e) {
            Main.LOG.error("", e);
        } finally {
            try {
                if (res != null)
                    res.close();
            } catch (SQLException e) {
                Main.LOG.error("", e);
            }
            sql.closeConnection();
        }
    }

    public void delete() {
        MySQL sq = Main.getMySQL();
        sq.openConnection();
        try {
            PreparedStatement cb = sq.getConnection().prepareStatement("DELETE IGNORE FROM Guilds WHERE GuildId = ?");
            cb.setString(1, guildId.toString());
            cb.execute();
            SettingsManager.guildConfigs.remove(this.guildId);
        } catch (SQLException e) {
            Main.LOG.error("General delete failed: " + e);
        } finally {
            sq.closeConnection();
        }
    }

}
