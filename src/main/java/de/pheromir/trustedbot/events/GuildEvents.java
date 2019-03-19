package de.pheromir.trustedbot.events;

import java.util.ArrayList;
import java.util.stream.Collectors;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.config.SettingsManager;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildEvents extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Main.getGuildConfig(e.getGuild()).delete();
		SettingsManager.guildConfigs.remove(e.getGuild().getIdLong());
	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent e) {
		ArrayList<String> twstreams = (ArrayList<String>) GuildConfig.getTwitchList().keySet().stream().filter(k -> GuildConfig.getTwitchList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
		ArrayList<String> reddits = (ArrayList<String>) GuildConfig.getRedditList().keySet().stream().filter(k -> GuildConfig.getRedditList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
		twstreams.forEach(s -> GuildConfig.removeTwitchStream(s, e.getChannel().getIdLong()));
		reddits.forEach(s -> GuildConfig.removeSubreddit(s, e.getChannel().getIdLong()));
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		Main.getGuildConfig(e.getGuild()).setUserCredits(e.getMember().getUser().getIdLong(), 0L);
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		if (Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getUser().getIdLong())) {
			Main.getGuildConfig(e.getGuild()).removeDJ(e.getUser().getIdLong());
		}
		if (Main.getExtraUsers().contains(e.getUser().getIdLong())) {
			Main.removeExtraUser(e.getUser().getIdLong());
		}
		Main.getGuildConfig(e.getGuild()).deleteUserCredits(e.getMember().getUser().getIdLong());
	}
	
	@Override
	public void onGuildReady(GuildReadyEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

}
