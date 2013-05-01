package com.feiyang.feeds.util;

/**
 * <p>
 * a very simple uuid generator which could generate 64-bit long value.
 * </p>
 * <li>high 52 bit is timestamp whose accuracy is in seconds.</li> <li>the
 * lowest 12 bit is auto-increment time ticket</li>
 * 
 * 
 * @author chenfei
 * 
 */
public class SimpleUuidService {
	private static final int TIMESTAMP_SHIFT = 12;
	private static final long MAX_TICKET = 1L << TIMESTAMP_SHIFT;
	private static volatile long ticket;
	private static volatile long timestamp = System.currentTimeMillis() << TIMESTAMP_SHIFT;

	public static long next() {
		if (ticket++ > MAX_TICKET) {
			timestamp += (1L << TIMESTAMP_SHIFT);
			ticket = 0;
		} else if ((System.currentTimeMillis() << TIMESTAMP_SHIFT) > timestamp) {
			timestamp = System.currentTimeMillis() << TIMESTAMP_SHIFT;
			ticket = 0;
		}
		return timestamp | ticket;
	}

	public static long timestamp(long id) {
		return id >>> TIMESTAMP_SHIFT;
	}
}
