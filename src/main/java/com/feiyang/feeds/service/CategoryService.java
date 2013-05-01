package com.feiyang.feeds.service;

import java.util.List;
import java.util.Map;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;

public interface CategoryService {
	/**
	 * user create a category in order to group some site together.
	 * 
	 * @param category
	 * @return
	 */
	boolean createCategory(User user, String name);

	/**
	 * add new site to a category and crawl this site to the storage, return
	 * this subscribe with latest feed content.
	 * 
	 * @param user
	 * @param categoryId
	 * @param site
	 * @return
	 */
	Map<Subscribe, List<FeedContent>> subscribeNewSite(User user, long categoryId, String site);

	/**
	 * add a already crawled site to this category. just return the latest
	 * content from the storage, doesn't crawl the site.
	 * 
	 * @param user
	 * @param categoryId
	 * @param site
	 * @return
	 */
	Map<Subscribe, List<FeedContent>> subscribeAlreadySite(User user, long categoryId, String site);
}
