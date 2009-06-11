// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\gui\\SingleLevelTreeParameterComponent.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import java.text.MessageFormat;
import java.util.Hashtable;

import fi.beans.scorm.TreeParameter;
import fi.dwo.client.gui.DwoButton;
import fi.dwo.parameters.system.TextMapper;

public class SingleLevelTreeParameterComponent extends ParameterComponent implements ParameterComponentIF,
        ActionListener {
    private TabPane tabPane;

    private TreeSheetCreator tabSheetCreator;

    private DwoButton button;
    
    private Component last;
    
    private DwoButton addButton; //with no elements, we show a button


    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     */
    public SingleLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);

    }

    public SingleLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        
        Hashtable tmp = defaultValue;
        if(defaultValue.containsKey(parameter.getName())) {
            tmp = (Hashtable) defaultValue.get(parameter.getName());
        }
        tabSheetCreator = new TreeSheetCreator(this, parameter, tmp);
        
        String[] arguments = new String[1];
        arguments[0] = "" + tabSheetCreator.getNrItems();
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button = new DwoButton(MessageFormat.format(s, arguments), parent.getColor());
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setSize(fm.stringWidth(button.getLabel()) + 20,
                fm.getHeight() + 10);
        button.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        button.addActionListener(this);
        
        arguments[0] = parameter.getItemLabel();
        s = TextMapper.getText(TextMapper.BTN_TREE_ADD_ITEM);

        addButton = new DwoButton(MessageFormat.format(s, arguments), getColor());
        fm = addButton.getFontMetrics(addButton.getFont());
        addButton.setSize(fm.stringWidth(addButton.getLabel()) + 20,
                fm.getHeight() + 10);
        addButton.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        addButton.addActionListener(this);

        
        if(tabSheetCreator.getNrItems() == 0) {
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

    /**
     * With a tree element, there is no help button so generate nothing and
     * return <code>after</code>
     * 
     * @see fi.dwo.parameters.gui.ParameterComponent#generatePostItems(java.awt.Component)
     */
    protected Component generatePostItems(Component after) {
	    postLabel = new FixedLabel("(" + TextMapper.getText(TextMapper.LBL_NO_ITEMS) + ")");
	    postLabel.setFont(ParameterConstants.LABEL_FONT);
        FontMetrics fm = postLabel.getFontMetrics(postLabel.getFont());
        postLabel.setSize(fm.stringWidth(postLabel.getText()) + 10, fm.getHeight());
        postLabel.setLocation(after.getLocation().x + after.getSize().width + 10, 1);

        if(tabSheetCreator.getNrItems() == 0) {
            this.add(postLabel);
            after = postLabel;
        }
        return after;
    }
    
    private void createTabPane() {
        if(tabPane == null) {
            tabPane = new TabPane();
            tabPane.setBackground(parent.getColor());
			/* The color is the other color than the parent */
			if(parent.getColor() == SELECTED_COLOR_1) {
                tabPane.setTabColor(SELECTED_COLOR_2);
			} else {
                tabPane.setTabColor(SELECTED_COLOR_1);
			}
			
			tabPane.setTabSheetCreator(tabSheetCreator);
			tabSheetCreator.setTabPane(tabPane);
			tabSheetCreator.createTabs();
			
			
			tabPane.setLocation(preLabel.getLocation());
			tabPane.addComponentListener(this);
        }        
    }

    public void noItems() {
        add(preLabel);
        add(addButton);
        postLabel.setLocation(addButton.getLocation().x + addButton.getSize().width + 10, 1);
        add(postLabel);
        if(tabPane != null) {
            remove(tabPane);
        }
        remove(button);
        parent.isFocussed(this);
        
        this.setSize(postLabel.getLocation().x + postLabel.getSize().width + 5, addButton
                .getSize().height + 2);

    }

    /**
     * @param arg0
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            createTabPane();
            
            add(tabPane);
            remove(preLabel);
            remove(button);
            
            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        } else if(e.getSource() == addButton) {
            createTabPane();
            tabSheetCreator.addTab();
            add(tabPane);
            remove(preLabel);
            remove(postLabel);
            remove(addButton);
            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);

        }


    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#unFocus()
     */
    public void unFocus() {
        if(tabPane != null) {
            this.remove(tabPane);
        }
        String[] arguments = new String[1];
        arguments[0] = "" + tabSheetCreator.getNrItems();
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button.setLabel(MessageFormat.format(s, arguments));

        if(tabSheetCreator.getNrItems() != 0) { 
	        this.add(button);     
	        this.add(preLabel);
        } else {
        }
        
        this.setSize(last.getLocation().x + last.getSize().width + 5, button
                .getSize().height + 2);

    }
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
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
    public void reset() {
        if(tabPane != null) {
            this.remove(tabPane);
        }
        tabPane = null;
        Hashtable tmp = defaultValue;
        if(defaultValue.containsKey(parameter.getName())) {
            tmp = (Hashtable) defaultValue.get(parameter.getName());
        }
        int oldNrItems = tabSheetCreator.getNrItems(); 
        
        tabSheetCreator = new TreeSheetCreator(this, (TreeParameter) parameter, tmp);
        if(tabSheetCreator.getNrItems() == 0) {
            noItems();
        }
        if((oldNrItems == 0) && (tabSheetCreator.getNrItems() != 0)) {
            //we showed the no items button, but we must remove it.
            remove(preLabel);
            remove(postLabel);
            remove(addButton);
        }
        unFocus();
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
    
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    public void addParameters(Hashtable parameters) {
        tabSheetCreator.addParameters(parameters);
    }
}