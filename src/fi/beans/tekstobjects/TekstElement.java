package fi.beans.tekstobjects;

import java.awt.*;

public class TekstElement extends Container
{
	public int ashoogte;
	public TekstVak tekstVak;
	
	
	public Component add( Component comp ) 
	{	Component c = super.add(comp);
		comp.setFont(getFont());
		return c;
	}
	public Component add( Component comp, int index ) 
	{	Component c = super.add(comp, index);
		comp.setFont(getFont());
		return c;
	}
	
	public void zetMaat()
  	{
	}
	
	public void setEditable(boolean b)
	{	
	}
	
	public void setSelectable(boolean b)
	{	
	}
	
	public boolean isSelected()
  	{	return false;
	}
	
	public boolean isSpatie()
	{	return false;
	}
	
	public void setSelected(boolean b)
  	{	
	}
	
	public void neemFocus(String richting, TekstElement fe)
	{
	}
	public void neemFocus(String richting)
	{
	}
	public String toString()
	{	return null;
	}
}
