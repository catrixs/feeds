package com.feiyang.feeds.api.json;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.model.FeedContent;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class FeedContentJson {
	public static JSONArray toJson(List<FeedContent> contents) throws JSONException {
		JSONArray array = new JSONArray();
		if (!CollectionUtils.isEmpty(contents)) {
			for (FeedContent content : contents) {
				array.put(toJson(content));
			}
		}
		return array;
	}

	public static JSONObject toJson(FeedContent content) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", content.getId());
		json.put("site", content.getSite());
		json.put("link", content.getLink());
		json.put("title", content.getTitle());
		json.put("description", content.getDescription());
		json.putOpt("author", content.getAuthor());
		json.putOpt("category", content.getCategory());
		json.putOpt("pubDate", content.getPubDate());
		return json;
	}
}
