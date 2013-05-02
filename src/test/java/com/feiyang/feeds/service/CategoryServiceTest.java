package com.feiyang.feeds.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.feiyang.feeds.Fixture;
import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.service.gea.CategoryServiceImpl;
import com.feiyang.feeds.service.gea.CrawlerServiceImp;
import com.feiyang.feeds.service.gea.FeedContentServiceImpl;
import com.feiyang.feeds.service.gea.SiteServiceImpl;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CategoryServiceTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalDatastoreServiceTestConfig());

	private Fixture fixture = new Fixture();

	private Random rnd = new Random();

	private CategoryService srv = new CategoryServiceImpl();

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		fixture.setUp(DatastoreServiceFactory.getDatastoreService());
		((CategoryServiceImpl) srv).setFeedContentService(new FeedContentServiceImpl());
		((CategoryServiceImpl) srv).setCrawlerService(new CrawlerServiceImp());
		((CategoryServiceImpl) srv).setSiteService(new SiteServiceImpl());
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
		fixture.tearDown(DatastoreServiceFactory.getDatastoreService());
	}

	@Test
	public void testCreateCategory() {
		String categoryName = rnd.nextDouble() + "";

		Category actual = srv.createCategory(fixture.userFixture, categoryName);
		assertNotNull(actual);
		assertEquals(categoryName, actual.getName());
		assertNull(actual.getSubscribes());
	}

	@Test
	public void testSubscribeNewSite() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeAlreadySite() {
		String categoryName = rnd.nextDouble() + "";

		Category c = srv.createCategory(fixture.userFixture, categoryName);

		Map<Subscribe, List<FeedContent>> actual = srv.subscribeSite(fixture.userFixture, c.getCategoryId(),
				fixture.feedFixture.iterator().next().getSite());
		assertEquals(1, actual.size());
		c = srv.queryCategory(fixture.userFixture, categoryName);

		// check for category's subscribe list.
		assertEquals(1, c.getSubscribes().size());
		assertEquals(actual.entrySet().iterator().next().getKey().getId(), c.getSubscribes().iterator().next()
				.longValue());

		// check for subscribe's content.
		Subscribe actualScribe = actual.keySet().iterator().next();
		assertEquals(2, actual.get(actualScribe).size());
		Set<String> expectFeedContent = new HashSet<>();
		Iterator<FeedContent> it = fixture.feedFixture.iterator();
		expectFeedContent.add(it.next().getDescription());
		expectFeedContent.add(it.next().getDescription());
		for (FeedContent actualContent : actual.get(actualScribe)) {
			assertTrue(expectFeedContent.remove(actualContent.getDescription()));
		}

		// check for duplicate subscribe.
		actual = srv.subscribeSite(fixture.userFixture, c.getCategoryId(), fixture.feedFixture.iterator().next()
				.getSite());
		assertEquals(1, actual.size());
		c = srv.queryCategory(fixture.userFixture, categoryName);

		// check for category's subscribe list.
		assertEquals(1, c.getSubscribes().size());
		assertEquals(actual.entrySet().iterator().next().getKey().getId(), c.getSubscribes().iterator().next()
				.longValue());

		assertEquals(2, actual.get(actualScribe).size());
	}

	@Test
	public void testDuplicateSubsribe() {
		testSubscribeAlreadySite();

	}
}
