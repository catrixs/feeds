package com.feiyang.feeds.service.gea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.CategoryEntityHelper;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.SubscribeEntityHelper;
import com.feiyang.feeds.model.User;
import com.feiyang.feeds.model.UserEntityHelper;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SiteService;
import com.feiyang.feeds.util.SimpleUuidService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Component
public class CategoryServiceImpl implements CategoryService {
	private static final int NEW_SUBSCRIBE_MAX_CONTENT = 15;

	private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class.getName());

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Autowired(required = true)
	private FeedContentService feedContentService;

	@Autowired(required = true)
	private CrawlerService crawlerService;

	@Autowired(required = true)
	private SiteService siteService;

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
	public Map<Subscribe, List<FeedContent>> subscribeSite(User user, long categoryId, String site) {
		if (categoryId <= 0) {
			throw new IllegalArgumentException(String.format("categoryId=%d, site=%s", categoryId, site));
		}

		Category category = queryCategory(user, categoryId);

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

			List<Long> subscribes = category.getSubscribes();
			if (subscribes == null) {
				subscribes = new ArrayList<>();
				subscribes.add(subscribe.getId());
				category.setSubscribes(subscribes);
			}
			datastore.put(CategoryEntityHelper.toEntity(category));
		} else {
			LOG.info(String.format("user(%d) category(%s) has already subscribe site(%s)", user.getUid(),
					category.getName(), site));
		}

		Map<Subscribe, List<FeedContent>> rs = new HashMap<>();
		rs.put(subscribe, contents);
		return rs;
	}

	@Override
	public Category queryCategory(User user, String name) {
		// query category.
		Key userKeyFilter = UserEntityHelper.key(user.getUid());
		PreparedQuery pq = datastore.prepare(new Query(CategoryEntityHelper.kind()).setAncestor(userKeyFilter)
				.setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name)));
		Entity entity = pq.asSingleEntity();
		return CategoryEntityHelper.toCategory(entity);
	}

	private Category queryCategory(User user, long categoryId) {
		// query category.
		Key categoryFilterKey = CategoryEntityHelper.key(user.getUid(), categoryId);
		PreparedQuery pq = datastore.prepare(new Query(categoryFilterKey));
		Entity entity = pq.asSingleEntity();
		Category category = CategoryEntityHelper.toCategory(entity);
		return category;
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
