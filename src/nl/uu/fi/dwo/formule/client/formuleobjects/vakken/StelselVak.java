package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Vector;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class StelselVak extends FormuleElementWithChildren
{
	//Vector<FormuleRegel> kinderen;
	public StelselVak(FormuleElement editor)
	{
		super(editor, 1);
		//kinderen = new Vector<FormuleRegel>();
		//new formuleRegel

		//setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());

		//ind1.setLocation(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		//this.setSize(5 * fm.getAscent() / 6 + getChild().width, fm.getAscent() / 4 + getChild().height);
		
		//getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		//this.paint();
		this.setChanged(true);
		//this.setAsHoogte(3 * fm.getAscent() / 4);
		
	}

	@Override
	public void paint()
	{
		
		if (this.isChanged() == false)
			return;

		maakMaat();
		
		for(int i = 0; i < getChildrenSize(); i++)
		{	getChild(i).paint();
		}
		//((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//((Graphics2D)g).setStroke(new BasicStroke(1.2f));
	    		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());
		
		ctx.beginPath();
		//ctx.moveTo(x, y);
		ctx.arc(10, 6, 5, 3 * Math.PI / 2, Math.PI, true);
		ctx.lineTo(5, height / 2 - 3);
		ctx.arc(1, height / 2 - 4, 4, 0, Math.PI / 2);
		ctx.arc(1, height / 2 + 4, 4, 3 * Math.PI / 2, 0);
		ctx.lineTo(5, height - 6);
		ctx.arc(10, height - 7, 5, Math.PI, Math.PI / 2, true);
		ctx.stroke();
		
		for(int i = 0; i < getChildrenSize(); i++)
			getChild(i).draw(ctx);
		//this.getChild().draw(ctx);
		this.drawCursor();
//		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//		((Graphics2D)g).setStroke(new BasicStroke(0.7f));
		
//		super.paint(g);
//		
//		
//		
//		
//		HTML5 wortel		
//		this.getChild().paint();
//
//		width = 5 * fm.getAscent() / 6 + getChild().width;
//		height = fm.getAscent() / 4 + getChild().height + 3; //+3 omdat dat werkt.. is misschien niet de netste oplossing.
//
//		this.setAsHoogte(getChild().getAsHoogte() + fm.getAscent() / 4);
//		this.setSize(width, height);
//
//		if (this.isSelected())
//		{
//			ctx.setFillStyle("#aaf");
//			ctx.fillRect(0, 0, this.width, this.height);
//		}
//
//		//ctx.setStrokeStyle("#000");
//		//ctx.setFillStyle("#000");
//		ctx.setStrokeStyle(color);
//		ctx.setFillStyle(color);
//		
//
//		ctx.setLineWidth(fm.getStrokeWidth());
//
//		ctx.beginPath();
//		ctx.moveTo(1, 2 * height / 3 - 1);
//		ctx.lineTo(fm.getAscent() / 3, height - 3);
//		ctx.stroke();
//		//this.drawline(ctx, 0, 2 * height / 3, fm.getAscent() / 3, height);
//
//		ctx.beginPath();
//		ctx.moveTo(2, 2 * height / 3 - 1);
//		ctx.lineTo(fm.getAscent() / 3 + 1, height - 3);
//		ctx.lineTo(2 * fm.getAscent() / 3 - 1, fm.getAscent() / 8);
//		ctx.lineTo(width, fm.getAscent() / 8);
//		ctx.stroke();
//
//		this.getChild().draw(ctx);
//		this.drawCursor();
	}
	
	public int bepaalKindMetFocus()
	{
		int kindMetFocus = 0;
		for(int i = 0; i < children.size(); i++)
		{
			//if(children.get(i).hasFocus())
			//{	kindMetFocus = i;
			//	break;
			//}
		}
		return kindMetFocus;
	}
	
	public void focusKindOmhoog()
	{
		int kindMetFocus = bepaalKindMetFocus();
		//if(kindMetFocus > 0)
		//	children.get(kindMetFocus - 1).neemFocus("rechts");
	}
	
	public void focusKindOmlaag()
	{
		int kindMetFocus = bepaalKindMetFocus();
		if(kindMetFocus == children.size() - 1)
		{	maakNieuwKind();
		}
		//children.get(kindMetFocus + 1).neemFocus("rechts");
		//zetMaat();
		paint();
	}
	
	public void maakNieuwKind()
	{
		FormuleRegel kind = new FormuleRegel(this);
		kind.setFont(getFont());
		children.add(kind);
		//add(kind);
	}
	
	public void deleteKind()
	{
		int kindMetFocus = bepaalKindMetFocus();
		FormuleRegel kind = children.get(kindMetFocus);
		//testen of kind leeg is.
		//Als het kind leeg is: kind verwijderen. 
		if(kind.toString().length() > 0)
			return;
		//remove(kind);
		children.remove(kindMetFocus);
		//if(kindMetFocus < children.size())
		//	children.get(kindMetFocus).neemFocus("rechts");
		//else
		//	children.get(kindMetFocus - 1).neemFocus("rechts");
		//zetMaat();
		paint();
	}

	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (x > getChild().x && x < getChild().x + getChild().width)
			return getChild().setCurrentElementAt(x - getChild().x, y - getChild().y);
		if (x <= getChild().x)
		{
			//if x < 1/2 of the "v" the cursor should be placed before this object
			if (x < fm.getAscent() / 3)
				return null;

			this.getChild().setIndexAt(-1);
			holder.setCurrentElement(this.getChild());
			return this.getChild();
		}

		holder.setCurrentElement(this);
		return this;
	}

	@Override
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		for(int i = 0; i < children.size(); i++)
			children.get(i).setFont(fm);
		maakMaat();
		return true;
	}
	
	private void maakMaat()
	{	//super.zetMaat();
		
		height = 5;
		width = 10;
		for(int i = 0; i < children.size(); i++)
		{
			height += children.get(i).height + 5;
			width = Math.max(width, 10 + children.get(i).width);
		}
		setSize(width, height);
		setAsHoogte(height / 2 + fm.getAscent()/2 - fm.getDescent()/2);
		
		int kindHoogte = 5;
		for(int i = 0; i < children.size(); i++)
		{	children.get(i).setPosition(10, kindHoogte);
			kindHoogte += children.get(i).height + 5;
		}
	
	}
	
