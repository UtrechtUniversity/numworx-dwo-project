//Source file: N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\DwoButton.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;

/**
 * A button in the good formats and style for the dwo. Default color is
 * MAIN_BACKGROUND
 * 
 * @author M.J.B. Kupers
 * @deprecated gebruik JButton
 */
public class DwoButton extends BorderedPanel implements MouseListener, FocusListener, KeyListener {
    private Vector actionListeners = new Vector();
    private boolean isMouseOver = false;
    
    private boolean isPressed = false;
    private boolean isFocussed = false;
    
    private String label;
    private Color onMouseOverColor = GuiConstants.SUB_BACKGROUND;
    private Color onMouseOverFontColor = Color.black;
    private String toolTip;
    private String actionCommand = "";

	/**
     * Creates a new DwoButton with the default style and colors for the Dwo.
     *  
     */
    public DwoButton() {
    	this("");
    }

    /**
     * Creates a new DwoButton with the specified caption. The default
     * background color will be used.
     * 
     * @param caption The caption of the button.
     */
    public DwoButton(String caption) {
    	this(caption, GuiConstants.MAIN_BACKGROUND);
    }
    
    /**
     * Creates a new DwoButton with the specified caption and background color.
     * 
     * @param caption The caption of the button.
     * @param bgColor The backgroundcolor of the button. Default color is
     *            GuiConstants.MAIN_BACKGROUND
     *  
     */
    public DwoButton(String caption, Color bgColor) {
        this.setBackground(bgColor);
        this.setLabel(caption);
        this.setFont(new Font("Arial", Font.BOLD, 12));
        if((bgColor == GuiConstants.SUB_BACKGROUND) || (bgColor.equals(GuiConstants.SUB_BACKGROUND))) {
            onMouseOverColor = GuiConstants.MAIN_BACKGROUND;
        }
        addMouseListener(this);
        //addFocusListener(this);
        //addKeyListener(this);
    }
    
    /**
     * Adds the specified action listener to receive action events from this
     * button. Action events occur when a user presses or releases the mouse
     * over this button. If l is null, no exception is thrown and no action is
     * performed.
     * 
     * @param l the action listener.
     * @see fi.dwo.client.gui.CourseIconIF#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        if (l != null) {
            actionListeners.addElement(l);
        }
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets the mininimum size of this component. The minimum size is the current size.
     * 
     * @return A dimension object indicating this component's minimum size.
     */
    public Dimension getMinimumSize() {
        if(!this.size().equals(new Dimension(0, 0))) {
            return this.size();
        } else {
	        FontMetrics fm = getFontMetrics(this.getFont());
	        int length = fm.stringWidth(this.getLabel());
	        return new Dimension(length + 8, fm.getHeight() + 6);
        }
    }
    public Color getOnclickColor() {
        return onMouseOverColor;
    }
    public Color getOnclickFontColor() {
        return onMouseOverFontColor;
    }
    public Color getOnMouseOverColor() {
        return onMouseOverColor;
    }
    public Color getOnMouseOverFontColor() {
        return onMouseOverFontColor;
    }

    /**
     * Gets the preferred size of this component. The preferred size is the current size.
     * 
     * @return A dimension object indicating this component's preferred size.
     */
    public Dimension getPreferredSize() {
        if(!this.size().equals(new Dimension(0, 0))) {
            return this.size();
        } else {
	        FontMetrics fm = getFontMetrics(this.getFont());
	        int length = fm.stringWidth(this.getLabel());
	        return new Dimension(length + 8, fm.getHeight() + 6);
        }
    }
    
    private void callActionListeners() {
        for (int i = 0; i < actionListeners.size(); i++) {
            ((ActionListener) actionListeners.elementAt(i)).actionPerformed(new ActionEvent(this, 0, actionCommand));
        }        
    }

    /**
     * Invoked when the mouse has been clicked on the DwoButton. 
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
        isPressed = false;
        isFocussed = false;
        repaint();
    }

    /**
     * Invoked when the mouse enters the DwoButton. A Hand Cursor is showed and
     * the button will be highlighted.
     * 
     * Op de Apple met safari, ontbreekt dit event als het scherm geen focus heeft
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        isMouseOver = true;
        repaint();
    }

    /**
     * Invoked when the mouse exits the DwoButton. The Default Cursor is showed
     * and the text will be displayed normal.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        isMouseOver = false;
        isFocussed = false;
        repaint();
    }

    /**
     * Invoked when a mouse button has been pressed on the DwoButton.
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        isPressed = true;
        isMouseOver = true;
        repaint();
    }

    /**
     * Invoked when a mouse button has been released on a component. The
     * ActionListeners are invoked.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        isFocussed = false;
        isPressed = false;
        if(isMouseOver) {
            callActionListeners();
        }
        repaint();
    }
    
    public void paint(Graphics g) {
        super.paint(g);

        FontMetrics fm = getFontMetrics(this.getFont());
        int length = fm.stringWidth(this.getLabel());
        int height = fm.getHeight();
        
        if(isFocussed) {
            g.setColor(Color.gray);
            g.drawRect((this.getSize().width / 2)
                    - (length / 2)- 2, (this.getSize().height / 2)
                    - (height / 2) - 2, length + 3, height + 3);
        }

        if(isMouseOver || isPressed) {
            if(isPressed) {
                g.setColor(onMouseOverColor.darker());
                g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
                g.setColor(onMouseOverColor.brighter());
                g.drawRect(2, 2, getSize().width - 4, getSize().height - 4);
            } else{
                g.setColor(onMouseOverColor.brighter());
                g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
                g.setColor(onMouseOverColor.darker());
                g.drawRect(2, 2, getSize().width - 4, getSize().height - 4);                
            }
            g.setColor(onMouseOverColor);
            g.fillRect(2, 2, getSize().width - 4, getSize().height - 4);
            g.setColor(onMouseOverFontColor);
        } else {
            g.setColor(this.getForeground());
        }
        g.drawString(this.getLabel(), (this.getSize().width / 2)
                - (length / 2), (this.getSize().height / 2)
                + (height / 2) - 1);
    }
    public void setLabel(String label) {
        this.label = label;
        repaint();
    }
    public void setOnMouseOverColor(Color onMouseOverColor) {
        this.onMouseOverColor = onMouseOverColor;
    }
    public void setOnMouseOverFontColor(Color onMouseOverFontColor) {
        this.onMouseOverFontColor = onMouseOverFontColor;
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        isFocussed = true;
        repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        isFocussed = false;
        isPressed = false;
        isMouseOver = false;
        repaint();
    }

    public boolean isFocusTraversable() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        if(isFocussed) {
            isPressed = true;
            repaint();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        if(isFocussed && (e.getKeyCode() == KeyEvent.VK_SPACE)) {
            callActionListeners();
        }
        isPressed = false;
        repaint();        
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
        if(isFocussed) {
            isPressed = true;
            repaint();
        }
    }

    /**
     * Sets the tooltip of this component.
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
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

	/**
	 * @return Returns the actionCommand.
	 */
	public String getActionCommand() {
		return actionCommand;
	}

	/**
	 * @param actionCommand The actionCommand to set.
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

}