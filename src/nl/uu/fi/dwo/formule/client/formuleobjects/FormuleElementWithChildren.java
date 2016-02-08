package nl.uu.fi.dwo.formule.client.formuleobjects;

import java.util.Vector;

import com.google.gwt.canvas.dom.client.CssColor;

import fi.wiskopdr.Letter;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

/**
 * Base element that contains one or more FormuleRegels (wortelvak, breukvak
 * etc..)
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class FormuleElementWithChildren extends FormuleElement
{
	protected Vector<FormuleRegel> children = new Vector<FormuleRegel>();

	private int currentChild = 0;

	public FormuleElementWithChildren(FormuleElement holder)
	{
		super(holder);
	}

	public FormuleElementWithChildren(FormuleElement holder, int children)
	{
		super(holder);
		this.createChildren(children);
	}

	protected void createChildren(int children)
	{
		FormuleRegel child;
		for (int i = 0; i < children; i++)
		{
			child = new FormuleRegel(this);
			this.children.add(child);
		}
	}

	public int getChildrenSize()
	{
		return this.children.size();
	}

	public FormuleRegel getChild(int i)
	{
		return this.children.get(i);
	}

	public FormuleRegel getChild()
	{
		return this.getChild(this.currentChild);
	}

	@Override
	public FormuleElement getCurrentOnNew()
	{
		//the current element with a new instance is the first child
		return this.getChild(0);
	}

	@Override
	public FormuleElement getCurrentOnNewOnSelection()
	{
		if (this.children.size() > 1)
			return getChild(1);
		return this;
	}

	String getOnRemove(FormuleRegel remove)
	{
		int index = this.children.indexOf(remove);
		FormuleElement ret = this.getChild(0);
		if (index == 0)
			ret = this.getChild(1);
		if (ret != null)
			return ret.toString();
		return "";
	}

	@Override
	public boolean setFont(FormuleFont fm)
	{
		//GWT.log("Setting childern font");
		if (super.setFont(fm) == false)
			return false;
		fm = this.getFont();
		//GWT.log(fm.getFontStyle() + " = new old: " + getChild().getFont().getFontStyle());
		for (int i = 0; i < this.getChildrenSize(); i++)
			getChild(i).setFont(fm);
		return true;
	}
	
	public boolean setColor(CssColor c)
	{
		if(super.setColor(c) == false)
			return false;
		color = c;
		for(int i = 0; i < this.getChildrenSize(); i++)
			getChild(i).setColor(c);
		return true;
	}

	@Override
	public void vulVak(String s)
	{
		if (s == null)
			return;

		for(int i = 0; i < this.getChildrenSize(); i++)
			this.getChild(i).deleteAll();
		
		FormuleRegel regel = this.getChild(0);

		while (s.length() > 0)
		{
			char ch0 = s.charAt(0);
			if (ch0 == '@')
				break;
			else if (ch0 == '$')
			{
				int niv = 1;
				int eind = 0;
				String sz = s.substring(2);
				while (niv > 0)
				{
					int eindB = sz.indexOf("$");
					int eindE = sz.indexOf("@");
					if (eindB < eindE && eindB != -1)
					{
						eind = eindB;
						niv++;
					}
					else
					{
						eind = eindE;
						niv--;
					}
					sz = sz.substring(eind + 1);
				}
				eind = s.length() - sz.length();
				char ch1 = s.charAt(1);
				//next child?
				if (ch1 == 'n')
				{
					regel = getChild(1);
					s = s.substring(2);
					continue;
				}
				else if (ch1 == 'k')
				{
					regel = getChild(2);
					s = s.substring(2);
					continue;
				}
				else if (ch1 == 'l')
				{
					regel = getChild(3);
					s = s.substring(2);
					continue;
				}
				else
				{
					FormuleElement ne = FormuleDecoder.getElementFromCharacter(ch1, regel);
					if (ne != null)
					{
						ne.vulVak(s.substring(2, eind));
						regel.insert(ne);
						s = s.substring(eind);
					}
				}
			}
			else
			{
				//hier iets aanpassen om ook woorden te kunnen gaan maken.
				if(s.length() > 1 && Letter.isCombined(s.charAt(1)))
				{
					FormuleTeken t = new FormuleTeken(this, s.substring(0, 2));
					regel.insert(t);
					s = s.substring(2);
				} else {

				regel.insert(new FormuleTeken(regel, s.charAt(0))); // FIXME combine here again!!
				s = s.substring(1);
			}}
		}
	}
	
	/*public void setSelected(boolean b)
	{
		for (int i = 0; i < this.children.size(); i++)
			//for (int i = this.selectionStart; i <= this.selectionEnd; i++)
			this.children.get(i).setSelected(b);
		this.setChanged(true);
	}*/

	public FormuleRegel selection(int selectionStartX, int selectionStartY, int selectionEndX, int selectionEndY)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			FormuleRegel regel = this.getChild(i);
			if (regel.x <= selectionStartX && regel.x + regel.width >= selectionEndX && regel.y <= selectionStartY && regel.y + regel.height >= selectionEndY)
				return regel.selection(selectionStartX - regel.x, selectionStartY - regel.y, selectionEndX - regel.x, selectionEndY - regel.y);
		}
		return null;
	}
}
