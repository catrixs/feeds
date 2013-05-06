package com.feiyang.feeds.api.aggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SubscribeService;

@Component
public class CategoryAggregatorImpl implements CategoryAggregator {
	@Autowired(required = true)
	private SubscribeService subscribeService;
	@Autowired(required = true)
	private FeedContentService feedContentService;

	@Override
	public void aggregate(List<Category> categories) {
		aggregate(categories, 0);
	}

	@Override
	public void aggregate(List<Category> categories, int aggregateFeedContentCount) {
		// fetch subscribe.
		Set<Long> subscribeIds = new HashSet<>();
		for (Category c : categories) {
			if (!CollectionUtils.isEmpty(c.getSubscribes())) {
				subscribeIds.addAll(c.getSubscribes());
			}
		}
		List<Subscribe> subscribes = subscribeService.querySubscribes(subscribeIds);
		Map<Long, Subscribe> subscribeMap = new HashMap<>();
		for (Subscribe subscribe : subscribes) {
			subscribeMap.put(subscribe.getId(), subscribe);
		}

		// fetch first unread feed content.
		Set<Long> feedIds = new HashSet<>();
		for (Subscribe subscribe : subscribes) {
			List<Long> unreadFeedIds = subscribe.getFeeds();
			if (!CollectionUtils.isEmpty(unreadFeedIds)) {
				if (aggregateFeedContentCount > 0) {
					for (int i = 0; i < aggregateFeedContentCount; i++) {
						feedIds.add(unreadFeedIds.get(i));
					}
				} else {
					feedIds.addAll(unreadFeedIds);
				}
			}
		}
		List<FeedContent> contents = feedContentService.queryContent(feedIds);
		Map<Long, FeedContent> contentMap = new HashMap<>();
		for (FeedContent feedContent : contents) {
			contentMap.put(feedContent.getId(), feedContent);
		}

		// aggregate
		for (Subscribe subscribe : subscribes) {
			if (!CollectionUtils.isEmpty(subscribe.getFeeds())) {
				for (Long feedId : subscribe.getFeeds()) {
					FeedContent unreadContent = contentMap.get(feedId);
					if (unreadContent != null) {
						List<FeedContent> unreadContents = subscribe.getContents();
						if (unreadContents == null) {
							unreadContents = new ArrayList<>();
							subscribe.setContents(unreadContents);
						}
						unreadContents.add(unreadContent);
					}
				}
			}
		}
		for (Category category : categories) {
			if (!CollectionUtils.isEmpty(category.getSubscribes())) {
				for (Long subscribeId : category.getSubscribes()) {
					Subscribe categorySubscribe = subscribeMap.get(subscribeId);
					if (categorySubscribe != null) {
						List<Subscribe> categorySubscribes = category.getScribes();
						if (categorySubscribes == null) {
							categorySubscribes = new ArrayList<>();
							category.setScribes(categorySubscribes);
						}
						categorySubscribes.add(categorySubscribe);
					}
				}
			}
		}
	}
}
