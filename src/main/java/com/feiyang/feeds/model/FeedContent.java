package com.feiyang.feeds.model;

import java.util.Date;

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
	private Date pubDate;

	public FeedContent() {

	}

	public FeedContent(long id, String site, String title, String link, String description) {
		super();
		this.id = id;
		this.site = site;
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public FeedContent(long id, String site, String link, String title, String description, String category,
			String author, Date pubDate) {
		super();
		this.id = id;
		this.site = site;
		this.link = link;
		this.title = title;
		this.description = description;
		this.category = category;
		this.author = author;
		this.pubDate = pubDate;
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

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedContent [id=");
		builder.append(id);
		builder.append(", site=");
		builder.append(site);
		builder.append(", link=");
		builder.append(link);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", category=");
		builder.append(category);
		builder.append(", author=");
		builder.append(author);
		builder.append(", pubDate=");
		builder.append(pubDate);
		builder.append("]");
		return builder.toString();
	}
}
