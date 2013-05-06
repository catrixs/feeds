package com.feiyang.feeds.service;

public interface SiteService {
	/**
	 * subscribe one site, if the site already subscribed by another, just
	 * return the info in the storage. otherwise store this site to storage.
	 * 
	 * @param site
	 * @return not exist true, exists false.
	 */
	boolean subscribeSite(String site);

	/**
	 * check this site has subscribes or not.
	 * 
	 * @param site
	 * @return
	 */
	boolean existSubscribe(String site);
}
