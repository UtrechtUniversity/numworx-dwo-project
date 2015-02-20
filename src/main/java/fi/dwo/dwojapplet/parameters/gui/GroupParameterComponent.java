// Source file: C:\\fi\\dwo\\parameters\\gui\\GroupParameterComponent.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Hashtable;

import fi.beans.scorm.ExtendedParameter;
import fi.beans.scorm.Parameter;

public class GroupParameterComponent extends ParameterComponent {

    private FlowLayout flow;

    public GroupParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);
    }

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @param isSub
     * @roseuid 425A519D006D
     */
    public GroupParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

        ParameterComponentIF pc;
        Component lastComponent = preLabel;
        int maxHeight = preLabel.getSize().height;
        if (parameter instanceof ExtendedParameter) {
            Parameter[] parameters = ((ExtendedParameter) parameter).getSubParameters();
            for (int i = 0; i < parameters.length; i++) {
                pc = ParameterComponentCreator.createComponent(this, parameters[i], defaultValue, true);
                this.registerComponent(pc);
                if (pc.getComponent().getSize().height > maxHeight) {
                    maxHeight = pc.getComponent().getSize().height;
                }
                pc.getComponent().setLocation(lastComponent.getLocation().x + lastComponent.getSize().width + 10, 1);
                pc.getComponent().setVisible(false);
                this.add(pc.getComponent());
                pc.getComponent().setVisible(true);
                lastComponent = pc.getComponent();
                lastComponent.addComponentListener(this);
            }
        }
        lastComponent = generatePostItems(lastComponent);

        this.setSize(lastComponent.getLocation().x + lastComponent.getSize().width + 5, maxHeight + 2);
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#isFocussed(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    @Override
    public void isFocussed(ParameterComponentIF component) {
        isFocussed();
        super.isFocussed(component);
        for (int i = 0; i < subComponents.size(); i++) {
            ((ParameterComponentIF) subComponents.elementAt(i)).setColor(this.getBackground());
        }
        if (parent != null) {
            parent.isFocussed(this);
        }
    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#unFocus()
     */

    @Override
    public void unFocus() {
        super.unFocus();
        for (int i = 0; i < subComponents.size(); i++) {
            ((ParameterComponentIF) subComponents.elementAt(i)).setColor(this.getBackground());
        }
        focussedComponent.unFocus();

    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */

    @Override
    public String getSequenceString(ParameterComponentIF component) {
        if (parent != null) {
            return parent.getSequenceString(this);
        } else {
            return super.getSequenceString(component);
        }
    }
}
