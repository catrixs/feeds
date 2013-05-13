package com.feiyang.feeds.model;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class CategoryEntityHelper {
	public static String kind() {
		return Category.class.getSimpleName();
	}

	public static Key key(long uid, long categoryId) {
		return KeyFactory.createKey(KeyFactory.createKey(User.class.getSimpleName(), uid),
		        Category.class.getSimpleName(), categoryId);
	}

	public static Category toCategory(Entity entity) {
		if (entity == null) {
			return null;
		}

		User user = new User(entity.getParent().getId(), "");
		long categoryId = entity.getKey().getId();
		String name = entity.getProperty("name").toString();
		@SuppressWarnings("unchecked")
		List<Long> subscribes = (List<Long>) entity.getProperty("subscribes");
		Category category = new Category(user, categoryId, name, subscribes);
		return category;
	}

	public static Entity toEntity(Category category) {
		if (category == null) {
			return null;
		}

		Key key = key(category.getUser().getUid(), category.getCategoryId());
		Entity entity = new Entity(key);
		entity.setProperty("name", category.getName());

		entity.setUnindexedProperty("subscribes", category.getSubscribes());
		return entity;
	}
}
