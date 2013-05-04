package com.feiyang.feeds.api.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.feiyang.feeds.Fixture;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CategoryJsonTest {
	private Fixture fixture = new Fixture();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToJson() throws JSONException {
		JSONObject json = CategoryJson.toJson(fixture.categoryFixture);
		System.err.println(json.toString());
	}

}
