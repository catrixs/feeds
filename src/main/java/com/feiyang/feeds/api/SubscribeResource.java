package com.feiyang.feeds.api;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.service.SubscribeService;

@Component
@Path(value = "/subscribe")
public class SubscribeResource {
	@Autowired(required = true)
	private SubscribeService subscribeService;

	@Path(value = "/read.json")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public String read(@QueryParam(value = "uid") long uid, @QueryParam(value = "sid") long subscribeId,
			@QueryParam(value = "feeds") String feedIds) {
		if (uid <= 0) {
			return "illegal uid=" + uid;
		}
		if (subscribeId <= 0) {
			return "illegal subscribe id=" + subscribeId;
		}

		if (!StringUtils.hasText(feedIds)) {
			return "{true}";
		}
		String[] idStrArray = StringUtils.split(feedIds, ",");
		Collection<Long> ids = new ArrayList<>(idStrArray.length);
		for (String string : idStrArray) {
			ids.add(Long.parseLong(string));
		}

		subscribeService.clearUnread(subscribeId, ids);
		return "{true}";
	}
}
