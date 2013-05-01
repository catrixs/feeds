package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.FeedContent;

public interface FeedContentService {
	/**
	 * get the latest feed content, if the storage haven't contain this site
	 * then start crawler to crawl it and store to beckends.
	 * 
	 * @param site
	 * @param limit
	 * @return
	 */
	List<FeedContent> latestContent(String site, int limit);

	List<FeedContent> saveConent(List<FeedContent> contents);
}
