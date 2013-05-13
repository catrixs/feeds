package com.feiyang.feeds.service;

import com.feiyang.feeds.model.User;

public interface UserService {
	/**
	 * registry new user.
	 * 
	 * @param name
	 * @return
	 */
	User createUser(String email, String password, String name);

	/**
	 * query user according to id.
	 * 
	 * @param uid
	 * @return
	 */
	User queryUser(long uid);

	/**
	 * query user according by email.
	 * 
	 * @param email
	 * @return
	 */
	User queryUser(String email);
}
