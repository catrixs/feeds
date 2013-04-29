package com.feiyang.feeds.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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

	@Autowired(required = true)
	private CrawlerService crawlerService;

	@Override
	public List<FeedContent> latestContent(String site, int limit) {
		if (!StringUtils.hasText(site) || limit <= 0) {
			return Collections.emptyList();
		}

		Filter filterBySite = new FilterPredicate("site", FilterOperator.EQUAL, site);
		PreparedQuery pq = datastore.prepare(new Query(FeedContentEntityHelper.kind()).setFilter(filterBySite));

		List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(limit));
		if (!CollectionUtils.isEmpty(entities)) {
			List<FeedContent> rs = new ArrayList<>(entities.size());
			for (Entity entity : entities) {
				rs.add(FeedContentEntityHelper.toFeedContent(entity));
			}
			return rs;
		} else {
			// this site is new one, so we must crawl it first.
			List<FeedContent> rs = crawlerService.crawl(site);

			// TODO this save procedure need to be background.
			List<Entity> toBeSaveContents = new ArrayList<>(rs.size());
			for (FeedContent feedContent : rs) {
				toBeSaveContents.add(FeedContentEntityHelper.toEntity(feedContent));
			}
			datastore.put(toBeSaveContents);

			return rs;
		}
	}

}
