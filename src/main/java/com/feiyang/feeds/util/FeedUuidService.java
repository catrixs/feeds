package com.feiyang.feeds.util;

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
	public static long id(FeedContent content) {
		return siteId(content.getSite()) | linkId(content.getLink());
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
