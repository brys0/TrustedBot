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
package de.pheromir.trustedbot.misc;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class RedditSubscription {

    private final String subreddit;
    private final HashMap<Long, SortType> channel;

    public RedditSubscription(String subreddit) {
        this.subreddit = subreddit;
        channel = new HashMap<>();
    }

    public boolean containsSorting(SortType sorttype) {
        return channel.containsValue(sorttype);
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
        channel.remove(channelId);
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
        NEW, HOT, BEST
    }
}
