package com.feiyang.feeds.service.gea;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.Site;
import com.feiyang.feeds.model.SiteEntityHelper;
import com.feiyang.feeds.service.SiteService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@Component
public class SiteServiceImpl implements SiteService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public boolean subscribeSite(String site) {
		if (!StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("illegal site to subscribe:site=%s", site));
		}

		Entity entity = querySite(site);
		if (entity == null) {
			entity = SiteEntityHelper.toEntity(new Site(site));
			datastore.put(entity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean existSubscribe(String site) {
		if (!StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("illegal site:site=%s", site));
		}

		Entity entity = querySite(site);
		return entity != null;
	}

	private Entity querySite(String site) {
		PreparedQuery pq = datastore.prepare(new Query(SiteEntityHelper.key(site)));
		Entity entity = pq.asSingleEntity();
		return entity;
	}
}
