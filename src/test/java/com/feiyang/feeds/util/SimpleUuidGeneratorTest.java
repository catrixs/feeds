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
		SimpleUuidGenerator uuid = new SimpleUuidGenerator((byte) 2);
		long id = uuid.next();
		System.err.println(Long.toHexString(id));

		byte biz = (byte) ((id & 0xF000L) >>> 12);
		assertEquals(2, biz);

		long id2 = uuid.next();
		System.err.println(Long.toHexString(id2));
		assertTrue(id2 > id);

		Thread.sleep(1000);
		long id3 = uuid.next();
		System.err.println(Long.toHexString(id3));
		long timestamp = id3 >>> 16;
		assertTrue(timestamp > (id >>> 16));

		long start = System.currentTimeMillis();
		long lastId = uuid.next();
		for (int i = 0; i < 100000; i++) {
			id = uuid.next();
			assertTrue(String.format("last=%d, now=%d", lastId, id), id > lastId);
			lastId = id;
		}
		long end = System.currentTimeMillis();
		System.err.printf("uuid perf=%d/100000", (end - start));
	}
}
