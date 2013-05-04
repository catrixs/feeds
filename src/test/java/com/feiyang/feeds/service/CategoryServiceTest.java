package com.feiyang.feeds.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.feiyang.feeds.Fixture;
import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CategoryServiceTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalDatastoreServiceTestConfig());

	private Fixture fixture = new Fixture();

	private Random rnd = new Random();

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		fixture.setUp(DatastoreServiceFactory.getDatastoreService());
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
		fixture.tearDown(DatastoreServiceFactory.getDatastoreService());
	}

	@Test
	public void testCreateCategory() {
		String categoryName = rnd.nextDouble() + "";

		Category actual = fixture.categoryServiceFixture.createCategory(fixture.userFixture, categoryName);
		assertNotNull(actual);
		assertEquals(categoryName, actual.getName());
		assertNull(actual.getSubscribes());
	}

	@Test
	public void testSubscribeNewSite() {
		String categoryName = rnd.nextDouble() + "";
		Category c = fixture.categoryServiceFixture.createCategory(fixture.userFixture, categoryName);
		Category actual = fixture.categoryServiceFixture.subscribeSite(fixture.userFixture, c.getCategoryId(),
				"http://www.huxiu.com/rss/0.xml");
		assertEquals(1, actual.getSubscribes().size());

		Subscribe subscribe = actual.getScribes().iterator().next();
		List<FeedContent> contents = subscribe.getContents();
		assertEquals(subscribe.getFeeds().size(), contents.size());
	}

	@Test
	public void testSubscribeAlreadySite() {
		String categoryName = rnd.nextDouble() + "";

		Category c = fixture.categoryServiceFixture.createCategory(fixture.userFixture, categoryName);
		String subscribeSite = fixture.feedFixture.iterator().next().getSite();

		Category ret = fixture.categoryServiceFixture.subscribeSite(fixture.userFixture, c.getCategoryId(),
				subscribeSite);
		c = fixture.categoryServiceFixture.queryCategory(fixture.userFixture, categoryName);

		// check for return.
		assertEquals(1, ret.getSubscribes().size());
		assertEquals(1, ret.getScribes().size());
		// check for subscribe's.
		Subscribe actualSubscribe = ret.getScribes().iterator().next();
		assertEquals(subscribeSite, actualSubscribe.getSite());

		// check for category's subscribe list.
		assertEquals(1, c.getSubscribes().size());
		assertEquals(1, c.getScribes().size());
		assertEquals(c.getSubscribes().iterator().next().longValue(), actualSubscribe.getId());

		// check for duplicate subscribe.
		ret = fixture.categoryServiceFixture.subscribeSite(fixture.userFixture, c.getCategoryId(), fixture.feedFixture
				.iterator().next().getSite());
		assertEquals(1, ret.getSubscribes().size());
		assertEquals(1, ret.getScribes().size());
	}
}
