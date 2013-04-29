package com.feiyang.feeds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.feiyang.feeds.model.Category;
import com.feiyang.feeds.model.FeedContent;
import com.feiyang.feeds.model.Subscribe;
import com.feiyang.feeds.model.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;

public class Fixture {

	public final User userFixture = new User(12345L, "test");
	public final Category categoryFixture = new Category(userFixture, 12345L, "testCategory", Arrays.asList(1L, 2L, 3L,
			4L, 5L));
	public final List<Subscribe> subscirbeFixture = Arrays.asList(new Subscribe[] {
			new Subscribe(1L, "test_site_1", userFixture.getUid(), Arrays.asList(1L, 2L)),
			new Subscribe(2L, "test_site_2", userFixture.getUid(), Arrays.asList(3L, 4L)),
			new Subscribe(3L, "test_site_3", userFixture.getUid(), Arrays.asList(5L, 6L)),
			new Subscribe(4L, "test_site_4", userFixture.getUid(), Arrays.asList(7L, 8L)),
			new Subscribe(5L, "test_site_5", userFixture.getUid(), Arrays.asList(9L, 10L)) });

	public final List<FeedContent> feedFixture = Arrays.asList(new FeedContent[] {
			new FeedContent(1L, "test_site_1", "title_1", "link_1", "desc_1"),
			new FeedContent(2L, "test_site_1", "title_2", "link_2", "desc_2"),
			new FeedContent(3L, "test_site_2", "title_3", "link_3", "desc_3"),
			new FeedContent(4L, "test_site_2", "title_4", "link_4", "desc_4"),
			new FeedContent(5L, "test_site_3", "title_5", "link_5", "desc_5"),
			new FeedContent(6L, "test_site_3", "title_6", "link_6", "desc_6"),
			new FeedContent(7L, "test_site_4", "title_7", "link_7", "desc_7"),
			new FeedContent(8L, "test_site_5", "title_8", "link_8", "desc_8"),
			new FeedContent(9L, "test_site_5", "title_9", "link_9", "desc_9"),
			new FeedContent(10L, "test_site_5", "title_10", "link_10", "desc_10") });

	private List<Key> keys;

	public void setUp(DatastoreService datastore) {
		keys = new ArrayList<>();
		keys.add(datastore.put(categoryFixture.toEntity()));

		for (Subscribe iterable_element : subscirbeFixture) {
			keys.add(datastore.put(iterable_element.toEntity()));
		}

		for (FeedContent iterable_element : feedFixture) {
			keys.add(datastore.put(iterable_element.toEntity()));
		}
		System.err.println(keys);
	}

	public void tearDown(DatastoreService datastore) {
	}
}
