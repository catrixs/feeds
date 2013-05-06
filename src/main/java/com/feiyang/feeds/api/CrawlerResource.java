package com.feiyang.feeds.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.api.json.FeedContentJson;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.SiteService;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@Component
@Path(value = "crawler")
public class CrawlerResource {
	@Autowired(required = true)
	private SiteService siteService;
	@Autowired(required = true)
	private CrawlerService crawlerService;

	@GET
	@Path(value = "crawl.json")
	@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
	public String crawl(@QueryParam(value = "site") String site) throws JSONException {
		if (!StringUtils.hasText(site)) {
			return "illegal crawl site=" + site;
		}

		List<FeedContent> contents = crawlerService.crawl(site);
		return FeedContentJson.toJson(contents).toString();
	}
}
