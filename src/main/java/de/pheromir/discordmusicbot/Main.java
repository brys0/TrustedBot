package de.pheromir.discordmusicbot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.pheromir.discordmusicbot.commands.CBCommand;
import de.pheromir.discordmusicbot.commands.DJAddCommand;
import de.pheromir.discordmusicbot.commands.DJRemoveCommand;
import de.pheromir.discordmusicbot.commands.ExtraAddCommand;
import de.pheromir.discordmusicbot.commands.ExtraRemoveCommand;
import de.pheromir.discordmusicbot.commands.GCCommand;
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
import de.pheromir.discordmusicbot.commands.RedditCommand;
import de.pheromir.discordmusicbot.commands.ResumeCommand;
import de.pheromir.discordmusicbot.commands.SkipCommand;
import de.pheromir.discordmusicbot.commands.StatusCommand;
import de.pheromir.discordmusicbot.commands.StopCommand;
import de.pheromir.discordmusicbot.commands.TwitchCommand;
import de.pheromir.discordmusicbot.commands.VolumeCommand;
import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.GuildConfig;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
import de.pheromir.discordmusicbot.tasks.CBCheck;
import de.pheromir.discordmusicbot.tasks.ClearRedditPostHistory;
import de.pheromir.discordmusicbot.tasks.RedditGrab;
import de.pheromir.discordmusicbot.tasks.TwitchCheck;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

public class Main {

	public static String token;
	public static AudioPlayerManager playerManager;
	public static HashMap<Long, GuildConfig> guildConfigs = new HashMap<>();
	public static String youtubeKey = "none".intern();
	public static String twitchKey = "none".intern();
	public static ArrayList<String> onlineTwitchList = new ArrayList<>();
	public static ArrayList<String> onlineCBList = new ArrayList<>();
	public static List<Long> extraPermissions = new ArrayList<>();
	public static JDA jda;
	public static final Long startMillis = System.currentTimeMillis();
	public static File configFile = new File("config.yml".intern());
	public static YamlConfiguration yaml = new YamlConfiguration();
	public static Configuration cfg;

	public static void main(String[] args) {
		System.out.println("Starting DiscordBot...");
		
		CommandClientBuilder builder = new CommandClientBuilder();
		
		/* CONFIG ERSTELLEN / AUSLESEN */
		
		if (!configFile.exists()) {
			try {
				Files.copy(Main.class.getResourceAsStream("/config.yml"), Paths.get("config.yml".intern()), StandardCopyOption.REPLACE_EXISTING);
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
			builder.setOwnerId(cfg.getString("AdminID"));
			youtubeKey = cfg.getString("API-Keys.YouTube");
			twitchKey = cfg.getString("API-Keys.Twitch");
			extraPermissions = cfg.getLongList("ExtraPermissions");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Guilds"
					+ " (GuildId VARCHAR(64) PRIMARY KEY,"
					+ " Volume INT NOT NULL DEFAULT 100)");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS DJs"
					+ " (GuildId VARCHAR(64) NOT NULL,"
					+ " UserId VARCHAR(64) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId),"
					+ " PRIMARY KEY (GuildId, UserId))");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Twitch"
					+ " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Username VARCHAR(32) NOT NULL,"
					+ " PRIMARY KEY (ChannelId, Username))");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Reddit"
					+ " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Subreddit VARCHAR(32) NOT NULL,"
					+ " PRIMARY KEY (ChannelId, Subreddit))");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Reddit_Posts"
					+ " (Url VARCHAR(191) PRIMARY KEY)");
			
			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Chaturbate"
					+ " (ChannelId VARCHAR(64) NOT NULL,"
					+ " Username VARCHAR(32) NOT NULL,"
					+ " PRIMARY KEY (ChannelId, Username))");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* COMMANDS KONFIGURIEREN */
		
		builder.setPrefix("!");
		builder.useHelpBuilder(false);
		

		builder.addCommands(new StatusCommand(), new ExtraAddCommand(), new ExtraRemoveCommand(), new MemoryCommand(), new GCCommand());
		builder.addCommands(new NekoCommand(), new LewdCommand(), new PatCommand(), new LizardCommand(), new KissCommand(), new HugCommand());
		builder.addCommands(new PlayCommand(), new StopCommand(), new VolumeCommand(), new SkipCommand(), new PauseCommand(), new ResumeCommand(), new PlayingCommand(), new PlaylistCommand(), new DJAddCommand(), new DJRemoveCommand());
		builder.addCommands(new GoogleCommand(), new RedditCommand(), new CBCommand());
		if (!twitchKey.equals("none") && !twitchKey.isEmpty()) {
			builder.addCommands(new TwitchCommand());
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TwitchCheck(), 5, 5, TimeUnit.MINUTES);
		}

		builder.setEmojis("\u2705", "\u26A0", "\u274C");
		try {
			/* BOT STARTEN */
			jda = new JDABuilder(
					AccountType.BOT).setToken(token).addEventListener(builder.build()).setAutoReconnect(true).build();
			jda.awaitReady();
			jda.getPresence().setGame(Game.playing("Trusted-Community.eu"));
			playerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(playerManager);
			loadAllGuildConfigs();
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new RedditGrab(), 60, 60, TimeUnit.SECONDS);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ClearRedditPostHistory(), 30, 30, TimeUnit.DAYS);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new CBCheck(), 30, 30, TimeUnit.MINUTES);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				System.gc();
			}, 2, 2, TimeUnit.HOURS);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				Main.jda.getPresence().setGame(Game.playing(String.format("%d / %d / %d", Runtime.getRuntime().freeMemory() / 1024L / 1024L, Runtime.getRuntime().totalMemory() / 1024L / 1024L, Runtime.getRuntime().maxMemory() / 1024L / 1024L)));
			}, 1, 1, TimeUnit.MINUTES);
			

		} catch (LoginException | InterruptedException | IllegalStateException e) {
			System.out.print("Fehler beim Start des Bots: ");
			if (e instanceof InterruptedException) {
				e.printStackTrace();
			} else {
				System.out.println("Bot-Token ungültig");
			}
		}
	}

	public static void loadAllGuildConfigs() {
		for (Guild g : jda.getGuilds()) {
			guildConfigs.put(g.getIdLong(), getGuildConfig(g));
		}
	}

	public static synchronized GuildConfig getGuildConfig(Guild g) {
		GuildConfig cfg = null;
		if (guildConfigs.containsKey(g.getIdLong())) {
			cfg = guildConfigs.get(g.getIdLong());
		} else {
			cfg = new GuildConfig(g);
			guildConfigs.put(g.getIdLong(), cfg);
		}
		return cfg;
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
