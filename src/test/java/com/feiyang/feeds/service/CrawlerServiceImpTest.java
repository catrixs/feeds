package com.feiyang.feeds.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.feiyang.feeds.Fixture;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Site;
import com.feiyang.feeds.util.FeedUuidService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CrawlerServiceImpTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
	        new LocalDatastoreServiceTestConfig());

	private Fixture fixture = new Fixture();

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		fixture.setUp(DatastoreServiceFactory.getDatastoreService());
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testCrawl() {
		Map<Site, List<FeedContent>> contents = fixture.crawlerServiceFixture.crawl("http://www.huxiu.com/rss/0.xml");
		Set<Long> set = new HashSet<>();
		for (Entry<Site, List<FeedContent>> entry : contents.entrySet()) {
			assertTrue(StringUtils.hasText(entry.getKey().getName()));
			for (FeedContent feedContent : entry.getValue()) {
				System.err.println(feedContent);
				assertTrue(StringUtils.hasText(feedContent.getSite()));
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
}
