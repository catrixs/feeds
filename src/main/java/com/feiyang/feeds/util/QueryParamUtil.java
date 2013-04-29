package com.feiyang.feeds.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

public abstract class QueryParamUtil {
	public static List<Long> splitToList(String param) {
		if (StringUtils.isEmpty(param)) {
			return Collections.emptyList();
		}

		String[] strIds = StringUtils.split(param, ",");
		List<Long> rs = new ArrayList<>(strIds.length);
		for (String strId : strIds) {
			rs.add(Long.parseLong(strId));
		}
		return rs;
	}
}
