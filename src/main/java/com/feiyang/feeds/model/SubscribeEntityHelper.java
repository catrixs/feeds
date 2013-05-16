package com.feiyang.feeds.model;

import java.util.ArrayList;
import java.util.Collection;
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

	public static Key key(long id) {
		return KeyFactory.createKey(kind(), id);
	}

	public static List<Key> keys(Collection<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}

		List<Key> rs = new ArrayList<>(ids.size());
		for (Long id : ids) {
			rs.add(key(id));
		}
		return rs;
	}

	public static List<Subscribe> toSubscribe(Collection<Entity> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyList();
		}

		List<Subscribe> rs = new ArrayList<>(entities.size());
		for (Entity entity : entities) {
			rs.add(toSubscribe(entity));
		}
		return rs;
	}

	public static Entity toEntity(Subscribe subscribe) {
		Key key = KeyFactory.createKey(kind(), subscribe.getId());
		Entity entity = new Entity(key);
		entity.setProperty("site", subscribe.getSite());

		entity.setUnindexedProperty("name", subscribe.getName());
		entity.setUnindexedProperty("uid", subscribe.getUid());
		entity.setUnindexedProperty("feeds", subscribe.getFeeds());
		return entity;
	}

	public static Subscribe toSubscribe(Entity entity) {
		if (entity == null) {
			return null;
		}

		long id = entity.getKey().getId();
		String site = (String) entity.getProperty("site");
		String name = (String) entity.getProperty("name");
		long uid = (long) entity.getProperty("uid");
		@SuppressWarnings("unchecked")
		List<Long> feeds = (List<Long>) entity.getProperty("feeds");

		return new Subscribe(id, site, name, uid, feeds);
	}
}
