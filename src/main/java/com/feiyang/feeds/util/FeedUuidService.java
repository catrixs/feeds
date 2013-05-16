package com.feiyang.feeds.util;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.feiyang.feeds.model.FeedContent;

/**
 * simple feed content id generator service.
 * 
 * <li>high 32 bit is site hashcode.</li> <li>low 32 bit is article link
 * hashcode.</li>
 * 
 * @author chenfei
 * 
 */
public abstract class FeedUuidService {
	private static final long MAX_TICKET = 1L << 16;
	private static volatile long ticket = 0;

	public static long id(FeedContent content) {
		assert content != null;
		assert content.getPubDate() != null;

		Date date = content.getPubDate();
		if (ticket++ > MAX_TICKET) {
			ticket = 0;
		}
		return (date.getTime() << 16) + ticket;
	}

	public static long siteId(String site) {
		if (!StringUtils.hasText(site)) {
			throw new IllegalArgumentException(String.format("illegal site or link to generate id:site=%s", site));
		}
		long high = site.hashCode();
		high <<= 32;
		return high;
	}

	public static long maxSiteId(long siteId) {
		long low = -1L << 32;
		low >>>= 32;
		return siteId | low;
	}

	public static long linkId(String link) {
		if (!StringUtils.hasText(link)) {
			throw new IllegalArgumentException(String.format("illegal site or link to generate id:site=%s", link));
		}
		long low = link.hashCode();
		low <<= 32;
		low >>>= 32;
		return low;
	}
}
