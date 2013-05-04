package com.feiyang.feeds.service.gea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.CategoryEntityHelper;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;
import com.feiyang.feeds.model.UserEntityHelper;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SiteService;
import com.feiyang.feeds.service.SubscribeService;
import com.feiyang.feeds.util.SimpleUuidService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Component
public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class.getName());

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Autowired(required = true)
	private FeedContentService feedContentService;

	@Autowired(required = true)
	private CrawlerService crawlerService;

	@Autowired(required = true)
	private SiteService siteService;

	@Autowired(required = true)
	private SubscribeService subscribeService;

	@Override
	public Category createCategory(User user, String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException(String.format("category name=%s", name));
		}

		long categoryId = SimpleUuidService.next();
		Category category = new Category(user, categoryId, name, null);
		Entity entity = category.toEntity();
		Key key = datastore.put(entity);
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("user create category:%s", key));
		}
		return category;
	}

	@Override
	public Category queryCategory(User user, String name) {
		// query category.
		Key userKeyFilter = UserEntityHelper.key(user.getUid());
		PreparedQuery pq = datastore.prepare(new Query(CategoryEntityHelper.kind()).setAncestor(userKeyFilter)
				.setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name)));
		Entity entity = pq.asSingleEntity();
		Category category = CategoryEntityHelper.toCategory(entity);
		if (category == null) {
			return null;
		}

		// query subscribes.
		List<Subscribe> subscribes = subscribeService.querySubscribes(category);
		category.setScribes(subscribes);

		return category;
	}

	@Override
	public Category subscribeSite(User user, long categoryId, String site) {
		if (user == null || user.getUid() <= 0 || !StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("illegal user or site to subscribe:user=%s, site=%s",
					user, site));
		}

		Category category = queryCategory(user, categoryId);
		if (category == null) {
			// TODO how to handle this error?
			return null;
		}

		Subscribe subscribe = subscribeService.subscribeSite(user, category, site);
		List<Long> subscribeIds = category.getSubscribes();
		if (subscribeIds == null) {
			subscribeIds = new ArrayList<>();
			category.setSubscribes(subscribeIds);
		}
		if (!subscribeIds.contains(subscribe.getId())) {
			subscribeIds.add(subscribe.getId());
			datastore.put(CategoryEntityHelper.toEntity(category));
		}

		category.setScribes(Arrays.asList(subscribe));
		return category;
	}

	private Category queryCategory(User user, long categoryId) {
		// query category.
		Key categoryFilterKey = CategoryEntityHelper.key(user.getUid(), categoryId);
		PreparedQuery pq = datastore.prepare(new Query(categoryFilterKey));
		Entity entity = pq.asSingleEntity();
		Category category = CategoryEntityHelper.toCategory(entity);
		return category;
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

	public SubscribeService getSubscribeService() {
		return subscribeService;
	}

	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
	}
}
