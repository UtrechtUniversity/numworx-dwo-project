// Source file: C:\\fi\\dwo\\parameters\\gui\\BooleanParameterComponent.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.FocusEvent;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import fi.beans.scorm.Parameter;
import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class BooleanParameterComponent extends ParameterComponent {

    private JRadioButton falseValue;
    private JRadioButton trueValue;

    public BooleanParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);
    }

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @param isSub
     * @roseuid 42567A5500AC
     */
    public BooleanParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        ButtonGroup cbg = new ButtonGroup();
        trueValue = new JRadioButton(TextMapper.getText(TextMapper.BOOLEAN_TRUE),
                false);
        cbg.add(trueValue);
        trueValue.setFont(ParameterConstants.LABEL_FONT);
        FontMetrics fm = trueValue.getFontMetrics(trueValue.getFont());
        trueValue.setSize(trueValue.getPreferredSize());
				//fm.stringWidth(trueValue.getLabel()) + 20, fm
        //.getHeight());

        trueValue.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        trueValue.addFocusListener(this);
        trueValue.setVisible(false);
        this.add(trueValue);
        trueValue.setVisible(true);

        falseValue = new JRadioButton(TextMapper.getText(TextMapper.BOOLEAN_FALSE),
                true);
        cbg.add(falseValue);
        falseValue.setFont(ParameterConstants.LABEL_FONT);
        fm = falseValue.getFontMetrics(falseValue.getFont());
        falseValue.setSize(falseValue.getPreferredSize());

        falseValue.setLocation(trueValue.getLocation().x + trueValue.getSize().width + 10, 1);
        falseValue.addFocusListener(this);
        falseValue.setVisible(false);
        this.add(falseValue);
        falseValue.setVisible(true);

        falseValue.setBackground(parent.getColor());
        trueValue.setBackground(parent.getColor());

        this.setSize(falseValue.getLocation().x + falseValue.getSize().width
                + ParameterComponent.LEFT_MARGIN, falseValue.getSize().height
                + ParameterComponent.COMPONENT_SPACING);

        Component last = generatePostItems(falseValue);

        if (this.defaultValue.containsKey(parameter.getName())) {
            String boolValueString = (String) (this.defaultValue.get(parameter.getName()));
            boolean boolValue = false;
            if (boolValueString != null & boolValueString.equals("true")) {
                boolValue = true;
            }
            if (boolValue) {
                trueValue.setSelected(true);
            } else {
                falseValue.setSelected(true);
            }
            //Boolean boolValue = (Boolean) this.defaultValue.get(parameter.getName());
            //if(boolValue.booleanValue()) {
            //  trueValue.setState(true);
            //} else {
            //    falseValue.setState(true);
            //}

        }
        this.setSize(last.getLocation().x + last.getSize().width + 5, Math.max(falseValue.getSize().height + 2, last.getSize().height + 2));

    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    @Override
    public void addParameters(Hashtable parameters) {
        String value = Boolean.toString(trueValue.isSelected());
        addParameter(parameters, value);
    }

    @Override
    public void focusGained(FocusEvent evt) {
        super.focusGained(evt);
        falseValue.setBackground(getBackground());
        trueValue.setBackground(getBackground());
    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */

    @Override
    public void reset() {
        if (this.defaultValue.containsKey(parameter.getName())) {
            Boolean boolValue = (Boolean) this.defaultValue.get(parameter.getName());
            if (boolValue.booleanValue()) {
                trueValue.setSelected(true);
            } else {
                falseValue.setSelected(true);
            }

        } else { //No launchdata -> default true is selected
            trueValue.setSelected(true);

        }
    }

    @Override
    public void unFocus() {
        super.unFocus();
        falseValue.setBackground(getBackground());
        trueValue.setBackground(getBackground());
    }
}
