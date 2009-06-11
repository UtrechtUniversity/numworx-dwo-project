/*
 * Created on May 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fi.dwo.parameters.gui;

/**
 * @author M.J.B. Kupers
 *
 */
public interface ItemLaunchdataCallBackIF {
    
    /**
     * For nested items. They can ask to the parent a string representing the
     * current sequence.
     * 
     * (The name is different from getSequenceString in ParameterComponentIF to prevent type conflicts)
     * 
     * @return java.lang.String
     * @roseuid 42567A5600EA
     */
    public String getSequenceStr(ItemLaunchdataIF component);


}
