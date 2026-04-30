package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;

class ReviewObjectMap implements ObjectMap {

	final static String STATES = ReviewActivity.INTERACTIE_PANEL_STATES;
	final static Set<String> reviewSet = new TreeSet<>(Arrays.asList(STATES, "hoogtes", "ingeklapt")); 
	
	final private ObjectMap delegate;
	ObjectMap review;

	public boolean containsKey(String key) {
		return delegate.containsKey(key);
	}

	public Object get(String key) {
		if (reviewSet.contains(key)) return review.get(key);
		return delegate.get(key);
	}

	public int getInt(String key) {
		return delegate.getInt(key);
	}

	public double getDouble(String key) {
		return delegate.getDouble(key);
	}

	public boolean getBoolean(String key) {
		if (reviewSet.contains(key)) return review.getBoolean(key, delegate.getBoolean(key));
		return delegate.getBoolean(key);
	}

	public boolean getBoolean(String key, boolean value) {
		return delegate.getBoolean(key, value);
	}

	public String getString(String key) {
		return delegate.getString(key);
	}

	public Map<String, Object> getMap(String key) {
		return delegate.getMap(key);
	}

	public List<Object> getList(String key) {
		return delegate.getList(key);
	}

	public ObjectMap getObjectMap(String key) {
		return delegate.getObjectMap(key);
	}

	public ObjectList getObjectList(String key) {
		if (STATES.equals(key)) return review.getObjectList(key);
		return delegate.getObjectList(key);
	}

	public List<String> getStringList(String key) {
		return delegate.getStringList(key);
	}

	public List<Integer> getIntegerList(String key) {
		return delegate.getIntegerList(key);
	}

	public List<Boolean> getBooleanList(String key) {
		return delegate.getBooleanList(key);
	}

	public List<Double> getDoubleList(String key) {
		if (reviewSet.contains(key) && review.containsKey(key)) return review.getDoubleList(key);
		return delegate.getDoubleList(key);
	}

	public List<Map<String, Object>> getMapList(String key) {
		if (STATES.equals(key)) return review.getMapList(key);
		return delegate.getMapList(key);
	}

	public double[] getDoubleArray(String key) {
		return delegate.getDoubleArray(key);
	}

	public int[] getIntArray(String key) {
		return delegate.getIntArray(key);
	}

	public String[] getStringArray(String key) {
		return delegate.getStringArray(key);
	}

	public boolean[] getBooleanArray(String key) {
		return delegate.getBooleanArray(key);
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public Set<String> keySet() {
		return delegate.keySet();
	}

	ReviewObjectMap(ObjectMap delegate) {
		this.delegate = delegate;
		this.review = delegate.getObjectMap(CorrectieView.REVIEW_INTERACTIE_DATA);
	}

}
