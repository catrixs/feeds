package com.feiyang.feeds.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.CategoryEntityHelper;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.FeedContentEntityHelper;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.SubscribeEntityHelper;
import com.feiyang.feeds.model.UserEntityHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Component
public class GAEFeedServiceImpl implements FeedService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public List<Category> showCategory(long uid) {
		if (uid <= 0) {
			return Collections.emptyList();
		}

		Query q = new Query(CategoryEntityHelper.kind()).setAncestor(UserEntityHelper.key(uid));
		PreparedQuery pq = datastore.prepare(q);

		List<Category> rs = new ArrayList<>();
		for (Entity result : pq.asIterable()) {
			Category category = CategoryEntityHelper.toCategory(result);
			rs.add(category);
		}
		return rs;
	}

	@Override
	public Map<Subscribe, List<FeedContent>> categoryFeeds(Category category) {
		if (category == null) {
			return Collections.emptyMap();
		}

		List<Long> subscribeIds = category.getSubscribes();
		if (CollectionUtils.isEmpty(subscribeIds)) {
			return Collections.emptyMap();
		}

		List<Key> keys = SubscribeEntityHelper.keys(subscribeIds);
		Filter subscribeFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, keys);

		PreparedQuery pq = datastore.prepare(new Query(Subscribe.class.getSimpleName()).setFilter(subscribeFilter));

		List<Subscribe> subscribes = new ArrayList<>();
		for (Entity entity : pq.asIterable()) {
			subscribes.add(SubscribeEntityHelper.toSubscribe(entity));
		}

		if (CollectionUtils.isEmpty(subscribes)) {
			return Collections.emptyMap();
		}

		Set<Long> feedIds = new HashSet<>();
		for (Subscribe subscribe : subscribes) {
			feedIds.addAll(subscribe.getFeeds());
		}

		List<Key> feedKeys = FeedContentEntityHelper.keys(feedIds);
		Filter feedFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, feedKeys);
		pq = datastore.prepare(new Query(FeedContentEntityHelper.kind()).setFilter(feedFilter));

		Map<Long, FeedContent> feedContents = new HashMap<>();
		for (Entity entity : pq.asIterable()) {
			FeedContent feed = FeedContentEntityHelper.toFeedContent(entity);
			feedContents.put(feed.getId(), feed);
		}

		Map<Subscribe, List<FeedContent>> rs = new HashMap<>();
		for (Subscribe subscribe : subscribes) {
			List<Long> ids = subscribe.getFeeds();
			List<FeedContent> contents = new ArrayList<>(ids.size());
			for (Long id : ids) {
				FeedContent content = feedContents.get(id);
				if (content != null) {
					contents.add(content);
				}
			}
			rs.put(subscribe, contents);
		}
		return rs;
	}
}
