package com.feiyang.feeds.api.aggregator;

import java.util.List;

import com.feiyang.feeds.model.Category;

public interface CategoryAggregator {
	/**
	 * @param categories
	 */
	void aggregate(List<Category> categories);

	/**
	 * @param categories
	 * @param aggregateFeedContentCount
	 */
	void aggregate(List<Category> categories, int aggregateFeedContentCount);
}
