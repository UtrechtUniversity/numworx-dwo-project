// Source file: C:\\fi\\dwo\\parameters\\gui\\ParameterComponentIF.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;

public interface ParameterComponentIF {

    /**
     * @roseuid 42567A2A038C
     */
    public void unFocus();

    /**
     * @return java.awt.Color
     * @roseuid 42567A2A0399
     */
    public Color getColor();

    public void setColor(Color c);

    /**
     * Adds this parameter (and possible subparameters) to the hashtable
     *
     *
     * @param parameters
     * @roseuid 42567A2A03A9
     */
    public void addParameters(Hashtable parameters);

    /**
     * @return java.awt.Component
     * @roseuid 42567A2A03D9
     */
    public Component getComponent();

    /**
     * @param parent
     * @roseuid 42567A56007D
     */
    public void setParentCom(ParameterComponentIF parent);

    /**
     * @return fi.dwo.parameters.gui.ParameterComponentIF
     * @roseuid 42567A56008D
     */
    public ParameterComponentIF getParentCom();

    /**
     * @param component
     * @roseuid 42567A56009C
     */
    public void isFocussed(ParameterComponentIF component);

    /**
     * For nested items. They can ask to the parent a string representing the
     * current sequence.
     *
     *
     * @param component
     * @return java.lang.String
     * @roseuid 42567A5600EA
     */
    public String getSequenceString(ParameterComponentIF component);

    /**
     * Register a component to the parent. At the end, the parent can ask to all
     * the registerd component for the parameters
     *
     *
     * @param component
     * @roseuid 42567A5600FA
     */
    public void registerComponent(ParameterComponentIF component);

    /**
     * @roseuid 425D00FF00BB
     */
    public void reset();

    public void setSequenceLabel(int nr);
}
