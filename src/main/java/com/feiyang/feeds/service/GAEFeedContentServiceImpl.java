package com.feiyang.feeds.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.FeedContentEntityHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Component
public class GAEFeedContentServiceImpl implements FeedContentService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public List<FeedContent> latestContent(String site, int limit) {
		if (!StringUtils.hasText(site) || limit <= 0) {
			return Collections.emptyList();
		}

		Filter filterBySite = new FilterPredicate("site", FilterOperator.EQUAL, site);
		PreparedQuery pq = datastore.prepare(new Query(FeedContentEntityHelper.kind()).setFilter(filterBySite));

		List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(limit));
		List<FeedContent> rs = new ArrayList<>(entities.size());
		for (Entity entity : entities) {
			rs.add(FeedContentEntityHelper.toFeedContent(entity));
		}
		return rs;
	}
}
