package com.feiyang.feeds.service;

import com.feiyang.feeds.model.Category;
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
	 * subscribe a site under a category.
	 * 
	 * @param user
	 * @param categoryId
	 * @param site
	 * @return the category with created Subscribe with the latest content.
	 */
	Category subscribeSite(User user, long categoryId, String site);
}
