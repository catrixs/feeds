package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.FeedContent;

public interface CrawlerService {
	/**
	 * crawl the rss or atom site content.
	 * 
	 * @param site
	 * @return
	 */
	List<FeedContent> crawl(String site);
}
