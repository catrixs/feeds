package com.feiyang.feeds.service.gea;

import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.Site;
import com.feiyang.feeds.model.SiteEntityHelper;
import com.feiyang.feeds.service.SiteService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class SiteServiceImpl implements SiteService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public boolean subscribeSite(String site) {
		if (!StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("illegal site to subscribe:site=%s", site));
		}

		Site siteInfo = new Site(site);
		PreparedQuery pq = datastore.prepare(new Query(SiteEntityHelper.key(siteInfo)));
		Entity entity = pq.asSingleEntity();
		if (entity == null) {
			entity = SiteEntityHelper.toEntity(siteInfo);
			datastore.put(entity);
			return true;
		} else {
			return false;
		}
	}

}
