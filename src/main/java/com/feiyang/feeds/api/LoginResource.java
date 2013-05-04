package com.feiyang.feeds.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.User;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.UserService;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@Component
@Path("/")
public class LoginResource {
	@Autowired(required = true)
	private UserService userService;
	@Autowired(required = true)
	private CategoryService categoryService;

	@Path("/login.json")
	@GET
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public Response login(@QueryParam(value = "name") String name, @QueryParam(value = "password") String password) {
		return Response.ok("").cookie(new NewCookie("name", "Hello, world!")).build();
	}

	@Path("/registry.json")
	@GET
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public String registry(@QueryParam("name") String name) {
		if (!StringUtils.hasText(name)) {
			throw new IllegalArgumentException("");
		}

		User user = userService.createUser(name);

		// create default user's category.
		categoryService.createCategory(user, "default");
		return new JSONObject(user).toString();
	}
}
