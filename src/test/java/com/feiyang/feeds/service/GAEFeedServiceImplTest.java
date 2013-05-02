package com.feiyang.feeds.service;

import static junit.framework.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.feiyang.feeds.Fixture;
import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.service.FeedService;
import com.feiyang.feeds.service.gea.FeedServiceImpl;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class GAEFeedServiceImplTest {
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
		fixture.tearDown(DatastoreServiceFactory.getDatastoreService());
	}

	@Test
	public void testShowCategory() {
		FeedService service = new FeedServiceImpl();
		List<Category> categories = service.showCategory(fixture.userFixture.getUid());

		assertEquals(1, categories.size());
		Category actual = categories.iterator().next();
		assertEquals(fixture.categoryFixture.getCategoryId(), actual.getCategoryId());
		assertEquals(fixture.categoryFixture.getName(), actual.getName());
		assertEquals(fixture.categoryFixture.getSubscribes(), actual.getSubscribes());
	}

	@Test
	public void testCategoryFeeds() {
		FeedService service = new FeedServiceImpl();
		List<Category> categories = service.showCategory(fixture.userFixture.getUid());
		Category category = categories.iterator().next();
		Map<Subscribe, List<FeedContent>> actual = service.categoryFeeds(category);

		assertEquals(5, actual.size());
		for (Entry<Subscribe, List<FeedContent>> entry : actual.entrySet()) {
			int index = fixture.subscirbeFixture.indexOf(entry.getKey());
			assertTrue(index >= 0);

			Subscribe expect = fixture.subscirbeFixture.get(index);
			assertEquals(expect.getFeeds(), entry.getKey().getFeeds());

			assertEquals(2, entry.getValue().size());
			for (FeedContent feedContent : entry.getValue()) {
				index = fixture.feedFixture.indexOf(feedContent);
				assertTrue(index >= 0);

				FeedContent expectContent = fixture.feedFixture.get(index);
				assertEquals(expectContent.getDescription(), feedContent.getDescription());
			}
		}
	}

}
