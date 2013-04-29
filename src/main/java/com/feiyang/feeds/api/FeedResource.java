package com.feiyang.feeds.api;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.service.FeedService;
import com.feiyang.feeds.util.QueryParamUtil;

@Component
@Path("/feed")
public class FeedResource {
	@Autowired
	private FeedService feedService;

	@GET
	@Path("/categories.json")
	@Produces({ MediaType.APPLICATION_JSON })
	public String categories() {
		List<Category> categories = feedService.showCategory(123456);
		StringBuilder buf = new StringBuilder();
		for (Category category : categories) {
			buf.append(category.toString());
		}
		return buf.toString();
	}

	@GET
	@Path("/feeds.json")
	@Produces({ MediaType.APPLICATION_JSON })
	public String feeds(long uid, long categoryId, String name, String subscribs) {
		Category category = new Category(null, categoryId, name, QueryParamUtil.splitToList(subscribs));
		Map<Subscribe, List<FeedContent>> contents = feedService.categoryFeeds(category);
		return contents.toString();
	}
}
