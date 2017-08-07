package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.List;

/**
 *
 * @author G.A.J. van der Plas
 */
public class CourseItem {
    private String key; //unique
    private String name;

    public CourseItem (){
        
    }
    
    public CourseItem (String aKey, String aName){
        key = aKey;
        name = aName;
    }
    
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
