package com.feiyang.feeds.api;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

@Component
@Path("/console")
public class ConsoleResource {
	public String startCrawler(String site) {
		return null;
	}
}
