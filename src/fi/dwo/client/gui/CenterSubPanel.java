// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CenterSubPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;

/**
 * This interface is used as the subpanel of the centerpanel.
 * 
 * @author M.J.B. Kupers
 *  
 */
public interface CenterSubPanel {

    /**
     * Called when this panel is closes. Can be used to save session-data, for
     * example the sco-data
     *  
     */
    public void end();

    /**
     * Returns the Header Panel.
     * 
     * @return The Header Panel.
     */
    public Container getHeaderPanel();

    /**
     * Sets the centerpanel.
     * 
     * @param centerPanel The centerpanel to set.
     */
    public void setCenterPanel(CenterPanel centerPanel);

    /**
     * Returns the component of the implementator. Is used to add to the gui.
     * 
     * @return The component to add to the gui.
     */
    public Component getComponent();
}