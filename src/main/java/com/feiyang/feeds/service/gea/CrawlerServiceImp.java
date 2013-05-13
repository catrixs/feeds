package com.feiyang.feeds.service.gea;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.feiyang.feeds.service.CrawlerService;
import com.feiyang.feeds.service.FeedContentService;
import com.feiyang.feeds.service.SubscribeService;

@Component
public class CrawlerServiceImp implements CrawlerService {
	private static final Logger LOG = Logger.getLogger(CrawlerService.class);

	@Autowired(required = true)
	private FeedContentService feedContentService;

	@Autowired(required = true)
	private SubscribeService subscribeService;

	private static class FeedParserListener extends DefaultFeedParserListener {
		private List<FeedContent> crawledContent;
		private String site;

		private String link;
		private String title;
		private String description;
		private String category;
		private String author;
		private Date pubDate;

		public FeedParserListener() {
			crawledContent = new ArrayList<>();
		}

		@Override
		public void onChannel(FeedParserState state, String title, String link, String description)
		        throws FeedParserException {
			site = link;
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
	public List<FeedContent> crawl(String site) {
		try {
			// crawl the rss site.
			ResourceRequest request = ResourceRequestFactory.getResourceRequest(site);
			InputStream is = request.getInputStream();
			FeedParserListener listener = new FeedParserListener();
			FeedParser parser = FeedParserFactory.newFeedParser();
			parser.parse(listener, is, site);
			List<FeedContent> contents = listener.crawledContent;

			// save content to storage.
			contents = feedContentService.saveConent(contents);

			// update unread.
			subscribeService.updateUnread(contents);

			return contents;
		} catch (IOException | FeedParserException e) {
			// TODO need some recovery mechanism.
			LOG.error(String.format("crawl site(%s) error:", site), e);
			return Collections.emptyList();
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
}
