package com.feiyang.feeds.service;

import java.util.List;
import java.util.Map;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;

public interface FeedService {
	List<Category> showCategory(long uid);

	Map<Subscribe, List<FeedContent>> categoryFeeds(Category category);
}
