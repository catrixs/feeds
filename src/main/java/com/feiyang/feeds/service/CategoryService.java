package com.feiyang.feeds.service;

import java.util.List;
import java.util.Map;

import com.feiyang.feeds.model.Category;
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
	Category createCategory(User user, String name);

	/**
	 * query the category from the storage.
	 * 
	 * @param user
	 * @param name
	 * @return
	 */
	Category queryCategory(User user, String name);

	/**
	 * add a already crawled site to this category. just return the latest
	 * content from the storage, doesn't crawl the site.
	 * 
	 * @param user
	 * @param categoryId
	 * @param site
	 * @return
	 */
	Map<Subscribe, List<FeedContent>> subscribeSite(User user, long categoryId, String site);
}
