package com.google.gwt.json.client;

public class JSONObject extends JSONValue {
	public JSONObject() {}

	public int size() { return 0; }
	public boolean containsKey(String key) { return false; }
	public JSONValue get(String key) { return null; }
	public JSONValue put(String key, JSONValue value) { return null; }
}
