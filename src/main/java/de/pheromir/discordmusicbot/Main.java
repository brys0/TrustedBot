package de.pheromir.discordmusicbot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.pheromir.discordmusicbot.commands.AliasAddCommand;
import de.pheromir.discordmusicbot.commands.AliasCmdsCommand;
import de.pheromir.discordmusicbot.commands.AliasRemoveCommand;
import de.pheromir.discordmusicbot.commands.CBCommand;
import de.pheromir.discordmusicbot.commands.DJAddCommand;
import de.pheromir.discordmusicbot.commands.DJRemoveCommand;
import de.pheromir.discordmusicbot.commands.ExtraAddCommand;
import de.pheromir.discordmusicbot.commands.ExtraRemoveCommand;
import de.pheromir.discordmusicbot.commands.ForwardCommand;
import de.pheromir.discordmusicbot.commands.GoogleCommand;
import de.pheromir.discordmusicbot.commands.HugCommand;
import de.pheromir.discordmusicbot.commands.KissCommand;
import de.pheromir.discordmusicbot.commands.LewdCommand;
import de.pheromir.discordmusicbot.commands.LizardCommand;
import de.pheromir.discordmusicbot.commands.MemoryCommand;
import de.pheromir.discordmusicbot.commands.NekoCommand;
import de.pheromir.discordmusicbot.commands.PatCommand;
import de.pheromir.discordmusicbot.commands.PauseCommand;
import de.pheromir.discordmusicbot.commands.PlayCommand;
import de.pheromir.discordmusicbot.commands.PlayingCommand;
import de.pheromir.discordmusicbot.commands.PlaylistCommand;
import de.pheromir.discordmusicbot.commands.PrefixCommand;
import de.pheromir.discordmusicbot.commands.RedditCommand;
import de.pheromir.discordmusicbot.commands.ResumeCommand;
import de.pheromir.discordmusicbot.commands.RewindCommand;
import de.pheromir.discordmusicbot.commands.SeekCommand;
import de.pheromir.discordmusicbot.commands.SkipCommand;
import de.pheromir.discordmusicbot.commands.StatusCommand;
import de.pheromir.discordmusicbot.commands.StopCommand;
import de.pheromir.discordmusicbot.commands.TextCmdAddCommand;
import de.pheromir.discordmusicbot.commands.TextCmdRemoveCommand;
import de.pheromir.discordmusicbot.commands.TextCmdsCommand;
import de.pheromir.discordmusicbot.commands.TwitchCommand;
import de.pheromir.discordmusicbot.commands.UrbanDictionaryCommand;
import de.pheromir.discordmusicbot.commands.VolumeCommand;
import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.GuildConfig;
import de.pheromir.discordmusicbot.config.SettingsManager;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
import de.pheromir.discordmusicbot.events.CmdListener;
import de.pheromir.discordmusicbot.events.GuildJoin;
import de.pheromir.discordmusicbot.events.GuildLeave;
import de.pheromir.discordmusicbot.events.Shutdown;
import de.pheromir.discordmusicbot.tasks.CBCheck;
import de.pheromir.discordmusicbot.tasks.ClearRedditPostHistory;
import de.pheromir.discordmusicbot.tasks.RedditGrab;
import de.pheromir.discordmusicbot.tasks.TwitchCheck;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

public class Main {

	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public static String token;
	public static AudioPlayerManager playerManager;
	public static String adminId = "none";
	public static String youtubeKey = "none";
	public static String twitchKey = "none";
	private static String spotifySecret = "none";
	private static String spotifyClient = "none";
	public static String spotifyToken = "none";
	public static ArrayList<String> onlineTwitchList = new ArrayList<>();
	public static ArrayList<String> onlineCBList = new ArrayList<>();
	public static List<Long> extraPermissions = new ArrayList<>();
	public static JDA jda;
	public static final Long startMillis = System.currentTimeMillis();
	public static File configFile = new File("config.yml");
	public static YamlConfiguration yaml = new YamlConfiguration();
	public static Configuration cfg;
	public static CommandClient commandClient;
	public static ScheduledExecutorService spotifyTask;

	public static void main(String[] args) {
		LOG.debug("Starting DiscordBot...");

		loadConfig();

		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		Unirest.setDefaultHeader("User-Agent", "Mozilla/5.0");

		/* COMMANDS KONFIGURIEREN */
		CommandClientBuilder builder = new CommandClientBuilder();
		builder.useHelpBuilder(false);
		builder.setOwnerId(adminId);
		builder.setGuildSettingsManager(new SettingsManager());
		builder.addCommands(new StatusCommand(), new ExtraAddCommand(), new ExtraRemoveCommand(), new MemoryCommand());
		builder.addCommands(new NekoCommand(), new LewdCommand(), new PatCommand(), new LizardCommand(), new KissCommand(), new HugCommand());
		builder.addCommands(new PlayCommand(), new StopCommand(), new VolumeCommand(), new SkipCommand(), new PauseCommand(), new ResumeCommand(), new PlayingCommand(), new PlaylistCommand(), new DJAddCommand(), new DJRemoveCommand(), new SeekCommand(), new ForwardCommand(), new RewindCommand());
		builder.addCommands(new GoogleCommand(), new RedditCommand(), new CBCommand(), new UrbanDictionaryCommand());
		builder.addCommands(new AliasAddCommand(), new AliasRemoveCommand(), new AliasCmdsCommand(), new TextCmdAddCommand(), new TextCmdRemoveCommand(), new TextCmdsCommand(), new PrefixCommand());
		builder.setLinkedCacheSize(512);
		builder.setListener(new CmdListener());
		builder.setGame(Game.playing("Trusted-Community.eu"));
		if (!twitchKey.equals("none") && !twitchKey.isEmpty()) {
			builder.addCommands(new TwitchCommand());
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TwitchCheck(), 5, 5, TimeUnit.MINUTES);
		}

