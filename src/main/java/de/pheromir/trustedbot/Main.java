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
package de.pheromir.trustedbot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.pheromir.trustedbot.commands.AliasAddCommand;
import de.pheromir.trustedbot.commands.AliasCmdsCommand;
import de.pheromir.trustedbot.commands.AliasRemoveCommand;
import de.pheromir.trustedbot.commands.CoinflipCommand;
import de.pheromir.trustedbot.commands.ColorCommand;
import de.pheromir.trustedbot.commands.CreditsCommand;
import de.pheromir.trustedbot.commands.DJAddCommand;
import de.pheromir.trustedbot.commands.DJRemoveCommand;
import de.pheromir.trustedbot.commands.DailyCommand;
import de.pheromir.trustedbot.commands.ExportCommand;
import de.pheromir.trustedbot.commands.ExtraAddCommand;
import de.pheromir.trustedbot.commands.ExtraRemoveCommand;
import de.pheromir.trustedbot.commands.ForwardCommand;
import de.pheromir.trustedbot.commands.GoogleCommand;
import de.pheromir.trustedbot.commands.ImportCommand;
import de.pheromir.trustedbot.commands.NumberFactCommand;
import de.pheromir.trustedbot.commands.PlayCommand;
import de.pheromir.trustedbot.commands.PlayingCommand;
import de.pheromir.trustedbot.commands.PrefixCommand;
import de.pheromir.trustedbot.commands.QueueCommand;
import de.pheromir.trustedbot.commands.R6Command;
import de.pheromir.trustedbot.commands.RandomServerIconCommand;
import de.pheromir.trustedbot.commands.RedditCommand;
import de.pheromir.trustedbot.commands.RewindCommand;
import de.pheromir.trustedbot.commands.SeekCommand;
import de.pheromir.trustedbot.commands.SetCreditsCommand;
import de.pheromir.trustedbot.commands.SkipCommand;
import de.pheromir.trustedbot.commands.StatsCommand;
import de.pheromir.trustedbot.commands.StatusCommand;
import de.pheromir.trustedbot.commands.StopCommand;
import de.pheromir.trustedbot.commands.TextCmdAddCommand;
import de.pheromir.trustedbot.commands.TextCmdRemoveCommand;
import de.pheromir.trustedbot.commands.TextCmdsCommand;
import de.pheromir.trustedbot.commands.ToggleCommand;
import de.pheromir.trustedbot.commands.TwitchCommand;
import de.pheromir.trustedbot.commands.UrbanDictionaryCommand;
import de.pheromir.trustedbot.commands.VolumeCommand;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.commands.images.CatCommand;
import de.pheromir.trustedbot.commands.images.CuddleCommand;
import de.pheromir.trustedbot.commands.images.DogCommand;
import de.pheromir.trustedbot.commands.images.EroKemoCommand;
import de.pheromir.trustedbot.commands.images.GooseCommand;
import de.pheromir.trustedbot.commands.images.HugCommand;
import de.pheromir.trustedbot.commands.images.KemoCommand;
import de.pheromir.trustedbot.commands.images.KissCommand;
import de.pheromir.trustedbot.commands.images.LewdCommand;
import de.pheromir.trustedbot.commands.images.LewdGifCommand;
import de.pheromir.trustedbot.commands.images.LewdKemoCommand;
import de.pheromir.trustedbot.commands.images.LewdYuriCommand;
import de.pheromir.trustedbot.commands.images.LizardCommand;
import de.pheromir.trustedbot.commands.images.LoliCommand;
import de.pheromir.trustedbot.commands.images.NekoCommand;
import de.pheromir.trustedbot.commands.images.NekoGifCommand;
import de.pheromir.trustedbot.commands.images.PatCommand;
import de.pheromir.trustedbot.commands.images.PokeCommand;
import de.pheromir.trustedbot.commands.images.TickleCommand;
import de.pheromir.trustedbot.commands.images.YuriCommand;
import de.pheromir.trustedbot.config.Configuration;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.config.SettingsManager;
import de.pheromir.trustedbot.config.YamlConfiguration;
import de.pheromir.trustedbot.events.CmdListener;
import de.pheromir.trustedbot.events.GuildEvents;
import de.pheromir.trustedbot.events.Shutdown;
import de.pheromir.trustedbot.tasks.RedditGrab;
import de.pheromir.trustedbot.tasks.TwitchCheck;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.User;

