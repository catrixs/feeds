package com.feiyang.feeds.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class UserEntityHelper {
	public static String kind() {
		return User.class.getSimpleName();
	}

	public static Key key(long uid) {
		return KeyFactory.createKey(User.class.getSimpleName(), uid);
	}
}
