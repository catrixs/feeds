package com.feiyang.feeds.api.json;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class CategoryJson {
	public static JSONObject toJson(List<Category> categories) throws JSONException {
		JSONObject json = new JSONObject();
		if (!CollectionUtils.isEmpty(categories)) {
			for (Category category : categories) {
				json.accumulate("categories", toJson(category));
			}
		}
		return json;
	}

	public static JSONObject toJson(Category category) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", category.getCategoryId());
		json.put("name", category.getName());
		if (!CollectionUtils.isEmpty(category.getScribes())) {
			for (Subscribe subscribe : category.getScribes()) {
				JSONObject subscribeJson = new JSONObject();
				subscribeJson.put("id", subscribe.getId());
				subscribeJson.put("site", subscribe.getSite());
				subscribeJson.put("unread", CollectionUtils.isEmpty(subscribe.getFeeds()) ? 0 : subscribe.getFeeds()
						.size());
				if (!CollectionUtils.isEmpty(subscribe.getContents())) {
					for (FeedContent content : subscribe.getContents()) {
						JSONObject contentJson = new JSONObject(content);
						subscribeJson.accumulate("feeds", contentJson);
					}
				}
				json.accumulate("subscribes", subscribeJson);
			}
		}
		return json;
	}
}
