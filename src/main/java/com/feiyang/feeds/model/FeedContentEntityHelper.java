package com.feiyang.feeds.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public abstract class FeedContentEntityHelper {
	public static String kind() {
		return FeedContent.class.getSimpleName();
	}

	public static Key key(long id) {
		return KeyFactory.createKey(kind(), id);
	}

	public static List<Key> keys(Collection<Long> ids) {
		List<Key> rs = new ArrayList<>();
		for (Long id : ids) {
			rs.add(key(id));
		}
		return rs;
	}

	public static FeedContent toFeedContent(Entity entity) {
		long id = entity.getKey().getId();
		String site = (String) entity.getProperty("site");
		String link = (String) entity.getProperty("link");
		String title = (String) entity.getProperty("title");
		String description = ((Text) entity.getProperty("desc")).getValue();
		String category = (String) entity.getProperty("category");
		String author = (String) entity.getProperty("author");
		Calendar pubDate = (Calendar) entity.getProperty("pubDate");
		FeedContent content = new FeedContent(id, site, link, title, description, category, author, pubDate);
		return content;
	}

	public static Entity toEntity(FeedContent feedContent) {
		Key feedKey = KeyFactory.createKey(FeedContent.class.getSimpleName(), feedContent.getId());
		Entity feedContentEntity = new Entity(feedKey);
		feedContentEntity.setProperty("site", feedContent.getSite());
		feedContentEntity.setProperty("link", feedContent.getLink());
		feedContentEntity.setProperty("title", feedContent.getTitle());
		feedContentEntity.setProperty("desc", new Text(feedContent.getDescription()));
		feedContentEntity.setProperty("author", feedContent.getAuthor());
		feedContentEntity.setProperty("pubDate", feedContent.getPubDate());
		feedContentEntity.setProperty("category", feedContent.getCategory());
		return feedContentEntity;
	}
}
