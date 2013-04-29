package com.feiyang.feeds.service;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.Subscribe;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class GAEOperationServiceImpl implements OperationService {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public boolean createCategory(Category category) {
		if (category == null) {
			return false;
		}

		Entity entity = category.toEntity();
		datastore.put(entity);
		return false;
	}

	@Override
	public boolean addSubscribe(Subscribe scribe) {
		// TODO Auto-generated method stub
		return false;
	}

}
