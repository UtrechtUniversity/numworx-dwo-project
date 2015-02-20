/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author M.J.B. Kupers
 *
 */
public class TabLabelPanel extends JPanel implements ActionListener {

    private GridLayout gridLayout;

    private Hashtable tabLabels;

    private TabLinkedLabel selectedTab;

    private TabPane tabPane;

    private Color selectedTabColor;

    public TabLabelPanel(TabPane tabPane) {
        super(null);
        //, BorderedPanel.EAST);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, getForeground()));
        this.tabPane = tabPane;
        tabLabels = new Hashtable();
        gridLayout = new GridLayout(0, 1);
        this.setLayout(gridLayout);
    }

    public void addTabLabel(String label, Object key) {
        TabLinkedLabel tmp = new TabLinkedLabel(label);
        tmp.setFont(ParameterConstants.LABEL_FONT);
        FontMetrics fm = tmp.getFontMetrics(tmp.getFont());
        tmp.setSize(fm.stringWidth(tmp.getText()) + 10, 30);
        tmp.setSelectedColor(selectedTabColor);
        tmp.setDeselectedColor(this.getBackground());
        tmp.addActionListener(this);
        tmp.setVisible(false);
        this.add(tmp);
        tmp.setVisible(true);
        tabLabels.put(tmp, key);

        /* If we don't have a tab selected, select thisone */
        if (selectedTab == null) {
            selectTab(tmp);
        }
        this.setSize(this.getLayout().preferredLayoutSize(this));
    }

    private TabLinkedLabel getLabel(Object key) {
        Enumeration enumer = tabLabels.keys();
        Object obj = null;
        boolean found = false;
        while ((!found) && enumer.hasMoreElements()) {
            obj = enumer.nextElement();
            if (tabLabels.get(obj) == key) {
                found = true;
            }
        }

        if (found) {
            return (TabLinkedLabel) obj;
        } else {
            return null;
        }
    }

    public void renameTab(String label, Object key) {
        TabLinkedLabel tmp = getLabel(key);
        if (tmp != null) {
            tmp.setText(label);
        }
    }

    public void removeTab(Object key) {
        Enumeration enumer = tabLabels.keys();
        Object obj = null;
        boolean found = false;
        Object next = null;
        while ((!found) && enumer.hasMoreElements()) {
            obj = enumer.nextElement();
            if (tabLabels.get(obj) == key) {
                found = true;
                Component[] ac = this.getComponents();
                for (int i = 0; i < ac.length; i++) {
                    if (ac[i] == (Component) obj) {
                        if ((i == 0) && (i + 1 < ac.length)) {
                            next = ac[i + 1];
                            break;
                        } else if (i > 0) {
                            next = ac[i - 1];
                            break;
                        } else if ((i == ac.length - 1) && (i > 0)) {
                            next = ac[i - 1];
                            break;
                        }
                    }
                }
            }
        }

        if (found) {
            this.remove((Component) obj);
            tabLabels.remove(obj);

            if (next != null) {
                selectTab((TabLinkedLabel) next);
            } else {
                selectedTab = null;
            }
            this.setSize(this.getLayout().preferredLayoutSize(this));
        }
    }

    /* (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (selectedTab != null) {
            g.setColor(selectedTabColor);
            Point location = selectedTab.getLocation();
            Dimension size = selectedTab.getSize();
            g.drawLine(this.getSize().width - 1, location.y + 1, this.getSize().width - 1, location.y + size.height - 2);
        }
    }

    public void selectTab(Object key) {
        TabLinkedLabel tll = getLabel(key);
        if (tll != null) {
            selectTab(tll);
        }
    }

    private void selectTab(TabLinkedLabel tabLinkedLabel) {
        if (tabLinkedLabel != selectedTab) {
            Object key = tabLabels.get(tabLinkedLabel);
            if (selectedTab != null) {
                selectedTab.setSelected(false); //the current was deselected
            }
            selectedTab = tabLinkedLabel;
            selectedTab.setSelected(true);
            this.repaint();
            tabPane.tabSelected(key);
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj instanceof TabLinkedLabel) {
            selectTab((TabLinkedLabel) obj);
        }
    }

    /**
     * @return Returns the selectedTabColor.
     */
    public Color getSelectedTabColor() {
        return selectedTabColor;
    }

    /**
     * @param selectedTabColor The selectedTabColor to set.
     */
    public void setSelectedTabColor(Color selectedTabColor) {
        this.selectedTabColor = selectedTabColor;
    }
    /* (non-Javadoc)
     * @see java.awt.Container#getInsets()
     */

    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        insets.top += 30;
        return insets;
    }
}
