// Source file: C:\\fi\\dwo\\parameters\\gui\\MainParameterComponent.java

package fi.dwo.parameters.gui;

import fi.beans.scorm.Parameter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;

public class MainParameterComponent extends ParameterComponent implements TabSheetIF {

    private GridBagLayout gridbag;
    
    
    
    /**
     * @param parameters
     * @param default
     * @roseuid 425A79BE02AF
     */
    public MainParameterComponent(Parameter[] parameters, Hashtable defaultValue) {
        super(null, null, defaultValue);
        this.setBackground(ParameterComponent.SELECTED_COLOR_1);

        GridBagConstraints c = new GridBagConstraints();
        gridbag = new GridBagLayout();
        this.setLayout(gridbag);

        c.insets = new Insets(COMPONENT_SPACING, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        ParameterComponentIF pc;
        for(int i = 0; i < parameters.length; i++) {
            pc = ParameterComponentCreator.createComponent(this, parameters[i], defaultValue);
            this.registerComponent(pc);
            gridbag.addLayoutComponent(pc.getComponent(), c);
            pc.getComponent().setVisible(false);
            this.add(pc.getComponent());
            pc.getComponent().setVisible(true);
            pc.getComponent().addComponentListener(this);
        }

        this.setSize(gridbag.preferredLayoutSize(this));
        this.validate();
        
        
    }
    
}