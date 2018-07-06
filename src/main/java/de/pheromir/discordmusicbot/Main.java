package de.pheromir.discordmusicbot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.pheromir.discordmusicbot.commands.DJAddCommand;
import de.pheromir.discordmusicbot.commands.DJRemoveCommand;
import de.pheromir.discordmusicbot.commands.GoogleCommand;
import de.pheromir.discordmusicbot.commands.HugCommand;
import de.pheromir.discordmusicbot.commands.KissCommand;
import de.pheromir.discordmusicbot.commands.LewdCommand;
import de.pheromir.discordmusicbot.commands.LizardCommand;
import de.pheromir.discordmusicbot.commands.NekoCommand;
import de.pheromir.discordmusicbot.commands.PatCommand;
import de.pheromir.discordmusicbot.commands.PauseCommand;
import de.pheromir.discordmusicbot.commands.PlayCommand;
import de.pheromir.discordmusicbot.commands.PlayingCommand;
import de.pheromir.discordmusicbot.commands.PlaylistCommand;
import de.pheromir.discordmusicbot.commands.RandomCommand;
import de.pheromir.discordmusicbot.commands.ResumeCommand;
import de.pheromir.discordmusicbot.commands.SkipCommand;
import de.pheromir.discordmusicbot.commands.StatusCommand;
import de.pheromir.discordmusicbot.commands.StopCommand;
import de.pheromir.discordmusicbot.commands.TwitchCommand;
import de.pheromir.discordmusicbot.commands.VolumeCommand;
import de.pheromir.discordmusicbot.config.Configuration;
import de.pheromir.discordmusicbot.config.YamlConfiguration;
import de.pheromir.discordmusicbot.handler.GuildMusicManager;
import de.pheromir.discordmusicbot.helper.GuildConfig;
import de.pheromir.discordmusicbot.helper.TwitchCheckTimer;
import de.pheromir.discordmusicbot.listener.VoiceChannelListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

public class Main {

	public static String token = "00000000";
	public static String adminID = "00000000";
	public static EventWaiter waiter = new EventWaiter();
	public static AudioPlayerManager playerManager;
	public static HashMap<Long, GuildMusicManager> musicManagers;
	public static HashMap<Long, GuildConfig> guildConfigs;
	public static String giphyKey = "none";
	public static String youtubeKey = "none";
	public static String twitchKey = "none";
	public static ArrayList<String> generalTwitchList = new ArrayList<>();
	public static ArrayList<String> onlineTwitchList = new ArrayList<>();
	public static JDA jda;

	public static void main(String[] args) {

		/* CONFIG ERSTELLEN / AUSLESEN */
		createConfig();

		if (token.equals("00000000")) {
			System.out.println("FEHLER: Ungültiger Token.");
			return;
		}

		/* COMMANDS KONFIGURIEREN */
		CommandClientBuilder builder = new CommandClientBuilder();
		builder.setPrefix("!");
		builder.useHelpBuilder(false);
		builder.setOwnerId(adminID);

		builder.addCommand(new StatusCommand());
		builder.addCommands(new NekoCommand(), new LewdCommand(), new PatCommand(), new LizardCommand(), new KissCommand(), new HugCommand());
		builder.addCommands(new PlayCommand(), new StopCommand(), new VolumeCommand(), new SkipCommand(), new PauseCommand(), new ResumeCommand(), new PlayingCommand(), new PlaylistCommand(),
			new DJAddCommand(), new DJRemoveCommand());
		builder.addCommands(new GoogleCommand());
		if (!giphyKey.equals("none") && !giphyKey.isEmpty()) {
			builder.addCommands(new RandomCommand());
		}
		if(!twitchKey.equals("none") && !twitchKey.isEmpty()) {
			builder.addCommands(new TwitchCommand());
		}

		builder.setEmojis("\u2705", "\u26A0", "\u274C");
		try {
			/* BOT STARTEN */
			jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(builder.build()).addEventListener(waiter).setAutoReconnect(true).buildBlocking();
			jda.getPresence().setGame(Game.playing("Trusted-Community.eu"));

			/* EVENTS */
			jda.addEventListener(new VoiceChannelListener());

			System.out.println("-----------------------------------");
			System.out.println("OWNERID: " + adminID);
			System.out.println("-----------------------------------");

			musicManagers = new HashMap<>();
			guildConfigs = new HashMap<>();
			loadAllGuildConfigs();
			renewGeneralTwitchList();
			if(!twitchKey.equals("none") && !twitchKey.isEmpty()) {
				Timer t = new Timer();
				t.schedule(new TwitchCheckTimer(), 10*1000, 5*60*1000);
			}

			playerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(playerManager);
			AudioSourceManagers.registerLocalSource(playerManager);

		} catch (LoginException | InterruptedException e) {
			System.out.println("Fehler beim Start des Bots: ");
			e.printStackTrace();
		}

	}

	public static void createConfig() {
		File dir = new File("config");
		if (!dir.exists())
			dir.mkdir();
		File configFile = new File("config//config.yml");
		YamlConfiguration yaml = new YamlConfiguration();
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				Configuration cfg = yaml.load(configFile);
				cfg.set("Token", "00000000");
				cfg.set("AdminID", "00000000");
				cfg.set("API-Keys.YouTube", "none");
				cfg.set("API-Keys.Giphy", "none");
				cfg.set("API-Keys.Twitch", "none");
				yaml.save(cfg, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Configuration cfg = yaml.load(configFile);
			token = cfg.getString("Token");
			adminID = cfg.getString("AdminID");
			giphyKey = cfg.getString("API-Keys.Giphy");
			youtubeKey = cfg.getString("API-Keys.YouTube");
			twitchKey = cfg.getString("API-Keys.Twitch");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = guild.getIdLong();
		GuildMusicManager musicManager = musicManagers.get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager, getGuildConfig(guild).getVolume(), guild);
			musicManagers.put(guildId, musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		return musicManager;
	}

	public static void loadAllGuildConfigs() {
		for (Guild g : jda.getGuilds()) {
			guildConfigs.put(g.getIdLong(), getGuildConfig(g));
		}
	}

	public static GuildConfig getGuildConfig(Guild g) {
		if (guildConfigs.containsKey(g.getIdLong())) {
			return guildConfigs.get(g.getIdLong());
		} else {
			GuildConfig gc = new GuildConfig(g);
			guildConfigs.put(g.getIdLong(), gc);
			return gc;
		}
	}
	
	public static void renewGeneralTwitchList() {
		for(Guild g : jda.getGuilds()) {
			for(String str : getGuildConfig(g).getTwitchList().keySet()) {
				if(!generalTwitchList.contains(str)) {
					generalTwitchList.add(str);
				}
			}
		}
	}

}
