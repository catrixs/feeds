package com.feiyang.feeds.service.gea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.SubscribeEntityHelper;
import com.feiyang.feeds.service.SubscribeService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

;

public class GAESubscribeServiceImpl implements SubscribeService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public void updateUnread(List<FeedContent> contents) {
		if (CollectionUtils.isEmpty(contents)) {
			return;
		}

		Map<String, List<Long>> sites = orderBySite(contents);
		updateUnread(sites, false);
	}

	private void updateUnread(Map<String, List<Long>> sites, boolean isClear) {
		for (Entry<String, List<Long>> entry : sites.entrySet()) {
			List<Subscribe> scribes = querySubscribe(entry.getKey());
			if (!CollectionUtils.isEmpty(scribes)) {
				for (Subscribe subscribe : scribes) {
					List<Long> unreadFeeds = subscribe.getFeeds();
					if (unreadFeeds == null) {
						unreadFeeds = new ArrayList<>();
						subscribe.setFeeds(unreadFeeds);
					}

					if (isClear) {
						unreadFeeds.removeAll(entry.getValue());
					} else {
						unreadFeeds.addAll(entry.getValue());
					}

					Entity entity = SubscribeEntityHelper.toEntity(subscribe);
					datastore.put(entity);
				}
			}
		}
	}

	private Map<String, List<Long>> orderBySite(List<FeedContent> contents) {
		Map<String, List<Long>> sites = new HashMap<>();
		for (FeedContent feedContent : contents) {
			List<Long> lst = sites.get(feedContent.getSite());
			if (lst == null) {
				lst = new ArrayList<>();
				sites.put(feedContent.getSite(), lst);
			}
			lst.add(feedContent.getId());
		}
		return sites;
	}

	@Override
	public void clearUnread(List<FeedContent> feedIds) {
		if (CollectionUtils.isEmpty(feedIds)) {
			return;
		}

		Map<String, List<Long>> sites = orderBySite(feedIds);
		updateUnread(sites, true);
	}

	public List<Subscribe> querySubscribe(String site) {
		if (!StringUtils.hasText(site)) {
			return Collections.emptyList();
		}

		Filter siteFilter = new FilterPredicate("site", FilterOperator.EQUAL, site);
		PreparedQuery pq = datastore.prepare(new Query(SubscribeEntityHelper.kind()).setFilter(siteFilter));
		List<Subscribe> rs = new ArrayList<>();
		for (Entity entity : pq.asIterable()) {
			rs.add(SubscribeEntityHelper.toSubscribe(entity));
		}
		return rs;
	}
}
