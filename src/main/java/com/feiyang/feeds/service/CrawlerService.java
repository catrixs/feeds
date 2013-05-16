package com.feiyang.feeds.service;

import java.util.List;
import java.util.Map;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Site;

public interface CrawlerService {
	/**
	 * crawl the rss or atom site content.
	 * 
	 * @param site
	 * @return
	 */
	Map<Site, List<FeedContent>> crawl(String site);
}
