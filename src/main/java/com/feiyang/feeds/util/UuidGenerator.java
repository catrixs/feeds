package com.feiyang.feeds.util;

public interface UuidGenerator {
	static final UuidGenerator INSTANCE = new SimpleUuidGenerator((byte) 0x0);

	long next();
}
