package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * This class is a panel that shows a Scrollbar if the content is to large for the size of the panel.
 * The difference with the default <code>ScrollPane</code> is that with no scrollbars, no border is showed.
 * note: The functionality is not complete (e.g. the deleteComponent methods are not implemented).
 * @author M.J.B. Kupers
 *
 */
public class AutoScrollPanel extends Panel implements ComponentListener {
    
    ScrollPane sp;
    Panel p;

    /**
     * 
     */
    public AutoScrollPanel() {
        super(null);
        p = new Panel();
        super.add(p);
        this.addComponentListener(this);
    }
    /**
     * @param layout
     */
    public AutoScrollPanel(LayoutManager layout) {
        super(null);
        p = new Panel(layout);
        super.add(p);
        this.addComponentListener(this);
    }
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        resizePanel();
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(ComponentEvent e) {
        resizePanel();
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentShown(ComponentEvent e) {
        
    }
    
    /**
     * Checks if the scrollbars must be showed.
     *
     */
    private void checkScrollBars() {
        if((p.getSize().width > getSize().width) || (p.getSize().height > getSize().height)) {
            //show scrollbar
            if(sp == null) {
                p.setVisible(false);
                super.remove(p);
                sp = new ScrollPane();
                sp.setSize(getSize());
                sp.add(p);
                super.add(sp);
                p.setLocation(0, 0);
                p.setVisible(true);
            }
            sp.validate();
        } else {
            //hide scrollbar
            if(sp != null) {
                p.setVisible(false);
                super.remove(sp);
                sp = null;
                super.add(p);
                p.setLocation(0, 0);
                p.setVisible(true);
            }
        }
        
    }
    
    /**
     * Resizes the subpanel to the size of the components.
     *
     */
    private void resizePanel() {
        int width, height;
        int maxwidth = 0;
        int maxheight = 0;
        Component comp;
        Component[] cmp = p.getComponents();
        for(int i = 0; i < cmp.length;i++) {
            comp = cmp[i];
            width = comp.getLocation().x + comp.getSize().width;
            if(width > maxwidth) {
                maxwidth = width;
            }
            height = comp.getLocation().y + comp.getSize().height;
            if(height > maxheight) {
                maxheight = height;
            }
        }
        
        p.setSize(maxwidth, maxheight);
        checkScrollBars();        
    }
    
    /**
     * Component comp is changed. 
     * Is he located out of the bounds of the panel, 
     * than resize the subpanel and check scrollbars.
     * @param comp The component that is changed.
     */
    private void resizePanel(Component comp) {
        int width = comp.getLocation().x + comp.getSize().width;
        int height = comp.getLocation().y + comp.getSize().height;
        if(width < p.getSize().width) {
            width = p.getSize().width;
        }
        if(height < p.getSize().height) {
            height = p.getSize().height;
        }
        p.setSize(width, height);        
        checkScrollBars();
    }

    /* (non-Javadoc)
     * @see java.awt.Container#add(java.awt.Component)
     */
    @Override
    public Component add(Component comp) {
        comp.addComponentListener(this);
        p.add(comp);
        resizePanel(comp);
        return comp;
    }
    /* (non-Javadoc)
     * @see java.awt.Container#add(java.awt.Component, int)
     */
    @Override
    public Component add(Component comp, int index) {
        comp.addComponentListener(this);
        p.add(comp, index);
        resizePanel(comp);
        return comp;
    }
    /* (non-Javadoc)
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int)
     */
    @Override
    public void add(Component comp, Object constraints, int index) {
        comp.addComponentListener(this);
        p.add(comp, constraints, index);
        resizePanel(comp);
    }
    /* (non-Javadoc)
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
     */
    @Override
    public void add(Component comp, Object constraints) {
        comp.addComponentListener(this);
        p.add(comp, constraints);
        resizePanel(comp);
    }
    /* (non-Javadoc)
     * @see java.awt.Container#add(java.lang.String, java.awt.Component)
     */
    @Override
    public Component add(String name, Component comp) {
        comp.addComponentListener(this);
        p.add(name, comp);
        resizePanel(comp);
        return comp;
    }
    /* (non-Javadoc)
     * @see java.awt.Container#getComponent(int)
     */
    @Override
    public Component getComponent(int n) {
        return p.getComponent(n);
    }
    /* (non-Javadoc)
     * @see java.awt.Component#getComponentAt(int, int)
     */
    @Override
    public Component getComponentAt(int x, int y) {
        return p.getComponentAt(x, y);
    }
    /* (non-Javadoc)
     * @see java.awt.Component#getComponentAt(java.awt.Point)
     */
    @Override
    public Component getComponentAt(Point p) {
        return this.p.getComponentAt(p);
    }
    /* (non-Javadoc)
     * @see java.awt.Container#getComponentCount()
     */
    @Override
    public int getComponentCount() {
        return p.getComponentCount();
    }
    /* (non-Javadoc)
     * @see java.awt.Container#getComponents()
     */
    @Override
    public Component[] getComponents() {
        return p.getComponents();
    }
}
