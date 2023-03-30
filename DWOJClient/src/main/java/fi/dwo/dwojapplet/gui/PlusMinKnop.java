package fi.dwo.dwojapplet.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

class PlusMinKnop extends JComponent implements MouseListener
{	
	private Polygon pijlPlus, pijlMin;
	private boolean enabled = true;
	private final Color buttonColorEnabled = new Color(60,60,60);
	private final Color buttonColorDisabled = new Color(150,150,150);
	static final int VERTIKAAL = 0;
	static final int HORIZONTAAL = 1;
	
	final private ActionEvent PLUS = new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "plus");
    final private ActionEvent MIN  = new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "min");
	
	PlusMinKnop(int x, int y, int b, int h,int soort)
	{
	    super();
	    setBounds(x,y,b,h);
		addMouseListener(this);
		if(soort==PlusMinKnop.VERTIKAAL)
		{	pijlPlus = new Polygon();
			pijlPlus.addPoint(0,b/2);
			pijlPlus.addPoint(b,b/2);
			pijlPlus.addPoint(b/2,0);
					
			pijlMin = new Polygon();
			pijlMin.addPoint(0,h-b/2);
			pijlMin.addPoint(b,h-b/2);
			pijlMin.addPoint(b/2,h);
		}
		else
		{	pijlMin = new Polygon();
			pijlMin.addPoint(0,h/2);
			pijlMin.addPoint(h/2,h);
			pijlMin.addPoint(h/2,0);
					
			pijlPlus = new Polygon();
			pijlPlus.addPoint(b-h/2,0);
			pijlPlus.addPoint(b-h/2,h);
			pijlPlus.addPoint(b,h/2);
		}
	}
	
	public void setEnabled(boolean b)
	{	boolean old = enabled;
		enabled = b;
		if(old != b) repaint();		
	}
	
	public void paintComponent(Graphics g)
	{	
		Color buttonColor = enabled ? buttonColorEnabled : buttonColorDisabled;
		g.setColor(buttonColor);
		g.fillPolygon(pijlPlus);
		g.drawPolygon(pijlPlus);
		g.fillPolygon(pijlMin);
		g.drawPolygon(pijlMin);
	}
	
	public void mousePressed(MouseEvent e)
	{	if(!enabled)
			return;
		if(pijlPlus.contains(e.getX(),e.getY()))
		{	produceAction(PLUS);
		}
		else if(pijlMin.contains(e.getX(),e.getY()))
		{	produceAction(MIN);
		}		
	}
			
	public void mouseReleased(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}	
	public void mouseEntered(MouseEvent e){;}
	
	//ActionProducer
	private ActionListener actionListener = null;
	
	public void addActionListener(ActionListener l) 
 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
 	}
 	
 	public void removeActionListener(ActionListener l)
 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
 	}	
 	
 	public void produceAction(ActionEvent command)
 	{	if (actionListener != null) {
 	      actionListener.actionPerformed( command );
 		}
 	}
 	//end ActionProducer
 	
}
