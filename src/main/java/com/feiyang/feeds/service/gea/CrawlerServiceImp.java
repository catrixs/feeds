package com.feiyang.feeds.service.gea;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.feedparser.DefaultFeedParserListener;
import org.apache.commons.feedparser.FeedParser;
import org.apache.commons.feedparser.FeedParserException;
import org.apache.commons.feedparser.FeedParserFactory;
import org.apache.commons.feedparser.FeedParserState;
import org.apache.commons.feedparser.network.ResourceRequest;
import org.apache.commons.feedparser.network.ResourceRequestFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Site;
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SiteService;
import com.feiyang.feeds.service.SubscribeService;

@Component
public class CrawlerServiceImp implements CrawlerService {
	private static final Logger LOG = Logger.getLogger(CrawlerService.class);
	@Autowired(required = true)
	private FeedContentService feedContentService;
	@Autowired(required = true)
	private SubscribeService subscribeService;
	@Autowired(required = true)
	private SiteService siteService;

	private static class FeedParserListener extends DefaultFeedParserListener {
		private List<FeedContent> crawledContent;
		private final String site;
		private String siteName;

		private String link;
		private String title;
		private String description;
		private String category;
		private String author;
		private Date pubDate;

		public FeedParserListener(String url) {
			crawledContent = new ArrayList<>();
			this.site = url;
		}

		@Override
		public void onChannel(FeedParserState state, String title, String link, String description)
		        throws FeedParserException {
			siteName = title;
		}

		public void onItem(FeedParserState state, String title, String link, String description, String permalink)
		        throws FeedParserException {
			this.title = title;
			this.link = link;
			this.description = description;
		}

		public void onCreated(FeedParserState state, Date date) throws FeedParserException {
			pubDate = date;
		}

		@Override
		public void onItemEnd() throws FeedParserException {
			crawledContent.add(new FeedContent(0L, site, link, title, description, category, author, pubDate));
			this.link = null;
			this.title = null;
			this.description = null;
			this.category = null;
			this.author = null;
			this.pubDate = null;
		}

		@Override
		public void onAuthor(FeedParserState state, String name, String email, String resource)
		        throws FeedParserException {
			this.author = name;
		}
	}

	@Override
	public Map<Site, List<FeedContent>> crawl(String url) {
		try {
			// crawl the rss site.
			ResourceRequest request = ResourceRequestFactory.getResourceRequest(url);
			InputStream is = request.getInputStream();
			FeedParserListener listener = new FeedParserListener(url);
			FeedParser parser = FeedParserFactory.newFeedParser();
			parser.parse(listener, is, url);
			List<FeedContent> contents = listener.crawledContent;

			// save content to storage.
			contents = feedContentService.saveConent(contents);

			// update unread.
			subscribeService.updateUnread(contents);

			// subscribe new site.
			Site site = new Site(url, listener.siteName);
			site = siteService.subscribeSite(site);

			Map<Site, List<FeedContent>> ret = new HashMap<>();
			ret.put(site, contents);
			return ret;
		} catch (IOException | FeedParserException e) {
			// TODO need some recovery mechanism.
			LOG.error(String.format("crawl site(%s) error:", url), e);
			return Collections.emptyMap();
		}
	}

	public FeedContentService getFeedContentService() {
		return feedContentService;
	}

	public void setFeedContentService(FeedContentService feedContentService) {
		this.feedContentService = feedContentService;
	}

	public SubscribeService getSubscribeService() {
		return subscribeService;
	}

	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
}
