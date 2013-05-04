package com.feiyang.feeds.service.gea;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.User;
import com.feiyang.feeds.model.UserEntityHelper;
import com.feiyang.feeds.service.UserService;
import com.feiyang.feeds.util.SimpleUuidService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Component
public class UserServiceImpl implements UserService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public User createUser(String name) {
		if (!StringUtils.hasText(name)) {
			throw new IllegalArgumentException("");
		}

		User user = new User(SimpleUuidService.next(), name);
		Entity entity = UserEntityHelper.toEntity(user);
		datastore.put(entity);
		return user;
	}

	@Override
	public User queryUser(long uid) {
		Assert.isTrue(uid > 0, String.format("[UserService query] illegal uid:uid=%d", uid));

		Key key = UserEntityHelper.key(uid);
		PreparedQuery pq = datastore.prepare(new Query(UserEntityHelper.kind()).setFilter(new FilterPredicate(
				Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key)));
		Entity entity = pq.asSingleEntity();
		return UserEntityHelper.toUser(entity);
	}
}
