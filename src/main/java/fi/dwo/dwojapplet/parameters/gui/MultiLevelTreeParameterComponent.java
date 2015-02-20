// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\gui\\MultiLevelTreeParameterComponent.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;

import fi.beans.scorm.Parameter;
import fi.beans.scorm.TreeParameter;
import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class MultiLevelTreeParameterComponent extends ParameterComponent
        implements ActionListener {
    private TabPane tabPane;

    private MultiLevelTreeTabSheet tabSheet;

    private JButton button;
    
    private JButton addButton; //with no elements, we show a button

    private Vector hashtables;
    
    private Component last;

    public MultiLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);
    }

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     */
    public MultiLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        
        Parameter subParameter = parameter.getSubParameters()[0];

        if(defaultValue.containsKey(parameter.getName())) {
            this.defaultValue = (Hashtable) defaultValue.get(parameter.getName());
        }
        /* If the parameter is not definied, use 0 as number of items */
        String sNrItems = (String) this.defaultValue.get(parameter.getItemCountName());
        int nrItems = 0;
        if(sNrItems != null) {
            nrItems = Integer.parseInt(sNrItems);
        } else {
            this.defaultValue.put(parameter.getItemCountName(), "0");
        }
        hashtables = new Vector(nrItems);
        
        /*
         * Split the launchdata (for every tree-item). 
         * Create a new hashtable and add the object.
         */
        Hashtable ht;
        Object item;
        for(int i = 1; i <= nrItems; i++) {
            ht = new Hashtable();
            item = this.defaultValue.get(subParameter.getName() + "_" + i);
            ht.put(subParameter.getName(), item);
            hashtables.addElement(ht);
        }
        

        String[] arguments = new String[1];
        arguments[0] = "" + nrItems;
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button = new JButton(MessageFormat.format(s, arguments));//, parent.getColor());
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setSize(button.getPreferredSize());
        //fm.stringWidth(button.getLabel()) + 20,
          //      fm.getHeight() + 10);
        button.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        button.addActionListener(this);

        arguments[0] = parameter.getItemLabel();
        s = TextMapper.getText(TextMapper.BTN_TREE_ADD_ITEM);

        addButton = new JButton(MessageFormat.format(s, arguments));//;, getColor());
        fm = addButton.getFontMetrics(addButton.getFont());
        addButton.setSize(addButton.getPreferredSize());
        //fm.stringWidth(addButton.getLabel()) + 20,
          //      fm.getHeight() + 10);
        addButton.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        addButton.addActionListener(this);

        
        if(nrItems == 0) {
            addButton.setVisible(false);
            this.add(addButton);
            addButton.setVisible(true);
            last = generatePostItems(addButton);
            
        } else {
            button.setVisible(false);
            this.add(button);
            button.setVisible(true);            
            last = generatePostItems(button);
        }
        
        this.setSize(last.getLocation().x + last.getSize().width + 5, button
                .getSize().height + 2);

    }

    private void createTabPane(boolean addItem) {
        if(tabPane == null) {
            tabPane = new TabPane();
            tabPane.setBackground(parent.getColor());
			/* The color is the other color than the parent */
			if(parent.getColor() == SELECTED_COLOR_1) {
                tabPane.setTabColor(SELECTED_COLOR_2);
			} else {
                tabPane.setTabColor(SELECTED_COLOR_1);
			}
			
			if((parameter instanceof TreeParameter) && (((TreeParameter) parameter).getSubParameters().length > 0)) {
				tabSheet = new MultiLevelTreeTabSheet(this, (TreeParameter) parameter, hashtables);
				if(addItem) {
				    tabSheet.addTab();
				}
				tabPane.addTabSheet(tabSheet, ((TreeParameter) parameter).getItemLabel());
			}
			
			tabPane.setLocation(preLabel.getLocation());
			tabPane.addComponentListener(this);
        } else if(addItem) {
            tabSheet.addTab();
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button) {
//System.out.println("start uitklappen");
            createTabPane(false);
            
            tabPane.setVisible(false);
            add(tabPane);
            tabPane.setVisible(true);
            remove(preLabel);
            remove(button);
            
            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            invalidate();
            //validate();
           // DwoHelper.getApplet().validate();
           // DwoHelper.getApplet().repaint();
//System.out.println("einde uitklappen " + getSize() + "pref = " + getPreferredSize());
        } else if(e.getSource() == addButton) {
            createTabPane(true);
            tabPane.setVisible(false);
            add(tabPane);
            tabPane.setVisible(true);
            remove(preLabel);
            remove(postLabel);
            remove(addButton);
            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            invalidate();
            //validate();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    @Override
    public void addParameters(Hashtable parameters) {
        if(tabSheet == null) {
            addParameter(parameters, defaultValue);
        } else {
            tabSheet.addParameters(parameters);
        }
    }

    /**
     * With a tree element, there is no help button so generate nothing and
     * return <code>after</code>
     * 
     * @see fi.dwo.parameters.gui.ParameterComponent#generatePostItems(java.awt.Component)
     */
    @Override
    protected Component generatePostItems(Component after) {
	    postLabel = new FixedLabel("(" + TextMapper.getText(TextMapper.LBL_NO_ITEMS) + ")");
	    postLabel.setFont(ParameterConstants.LABEL_FONT);
        FontMetrics fm = postLabel.getFontMetrics(postLabel.getFont());
        postLabel.setSize(fm.stringWidth(postLabel.getText()) + 10, fm.getHeight());
        postLabel.setLocation(after.getLocation().x + after.getSize().width + 10, 1);

        if(hashtables.size() == 0) {
            this.add(postLabel);
            after = postLabel;
        }
        return after;
    }

    public void noItems() {
        add(preLabel);
        add(addButton);
        postLabel.setLocation(addButton.getLocation().x + addButton.getSize().width + 10, 1);
        add(postLabel);
        remove(tabPane);
        remove(button);
        parent.isFocussed(this);
        
        this.setSize(postLabel.getLocation().x + postLabel.getSize().width + 5, addButton
                .getSize().height + 2);
    }
    
    private int getNrItems() {
        if(tabSheet == null) {
            return hashtables.size();
        } else {
            return tabSheet.getNrItems();
        }
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#unFocus()
     */
    @Override
    public void unFocus() {
        if(tabPane != null) {
            this.remove(tabPane);
        }
        String[] arguments = new String[1];
        arguments[0] = "" + getNrItems();
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button.setLabel(MessageFormat.format(s, arguments));

        
        if(getNrItems() != 0) {
	        this.add(preLabel);
	        this.add(button);     
        }
        
        this.setSize(last.getLocation().x + last.getSize().width + 5, button
                .getSize().height + 2);
    }
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(ComponentEvent e) {
        if(e.getSource() == tabPane) {
            this.setVisible(false);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            this.setVisible(true);
        }
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */
    @Override
    public void reset() {
        if(tabPane != null) {
            this.remove(tabPane);
        }
        tabPane = null;
        if(tabSheet != null) { //the tabsheet was never opened, so it wasn't changed
	        if((tabSheet.getNrItems() == 0) && (hashtables.size() != 0)) {
	            //we showed the no items button, but we must remove it.
	            remove(preLabel);
	            remove(postLabel);
	            remove(addButton);
	        }
	        tabSheet = null;
        }
        
        if(hashtables.size() == 0) {
            noItems();
        } else {
            unFocus();            
        }
    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    @Override
    public String getSequenceString(ParameterComponentIF component) {
        if(parent != null) {
            return parent.getSequenceString(this);
        } else {
            return super.getSequenceString(component);
        }
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