package com.feiyang.feeds.model;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Category {
	private User user;
	private long categoryId;

	private String name;
	private List<Long> subscribes;

	public Category(User user, long categoryId, String name, List<Long> subscribes) {
		super();
		this.user = user;
		this.categoryId = categoryId;
		this.name = name;
		this.subscribes = subscribes;
	}

	@SuppressWarnings("unchecked")
	public Category(Entity entity) {
		user = new User(entity.getParent().getId(), null);
		categoryId = entity.getKey().getId();

		name = entity.getProperty("name").toString();
		subscribes = (List<Long>) entity.getProperty("subscribes");
	}

	public Entity toEntity() {
		Key key = KeyFactory.createKey(KeyFactory.createKey(User.class.getSimpleName(), user.getUid()),
				Category.class.getSimpleName(), categoryId);
		Entity entity = new Entity(key);
		entity.setProperty("name", name);
		entity.setProperty("subscribes", subscribes);
		return entity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getSubscribes() {
		return subscribes;
	}

	public void setSubscribes(List<Long> subscribes) {
		this.subscribes = subscribes;
	}
}
