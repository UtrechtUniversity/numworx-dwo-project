//Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\ItemLaunchdataIF.java

package fi.dwo.dwojapplet.parameters.gui;

import java.util.Hashtable;

public interface ItemLaunchdataIF 
{
    
    /**
    @param parameters
     */
    public void addParameters(Hashtable parameters);
    
    /**
    @return Object
     */
    public Object getKey();
    
    /**
    @param key
     */
    public void setKey(Object key);
    
    public Hashtable getLaunchdata();
}
