// Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TabPane.java
package fi.dwo.dwojapplet.parameters.gui;

import fi.beans.stringutils.StringUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class TabPane extends JPanel implements ActionListener, ComponentListener {

    private Color tabColor;

    private TabSheetCreatorIF tabSheetCreator;

    private Hashtable createdTabs;

    private TabLabelPanel tabLabelPanel;

    private JPanel tabSheetPanel;
    private JPanel topTabSheetPanel;

    private TabSheetIF selectedTab;

    private int largestWidth;

    public TabPane() {
        super(new BorderLayout(), false);
        //setDebugGraphicsOptions(DebugGraphics.LOG_OPTION);
        setOpaque(false);
        createdTabs = new Hashtable();
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setDoubleBuffered(false);
        add(p, BorderLayout.WEST);
        tabLabelPanel = new TabLabelPanel(this);
        tabLabelPanel.setVisible(false);
        p.add(tabLabelPanel, BorderLayout.NORTH);
        tabLabelPanel.setVisible(true);
        JPanel panel = new JPanel(null);
        p.add(panel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, getForeground()));
        panel.setOpaque(false);
        panel.setDoubleBuffered(false);
        tabLabelPanel.addComponentListener(this);

        tabSheetPanel = new JPanel(new BorderLayout(), false);
        tabSheetPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, getForeground()));
        tabSheetPanel.setVisible(true);			// false, 1011
        tabSheetPanel.setOpaque(true); //must be true
        add(tabSheetPanel, BorderLayout.CENTER);
        tabSheetPanel.setVisible(true);

        topTabSheetPanel = new JPanel(new BorderLayout(), false);
        topTabSheetPanel.setOpaque(true);
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
        if (topTabSheetPanel != null) {
            topTabSheetPanel.setBackground(color);
        }

    }

    /**
     * @param color
     */
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (tabLabelPanel != null) {
            tabLabelPanel.setBackground(color);
        }
        if (tabSheetPanel != null) {
            tabSheetPanel.setBackground(color);
        }

       // if(panel != null)
        // 	panel.setBackground(color);
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
        if (createdTabs.containsKey(key)) {
            createdTabs.remove(key);
        }
        tabLabelPanel.removeTab(key);
    }

    /**
     * @param arg0
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {

    }

    public void selectTab(Object key) {
        tabLabelPanel.selectTab(key);
    }

    /**
     * Indicates that a tab is selected
     *
     * @param key
     */
    protected void tabSelected(Object key) {
        this.setVisible(false);
        if (selectedTab != null) {
            selectedTab.getComponent().removeComponentListener(this);
            topTabSheetPanel.remove(selectedTab.getComponent());
        }
        TabSheetIF ts = null;
        if (!createdTabs.containsKey(key)) {
            if (tabSheetCreator != null) {
                ts = tabSheetCreator.createTabSheet(key);
                createdTabs.put(key, ts);
            }
        } else {
            ts = (TabSheetIF) createdTabs.get(key);
        }

        if (ts != null) {
            ts.getComponent().setVisible(false);
            topTabSheetPanel.add(ts.getComponent(), BorderLayout.WEST);
            ts.getComponent().setVisible(true);
            ts.getComponent().addComponentListener(this);
            selectedTab = ts;
        }

        Dimension prefSize1 = tabLabelPanel.getPreferredSize();
        Dimension prefSize2 = tabSheetPanel.getPreferredSize();
        int width = prefSize1.width + prefSize2.width;

        if (width > largestWidth) {
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
    @Override
    public void componentHidden(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(ComponentEvent e) {
        if (false) {
            setSize(getPreferredSize());
            invalidate();
            return;
        }
        Insets inset = getInsets();
        Dimension prefSize1 = tabLabelPanel.getPreferredSize();
        Dimension prefSize2 = tabSheetPanel.getPreferredSize();
        int width = prefSize1.width + prefSize2.width + inset.left + inset.right;

        if (width > largestWidth) {
            largestWidth = width;
        }
        int height = Math.max(prefSize1.height, prefSize2.height) + inset.top + inset.bottom;

        if ((getSize().width != largestWidth) || (getSize().height != height)) {
            this.setSize(largestWidth, height);
            invalidate();
        }

        this.validate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentShown(ComponentEvent e) {
//        this.setSize(this.getLayout().preferredLayoutSize(this));
//        this.validate();
    }
    /* (non-Javadoc)
     * @see java.awt.Component#getPreferredSize()
     */

    @Override
    public Dimension getPreferredSize() {
//    	System.out.println("Tabpane pref=" + super.getPreferredSize() + " now = " + getSize());
        return new Dimension(largestWidth, super.getPreferredSize().height);
    }

    @Override
    public Dimension getMinimumSize() {
//    	System.out.println("Tabpane min=" + super.getPreferredSize() + " now = " + getSize());
        return super.getPreferredSize();

    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {

        super.setBounds(x, y, width, height);
    }


    /* (non-Javadoc)
     * @see java.awt.Component#processComponentEvent(java.awt.event.ComponentEvent)
     */
    @Override
    protected void processComponentEvent(ComponentEvent e) {

        super.processComponentEvent(e);
    }

}
