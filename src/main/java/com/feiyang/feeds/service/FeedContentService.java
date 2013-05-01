package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.util.FeedUuidService;

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

	/**
	 * save the feed content to storage. <li>the feed content id will be reset
	 * by {@link FeedUuidService}.</li><li>the already stored content wouldn't
	 * store again.</li>
	 * 
	 * @param contents
	 * @return exclude already stored.
	 */
	List<FeedContent> saveConent(List<FeedContent> contents);
}
