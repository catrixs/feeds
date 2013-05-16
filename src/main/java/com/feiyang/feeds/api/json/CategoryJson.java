package com.feiyang.feeds.api.json;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.Subscribe;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class CategoryJson {
	public static JSONObject toJson(List<Category> categories) throws JSONException {
		JSONObject json = new JSONObject();
		if (!CollectionUtils.isEmpty(categories)) {
			JSONArray array = new JSONArray();
			for (Category category : categories) {
				array.put(toJson(category));
			}
			json.put("categories", array);
		}
		return json;
	}

	public static JSONObject toJson(Category category) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", category.getCategoryId());
		json.put("name", category.getName());
		JSONArray array = new JSONArray();
		if (!CollectionUtils.isEmpty(category.getScribes())) {
			for (Subscribe subscribe : category.getScribes()) {
				JSONObject subscribeJson = new JSONObject();
				subscribeJson.put("id", subscribe.getId());
				subscribeJson.put("site", subscribe.getSite());
				subscribeJson.put("name", subscribe.getName());
				subscribeJson.put("unread", CollectionUtils.isEmpty(subscribe.getFeeds()) ? 0 : subscribe.getFeeds()
				        .size());
				if (!CollectionUtils.isEmpty(subscribe.getContents())) {
					JSONArray contentJson = FeedContentJson.toJson(subscribe.getContents());
					subscribeJson.put("feeds", contentJson);
				}
				array.put(subscribeJson);
			}
		}
		json.put("subscribes", array);
		return json;
	}
}
