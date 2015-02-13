 // Source file:
// C:\\parameters\\fi\\dwo\\parameters\\gui\\SingleLevelTreeTabSheet.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;

import fi.beans.scorm.TreeParameter;
import fi.dwo.parameters.system.TextMapper;
import fi.dwo.parameters.test.ParametersTest;

public class SingleLevelTreeTabSheet extends TreeTabSheet implements
        ItemLaunchdataIF, ActionListener {
    
    private JButton addButton;
    private JButton deleteButton;

    private GridBagLayout gridbag;
    private Object key;
    
    private TreeSheetCreator treeSheetCreator;
    
    /**
     * @param treeSheetCreator
     * @param launchdata
     */
    public SingleLevelTreeTabSheet(TreeSheetCreator treeSheetCreator, TreeParameter parameter,
            Hashtable defaultValue) {
        super(treeSheetCreator, parameter, defaultValue);
        this.treeSheetCreator = treeSheetCreator;

        /* The color is the other color than the parent */
		if(parent.getColor() == SELECTED_COLOR_1) {
			this.setBackground(SELECTED_COLOR_2);
		} else {
			this.setBackground(SELECTED_COLOR_1);
		}
		//setBackground(ParametersTest.randomColor());
		
        GridBagConstraints c = new GridBagConstraints();
        gridbag = new GridBagLayout();
        this.setLayout(gridbag);

        c.insets = new Insets(2, 4, 20, 4);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;
        
        String[] arguments = new String[1];
        arguments[0] = parameter.getItemLabel();
        String s = TextMapper.getText(TextMapper.BTN_TREE_ADD_ITEM);

        addButton = new JButton(MessageFormat.format(s, arguments));
        FontMetrics fm = addButton.getFontMetrics(addButton.getFont());
        addButton.setSize(fm.stringWidth(addButton.getLabel()) + 20,
                fm.getHeight() + 10);
        addButton.setVisible(false);
        addButton.addActionListener(this);
        this.add(addButton);
        gridbag.addLayoutComponent(addButton, c);
        addButton.setVisible(true);
        
        s = TextMapper.getText(TextMapper.BTN_TREE_DELETE_ITEM);

        deleteButton = new JButton(MessageFormat.format(s, arguments));//, getColor());
        fm = deleteButton.getFontMetrics(deleteButton.getFont());
        deleteButton.setSize(fm.stringWidth(deleteButton.getLabel()) + 20,
                fm.getHeight() + 10);
        deleteButton.setVisible(false);
        deleteButton.addActionListener(this);
        this.add(deleteButton);
        gridbag.addLayoutComponent(deleteButton, c);
        deleteButton.setVisible(true);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel p = new JPanel();
        p.setOpaque(false);
//        p.setBackground(ParametersTest.randomColor());
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);
        gridbag.addLayoutComponent(p, c);
        
        c.insets = new Insets(COMPONENT_SPACING, 4, 0, 4);
        
        ParameterComponentIF pc;
        for(int i = 0; i < parameter.getSubParameters().length; i++) {
            pc = ParameterComponentCreator.createComponent(this, parameter.getSubParameters()[i], defaultValue);
            this.registerComponent(pc);
            gridbag.addLayoutComponent(pc.getComponent(), c);
            this.add(pc.getComponent());
            pc.getComponent().addComponentListener(this);
            if(i == 0) {
                pc.getComponent().requestFocus();
            }
        }

        /* Was het probleem dat knoppen groter worden met een subpanel */
//        p = new Panel();
//        p.setBackground(ParametersTest.randomColor());
//        p.setFocusable(true);
//        this.add(p);
//        c.insets = new Insets(0, 0, 0, 0);
//        c.fill = GridBagConstraints.BOTH;
//        gridbag.addLayoutComponent(p, c);

        this.setSize(gridbag.preferredLayoutSize(this));
        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == deleteButton) {
            treeSheetCreator.removeTab(this);
        } else if(e.getSource() == addButton) {
            treeSheetCreator.addTab();
        }
        
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    public void addParameters(Hashtable parameters) {
        // TODO Auto-generated method stub
        super.addParameters(parameters);
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    public String getSequenceString(ParameterComponentIF component) {
        if(parent != null) {
            return parent.getSequenceString(this);
        } else {
            return super.getSequenceString(component);
        }
    }
    

    /**
     * @return Object
     */
    public Object getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.parameters.gui.ItemLaunchdataIF#getLaunchdata()
     */
    public Hashtable getLaunchdata() {
        return null;
    }

    /**
     * @param key
     */
    public void setKey(Object key) {
        this.key = key;
    }

    public Dimension getPreferredSize() {
    	Dimension size = getLayout().preferredLayoutSize(this);
//System.out.println("single tab sheet pref=" + size + "now = " + getSize());
    	return size;
    	
    }
    
    public Dimension getMinimumSize() { 
    	return getPreferredSize();
    }
    
}