public class Main {

	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public static String token;
	public static AudioPlayerManager playerManager;
	public static String adminId = "none";
	public static String youtubeKey = "none";
	public static String twitchClientId = "none";
	private static String twitchSecret = "none";
	public static String twitchToken = "none";
	private static String spotifySecret = "none";
	private static String spotifyClient = "none";
	public static String spotifyToken = "none";
	public static String pastebinKey = "none";
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
	public static ScheduledExecutorService twitchAuthTask;
	public static ScheduledFuture<?> redditTask;
	public static ScheduledFuture<?> twitchTask;
	public static ScheduledFuture<?> rewardTask;
	public static ScheduledFuture<?> avatarTask;
	public static ScheduledFuture<?> serverIconTask;
	public static long exceptionAmount = 0;

	public static final String COMMAND_DISABLED = "This command is disabled in this guild.";

	public static void main(String[] args) {
		LOG.debug("Starting DiscordBot...");

		loadConfig();

		Main.LOG.debug("AdminID: " + adminId);

		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		Unirest.setDefaultHeader("User-Agent", "Mozilla/5.0");
		Unirest.setTimeouts(10000, 30000);
		EventWaiter waiter = new EventWaiter();

		/* COMMANDS KONFIGURIEREN */
		CommandClientBuilder cbuilder = new CommandClientBuilder();
		cbuilder.setOwnerId(adminId);
		cbuilder.setGuildSettingsManager(new SettingsManager());
		// Owner Commands + Settings
		cbuilder.addCommands(new StatusCommand(), new StatsCommand(), new ExtraAddCommand(), new ExtraRemoveCommand(), new PrefixCommand(), new ToggleCommand());
		// Music
		cbuilder.addCommands(new PlayCommand(), new StopCommand(), new VolumeCommand(), new SkipCommand(), new PlayingCommand(), new QueueCommand(), new DJAddCommand(), new DJRemoveCommand(), new SeekCommand(), new ForwardCommand(), new RewindCommand(), new ExportCommand(), new ImportCommand());
		// Alias + Custom Commands
		cbuilder.addCommands(new AliasAddCommand(), new AliasRemoveCommand(), new AliasCmdsCommand(), new TextCmdAddCommand(), new TextCmdRemoveCommand(), new TextCmdsCommand());
		// Subscription Commands
		cbuilder.addCommands(new RedditCommand());
		if (!twitchClientId.equals("none") && !twitchClientId.isEmpty()) {
			cbuilder.addCommands(new TwitchCommand());
			twitchTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TwitchCheck(), 1, 5, TimeUnit.MINUTES);
		}
		// Money
		cbuilder.addCommands(new CreditsCommand(), new SetCreditsCommand(), new DailyCommand());
		// Minigames and Gambling
		cbuilder.addCommands(new CoinflipCommand());
		// Fun
		cbuilder.addCommands(new NekoCommand(), new NekoGifCommand(), new KemoCommand(), new TickleCommand(), new PokeCommand(), new CuddleCommand(), new PatCommand(), new LizardCommand(), new GooseCommand(), new CatCommand(), new DogCommand(), new KissCommand(), new HugCommand(), new LewdCommand(), new LewdGifCommand(), new EroKemoCommand(), new LoliCommand(), new LewdKemoCommand(), new LewdYuriCommand(), new YuriCommand());
		// Misc
		cbuilder.addCommands(new RandomServerIconCommand(), new GoogleCommand(), new NumberFactCommand(), new UrbanDictionaryCommand(), new R6Command(), new ColorCommand(waiter));

		cbuilder.setLinkedCacheSize(0);
		cbuilder.setListener(new CmdListener());
		cbuilder.setGame(Game.playing("Trusted-Community.eu"));

		cbuilder.setEmojis("\u2705", "", "");

		cbuilder.setHelpConsumer((event) -> getHelpConsumer(event));

