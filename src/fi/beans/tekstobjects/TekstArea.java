package fi.beans.tekstobjects;

import java.awt.*;

public class TekstArea extends Panel
{	
	public static int CENTER = 0;
	public static int LEFT = 1;
	public static int RIGHT = 2;
	
	private Image im;
	private Graphics gIm;
	private boolean resized = false;
	
	private boolean editable = false;
	private boolean selectable = false;
	
	private TekstVak tekstVak;
	
	public TekstArea()
	{	setLayout(null);
		setBackground(getBackground());
		tekstVak = new TekstVak();
		tekstVak.setLocation(0,0);
		add(tekstVak);
		//tekstVak.setEditable(false);
		//tekstVak.setSelectable(false);
	}
	
	public void setFont(Font font)
	{	tekstVak.setFont(font);
	}
	
	public void setText(String s)
	{	tekstVak.zetTekst(s);
		tekstVak.setEditable(editable);
	    tekstVak.setSelectable(selectable);
	}
	
	public String getText(String s)
	{	return tekstVak.toString();
	}
	
	public void setEditable(boolean b)
	{	editable = b;
		if(b)selectable = true;
		tekstVak.setSelectable(b);
		tekstVak.setEditable(b);
	}
	
	public void setAllignment(int all)
	{	tekstVak.setAllignment(all);
	}
	
	public void paint(Graphics g)
	{	
		{ 	if(im==null || resized)
			{	if(resized && gIm!=null)
				{	gIm.dispose();
				}
				im = createImage(getSize().width,getSize().height);
  				gIm = im.getGraphics();
				resized = false;
			}
			gIm.setColor(getBackground());
			gIm.fillRect(0,0,getSize().width,getSize().height);
			super.paint(gIm);
			g.drawImage(im, 0, 0, null);
  		}
	}
	
	public void destroy()
	{	if(gIm!=null)
		{	gIm.dispose();
			gIm = null;
		}
	}
	
	public void setSize(int b, int h)
	{	resized = true;
		tekstVak.setSize(b,h);
		super.setSize(b,h);
	}
	
	public void resize()
	{	resized = true;
		super.setSize(getSize().width, tekstVak.getSize().height);
	}
	
	public void setBounds(int x, int y, int b, int h)
	{	resized = true;
		tekstVak.setBounds(0,0,b,h);
		super.setBounds(x,y,b,h);
	}
	
	public void update(Graphics g)
	{	paint(g);
	}
}