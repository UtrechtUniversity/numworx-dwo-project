// Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TabPane.java

package fi.dwo.parameters.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;

import fi.beans.stringutils.StringUtils;
import fi.dwo.client.gui.BorderedPanel;

public class TabPane extends Panel implements ActionListener, ComponentListener {
    private Color tabColor;

    private TabSheetCreatorIF tabSheetCreator;

    private Hashtable createdTabs;
    
    private TabLabelPanel tabLabelPanel;
    
    private BorderedPanel tabSheetPanel;
    private Panel topTabSheetPanel;
    
    private TabSheetIF selectedTab;
    
    private int largestWidth;
    
    public TabPane() {
        super(new BorderLayout());
        createdTabs = new Hashtable();
        Panel p = new Panel(new BorderLayout());
        add(p, BorderLayout.WEST);
        tabLabelPanel = new TabLabelPanel(this);
        tabLabelPanel.setVisible(false);
        p.add(tabLabelPanel, BorderLayout.NORTH);
        tabLabelPanel.setVisible(true);
        p.add(new BorderedPanel(null, BorderedPanel.EAST), BorderLayout.CENTER);
        tabLabelPanel.addComponentListener(this);
        
        tabSheetPanel = new BorderedPanel(new BorderLayout(), BorderedPanel.NORTH | BorderedPanel.SOUTH | BorderedPanel.EAST);
        tabSheetPanel.setVisible(false);
        add(tabSheetPanel, BorderLayout.CENTER);
        tabSheetPanel.setVisible(true);
        
        topTabSheetPanel = new Panel(new BorderLayout());
        tabSheetPanel.add(topTabSheetPanel, BorderLayout.NORTH);
        
        selectedTab = null;
        
        Dimension prefSize1 = tabLabelPanel.getPreferredSize();
        Dimension prefSize2 = tabSheetPanel.getPreferredSize();
        int width = prefSize1.width + prefSize2.width;
        int height = Math.max(prefSize1.height, prefSize2.height);
        this.setSize(width, height);
        largestWidth = width;
    }

    /**
     * @param tabSheet
     * @param label
     */
    public void addTabSheet(TabSheetIF tabSheet, String label) {
        String random = StringUtils.randomstring();
        addTabSheet(tabSheet, label, random);
    }

    /**
     * @param tabSheet
     * @param label
     * @param key
     */
    public void addTabSheet(TabSheetIF tabSheet, String label, Object key) {
        createdTabs.put(key, tabSheet);
        tabLabelPanel.addTabLabel(label + ":", key);
        
    }

    /**
     * @param tabSheetCreator
     */
    public void setTabSheetCreator(TabSheetCreatorIF tabSheetCreator) {
        this.tabSheetCreator = tabSheetCreator; 
    }

    /**
     * @param label
     * @param key
     */
    public void addRuntimeTabSheet(String label, Object key) {
        tabLabelPanel.addTabLabel(label + ":", key);
    }

    /**
     * @param color
     */
    public void setTabColor(Color color) {
        tabColor = color;
        tabLabelPanel.setSelectedTabColor(color);
        tabSheetPanel.setBackground(color);

    }

    /**
     * @param color
     */
    public void setBackground(Color color) {
        super.setBackground(color);
        tabLabelPanel.setBackground(color);

    }

    /**
     * @param newLabel
     * @param key
     */
    public void renameTab(String newLabel, Object key) {
        tabLabelPanel.renameTab(newLabel + ":", key);
    }

    /**
     * @param key
     */
    public void removeTab(Object key) {
        if(createdTabs.containsKey(key)) {
            createdTabs.remove(key);
        }
        tabLabelPanel.removeTab(key);
    }

    /**
     * @param arg0
     */
    public void actionPerformed(ActionEvent arg0) {

    }
    
    public void selectTab(Object key) {
        tabLabelPanel.selectTab(key);
    }
    
    /**
     * Indicates that a tab is selected
     * @param key
     */
    protected void tabSelected(Object key) {
        this.setVisible(false);
        if(selectedTab != null) {
            selectedTab.getComponent().removeComponentListener(this);
            topTabSheetPanel.remove(selectedTab.getComponent());
        }
        TabSheetIF ts = null;
        if(!createdTabs.containsKey(key)) {
            if(tabSheetCreator != null) {
                ts = tabSheetCreator.createTabSheet(key);
                createdTabs.put(key, ts);
            }
        } else {
            ts = (TabSheetIF) createdTabs.get(key);
        }
        
        if(ts != null) {
            ts.getComponent().setVisible(false);
            topTabSheetPanel.add(ts.getComponent(), BorderLayout.WEST);
            ts.getComponent().setVisible(true);
            ts.getComponent().addComponentListener(this);
            selectedTab = ts;
        }
        
        Dimension prefSize1 = tabLabelPanel.getPreferredSize();
        Dimension prefSize2 = tabSheetPanel.getPreferredSize();
        int width = prefSize1.width + prefSize2.width;
        
        if(width > largestWidth) {
            largestWidth = width;
        }
        int height = Math.max(prefSize1.height, prefSize2.height);
        
        this.setSize(largestWidth, height);
        this.validate();
        this.setVisible(true);
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        Dimension prefSize1 = tabLabelPanel.getPreferredSize();
        Dimension prefSize2 = tabSheetPanel.getPreferredSize();
        int width = prefSize1.width + prefSize2.width;
        
        if(width > largestWidth) {
            largestWidth = width;
        }
        int height = Math.max(prefSize1.height, prefSize2.height);

        if((getSize().width != largestWidth) || (getSize().height != height)) {
	        this.setSize(largestWidth, height);
        }
        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }
    /* (non-Javadoc)
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return super.getSize();
    }
}