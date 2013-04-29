package com.feiyang.feeds.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.stereotype.Component;

import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.util.UuidGenerator;

@Component
public class CrawlerServiceImp implements CrawlerService {
	private static final Logger LOG = Logger.getLogger(CrawlerService.class);

	private static class FeedParserListener extends DefaultFeedParserListener {
		private List<FeedContent> crawledContent;
		private String site;
		private Calendar calendar;

		public FeedParserListener() {
			crawledContent = new ArrayList<>();
		}

		@Override
		public void onChannel(FeedParserState state, String title, String link, String description)
				throws FeedParserException {
			site = title;
		}

		public void onItem(FeedParserState state, String title, String link, String description, String permalink)
				throws FeedParserException {
			crawledContent.add(new FeedContent(UuidGenerator.INSTANCE.next(), site, title, link, description));
		}

		public void onCreated(FeedParserState state, Date date) throws FeedParserException {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		}
	}

	@Override
	public List<FeedContent> crawl(String site) {
		try {
			ResourceRequest request = ResourceRequestFactory.getResourceRequest(site);
			InputStream is = request.getInputStream();
			FeedParserListener listener = new FeedParserListener();
			FeedParser parser = FeedParserFactory.newFeedParser();
			parser.parse(listener, is, site);
			return listener.crawledContent;
		} catch (IOException | FeedParserException e) {
			// TODO need some recovery mechanism.
			LOG.error(String.format("crawl site(%s) error:", site), e);
			return Collections.emptyList();
		}
	}
}
