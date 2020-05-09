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
package de.pheromir.trustedbot.events;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.config.SettingsManager;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class GuildEvents extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		if(Main.getGuildConfig(e.getGuild()).player.getPlayingTrack() != null) {
			Main.getGuildConfig(e.getGuild()).player.destroy();
		}
		SettingsManager.guildConfigs.remove(e.getGuild().getIdLong());
		
	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent e) {
		long channelId = e.getChannel().getIdLong();
		GuildConfig.getTwitchList().entrySet().stream().filter(ent -> ent.getValue().contains(channelId)).forEach(ent -> GuildConfig.removeTwitchStream(ent.getKey(), channelId));
		GuildConfig.getRedditList().entrySet().stream().filter(ent -> ent.getValue().containsChannel(channelId)).forEach(ent -> GuildConfig.removeSubreddit(ent.getKey(), channelId));
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		Main.getGuildConfig(e.getGuild()).setUserCredits(e.getMember().getUser().getIdLong(), 0L);
	}

	// @Override
	// public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
	// GuildConfig gc = Main.getGuildConfig(e.getGuild());
	// if (gc.getDJs().contains(e.getUser().getIdLong())) {
	// gc.removeDJ(e.getUser().getIdLong());
	// }
	// if (Main.getExtraUsers().contains(e.getUser().getIdLong())) {
	// Main.removeExtraUser(e.getUser().getIdLong());
	// }
	// gc.deleteUserCredits(e.getMember().getUser().getIdLong());
	// }

	@Override
	public void onGuildReady(GuildReadyEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

}
