// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\domain\\LaunchdataConvertor.java

package fi.dwo.dwojapplet.parameters.domain;

import java.util.Hashtable;

import fi.beans.scorm.ExtendedParameter;
import fi.beans.scorm.Parameter;
import fi.beans.scorm.TreeParameter;

/**
 * This convertor is used to convert the Launchdata from the parameters 
 * (with hashtables) to launchdata which applets understands (without hashtables).
 * @author M.J.B. Kupers
 *
 */
public class LaunchdataConvertor implements ConvertorIF {
    
    private static ConvertorIF _launchdataConvertor = null;
    
    private Hashtable clearedLaunchdata;

    public LaunchdataConvertor() {

    }
    
    /**
     * All the items in the tree are grouped together in one hashtable and returned.
     * If this is a nested-tree. The nested numbers are removed.
     * @param parameter
     * @param nrPrefix
     * @param source
     * @return
     */
    private Object convertTree(TreeParameter parameter, String srcPrefix, String nrPrefix, Hashtable source) {
        Hashtable ht = new Hashtable();
        /* If the parameter is not definied, use 0 as number of items */
        String sNrItems = (String) source.remove(parameter.getItemCountName() + srcPrefix);
        int nrItems = 0;
        if(sNrItems != null) {
            nrItems = Integer.parseInt(sNrItems);
        }
        ht.put(parameter.getItemCountName(), Integer.toString(nrItems));
        
        for(int i = 1; i <= nrItems; i++) {
            convertParams(parameter.getSubParameters(), srcPrefix + "_" + i, "_" + i, source, ht);
        }
        return ht;
    }
    
    private Object convertParameter(Parameter parameter, String srcPrefix, String nrPrefix, Hashtable source) {
        return source.remove(parameter.getName() + srcPrefix);
    }
    
    private void convertParams(Parameter[] parameters, String srcPrefix, String nrPrefix, Hashtable source, Hashtable target) {
        Object obj;
        for(int i = 0; i < parameters.length; i++) {
            obj = null;
            if(parameters[i] instanceof TreeParameter) {
                obj = convertTree((TreeParameter) parameters[i], srcPrefix, nrPrefix, source);
            } else if(parameters[i] instanceof ExtendedParameter) {
                convertParams(((ExtendedParameter) parameters[i]).getSubParameters(), srcPrefix, nrPrefix, source, target);
            } else {
                obj = convertParameter(parameters[i], srcPrefix, nrPrefix, source);
            }
            if(obj != null) {
                target.put(parameters[i].getName() + nrPrefix, obj);
            }
        }
    }

    /**
     * Converts the parameters-launchdata to the correct DWO-launchdata.
     * The return value is a hashtable where all the items (also tree items) are mapped.
     * @param launchdata
     * @param parameters
     * @return Object
     */
    @Override
    public Object convertHashtable(Hashtable launchdata, Parameter[] parameters) {
        Hashtable ht = new Hashtable();
        clearedLaunchdata = (Hashtable) launchdata.clone();
        convertParams(parameters, "", "", clearedLaunchdata, ht);
        return ht;
    }

    
    public static ConvertorIF instance() {
        if(_launchdataConvertor == null) {
            _launchdataConvertor = new LaunchdataConvertor();
        }
        return _launchdataConvertor;
    }
    
    /**
     * Converts the TreeParameter in the DWO-format to the parameters-launchdata
     * @param parameter The TreeParameter to convert.
     * @param srcPrefix The number int the source
     * @param nrPrefix
     * @param source
     * @param target
     */
    private void createTree(TreeParameter parameter, String srcPrefix, String nrPrefix, Hashtable source, Hashtable target) {
        Hashtable ht = (Hashtable) source.get(parameter.getName() + srcPrefix);
        if(ht != null) { //was he found?
            /* If the parameter is not definied, use 0 as number of items */
            String sNrItems = (String) ht.get(parameter.getItemCountName());
            int nrItems = 0;
            if(sNrItems != null) {
                nrItems = Integer.parseInt(sNrItems);
            }
	        target.put(parameter.getItemCountName() + nrPrefix, Integer.toString(nrItems));
	        
	        for(int i = 1; i <= nrItems; i++) {
	            createParams(parameter.getSubParameters(), "_" + i, nrPrefix + "_" + i, ht, target);
	        }
        }
    }
    
    private void createParameter(Parameter parameter, String srcPrefix, String nrPrefix, Hashtable source, Hashtable target) {
        Object obj = source.get(parameter.getName() + srcPrefix); 
        if(obj != null) { //was het found?
            target.put(parameter.getName() + nrPrefix, obj);
        }
    }

    
    private void createParams(Parameter[] parameters, String srcPrefix, String nrPrefix, Hashtable source, Hashtable target) {
        Object obj;
        for(int i = 0; i < parameters.length; i++) {
            obj = null;
            if(parameters[i] instanceof TreeParameter) {
                createTree((TreeParameter) parameters[i], srcPrefix, nrPrefix, source, target);
            } else if(parameters[i] instanceof ExtendedParameter) {
                createParams(((ExtendedParameter) parameters[i]).getSubParameters(), srcPrefix, nrPrefix, source, target);
            } else {
                createParameter(parameters[i], srcPrefix, nrPrefix, source, target);
            }
            if(obj != null) {
                target.put(parameters[i].getName(), obj);
            }
        }
    }

    /**
     * Converts the DWO-launchdata to the parameters-launchdata
     * @param obj
     * @param parameters
     * @return java.util.Hashtable
     */
    @Override
    public Hashtable createHashtable(Object obj, Parameter[] parameters) {
        if(obj instanceof Hashtable) {
            Hashtable source = (Hashtable) obj;
            Hashtable result;
            if(clearedLaunchdata != null) {
                result = (Hashtable) clearedLaunchdata.clone();
            } else {
                result = new Hashtable();
            }
            
            createParams(parameters, "", "", source, result);
            
            return result;
        } else {
            return null;
        }
    }
}