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

	private transient List<Subscribe> scribes;

	public Category(User user, long categoryId, String name, List<Long> subscribes) {
		super();
		this.user = user;
		this.categoryId = categoryId;
		this.name = name;
		this.subscribes = subscribes;
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

	public List<Subscribe> getScribes() {
		return scribes;
	}

	public void setScribes(List<Subscribe> scribes) {
		this.scribes = scribes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Category)) {
			return false;
		}
		Category other = (Category) obj;
		if (categoryId != other.categoryId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Category [user=");
		builder.append(user);
		builder.append(", categoryId=");
		builder.append(categoryId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", subscribes=");
		builder.append(subscribes);
		builder.append("]");
		return builder.toString();
	}
}
