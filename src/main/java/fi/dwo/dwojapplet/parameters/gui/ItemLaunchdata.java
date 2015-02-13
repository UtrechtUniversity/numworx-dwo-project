// Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\ItemLaunchdata.java

package fi.dwo.dwojapplet.parameters.gui;

import java.util.Enumeration;
import java.util.Hashtable;

public class ItemLaunchdata implements ItemLaunchdataIF {
    private Hashtable launchdata;

    private Object key;
    
    private ItemLaunchdataCallBackIF parent;

    /**
     * @param launchdata
     */
    public ItemLaunchdata(ItemLaunchdataCallBackIF parent, Hashtable launchdata) {
        this.parent = parent;
        this.launchdata = launchdata;
    }

    /**
     * @param parameters
     */
    public void addParameters(Hashtable parameters) {
        Enumeration enumer = launchdata.keys();
        while(enumer.hasMoreElements()) {
            String s = (String) enumer.nextElement();
            parameters.put(s + parent.getSequenceStr(this), launchdata.get(s));
        }

    }

    /**
     * @return Object
     */
    public Object getKey() {
        return key;
    }

    /**
     * @param key
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ItemLaunchdataIF#getLaunchdata()
     */
    public Hashtable getLaunchdata() {
        return launchdata;
    }
}