// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\gui\\MultiLevelTreeTabSheet.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormTree;
import fi.beans.scorm.TreeParameter;
import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class MultiLevelTreeTabSheet extends TreeTabSheet implements
        DeleteTreeItemIF, ActionListener {

    private GridBagLayout gridbag;

    private JButton addButton;

    private MultiLevelTreeParameterComponent parentCom;

    private Parameter subParameter;

    /**
     * @param parent
     * @param launchdata
     * @param defaultValue
     */
    public MultiLevelTreeTabSheet(MultiLevelTreeParameterComponent parent,
            TreeParameter parameter, Vector defaultValue) {
        super(parent, parameter, null);
        parentCom = parent;
        if (parameter.getSubParameters().length > 0) {
            subParameter = parameter.getSubParameters()[0];
        } else {
            subParameter = parameter;
        }
        /* The color is the other color than the parent */
        if (parent.getColor() == SELECTED_COLOR_1) {
            this.setBackground(SELECTED_COLOR_2);
        } else {
            this.setBackground(SELECTED_COLOR_1);
        }

        GridBagConstraints c = new GridBagConstraints();
        gridbag = new GridBagLayout();
        this.setLayout(gridbag);

        c.insets = new Insets(2, 4, 20, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        String[] arguments = new String[1];
        arguments[0] = subParameter.getPreLabel();
        String s = TextMapper.getText(TextMapper.BTN_TREE_ADD_ITEM);

        addButton = new JButton(MessageFormat.format(s, arguments));
        //,getColor());
        FontMetrics fm = addButton.getFontMetrics(addButton.getFont());
        addButton.setSize(fm.stringWidth(addButton.getLabel()) + 20, fm
                .getHeight() + 10);
        addButton.setVisible(false);
        addButton.addActionListener(this);
        this.add(addButton);
        gridbag.addLayoutComponent(addButton, c);
        addButton.setVisible(true);

        c.gridwidth = GridBagConstraints.REMAINDER;
        JPanel p = new JPanel();
        p.setOpaque(false);
        gridbag.addLayoutComponent(p, c);
        this.add(p);

        c.insets = new Insets(COMPONENT_SPACING, 4, 0, 4);

        ParameterComponentIF pc;
        for (int i = 0; i < defaultValue.size(); i++) {
            pc = ParameterComponentCreator.createTreeComponent(this,
                    subParameter, (Hashtable) defaultValue.elementAt(i));
            pc.setSequenceLabel(i + 1);
            this.registerComponent(pc);
            gridbag.addLayoutComponent(pc.getComponent(), c);
            this.add(pc.getComponent());
            pc.getComponent().addComponentListener(this);
            if (i == 0) {
                pc.getComponent().requestFocus();
            }
        }

        JPanel panel = new JPanel();
        panel.setOpaque(false);
//        panel.setFocusable(true); //TODO weghalen!!
        panel.setVisible(false);
        this.add(panel);
        panel.setVisible(true);
        c.insets = new Insets(0, 0, 0, 0);
//        gridbag.addLayoutComponent(panel, c);

        this.setSize(gridbag.preferredLayoutSize(this));
        //doLayout();
        this.invalidate();
    }

    /**
     * @return int
     */
    public int getNrItems() {
        return subComponents.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.parameters.gui.DeleteTreeItemIF#deleteItem(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    @Override
    public void deleteItem(ParameterComponentIF parameterComponent) {
        int index = subComponents.indexOf(parameterComponent);
        subComponents.removeElement(parameterComponent);
        for (int i = index; i < subComponents.size(); i++) {
            ((ParameterComponentIF) subComponents.elementAt(i))
                    .setSequenceLabel(i + 1);
        }

        Component com;
        if (index < subComponents.size()) {
            com = ((ParameterComponentIF) subComponents.elementAt(index))
                    .getComponent();
        } else if (subComponents.size() > 0) {
            com = ((ParameterComponentIF) subComponents.elementAt(subComponents
                    .size() - 1)).getComponent();
        } else {
            com = addButton;
        }

        this.remove(parameterComponent.getComponent());
        gridbag.removeLayoutComponent(parameterComponent.getComponent());
        this.setSize(gridbag.preferredLayoutSize(this));

        this.validate();
        if (getNrItems() == 0) {
            parentCom.noItems();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    @Override
    public void addParameters(Hashtable parameters) {
        Hashtable ht = new Hashtable();
        ht.put(((TreeParameter) parameter).getItemCountName(), Integer.toString(getNrItems()));
        for (int i = 0; i < subComponents.size(); i++) {
            ((ParameterComponentIF) subComponents.elementAt(i)).addParameters(ht);
        }
        addParameter(parameters, ht);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addTab();
        }
    }

    public void addTab() {
        if (parameter.getType() instanceof ScormTree) {
            ScormTree scormTree = (ScormTree) parameter.getType();
            if ((scormTree.getMaxItems() != -1)
                    && (scormTree.getMaxItems() <= getNrItems())) {
                String[] arguments = new String[2];
                arguments[0] = Integer.toString(scormTree.getMaxItems());
                arguments[1] = parameter.getPreLabel();
                String s = TextMapper
                        .getText(TextMapper.MSG_TO_MANY_TREE_ITEMS);

                s = MessageFormat.format(s, arguments);
                javax.swing.JOptionPane.showMessageDialog(null, s);
                return;
            }
        }
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(COMPONENT_SPACING, 4, 0, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        ParameterComponentIF pc = ParameterComponentCreator
                .createTreeComponent(this, subParameter, null);
        pc.setSequenceLabel(subComponents.size() + 1);
        this.registerComponent(pc);
        gridbag.addLayoutComponent(pc.getComponent(), c);
        this.add(pc.getComponent(), this.countComponents() - 1);
        pc.getComponent().addComponentListener(this);
        this.setSize(gridbag.preferredLayoutSize(this));
        validate();
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    @Override
    public String getSequenceString(ParameterComponentIF component) {
        return "_" + (subComponents.indexOf(component) + 1);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        super.setBounds(x, y, width, height);
    }

}
