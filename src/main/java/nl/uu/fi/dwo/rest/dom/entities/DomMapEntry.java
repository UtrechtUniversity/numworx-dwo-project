package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomMapEntry<PersistenceId, V>  {

    private PersistenceId key;
    private V value;

    public DomMapEntry() {
    }
    
    public DomMapEntry(Map.Entry<PersistenceId, V> entry) {
        key = entry.getKey();
        value = entry.getValue();
    }

    public PersistenceId getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(PersistenceId key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V aValue) {
        value = aValue;
    }

}
