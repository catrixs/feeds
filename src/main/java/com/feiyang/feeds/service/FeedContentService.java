package com.feiyang.feeds.service;

import java.util.List;

import com.feiyang.feeds.model.FeedContent;

public interface FeedContentService {
	List<FeedContent> latestContent(String site, int limit);
}
