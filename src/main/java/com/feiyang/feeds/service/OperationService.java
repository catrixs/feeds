package com.feiyang.feeds.service;

import java.util.List;
import java.util.Map;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;

public interface OperationService {
	/**
	 * user create a category in order to group some site together.
	 * 
	 * @param category
	 * @return
	 */
	boolean createCategory(User user, String name);

	/**
	 * add one site to a category and return this subscribe with latest feed
	 * content.
	 * 
	 * @param user
	 * @param categoryId
	 * @param site
	 * @return
	 */
	Map<Subscribe, List<FeedContent>> addSubscribe(User user, long categoryId, String site);
}
