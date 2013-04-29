package com.feiyang.feeds.util;

/**
 * <p>
 * a very simple uuid generator which could generate 64-bit long value.
 * </p>
 * <li>high 54 bit is timestamp whose accuracy is in seconds.</li> <li>next 2
 * bit is extend which could be used by represents service type.</li> <li>the
 * lowest 8 bit is auto-increment time ticket</li>
 * 
 * 
 * @author chenfei
 * 
 */
public class SimpleUuidGenerator implements UuidGenerator {
	private static final long MAX_TICKET = 1L << 12;
	private final long bizFlag;
	private volatile long ticket;
	private volatile long timestamp;

	public SimpleUuidGenerator(byte bizFlag) {
		this.bizFlag = (((long) bizFlag) << 12) & 0xF000L;
		this.timestamp = System.currentTimeMillis() << 16;
		this.ticket = 0;
	}

	@Override
	public long next() {
		if (ticket++ > MAX_TICKET) {
			timestamp += (1L << 16);
			ticket = 0;
		} else if ((System.currentTimeMillis() << 16) > timestamp) {
			timestamp = System.currentTimeMillis() << 16;
			ticket = 0;
		}
		return timestamp | bizFlag | ticket;
	}
}
