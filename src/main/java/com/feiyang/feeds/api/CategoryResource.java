package com.feiyang.feeds.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

	@GET
	@Path("/create.json")
	@Produces({ MediaType.APPLICATION_JSON })
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
}
