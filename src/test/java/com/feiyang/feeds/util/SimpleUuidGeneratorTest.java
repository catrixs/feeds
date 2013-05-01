package com.feiyang.feeds.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleUuidGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNext() throws Exception {
		long id = SimpleUuidService.next();
		System.err.println(Long.toHexString(id));

		long id2 = SimpleUuidService.next();
		System.err.println(Long.toHexString(id2));
		assertTrue(id2 > id);

		Thread.sleep(1000);
		long id3 = SimpleUuidService.next();
		System.err.println(Long.toHexString(id3));
		long timestamp = id3 >>> 16;
		assertTrue(timestamp > (id >>> 16));

		long start = System.currentTimeMillis();
		long lastId = SimpleUuidService.next();
		for (int i = 0; i < 100000; i++) {
			id = SimpleUuidService.next();
			assertTrue(String.format("last=%d, now=%d", lastId, id), id > lastId);
			lastId = id;
		}
		long end = System.currentTimeMillis();
		System.err.printf("uuid perf=%d/100000", (end - start));
	}
}