//	public int getAsHoogte()
//	{	return getChild().getAsHoogte() + fm.getAscent() / 4;
//	}
	
	public void setEditable(boolean b)
	{
		for(int i = 0; i < children.size(); i++)
			children.get(i).setEditable(b);
	}
	
	public void vulVak(String s)
	{
		//hier komt altijd een string in waarin de elementen zijn gescheiden door $n. 
		children = new Vector<FormuleRegel>();
		
		while(s.length()>0)
		{	char ch0 = s.charAt(0);
			if(ch0=='@')
			{	break;
			}
			else if(ch0=='$')
			{	int niv = 1;
				int eind = 0;
				String sz = s.substring(2);
				while(niv>0 )
				{	int eindB = sz.indexOf("$");
					int eindE = sz.indexOf("@");
					if(eindB < eindE && eindB!=-1)
					{	eind = eindB;
						niv++;
					}
					else
					{	eind = eindE;
						niv--;
					}
					sz = sz.substring(eind+1);
				}
				eind = s.length()-sz.length();				
				char ch1 = s.charAt(1);
				if(ch1 == 'n')
				{	maakNieuwKind();
				children.get(children.size() - 1).insert(s.substring(2, eind));
					s = s.substring(eind);
				}
			}
		}
		//for(int i = 0; i < children.size(); i++)
		//	children.get(i).zetMaat();	
	}

	@Override
	public String toString()
	{
		String string = "$Q";
		if(children.size() > 0)
		{	for(int i = 0; i < children.size(); i++)
				string = string + "$n" +  children.get(i).toString() + "@";
		}
		return string + "@";
	}
	

}
