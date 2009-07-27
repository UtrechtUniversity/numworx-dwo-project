//Source file: N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ResultScoreButton.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;
import fi.dwo.client.domain.ResultScoreIF;
import fi.dwo.client.system.TextMapper;

/**
 * Shows a panel with the color representing the score. It also shows the score.
 */
public class ResultScoreButton extends JPanel implements
        /*MouseListener,*/ ActionListener, ToolTipIF {
    private float score;

    private ResultScoreIF domain;

    //private ResultsModulePanel resultsModulePanel;
    
    private String toolTip;

    /**
     * Creates a new ResultScoreButton. This represents a ResultScore, and shows
     * a color and the score.
     * 
     * @param score The score to show.
     * @param rs The ResultScore linked to this button.
     */
    public ResultScoreButton(float score, ResultScoreIF rs) {
        super(new BorderLayout());
        domain = rs;
        this.score = score;
        int red = 255;
        int green = 255;
        int blue = 0;
        JComponent l;
        if (score != 0) {
            if(score == -1) { //it is -1 he did the course but has no score
                score = 0;
            }
            if (score > 100) {
                red = 0;
            } else {
	            if (score < 50) {
	                green = (int) (green * (score / 50));
	            } else {
	                red = (int) (red * (1 - (score - 50) / 50));
	            }
            }
            if(red>255)red=255;
	        if(green>255)green=255;
	        if(blue>255)blue=255;
	        if(red<0)red=0;
	        if(green<0)green=0;
	        if(blue<0)blue=0;
	        
            this.setBackground(new Color(red, green, blue));

            if (domain.isDeepest()) {
                LinkedLabel ll = new LinkedLabel(((int) score) + " %");
                ll.addActionListener(this);
                ll.setMouseoverColor(Color.black);
                String[] arguments = new String[2];
                arguments[0] = domain.getUserGroup().getName();
                arguments[1] = domain.getLessonGroup().getToolTip();
                String s = TextMapper.getText(TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON);
                s = MessageFormat.format(s, arguments);
                this.setToolTip(s);
                ll.setToolTipText(s);
                l = ll;
            } else {
                l = new JLabel(((int) score) + " %");
                //addMouseListener(this);
                           }
        } else {
        	l = new JLabel(" ");
        	//addMouseListener(this);
        	 
        }
            center(l);
            FontMetrics fm;
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(getText(l)) + 10, fm.getHeight());

            this.setSize(l.getSize().width + 5, l.getSize().height + 5);
            this.add(l, BorderLayout.CENTER);

            

    }
    
    /**
     * De text van label of button
     * @param l label of button
     * @return
     */
	private String getText(JComponent l) {
		if(l instanceof JLabel)
			return ((JLabel) l).getText();
		if(l instanceof AbstractButton)
			return ((AbstractButton)l).getText();
		return "";
	}
/**
 * setHorizontalAlignment voor labels en buttons.
 * Helaas geen common class/interface.
 * @param l button of label
 */
	private void center(JComponent l) {
		if(l instanceof AbstractButton)
			((AbstractButton) l).setHorizontalAlignment(SwingConstants.CENTER);
		if(l instanceof JLabel)
			((JLabel) l).setHorizontalAlignment(SwingConstants.CENTER);
	}
    
    public void setBarMode()
    {	setBackground(GuiConstants.MAIN_BACKGROUND);
    	JPanel p = new JPanel();
    	p.setLayout(null);
    	JPanel bar = new JPanel();
    	p.setBounds(0,0,(int)(getSize().width*score/100),getSize().height/3);
    	p.setPreferredSize(p.getSize());
    	p.setBackground(new Color(200,220,240));
    	bar.setBounds(0,0,(int)(getSize().width*score/100),getSize().height/3);
    	bar.setPreferredSize(getSize());
    	bar.setBackground(new Color(0,180,0));
    	
    	add(p,BorderLayout.SOUTH);
    	p.add(bar);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        domain.showResult();
    }

    /**
     * Invoked when the mouse has been clicked on the CourseIcon. The result is
     * showed.
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
        domain.showResult();
    }

    /**
     * Invoked when the mouse enters the CourseIcon. If the current ResultScore
     * is the deepest, a Hand Cursor is showed.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        if (domain.isDeepest()) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            repaint();
        }
    }

    /**
     * Invoked when the mouse exits the CourseIcon. The Default Cursor is
     * showed.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        if (domain.isDeepest()) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            repaint();
        }

    }

    /**
     * Invoked when a mouse button has been pressed on the ResultScoreButton.
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
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

}