package com.feiyang.feeds.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class SiteEntityHelper {
	public static String kind() {
		return Site.class.getSimpleName();
	}

	public static Key key(Site site) {
		return KeyFactory.createKey(kind(), site.getSite());
	}

	public static Entity toEntity(Site site) {
		if (site == null) {
			return null;
		}

		return new Entity(key(site));
	}

	public static Site toSite(Entity entity) {
		if (entity == null) {
			return null;
		}

		return new Site(entity.getKey().getName());
	}
}
