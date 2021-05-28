package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fi.beans.numworxlf.Constants;

public class PlusMinKnop extends JPanel implements MouseListener
{	
	private Polygon pijlPlus, pijlMin;
	private boolean ingedrukt = false;
	private boolean plus;
	private boolean enabled = true;
	private Color buttonColorEnabled = Constants.colorBlue3;//new Color(60,60,60);
	private Color buttonColorDisabled = new Color(180,195,228);//new Color(150,150,150);
	private LoopDraad loopDraad;
	public static int VERTIKAAL = 0;
	public static int HORIZONTAAL = 1;
	
	
	
	public PlusMinKnop(int x, int y, int b, int h,int soort)
	{	setLayout(null);
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
		{	int m = h/6;
			pijlMin = new Polygon();
			pijlMin.addPoint(0,h/2);
			pijlMin.addPoint(h/2,h);
			pijlMin.addPoint(h/2,0);
					
			pijlPlus = new Polygon();
			pijlPlus.addPoint(b-h/2,0);
			pijlPlus.addPoint(b-h/2,h);
			pijlPlus.addPoint(b,h/2);
			/*
			pijlPlus = new Polygon();
			pijlPlus.addPoint(2*m,h/2);
			pijlPlus.addPoint(h/2+m,h-m);
			pijlPlus.addPoint(h/2+m,0+m);
					
			pijlMin = new Polygon();
			pijlMin.addPoint(b-h/2-m,0+m);
			pijlMin.addPoint(b-h/2-m,h-m);
			pijlMin.addPoint(b-2*m,h/2);*/
		}
		
	}
	
	public void setEnabled(boolean b)
	{	boolean old = enabled;
		enabled = b;
		if(old != b) repaint();
		
	}
	public void paintComponent(Graphics g)
	{	/*g.setColor(new Color(212,208,200));
		g.fillRect(0,0,getSize().height,getSize().height);
		g.fillRect(getSize().width - getSize().height,0,getSize().height,getSize().height);
		g.setColor(Color.white);
		g.drawRect(0,0,getSize().height,getSize().height);
		g.drawRect(getSize().width - getSize().height,0,getSize().height,getSize().height);
		g.setColor(Color.black);
		g.drawRect(-1,-1,getSize().height,getSize().height);
		g.drawRect(-2,-2,getSize().height,getSize().height);
		g.drawLine(getSize().width - 1,0,getSize().width - 1,getSize().height);
		g.drawLine(getSize().width - 2,0,getSize().width - 2,getSize().height);
		g.drawLine(getSize().width - getSize().height,getSize().height-1,getSize().width - 1,getSize().height-1);
		g.drawLine(getSize().width - getSize().height,getSize().height-2,getSize().width - 1,getSize().height-2);
		*/
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
		ingedrukt = true;
		if(pijlPlus.contains(e.getX(),e.getY()))
		{	plus = true;
			produceAction("plus");
		}
		else if(pijlMin.contains(e.getX(),e.getY()))
		{	plus = false;
			produceAction("min");
		}
		
		/*Thread pauze = new Thread()
			{	public void run()
				{	try
	    			{   sleep(300);
					}
	    			catch(InterruptedException e){}
					if(ingedrukt)
					{	if(loopDraad!=null)
						{	loopDraad.maakDood();
							loopDraad=null;
						}
						loopDraad = new LoopDraad();
						loopDraad.start();
					}
				}
			};
		pauze.start();*/
	}
		
	public void mouseDragged(MouseEvent e)
	{	
	}
	
	public void mouseReleased(MouseEvent e)
	{	ingedrukt = false;
		if(loopDraad!=null)
		{	loopDraad.maakDood();
			loopDraad=null;
		}
	}
	public void mouseMoved(MouseEvent e){;}
	public void mouseExited(MouseEvent e){;}
	public void mouseClicked(MouseEvent e)
	{	
	}
	
	public void mouseEntered(MouseEvent e){;}
	
	//ActionProducer
	private ActionListener actionListener = null;
	
	public void addActionListener(ActionListener l) 
 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
 	}
 	
 	public void removeActionListener(ActionListener l)
 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
 	}	
 	
 	public void produceAction(String command)
 	{	if (actionListener != null)
 		{	actionListener.actionPerformed( new ActionEvent(this, 0, command) );
 		}
 	}
 	//end ActionProducer
 	
 	public class LoopDraad extends Thread
	{	boolean dood = false;
		public void run()
		{	while(!dood)
			{	if(plus)produceAction("plus");
				else produceAction("min");
				try
				{	sleep(100);
				}
				catch(InterruptedException ex){}
			}
		}
		public void maakDood()
		{	dood = true;
		}
	}
}
