package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;

public interface SubscribeService {
	/**
	 * when new feed content has been found, use this service to update user's
	 * unread count. it will increase unread count of each {@link Subscribe}
	 * according to the site.
	 * 
	 * @param contents
	 */
	void updateUnread(List<FeedContent> contents);

	/**
	 * clear user's subscribe unread feed ids.
	 * 
	 * @param feedIds
	 *            FeedContent contains id and site.
	 */
	void clearUnread(long subscribeId, List<FeedContent> feedIds);

	/**
	 * @param category
	 * @return
	 */
	List<Subscribe> querySubscribes(Category category);

	/**
	 * registry a new site subscribe.
	 * 
	 * @param user
	 * @param category
	 * @param site
	 * @return
	 */
	Subscribe subscribeSite(User user, Category category, String site);
}
