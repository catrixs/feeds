package com.feiyang.feeds.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.Fixture;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.service.gea.GAEFeedContentServiceImpl;
import com.feiyang.feeds.util.FeedUuidService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CrawlerServiceImpTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalDatastoreServiceTestConfig());

	private Fixture fixture = new Fixture();

	private CrawlerService crawlerService = new CrawlerServiceImp();

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		fixture.setUp(DatastoreServiceFactory.getDatastoreService());

		((CrawlerServiceImp) crawlerService).setFeedContentService(new GAEFeedContentServiceImpl());
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testCrawl() {
		List<FeedContent> contents = crawlerService.crawl("http://www.huxiu.com/rss/0.xml");
		Set<Long> set = new HashSet<>();
		for (FeedContent feedContent : contents) {
			System.err.println(feedContent);
			assertTrue(StringUtils.hasText(feedContent.getSite()));
			assertTrue(StringUtils.hasText(feedContent.getAuthor()));
			// assertTrue(StringUtils.hasText(feedContent.getCategory()));
			assertTrue(StringUtils.hasText(feedContent.getDescription()));
			assertTrue(StringUtils.hasText(feedContent.getLink()));
			assertTrue(StringUtils.hasText(feedContent.getTitle()));
			assertNotNull(feedContent.getPubDate());

			boolean add = set.add(FeedUuidService.id(feedContent));
			assertTrue(add);
		}
	}
}
