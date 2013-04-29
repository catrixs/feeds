package com.feiyang.feeds.model;

public class User {
	private long uid;

	private String username;

	public User(long uid, String username) {
		super();
		this.uid = uid;
		this.username = username;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
