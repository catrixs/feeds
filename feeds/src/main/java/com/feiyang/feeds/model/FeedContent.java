package com.feiyang.feeds.model;

import java.util.Calendar;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Feed content archive.
 * 
 * @author chenfei
 * 
 */
public class FeedContent {
	private long id;
	private String site;
	private String link;

	private String title;
	private String description;

	private String category;
	private String author;
	private Calendar pubDate;

	public FeedContent(long id, String site, String title, String link, String description) {
		super();
		this.id = id;
		this.site = site;
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public FeedContent(Entity entity) {
		this.id = entity.getKey().getId();
		this.site = (String) entity.getProperty("site");
		this.link = (String) entity.getProperty("link");
		this.title = (String) entity.getProperty("title");
		this.description = ((Text) entity.getProperty("desc")).getValue();
		this.category = (String) entity.getProperty("category");
		this.author = (String) entity.getProperty("author");
		this.pubDate = (Calendar) entity.getProperty("pubDate");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getPubDate() {
		return pubDate;
	}

	public void setPubDate(Calendar pubDate) {
		this.pubDate = pubDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FeedContent)) {
			return false;
		}
		FeedContent other = (FeedContent) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public Entity toEntity() {
		Key feedKey = KeyFactory.createKey(FeedContent.class.getSimpleName(), id);
		Entity feedContentEntity = new Entity(feedKey);
		feedContentEntity.setProperty("site", site);
		feedContentEntity.setProperty("link", link);
		feedContentEntity.setProperty("title", title);
		feedContentEntity.setProperty("desc", new Text(description));
		feedContentEntity.setProperty("author", author);
		feedContentEntity.setProperty("pubDate", pubDate);
		feedContentEntity.setProperty("category", category);
		return feedContentEntity;
	}
}
