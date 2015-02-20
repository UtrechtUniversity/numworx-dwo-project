// Source file: C:\\fi\\dwo\\parameters\\gui\\HelpButton.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class HelpButton extends JButton implements  MouseListener, ActionListener {

    private String toolTip;
    
    private Image mouseOutImage;
    private Image mouseOverImage;
    private boolean mouseOver = false;
    private String helpText;


    /**
     * @roseuid 425E240E00FA
     */
    public HelpButton(String helpText) {
        this.helpText = helpText;
        setBorderPainted(false);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = this.getClass().getResource(ParameterConstants.HELP_IMAGE1);
        mouseOutImage = toolkit.getImage(url);
        Icon mouseOutIcon = new ImageIcon(mouseOutImage);
        setIcon(mouseOutIcon);
        
        url = this.getClass().getResource(ParameterConstants.HELP_IMAGE2);
        mouseOverImage = toolkit.getImage(url);
        Icon mouseOverIcon = new ImageIcon(mouseOverImage);
        setSelectedIcon(mouseOverIcon);
        
        setSize(mouseOutIcon.getIconWidth(), mouseOutIcon.getIconHeight());
        this.setToolTip(TextMapper.getText(TextMapper.TLTP_HELP));
        
        this.addMouseListener(this);
        this.addActionListener(this);
    }
    

    /**
     * Paints the image on the panel and calls the super.paint(g).
     * 
     * @param g The graphics context to use for painting.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(mouseOver) {
            g.drawImage(mouseOverImage, 0, 0, this);
        } else {
            g.drawImage(mouseOutImage, 0, 0, this);            
        }
    }
    

    /**
     * Sets the tooltip of this component.
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        //ToolTipManager.registerComponent(this);
        setToolTipText(toolTip);
    }

    /**
     * Returns the tooltip of this component.
     * @return The tooltip of this component. 
     * @see fi.beans.tooltip.ToolTipIF#getToolTip()
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Returns this component.
     * @return This component.
     * @see fi.beans.tooltip.ToolTipIF#getComponent()
     */
    public Component getComponent() {
        return this;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
        
    }

    /**
     * Invoked when the mouse exits the HelpButton. The Default Cursor is
     * showed.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseOver = false;
        repaint();
        
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseOver = false;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this) {
            HelpDialog.showHelpDialog(this, helpText);            
        }
        
    }
}