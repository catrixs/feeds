package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;

public interface SubscribeService {
	/**
	 * when new feed content has been found, use this service to update user's
	 * unread count. it will increase unread count of each {@link Subscribe}
	 * according to the site.
	 * 
	 * @param contents
	 */
	void updateUnread(List<FeedContent> contents);

	void clearUnread(List<FeedContent> feedIds);
}
