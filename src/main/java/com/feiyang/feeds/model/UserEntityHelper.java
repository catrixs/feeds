package com.feiyang.feeds.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class UserEntityHelper {
	public static String kind() {
		return User.class.getSimpleName();
	}

	public static Key key(long uid) {
		return KeyFactory.createKey(User.class.getSimpleName(), uid);
	}

	public static Entity toEntity(User user) {
		if (user == null) {
			return null;
		}

		Entity entity = new Entity(KeyFactory.createKey(kind(), user.getUid()));
		entity.setProperty("name", user.getUsername());
		entity.setProperty("email", user.getEmail());
		entity.setUnindexedProperty("password", user.getPassword());
		return entity;
	}

	public static User toUser(Entity entity) {
		if (entity == null) {
			return null;
		}

		long uid = entity.getKey().getId();
		String name = (String) entity.getProperty("name");
		String email = (String) entity.getProperty("email");
		String password = (String) entity.getProperty("password");

		return new User(uid, email, password, name);
	}
}
