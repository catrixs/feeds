package com.feiyang.feeds.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.feiyang.feeds.api.json.CategoryJson;
import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SubscribeService;
import com.feiyang.feeds.service.UserService;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@Component
@Path("/view")
public class FeedResource {
	@Autowired(required = true)
	private UserService userService;
	@Autowired(required = true)
	private CategoryService categoryService;
	@Autowired(required = true)
	private SubscribeService subscribeService;
	@Autowired(required = true)
	private FeedContentService feedContentService;

	@GET
	@Path("/home.json")
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public String home(@CookieParam(value = "uid") long uid) throws JSONException {
		if (uid <= 0) {
			throw new IllegalArgumentException(String.format("[home.json] illegal uid:uid=%d", uid));
		}

		User user = userService.queryUser(uid);
		if (user == null) {
			return Response.status(400).build().toString();
		}

		List<Category> categories = categoryService.queryCategory(user);
		if (CollectionUtils.isEmpty(categories)) {
			return "{}";
		}

		// fetch subscribe.
		Set<Long> subscribeIds = new HashSet<>();
		for (Category c : categories) {
			if (!CollectionUtils.isEmpty(c.getSubscribes())) {
				subscribeIds.addAll(c.getSubscribes());
			}
		}
		List<Subscribe> subscribes = subscribeService.querySubscribes(subscribeIds);
		Map<Long, Subscribe> subscribeMap = new HashMap<>();
		for (Subscribe subscribe : subscribes) {
			subscribeMap.put(subscribe.getId(), subscribe);
		}

		// fetch first unread feed content.
		Set<Long> feedIds = new HashSet<>();
		for (Subscribe subscribe : subscribes) {
			List<Long> unreadFeedIds = subscribe.getFeeds();
			if (!CollectionUtils.isEmpty(unreadFeedIds)) {
				feedIds.add(unreadFeedIds.get(0));
			}
		}
		List<FeedContent> contents = feedContentService.queryContent(feedIds);
		Map<Long, FeedContent> contentMap = new HashMap<>();
		for (FeedContent feedContent : contents) {
			contentMap.put(feedContent.getId(), feedContent);
		}

		// aggregate
		for (Subscribe subscribe : subscribes) {
			if (!CollectionUtils.isEmpty(subscribe.getFeeds())) {
				for (Long feedId : subscribe.getFeeds()) {
					FeedContent unreadContent = contentMap.get(feedId);
					if (unreadContent != null) {
						List<FeedContent> unreadContents = subscribe.getContents();
						if (unreadContents == null) {
							unreadContents = new ArrayList<>();
							subscribe.setContents(unreadContents);
						}
						unreadContents.add(unreadContent);
					}
				}
			}
		}
		for (Category category : categories) {
			if (!CollectionUtils.isEmpty(category.getSubscribes())) {
				for (Long subscribeId : category.getSubscribes()) {
					Subscribe categorySubscribe = subscribeMap.get(subscribeId);
					if (categorySubscribe != null) {
						List<Subscribe> categorySubscribes = category.getScribes();
						if (categorySubscribes == null) {
							categorySubscribes = new ArrayList<>();
							category.setScribes(categorySubscribes);
						}
						categorySubscribes.add(categorySubscribe);
					}
				}
			}
		}

		return CategoryJson.toJson(categories).toString();
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public SubscribeService getSubscribeService() {
		return subscribeService;
	}

	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
	}

	public FeedContentService getFeedContentService() {
		return feedContentService;
	}

	public void setFeedContentService(FeedContentService feedContentService) {
		this.feedContentService = feedContentService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
