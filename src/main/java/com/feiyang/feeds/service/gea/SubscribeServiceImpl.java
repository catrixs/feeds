package com.feiyang.feeds.service.gea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.SubscribeEntityHelper;
import com.feiyang.feeds.model.User;
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SiteService;
import com.feiyang.feeds.service.SubscribeService;
import com.feiyang.feeds.util.SimpleUuidService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SubscribeServiceImpl implements SubscribeService {
	private static final int NEW_SUBSCRIBE_MAX_CONTENT = 15;
	private static final Logger LOG = Logger.getLogger(SubscribeServiceImpl.class.getName());

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Autowired(required = true)
	private FeedContentService feedContentService;

	@Autowired(required = true)
	private CrawlerService crawlerService;

	@Autowired(required = true)
	private SiteService siteService;

	@Override
	public void updateUnread(List<FeedContent> contents) {
		if (CollectionUtils.isEmpty(contents)) {
			return;
		}

		Map<String, List<Long>> sites = orderBySite(contents);
		updateUnread(sites);
	}

	private void updateUnread(Map<String, List<Long>> sites) {
		for (Entry<String, List<Long>> entry : sites.entrySet()) {
			List<Subscribe> scribes = querySubscribe(entry.getKey());
			if (!CollectionUtils.isEmpty(scribes)) {
				for (Subscribe subscribe : scribes) {
					List<Long> unreadFeeds = subscribe.getFeeds();
					if (unreadFeeds == null) {
						unreadFeeds = new ArrayList<>();
						subscribe.setFeeds(unreadFeeds);
					}
					unreadFeeds.addAll(entry.getValue());
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
	public void clearUnread(long subscribeId, List<FeedContent> feedIds) {
		Assert.isTrue(subscribeId > 0, String.format("[clear unread] illegal subscribeId:subscribeId=%d", subscribeId));

		if (CollectionUtils.isEmpty(feedIds)) {
			return;
		}

		Subscribe scribe = querySubscribe(subscribeId);
		List<Long> unreadFeeds = scribe.getFeeds();
		if (CollectionUtils.isEmpty(unreadFeeds)) {
			return;
		}

		boolean dirty = false;
		for (FeedContent feed : feedIds) {
			dirty |= unreadFeeds.remove(feed.getId());
		}
		if (dirty) {
			datastore.put(SubscribeEntityHelper.toEntity(scribe));
		}
	}

	@Override
	public List<Subscribe> querySubscribes(Category category) {
		if (category == null || CollectionUtils.isEmpty(category.getSubscribes())) {
			return Collections.emptyList();
		}

		List<Key> keys = SubscribeEntityHelper.keys(category.getSubscribes());
		Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, keys);
		PreparedQuery pq = datastore.prepare(new Query(SubscribeEntityHelper.kind()).setFilter(keyFilter));
		List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(keys.size()));
		return SubscribeEntityHelper.toSubscribe(entities);
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

	public Subscribe querySubscribe(long subscribeId) {
		PreparedQuery pq = datastore.prepare(new Query(SubscribeEntityHelper.key(subscribeId)));
		Entity entity = pq.asSingleEntity();
		return SubscribeEntityHelper.toSubscribe(entity);
	}

	@Override
	public Subscribe subscribeSite(User user, Category category, String site) {
		if (category == null || category.getCategoryId() <= 0 || !StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("categoryId=%s, site=%s", category, site));
		}

		// fetch latest feed content.
		List<FeedContent> contents = null;
		boolean siteExist = siteService.subscribeSite(site);
		if (siteExist) {
			contents = crawlerService.crawl(site);
		} else {
			contents = feedContentService.latestContent(site, NEW_SUBSCRIBE_MAX_CONTENT);
		}
		List<Long> feedIds = new ArrayList<>();
		for (FeedContent feedContent : contents) {
			feedIds.add(feedContent.getId());
		}

		// check this site already exists in category. if so, just return true;
		Subscribe subscribe = checkAlreadySubscribed(category, site);
		if (subscribe == null) {
			// save the new subscribe to storage.
			subscribe = new Subscribe(SimpleUuidService.next(), site, user.getUid(), feedIds);
			datastore.put(SubscribeEntityHelper.toEntity(subscribe));
		} else {
			LOG.info(String.format("user(%d) category(%s) has already subscribe site(%s)", user.getUid(),
					category.getName(), site));
		}

		subscribe.setContents(contents);
		return subscribe;
	}

	private Subscribe checkAlreadySubscribed(Category category, String site) {
		if (CollectionUtils.isEmpty(category.getSubscribes())) {
			return null;
		}

		List<Key> keys = SubscribeEntityHelper.keys(category.getSubscribes());
		Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, keys);
		Filter filter = CompositeFilterOperator.and(keyFilter, new FilterPredicate("site", FilterOperator.EQUAL, site));
		PreparedQuery pq = datastore.prepare(new Query(SubscribeEntityHelper.kind()).setFilter(filter));
		Entity entity = pq.asSingleEntity();
		return SubscribeEntityHelper.toSubscribe(entity);
	}

	public FeedContentService getFeedContentService() {
		return feedContentService;
	}

	public void setFeedContentService(FeedContentService feedContentService) {
		this.feedContentService = feedContentService;
	}

	public CrawlerService getCrawlerService() {
		return crawlerService;
	}

	public void setCrawlerService(CrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
}
