package de.pheromir.discordmusicbot.listener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	public static ArrayList<String> nsfwRedditPosts = new ArrayList<>();
	public static TextChannel serverchannel = null;

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getChannelType() != ChannelType.TEXT) return;
		TextChannel sc = e.getTextChannel();
		if(sc.getName().equalsIgnoreCase("nsfw-reddit")) {
			serverchannel = sc;
			if(e.getMessage().getAuthor().isBot()) {
				if(!nsfwRedditPosts.contains(this.getImageLink(e.getMessage().getContentDisplay()))) {
					nsfwRedditPosts.add(this.getImageLink(e.getMessage().getContentDisplay()));
				} else {
					e.getMessage().delete().complete();
				}
			}
		}
	}
	
	public String getImageLink(String msg) {
		Pattern pattern = Pattern.compile("(http.*)$");
		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
		    return matcher.group(1);
		} else return "";
	}
}