		commandClient = cbuilder.build();
		try {
			/* - - - - - - - - - - - BOT STARTEN - - - - - - - - - - - - - - */
			jda = new JDABuilder(
					AccountType.BOT).setToken(token).addEventListener(commandClient, new GuildEvents(), new Shutdown(), waiter).setAutoReconnect(true).build();
			jda.awaitReady();
			jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
			
			// - - - - TASKS - - - - -
			
			
			
			redditTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new RedditGrab(), 1, 10, TimeUnit.MINUTES);
			rewardTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				LocalTime today = LocalTime.now();
				int hour = today.getHour();
				int min = today.getMinute();
				if (hour == 0 && min == 0) {
					Main.LOG.debug("Resetting daily rewards..");
					jda.getGuilds().forEach(gld -> Main.getGuildConfig(gld).resetDailyRewards());
				}
			}, 1, 1, TimeUnit.MINUTES);

			avatarTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				try {
					jda.getSelfUser().getManager().setAvatar(Icon.from(Unirest.get(Methods.getRandomAvatarURL()).asBinary().getBody())).queue();
				} catch (Exception e) {
					LOG.error("Error updating RandomAvatar: ", e);
				}
			}, 0, 1, TimeUnit.HOURS);

			serverIconTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				try {
					jda.getGuilds().forEach(g -> {
						if (getGuildConfig(g).getRandomServerIcon()) {
							try {
								g.getManager().setIcon(Icon.from(Unirest.get(Methods.getRandomAvatarURL()).asBinary().getBody())).queue();
							} catch (IOException | UnirestException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					LOG.error("Error updating random ServerIcon: ", e);
				}
			}, 0, 1, TimeUnit.HOURS);

			if (!spotifyClient.equals("none") && !spotifySecret.equals("none")) {
				spotifyTask = Executors.newScheduledThreadPool(1);
				spotifyTask.scheduleAtFixedRate(() -> {
					Thread.currentThread().setName("Spotify-Task");
					Unirest.post("https://accounts.spotify.com/api/token").basicAuth(spotifyClient, spotifySecret).header("Content-Type", "application/x-www-form-urlencoded").body("grant_type=client_credentials").asJsonAsync(new Callback<JsonNode>() {

						@Override
						public void cancelled() {
							spotifyToken = "none";
							LOG.error("Spotify Token renew cancelled.");
						}

						@Override
						public void completed(HttpResponse<JsonNode> r) {
							JSONObject jo = r.getBody().getObject();
							if (jo.has("error") && jo.getString("error").equals("invalid_client")) {
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
			
			if(!twitchClientId.equals("none") && !twitchSecret.equals("none")) {
				twitchAuthTask = Executors.newScheduledThreadPool(1);
				twitchAuthTask.scheduleAtFixedRate(() -> {
					Unirest.post(String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials", Main.twitchClientId, Main.twitchSecret)).asJsonAsync(new Callback<JsonNode>() {

						@Override
						public void completed(HttpResponse<JsonNode> response) {
							if(response.getStatus() != 200) {
								LOG.error("Error getting a twitch access token");
								if(response.getStatus() == 403) {
									LOG.error("Cause: " + response.getBody().getObject().getString("message"));
								}
								twitchToken = "none";
								twitchAuthTask.shutdownNow();
								return;
							}
							twitchToken = response.getBody().getObject().getString("access_token");
							LOG.debug("Twitch App Token renewed.");
						}

						@Override
						public void failed(UnirestException e) {
							twitchToken = "none";
							LOG.error("Twitch Token renew failed: " + e.getMessage());
						}

						@Override
						public void cancelled() {
							twitchToken = "none";
							LOG.error("Twitch Token renew cancelled.");
						}
						
					});
				}, 0, 12, TimeUnit.HOURS);
			}
			// - - - - - - -

		} catch (LoginException | InterruptedException | IllegalStateException e) {
			if (e instanceof InterruptedException) {
				LOG.error("Error while starting the bot:", e);
			} else {
				LOG.error("Error while starting the bot: Bot-Token invalid");
			}
			return;
		}
		LOG.info("Bot startup complete.");
		new Thread(new ConsoleCommands()).start();
	}

	private static void loadConfig() {
		if (!configFile.exists()) {
			try {
				Files.copy(Main.class.getResourceAsStream("/config.yml"), Paths.get("config.yml"), StandardCopyOption.REPLACE_EXISTING);
				LOG.error("-- Please set up the configuration file --");
				System.exit(1);
			} catch (IOException e) {
				LOG.error("", e);
			}
		}

		try {
			cfg = yaml.load(configFile);
			token = cfg.getString("Token");
			adminId = cfg.getString("AdminID").replaceAll("[A-Za-z]+", "");
			youtubeKey = cfg.getString("API-Keys.YouTube");
			twitchClientId = cfg.getString("API-Keys.TwitchClientId");
			twitchSecret = cfg.getString("API-Keys.TwitchSecret");
			spotifyClient = cfg.getString("API-Keys.Spotify.Client");
			spotifySecret = cfg.getString("API-Keys.Spotify.Secret");
			pastebinKey = cfg.getString("API-Keys.Pastebin");
			extraPermissions = cfg.getLongList("ExtraPermissions");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Guilds" + " (GuildId VARCHAR(64) PRIMARY KEY,"
					+ " Volume INT NOT NULL DEFAULT 100," + " Prefix VARCHAR(16) NOT NULL DEFAULT \"!\","
					+ " RandomServerIcon BOOLEAN NOT NULL DEFAULT FALSE);");

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

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS AliasCommands" + " (Name VARCHAR(128) NOT NULL,"
					+ " GuildId VARCHAR(64) NOT NULL," + " Command VARCHAR(64) NOT NULL,"
					+ " Arguments LONGTEXT NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (Name, GuildId));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS CustomCommands" + " (Name VARCHAR(128) NOT NULL,"
					+ " GuildId VARCHAR(64) NOT NULL," + " Text LONGTEXT NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (Name, GuildId));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS DisabledCommands" + " (GuildId VARCHAR(64) NOT NULL,"
					+ " Command VARCHAR(128) NOT NULL,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (GuildId, Command));");

			Methods.mySQLQuery("CREATE TABLE IF NOT EXISTS Credits" + " (GuildId VARCHAR(64) NOT NULL,"
					+ " UserId VARCHAR(64) NOT NULL," + " Amount BIGINT NOT NULL DEFAULT 0,"
					+ " FOREIGN KEY (GuildId) REFERENCES Guilds(GuildId) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ " PRIMARY KEY (GuildId, UserId));");

		} catch (IOException e) {
			LOG.error("", e);
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
				LOG.error("", e);
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
				LOG.error("", e);
			}
		}
	}

	public static MySQL getMySQL() {
		return new MySQL();
	}

	public static void getHelpConsumer(CommandEvent event) {
		StringBuilder builder = new StringBuilder("**Available commands for "
				+ (event.getChannelType() == ChannelType.TEXT ? "the requested Guild" : "Direct Messages")
				+ ":**\n*Note: The command prefix may vary between guilds. The prefix in Direct Messages is always "
				+ commandClient.getTextualPrefix() + ".*\n");
		builder.append("\nCommands with the `[NSFW]`-tag can only be used in NSFW-Channels.\n");
		Category category = null;
		for (Command command1 : commandClient.getCommands()) {
			TrustedCommand command = (TrustedCommand) command1;
			if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())
					&& (!command.isGuildOnly() && event.getChannelType() == ChannelType.PRIVATE
							|| (event.isFromType(ChannelType.TEXT)
									&& event.getMember().hasPermission(command.getUserPermissions())
									&& !Main.getGuildConfig(event.getGuild()).isCommandDisabled(command.getName())))) {
				if (!Objects.equals(category, command.getCategory())) {
					category = command.getCategory();
					builder.append("\n\n__").append(category == null ? "No Category"
							: category.getName()).append("__:\n");
				}
				builder.append("\n`").append(event.getChannelType() == ChannelType.TEXT
						? Main.getGuildConfig(event.getGuild()).getPrefix()
						: commandClient.getTextualPrefix()).append(command.getName()).append(command.getArguments() == null
								? "`"
								: " " + command.getArguments() + "`").append(" - ").append(command.getHelp());
				if (command.isNSFW()) {
					builder.append(" *[NSFW]*");
				}
				if (command.costsCredits()) {
					builder.append(" **[" + command.getCreditCost() + " credit"
							+ (command.getCreditCost() == 1 ? "" : "s") + "]**");
				}
			}
		}
		User owner = event.getJDA().getUserById(commandClient.getOwnerId());
		if (owner != null) {
			// builder.append("\n\nFor additional help, contact
			// **").append(owner.getName()).append("**#").append(owner.getDiscriminator());
			if (commandClient.getServerInvite() != null)
				builder.append(" or join ").append(commandClient.getServerInvite());
		}
		event.replyInDm(builder.toString(), unused -> {
			if (event.isFromType(ChannelType.TEXT))
				event.reactSuccess();
		}, t -> event.reply("Help cannot be sent because you are blocking Direct Messages."));
	}

}
