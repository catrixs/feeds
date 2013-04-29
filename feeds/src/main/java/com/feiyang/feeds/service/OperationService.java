package com.feiyang.feeds.service;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.Subscribe;

public interface OperationService {
	boolean createCategory(Category category);

	boolean addSubscribe(Subscribe scribe);
}
