package com.feiyang.feeds.api;

import java.util.Arrays;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.api.aggregator.CategoryAggregator;
import com.feiyang.feeds.api.json.CategoryJson;
import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.User;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.UserService;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@Component
@Path("/category")
public class CategoryResource {
	@Autowired(required = true)
	private UserService userService;
	@Autowired(required = true)
	private CategoryService categoryService;

	@Autowired(required = true)
	private CategoryAggregator categoryAggregator;

	@GET
	@Path("/create.json")
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public String createCategory(@QueryParam(value = "uid") long uid, @QueryParam(value = "name") String name)
	        throws JSONException {
		if (!StringUtils.hasText(name)) {
			return "illegal uid=" + uid;
		}
		if (!StringUtils.hasText(name)) {
			return "illegal name=" + name;
		}

		User user = userService.queryUser(uid);
		if (user == null) {
			return "no user which uid=" + uid;
		}

		Category category = categoryService.createCategory(user, name);
		return CategoryJson.toJson(category).toString();
	}

	@GET
	@Path("/subscribe.json")
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public String subscribe(@CookieParam(value = "uid") long uid, @QueryParam(value = "cid") long categoryId,
	        @QueryParam(value = "site") String site) throws JSONException {
		if (uid <= 0) {
			return "illegal uid=" + uid;
		}
		if (categoryId <= 0) {
			return "illegal category id=" + uid;
		}
		if (!StringUtils.hasText(site)) {
			return "illegal site=" + site;
		}

		User user = userService.queryUser(uid);
		if (user == null) {
			return "no user which uid=" + uid;
		}

		Category c = categoryService.subscribeSite(user, categoryId, site);
		return CategoryJson.toJson(c).toString();
	}

	@GET
	@Path("/show.json")
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public String feeds(@CookieParam(value = "uid") long uid, @QueryParam(value = "cid") long categoryId)
	        throws JSONException {
		if (uid <= 0) {
			return "illegal uid=" + uid;
		}
		if (categoryId <= 0) {
			return "illegal category id=" + uid;
		}

		User user = userService.queryUser(uid);
		if (user == null) {
			return "no user which uid=" + uid;
		}

		Category c = categoryService.queryCategory(user, categoryId);
		if (c == null) {
			return "{}";
		}
		categoryAggregator.aggregate(Arrays.asList(c));
		return CategoryJson.toJson(c).toString();
	}
}
