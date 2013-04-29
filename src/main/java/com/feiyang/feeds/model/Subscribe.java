package com.feiyang.feeds.model;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * <p>
 * user's subscribe feed site.
 * </p>
 * 
 * <li>fetch by uid when user login.</li> <li>fetch category by site when site
 * has new feed in order to .</li>
 * 
 * @author chenfei
 * 
 */
public class Subscribe {
	private long id;
	private String site;

	private long uid;
	private List<Long> feeds;

	public Subscribe(long id, String site, long uid, List<Long> feeds) {
		this.id = id;
		this.site = site;
		this.uid = uid;
		this.feeds = feeds;
	}

	@SuppressWarnings("unchecked")
	public Subscribe(Entity entity) {
		id = entity.getKey().getId();
		site = entity.getProperty("site").toString();
		uid = (long) entity.getProperty("uid");
		feeds = (List<Long>) entity.getProperty("feeds");
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
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

	public List<Long> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Long> feeds) {
		this.feeds = feeds;
	}

	public Entity toEntity() {
		Key key = KeyFactory.createKey(Subscribe.class.getSimpleName(), id);
		Entity entity = new Entity(key);
		entity.setProperty("site", site);

		entity.setUnindexedProperty("uid", uid);
		entity.setUnindexedProperty("feeds", feeds);
		return entity;
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
		if (!(obj instanceof Subscribe)) {
			return false;
		}
		Subscribe other = (Subscribe) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Subscribe [id=");
		builder.append(id);
		builder.append(", site=");
		builder.append(site);
		builder.append(", uid=");
		builder.append(uid);
		builder.append(", feeds=");
		builder.append(feeds);
		builder.append("]");
		return builder.toString();
	}
}
