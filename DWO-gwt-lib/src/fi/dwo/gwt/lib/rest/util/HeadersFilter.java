package fi.dwo.gwt.lib.rest.util;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.http.client.RequestBuilder;

public class HeadersFilter implements DispatcherFilter {

	private final Map<String,String> headers;
	
	HeadersFilter() {
		this(new HashMap<String,String>());
	}

	HeadersFilter(Map<String, String> headers) {
		this.headers = headers;
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
	}

	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		for(Map.Entry<String, String> entry: headers.entrySet()) {
			builder.setHeader(entry.getKey(), entry.getValue());
		}
		return true;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public int size() {
		return headers.size();
	}

	public boolean containsKey(Object key) {
		return headers.containsKey(key);
	}

	public String get(Object key) {
		return headers.get(key);
	}

	public String put(String key, String value) {
		return headers.put(key, value);
	}

	public String remove(Object key) {
		return headers.remove(key);
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		headers.putAll(m);
	}

	public void clear() {
		headers.clear();
	}

	public final static HeadersFilter instance = new HeadersFilter();
}
