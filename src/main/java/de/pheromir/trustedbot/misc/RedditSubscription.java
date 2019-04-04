package de.pheromir.trustedbot.misc;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class RedditSubscription {
	
	private String subreddit;
	private HashMap<Long, SortType> channel;
	
	public RedditSubscription(String subreddit) {
		this.subreddit = subreddit;
		channel = new HashMap<>();
	}
	
	public boolean containsSorting(SortType sorttype) {
		return channel.values().contains(sorttype);
	}
	
	public String getSubreddit() {
		return subreddit;
	}
	
	public void addChannel(Long channelId) {
		addChannel(channelId, SortType.HOT);
	}
	
	public void addChannel(Long channelId, SortType sorttype) {
		channel.put(channelId, sorttype);
	}
	
	public void removeChannel(Long channelId) {
		if(channel.containsKey(channelId)) {
			channel.remove(channelId);
		}
	}
	
	public boolean containsChannel(Long channelId) {
		return channel.containsKey(channelId);
	}
	
	public Set<Long> getChannels() {
		return channel.keySet();
	}
	
	public Set<Long> getChannels(SortType sortType) {
		return channel.keySet().stream().filter(chid -> channel.get(chid) == sortType).collect(Collectors.toSet());
	}
	
	public SortType getSortType(Long channelId) {
		return channel.get(channelId);
	}
	
	public enum SortType {
		NEW, HOT, BEST;
	}
}

