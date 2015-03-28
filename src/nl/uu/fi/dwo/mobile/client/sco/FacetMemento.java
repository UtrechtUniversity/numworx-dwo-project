package nl.uu.fi.dwo.mobile.client.sco;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.dev.json.JsonString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

public class FacetMemento extends Memento {

	private static class StringList extends AbstractList<String> implements List<String> {

		JSONArray array;
		
		private StringList() {
			array = new JSONArray();
		}

		@Override
		public String get(int index) {
			JSONValue jsonValue = array.get(index);
			return fromJSON(jsonValue);
		}

		private String fromJSON(JSONValue jsonValue) {
			if (jsonValue == null ||
			    jsonValue == JSONNull.getInstance()) 
				return null;
			return jsonValue.isString().stringValue();
		}

		@Override
		public int size() {
			return array.size();
		}

		@Override
		public String set(int index, String element) {
			String prev = get(index);
			array.set(index, toJSON(element));
			return prev;
		}

		@Override
		public void add(int index, String element) {
			if(index == size()) {
				JSONValue value;
				value = toJSON(element);
				array.set(index, value);
			} else
				super.add(index, element);
		}

		private JSONValue toJSON(String element) {
			JSONValue value;
			if(element != null) value = new JSONString(element);
			else value = JSONNull.getInstance();
			return value;
		}

		public String toString() {
			return array.toString();
		}
	}
	
	
	private FacetAware view;
	
	public FacetMemento(Scorm2004IF api, FacetAware view) {
		super(api);
		this.view = view;
	}

	@Override
	String getValue(String key) {
		if(SUSPEND_DATA == key)
		{
			try {
				String value = super.getValue(key);
				JSONValue ob = JSONParser.parseStrict(value);
				return ob.isArray().get(0).isString().stringValue();
			} catch (Exception e) {
				return "";
			}
		}
		return super.getValue(key);
	}

	@Override
	boolean setValue(String key, String value) {
		if(SUSPEND_DATA == key) {
			StringList list = new StringList();
			list.add(value);
			view.getResponses(list);
			value = list.toString();
		}
		return super.setValue(key, value);
	}


}
