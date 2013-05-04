package com.feiyang.feeds.service;

import java.util.List;

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
	 * @param categoryId
	 * @return
	 */
	Category queryCategory(User user, long categoryId);

	/**
	 * query the category from the storage.
	 * 
	 * @param user
	 * @param name
	 * @return
	 */
	Category queryCategory(User user, String name);

	/**
	 * query user's all category.
	 * 
	 * @param user
	 * @return
	 */
	List<Category> queryCategory(User user);

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
