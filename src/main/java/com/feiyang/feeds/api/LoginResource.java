package com.feiyang.feeds.api;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.User;
import com.feiyang.feeds.service.CategoryService;
import com.feiyang.feeds.service.UserService;

@Component
@Path("/account")
public class LoginResource {
	@Autowired(required = true)
	private UserService userService;
	@Autowired(required = true)
	private CategoryService categoryService;

	@Path("/login.json")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.TEXT_HTML + "; charset=utf-8" })
	public Response login(@FormParam(value = "email") String email, @FormParam(value = "password") String password)
	        throws URISyntaxException {
		User user = userService.queryUser(email);
		if (user == null) {
			return Response.seeOther(new URI("/registry.html")).build();
		}
		return redirectToHomepage(user);
	}

	private Response redirectToHomepage(User user) throws URISyntaxException {
		return Response.seeOther(new URI("/googleReader.html"))
		        .cookie(new NewCookie(new Cookie("uid", "" + user.getUid(), "/", null))).build();
	}

	@Path("/registry.json")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.TEXT_HTML + "; charset=utf-8" })
	public Response registry(@FormParam("email") String email, @FormParam("password") String password,
	        @FormParam("name") String name) throws URISyntaxException {
		if (!StringUtils.hasText(name)) {
			throw new IllegalArgumentException("");
		}

		User user = userService.createUser(email, password, name);

		// create default user's category.
		categoryService.createCategory(user, "default");
		return redirectToHomepage(user);
	}
}
