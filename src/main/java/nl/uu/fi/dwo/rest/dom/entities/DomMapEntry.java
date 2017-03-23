package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DomMapEntry<K, V> implements Map.Entry<K, V> {

    private K key;
    V value;

    public DomMapEntry() {
    }
    
    public DomMapEntry(Map.Entry<K, V> entry) {
        key = entry.getKey();
        value = entry.getValue();
    }

    @Override
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V aValue) {
        V oldValue;
        oldValue = value;
        value = aValue;
        return oldValue;
    }

}
