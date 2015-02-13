package fi.dwo.dwojapplet.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mover extends JComponent  {

	public Mover() {
		this(10);
	}
	/**
	 * Minimum X position
	 */
	private int minX = 0;


	public Mover(int width) {
		enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
		setPreferredSize(new Dimension(width,width));
		setMinimumSize(getPreferredSize());
		setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
		setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	}

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("testing");
		Box box = Box.createHorizontalBox();
		JPanel center = new JPanel();
		center.setSize(new Dimension(200,200));
		center.setOpaque(true);
		center.setBackground(Color.cyan);
		JPanel left = new JPanel(new BorderLayout());
		Mover mover = new Mover();
		mover.setOpaque(true);
		mover.setBackground(Color.white);
		JPanel menu = new JPanel();
		menu.setOpaque(true);
		menu.setBackground(Color.green);
		menu.setPreferredSize(new Dimension(30,200));
		left.setMinimumSize(new Dimension(10, 0));
		left.add(menu, BorderLayout.CENTER);
		left.add(mover, BorderLayout.EAST);
		box.add(left);
		box.add(center);
		frame.setContentPane(box);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
	}


	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		track(e);
	}

	private int lastX, lastY, relX, relY;
	private void track(MouseEvent e) {
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
				relX = e.getX();
//System.out.println("now " + getParent().getSize()  + ", " + getBounds());
				break;
		case MouseEvent.MOUSE_DRAGGED:
				Dimension size = getParent().getSize();
//System.out.println("voor " + size);
				int min;
//				min = getParent().getMinimumSize().width;
// System.out.println(min);
				min = minX + getWidth();
				//size.width += curX-lastX;
				size.width += e.getX()- relX;
				size.width = Math.max(size.width, min);
//System.out.println("na " + size + ", delta " + (e.getX()) + "-" + relX );
				
				//size.height += curY-lastY;
//System.out.println("change "+ getParent().getSize() + " to " + size);				
				getParent().setSize(size);
// DONE 1.4 maken!
				if(getParent() instanceof JComponent)
				{
					JComponent swingParent = (JComponent) getParent();
					swingParent.setPreferredSize(size);
					size.height = swingParent.getMinimumSize().height;
					swingParent.setMinimumSize(size);
					size.height = swingParent.getMaximumSize().height;
					swingParent.setMaximumSize(size);
				}
				getParent().invalidate();
				getParent().validate();
//System.out.println("got " + getParent().getSize());
		}
		
	}


	protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e);
		track(e);
	}



	protected void paintComponent(Graphics g) {
		if(isOpaque())
		{
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

}