		builder.setEmojis("\u2705", "\u26A0", "\u274C");
		commandClient = builder.build();
		try {
			/* BOT STARTEN */
			jda = new JDABuilder(
					AccountType.BOT).setToken(token).addEventListener(commandClient, new GuildLeave(), new GuildJoin(), new Shutdown()).setAutoReconnect(true).build();
			jda.awaitReady();
			jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new RedditGrab(), 15, 30, TimeUnit.MINUTES);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ClearRedditPostHistory(), 30, 30, TimeUnit.DAYS);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new CBCheck(), 15, 15, TimeUnit.MINUTES);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				Main.jda.getPresence().setGame(Game.playing("Trusted-Community.eu"));
			}, 0, 60, TimeUnit.SECONDS);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				Main.jda.getPresence().setGame(Game.playing(String.format("%d / %d / %d", Runtime.getRuntime().freeMemory()
						/ 1024L / 1024L, Runtime.getRuntime().totalMemory() / 1024L
								/ 1024L, Runtime.getRuntime().maxMemory() / 1024L / 1024L)));
			}, 30, 60, TimeUnit.SECONDS);
			if (!spotifyClient.equals("none") && !spotifySecret.equals("none")) {
				spotifyTask = Executors.newScheduledThreadPool(1);
				spotifyTask.scheduleAtFixedRate(() -> {
					Unirest.post("https://accounts.spotify.com/api/token").basicAuth(spotifyClient, spotifySecret).header("Content-Type", "application/x-www-form-urlencoded").body("grant_type=client_credentials").asJsonAsync(new Callback<JsonNode>() {

						@Override
						public void cancelled() {
							spotifyToken = "none";
							LOG.error("Spotify Token renew cancelled.");
						}

						@Override
						public void completed(HttpResponse<JsonNode> r) {
							JSONObject jo = r.getBody().getObject();
							if(jo.has("error") && jo.getString("error").equals("invalid_client")) {
								LOG.error("Spotify Token renew failed: Invalid Client");
								spotifyToken = "none";
								spotifyTask.shutdownNow();
								return;
							}
							spotifyToken = jo.getString("access_token");
							LOG.debug("Spotify Token renewed.");
						}

						@Override
						public void failed(UnirestException e) {
							spotifyToken = "none";
							LOG.error("Spotify Token renew failed: " + e.getMessage());
						}
					});
				}, 0, 3600, TimeUnit.SECONDS);
			}

		} catch (LoginException | InterruptedException | IllegalStateException e) {
			System.out.print("Fehler beim Start des Bots: ");
			if (e instanceof InterruptedException) {
				e.printStackTrace();
			} else {
				System.out.println("Bot-Token ungültig");
			}
		}
	}

	private static void loadConfig() {
		if (!configFile.exists()) {
			try {
				Files.copy(Main.class.getResourceAsStream("/config.yml"), Paths.get("config.yml"), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("-- Please set up the configuration file --");
				Thread.sleep(30000);
				System.exit(1);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			cfg = yaml.load(configFile);
			token = cfg.getString("Token");
			adminId = cfg.getString("AdminID");
			youtubeKey = cfg.getString("API-Keys.YouTube");
			twitchKey = cfg.getString("API-Keys.Twitch");
			spotifyClient = cfg.getString("API-Keys.Spotify.Client");
			spotifySecret = cfg.getString("API-Keys.Spotify.Secret");
			extraPermissions = cfg.getLongList("ExtraPermissions");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Guilds" + " (GuildId VARCHAR(64) PRIMARY KEY,"
					+ " Volume INT NOT NULL DEFAULT 100," + " Prefix VARCHAR(16) NOT NULL DEFAULT \"!\");");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS DJs" + " (GuildId VARCHAR(64) NOT NULL,"
					+ " UserId VARCHAR(64) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (GuildId, UserId));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Twitch" + " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Username VARCHAR(32) NOT NULL," + " GuildId VARCHAR(64) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (ChannelId, Username));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Reddit" + " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Subreddit VARCHAR(32) NOT NULL," + " GuildId VARCHAR(64) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (ChannelId, Subreddit));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Reddit_Posts" + " (Url VARCHAR(191) PRIMARY KEY);");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Chaturbate" + " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Username VARCHAR(32) NOT NULL," + " GuildId VARCHAR(64) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (ChannelId, Username));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS AliasCommands" + " (Name VARCHAR(128) NOT NULL,"
					+ " GuildId VARCHAR(64) NOT NULL," + " Command VARCHAR(64) NOT NULL,"
					+ " Arguments LONGTEXT NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (Name, GuildId));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS CustomCommands" + " (Name VARCHAR(128) NOT NULL,"
					+ " GuildId VARCHAR(64) NOT NULL," + " Text LONGTEXT NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (Name, GuildId));");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static GuildConfig getGuildConfig(Guild g) {
		return Main.commandClient.getSettingsFor(g);
	}

	public static List<Long> getExtraUsers() {
		return extraPermissions;
	}

	public static void addExtraUser(Long id) {
		if (!extraPermissions.contains(id)) {
			extraPermissions.add(id);
			cfg.set("ExtraPermissions", extraPermissions);
			try {
				yaml.save(cfg, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeExtraUser(Long id) {
		if (extraPermissions.contains(id)) {
			extraPermissions.remove(id);
			cfg.set("ExtraPermissions", extraPermissions);
			try {
				yaml.save(cfg, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static MySQL getMySQL() {
		return new MySQL();
	}

}
