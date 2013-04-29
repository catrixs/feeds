package com.feiyang.feeds.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SubscribeEntityHelper {
	public static String kind() {
		return Subscribe.class.getSimpleName();
	}

	public static List<Key> keys(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}

		List<Key> rs = new ArrayList<>(ids.size());
		for (Long id : ids) {
			rs.add(KeyFactory.createKey(kind(), id));
		}
		return rs;
	}

	public static Entity toEntity(Subscribe subscribe) {
		Key key = KeyFactory.createKey(kind(), subscribe.getId());
		Entity entity = new Entity(key);
		entity.setProperty("site", subscribe.getSite());

		entity.setUnindexedProperty("uid", subscribe.getUid());
		entity.setUnindexedProperty("feeds", subscribe.getFeeds());
		return entity;
	}

	public static Subscribe toSubscribe(Entity entity) {
		long id = entity.getKey().getId();
		String site = entity.getProperty("site").toString();
		long uid = (long) entity.getProperty("uid");
		@SuppressWarnings("unchecked")
		List<Long> feeds = (List<Long>) entity.getProperty("feeds");

		return new Subscribe(id, site, uid, feeds);
	}
}
