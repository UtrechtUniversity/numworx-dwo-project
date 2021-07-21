package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ValueSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> implements Comparator<Map.Entry<K, V>> {

	final Comparator<V> cmp;
	final TreeSet<Entry<K,V>> set;
	
	public ValueSortedMap(Comparator<V> cmp) {
		this.cmp = cmp;
		this.set = new TreeSet<>(this);
	}


	@Override
	public Set<Entry<K, V>> entrySet() {
		return set;
	}


	@Override
	public int compare(Entry<K, V> o1, Entry<K, V> o2) {
		int key = o1.getKey().compareTo(o2.getKey());
		if (key == 0) return 0;
		int val = cmp.compare(o1.getValue(), o2.getValue());
		return val == 0? key : val;
	}


	@Override
	public V put(K key, V value) {
		V old = null;
		Iterator<Entry<K,V>> i = set.iterator();
		Map.Entry<K, V> kv = new SimpleEntry<K, V>(key, value);
		while(i.hasNext()) {
			Entry<K,V> entry = i.next();
			if (Objects.equals(key, entry.getKey())) {
				i.remove();
				old = entry.getValue();
				break;				
			}
		}
		set.add(kv);
		return old;
	}

}
