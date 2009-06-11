/*
 * Created on Apr 14, 2005
 *
 */
package fi.dwo.parameters.gui;

import java.util.Hashtable;

import fi.beans.scorm.DataTypeIF;
import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormBoolean;
import fi.beans.scorm.ScormDouble;
import fi.beans.scorm.ScormFormula;
import fi.beans.scorm.ScormGroup;
import fi.beans.scorm.ScormInteger;
import fi.beans.scorm.ScormString;
import fi.beans.scorm.ScormText;
import fi.beans.scorm.ScormTree;
import fi.beans.scorm.TreeParameter;

/**
 * @author M.J.B. Kupers
 *
 */
public class ParameterComponentCreator {

    /**
     * 
     */
    public ParameterComponentCreator() {
        super();
    }
    
    public static ParameterComponentIF createComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	return createComponent(parent, parameter,defaultValue, false);
    }

    public static ParameterComponentIF createComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        DataTypeIF type = parameter.getType();
        if(type instanceof ScormInteger) {
            return new IntegerParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormBoolean) {
            return new BooleanParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormDouble) {
            return new DoubleParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormString) {
            return new StringParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormText) {
            return new TextParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormFormula) {
            return new FormulaParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormGroup) {
            return new GroupParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormTree) {
            if(type.getMustTabSheet()) {
                return new SingleLevelTreeParameterComponent(parent, (TreeParameter) parameter, defaultValue, isSub);
            } else {
                return new MultiLevelTreeParameterComponent(parent, (TreeParameter) parameter, defaultValue, isSub);
            }
        } else {
            return null;
        }
        
    }

    public static ParameterComponentIF createTreeComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	return createTreeComponent(parent, parameter,defaultValue, false);
    }

    public static ParameterComponentIF createTreeComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        DataTypeIF type = parameter.getType();
        if(type instanceof ScormInteger) {
            return new TreeIntegerParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormBoolean) {
            return new TreeBooleanParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormDouble) {
            return new TreeDoubleParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormString) {
            return new TreeStringParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormFormula) {
            return new TreeFormulaParameterComponent(parent, parameter, defaultValue, isSub);
        } else if(type instanceof ScormGroup) {
            return new TreeGroupParameterComponent(parent, parameter, defaultValue, isSub);
        } else {
            return null;
        }
        
    }
    
}
