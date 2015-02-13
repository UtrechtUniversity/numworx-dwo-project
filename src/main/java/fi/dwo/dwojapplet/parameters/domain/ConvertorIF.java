// Source file: C:\\parameters\\fi\\dwo\\parameters\\domain\\ConvertorIF.java

package fi.dwo.dwojapplet.parameters.domain;

import java.util.Hashtable;
import fi.beans.scorm.Parameter;

/**
 * The Convertor is used to convert the launchdata from the parameters in the right format.
 * 
 * @author M.J.B. Kupers
 *
 */
public interface ConvertorIF {

    /**
     * @param launchdata
     * @param parameters
     * @return Object
     */
    public Object convertHashtable(Hashtable launchdata, Parameter[] parameters);

    /**
     * @param obj
     * @param parameters
     * @return java.util.Hashtable
     */
    public Hashtable createHashtable(Object obj, Parameter[] parameters);
}