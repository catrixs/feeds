package com.feiyang.feeds.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;

@Component
public class GAEFeedServiceImpl implements FeedService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public List<Category> showCategory(long uid) {
		if (uid <= 0) {
			return Collections.emptyList();
		}

		Query q = new Query(Category.class.getSimpleName()).setAncestor(KeyFactory.createKey(
				User.class.getSimpleName(), uid));
		PreparedQuery pq = datastore.prepare(q);

		List<Category> rs = new ArrayList<>();
		for (Entity result : pq.asIterable()) {
			Category category = new Category(result);
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

		List<Key> keys = new ArrayList<>(subscribeIds.size());
		for (Long subscribId : subscribeIds) {
			keys.add(KeyFactory.createKey(Subscribe.class.getSimpleName(), subscribId));
		}
		Filter subscribeFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, keys);

		PreparedQuery pq = datastore.prepare(new Query(Subscribe.class.getSimpleName()).setFilter(subscribeFilter));

		List<Subscribe> subscribes = new ArrayList<>();
		for (Entity entity : pq.asIterable()) {
			subscribes.add(new Subscribe(entity));
		}

		if (CollectionUtils.isEmpty(subscribes)) {
			return Collections.emptyMap();
		}

		List<Key> feedIds = new ArrayList<>();
		for (Subscribe subscribe : subscribes) {
			for (Long feedId : subscribe.getFeeds()) {
				feedIds.add(KeyFactory.createKey(FeedContent.class.getSimpleName(), feedId));
			}
		}

		Filter feedFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, feedIds);
		pq = datastore.prepare(new Query(FeedContent.class.getSimpleName()).setFilter(feedFilter));

		Map<Long, FeedContent> feedContents = new HashMap<>();
		for (Entity entity : pq.asIterable()) {
			FeedContent feed = new FeedContent(entity);
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
