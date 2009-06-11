package fi.beans.tekstobjects;

import java.awt.*;

public class TekstTeken extends TekstElement
{
	private FontMetrics fm;
	
	private String teken;
	private char character;
	private boolean selected = false;
	
	public TekstTeken(char tk)
	{	character = tk;
		//if(tk=='+' || tk=='-' || tk=='=')
		//{	teken = " "+tk+" ";
		//}
		//else if(tk=='*')
		//{	teken = null;
		//}
		//else 
			if(tk=='\n')
		{	teken = "";
		}
		else teken = ""+tk;
		
		selected = false;
	}
	
	public void setFont(Font f)
	{	//if(Character.isLetter(character) && !functieTeken)
		//{	super.setFont(new Font(f.getName(),Font.ITALIC,f.getSize()));
		//}
		//else
		{	super.setFont(f);
		}
		fm = getFontMetrics(getFont());
		if(teken!=null)
		{	setSize(fm.stringWidth(teken),fm.getFont().getSize());
		}
		else if(character=='*')
		{	setSize(fm.getAscent()/3,fm.getFont().getSize());
		}
		ashoogte = fm.getAscent()/2;
		
	}
	
	public void paint(Graphics g)
	{	if(selected)
		{	g.setColor(Color.black);
			g.fillRect(0,0,getSize().width,getSize().height);
		}
	}
	
	public void paint(Graphics gr, int x, int y)
	{	
		Graphics g = (Graphics2D)gr;
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(selected)g.setColor(Color.white);
		else g.setColor(Color.black);
		
		g.setFont(getFont());
		if(teken!=null)g.drawString(teken,x,y+fm.getAscent());
		else if(character=='*')
		{	g.drawLine(x+fm.getAscent()/6,y+5*fm.getAscent()/8,x+fm.getAscent()/6,y+5*fm.getAscent()/8);
		}
	}
	
	public char geefChar()
	{	return character;
	}
	
	//public void zetFunctieTeken(boolean b)
	//{	functieTeken = b;
	//	super.setFont(new Font(getFont().getName(),Font.PLAIN,getFont().getSize()));
	//	fm = getFontMetrics(getFont());
	//}
	
	public void setSelected(boolean b)
	{	if(selected!=b)
		{	selected = b;
			repaint();
		}
	}
	
	public boolean isSpatie()
	{	return character==' ';
	}
	
	public boolean isSelected()
	{	return selected;
	}
	
	public String toString()
	{	return "" + character;
	}
}
