package nl.uu.fi.dwo.formule.client.formuleobjects;

import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Breukvak;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;

/**
 * Formula line
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleRegel extends FormuleElement
{
	protected Vector<FormuleElement> children = new Vector<FormuleElement>();
	static private Logger logger = Logger.getLogger("FormuleRegel");
	
	//private FormuleEditorWithAnswer editorParent;

	private int nextx = 0;
	private int nexty = 0;

	private int defaultwidth = 0;
	private int defaultheight = 0;

	private int currentPosition = -1;

	private int selectionDragStart = -1;
	private int selectionStart = -1;
	private int selectionStartx = 0;

	//TODO: implement non editable lines
	private boolean editable = true;
	//private CssColor color = CssColor.make(0, 0, 0);

	//how many children are numbers?
	private int nonNumberChildern = 0;

	private boolean smalltext = false;

	private int[] selectioncords =
	{ 0, 0, 0, 0 };

	public FormuleRegel(FormuleHolder holder)
	{
		super(holder);
		init();
	}

	public FormuleRegel(FormuleElement holder)
	{
		super(holder);
		init();
	}

	private void init()
	{
		ctx.setFont(fm.getFontStyle());
		//default when there are no children
		defaultwidth = fm.getAscent() / 2;
		//fm.getHeight() is hetzelfde als fm.getAscent() + fm.getDescent() (dat wordt in wiskOpdr gebruikt).
		defaultheight = fm.getHeight();
		this.setSize(defaultwidth, defaultheight);
		//this.setAsHoogte(this.height / 2);
		//In wiskOpdr wordt ashoogte gezet op fm.getAscent()/2.
		//this.setAsHoogte(fm.getAscent() / 2);
		this.setAsHoogte(fm.getAscent()); //maakt ook geen verschil.. ?
		this.setChanged(true);
	}

	private void addElement(FormuleElement e)
	{
		if (this.selectionStart != -1)
		{
			this.deleteSelection();
		}
		//this.children.add(e);
		this.children.add(++this.currentPosition, e);

		if (e.isNumber() == false)
			addNonNumberChild();
		//this should be done in the new child, but just to make sure the changed value is set to true.
		this.setChanged(true);
		
		holder.paint();
		
		//resizeEditorWithAnswer();
		
		//int asHoogte = holder.getMainRegel().getAsHoogte();
		//int hoogte = holder.getMainRegel().getHeight();
		//((Panel)(holder.getMainRegel().getParent())).getElement().getStyle().setProperty("top", (hoogte-asHoogte-Math.rint(font_size*0.33)-2)+"px");
		
	}

	public void setSmallText(boolean val)
	{
		if (smalltext == val)
			return;
		smalltext = val;

		FormuleFontChanges fc;
		if ((fc = this.getFontChanges()) == null)
		{	fc = new FormuleFontChanges();
		}
		
		if (smalltext == true)
			fc.setSmallText(FormuleFontChanges.TRUE);
		else
			fc.setSmallText(FormuleFontChanges.FALSE);

		
		this.setFontChanges(fc);
	}

	private void addNonNumberChild()
	{
		this.nonNumberChildern++;
		/*
		if (this.nonNumberChildern == 1)
		{
			//if (smalltext == false)
				//return;

			FormuleFontChanges fc;
			if ((fc = this.getFontChanges()) == null)
				fc = new FormuleFontChanges();
			fc.setSmallText(FormuleFontChanges.FALSE);
			this.setFontChanges(fc);
			this.setChanged(true);
			GWT.log("change font");
		}
		*/
	}

	private void removeNonNumberChild()
	{
		this.nonNumberChildern--;
		/*
		if (this.nonNumberChildern == 0)
		{
			if (smalltext == false)
				return;

			FormuleFontChanges fc;
			if ((fc = this.getFontChanges()) == null)
				fc = new FormuleFontChanges();
			fc.setSmallText(FormuleFontChanges.TRUE);
			this.setFontChanges(fc);
			this.setChanged(true);
			GWT.log("remove font");
		}
		*/
	}

	@Deprecated
	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{

		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (x < 0)
		{
			this.currentPosition = -1;
			//should only happen if the line has no children
			holder.setCurrentElement(this);
			return this;
		}
		//height is always correct here
		int w = 0;
		//if it gets here, this is the current line. If one of the children has (or is) a formuleregel too, it will override it.
		holder.setCurrentRegel(this);
		FormuleElement prev = null;
		if (x > 0)
			for (int i = 0; i < this.children.size(); i++)
			{
				FormuleElement e = this.children.get(i);
				this.currentPosition = i;
				w += e.width;
				if (x < w)
				{
					e = e.setCurrentElementAt(x - e.x, y - e.y);
					if (e == null)
					{
						this.currentPosition--;
						if (prev != null)
							e = prev.setCurrentElementAt(x - prev.x, y - prev.y);
						else
							break;
					}
					return e;
				}
				prev = e;
			}
		if (x > this.width)
		{
			this.currentPosition = this.children.size() - 1;
			holder.setCurrentElement(this);
		}
		else
		{
			this.currentPosition = -1;
			//should only happen if the line has no children
			holder.setCurrentElement(this);
		}
		return this;
	}

	@Override
	public void paintObject()
	{
		this.height = defaultheight;
		this.width = 0;
		this.nextx = 0;
		this.nexty = 0;

		//painting coordinates
		//int paintabove = height / 2;
		//int paintbelow = height / 2;
		//int paintabove = fm.getAscent() / 2;
		//int paintbelow = height - fm.getAscent() / 2;
		int paintabove = fm.getAscent();
		int paintbelow = height - fm.getAscent();
		

		
		int paintabove_e = 0;
		int paintbelow_e = 0;

		int eldrawheight = 0;
		//we first have to find the right size
		for (int i = 0; i < this.children.size(); i++)
		{
			FormuleElement e = this.children.get(i);
			if(i ==0 && e instanceof FormuleTeken && !((FormuleTeken) e).getFunctieTeken() && !e.isNumber())
			{	nextx += 2;
				width += 2;
			}
			//repaint child (if it has changed)
			e.paint();
			width += e.width;
			e.setPosition(nextx, nexty);

			paintabove_e = e.getAsHoogte();
			paintbelow_e = e.height - e.getAsHoogte();

			if (paintabove < paintabove_e)
				paintabove = paintabove_e;
			if (paintbelow < paintbelow_e)
				paintbelow = paintbelow_e;
			//if (e.getAsHoogte() >= e.height / 2)
			//	eldrawheight = 2 * e.getAsHoogte();
			//else
			eldrawheight = paintabove + paintbelow;//(e.height / 2 - e.getAsHoogte()) * 2;

			if (eldrawheight > this.height)
				this.height = eldrawheight;
			nextx += e.width;
			
			//breedtes aanpassen: formuletekens hebben grotere breedte nodig om zichzelf helemaal te tekenen. 
			//hier weer een beetje breedte weghalen om te zorgen dat tekens (in woorden bijv) dicht genoeg op elkaar staan.
			//nog kijken of ik hier niet te veel weghaal.
		
			if(i + 1 < this.children.size())
			{
				FormuleElement e2 = this.children.get(i + 1);
				if(e instanceof FormuleTeken && Character.isLetter(((FormuleTeken) e).geefChar()) && !((FormuleTeken) e).getFunctieTeken() &&
						e2 instanceof FormuleTeken && Character.isLetter(((FormuleTeken) e2).geefChar()) && !((FormuleTeken) e2).getFunctieTeken())
				{
					width-= 2;
					nextx-= 2;
				}
				else
				{
					width--;
					nextx--;
				}
			}
			
			
		}

		//draw all the childs canvases on this canvas
		if (this.children.size() == 0)
			this.width = defaultwidth;
		this.setSize(width, height);
		this.setAsHoogte(paintabove);

		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor)
			if (((FormuleHolder) holder).getCurrentRegel() == this && this.parent != null)
			{
				//draw background
				ctx.setFillStyle("#eee");
				ctx.fillRect(0, 0, width, height);
			}

		if (this.children.size() == 0 && holder.isInputNeeded())
		{
			//draw square if line is empty
			ctx.setStrokeStyle("#888");
			ctx.beginPath();
			ctx.moveTo(0, 0);
			ctx.lineTo(width, 0);
			ctx.lineTo(width, height);
			ctx.lineTo(0, height);
			ctx.lineTo(0, 0);
			ctx.stroke();
		}

		int elx, ely;
		FormuleElement e;
		for (int i = 0; i < this.children.size(); i++)
		{
			e = this.children.get(i);
			//if(e instanceof Breukvak)
			//	System.out.println("breukVak: " + e.toString());
			//else
			//	System.out.println("ander vak: " + e.toString());
			elx = e.getX();
			//ely = this.height / 2 - e.getAsHoogte();
			ely = paintabove - e.getAsHoogte();
			//System.out.println("tekenHoogte: " + ely);
			e.draw(this.ctx, elx, ely);
		}

		int x = this.width;
		if (this.currentPosition == -1 || this.children.size() == 0)
			x = 0;
		this.drawCursor(x);

		//draw selection line
		ctx.setStrokeStyle("#f00");
		ctx.setLineWidth(2.0);
		this.drawline(ctx, selectioncords[0], selectioncords[1], selectioncords[2], selectioncords[3]);
	}

	/**
	 * backspace
	 */
	public void removePrevious()
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;
		FormuleEditor holder = (FormuleEditor) this.holder;
		if (this.selectionStart != -1)
		{
			this.deleteSelection();
			return;
		}

		if (currentPosition < 0)
		{
			if (this.parent == null)
				return;
			//remove this element

			if (this.parent instanceof FormuleElementWithChildren)
			{

				FormuleElementWithChildren fewc = (FormuleElementWithChildren) this.parent;
				boolean empty = true;
				String onRemove = "";
				for (int i = 0; i < fewc.children.size(); i++)
				{
					if (fewc.getChild(i).children.size() > 0)
					{
						if (empty == false)
							return;
						empty = false;
						onRemove = fewc.getChild(i).toString();
					}
				}
				FormuleRegel newline = this.parent.getRegelParent();

				for (int i = 0; i < newline.children.size(); i++)
					if (newline.children.get(i) == this.parent)
						newline.currentPosition = i;

				holder.setCurrentRegel(newline);
				holder.removeCurrentElement();

				//holder.insert(onRemove);
			}
			return;
		}

		FormuleElement removeObj = this.children.elementAt(this.currentPosition);
		String onRemove = "";
		if (removeObj instanceof FormuleElementWithChildren)
		{

			FormuleElementWithChildren fewc = (FormuleElementWithChildren) removeObj;
			boolean empty = true;

			for (int i = 0; i < fewc.children.size(); i++)
			{
				if (fewc.getChild(i).children.size() > 0)
				{
					if (empty == false)
					{
						onRemove = "";
						break;
					}
					empty = false;
					onRemove = fewc.getChild(i).toString();
				}
			}

		}

		if (this.children.elementAt(this.currentPosition).isNumber() == false)
			removeNonNumberChild();

		this.children.removeElementAt(this.currentPosition);
		this.currentPosition--;

		//backspace, so the current element is removed therefor there is a different currentElement
		if (this.currentPosition >= 0)
		{
			holder.setCurrentElement(this.children.get(this.currentPosition));
		}
		else
			holder.setCurrentElement(this);

		if (onRemove.equalsIgnoreCase("") == false)
			holder.insert(onRemove);

		this.setChanged(true);
		//resizeEditorWithAnswer();
	}

	/**
	 * delete
	 */
	public void removeNext()
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;
		FormuleEditor holder = (FormuleEditor) this.holder;
		//with selection, remove the selection
		if (this.selectionStart != -1)
		{
			this.deleteSelection();
			return;
		}
		if (currentPosition == this.children.size() - 1)
			return;

		FormuleElement removeObj = this.children.elementAt(this.currentPosition + 1);
		String onRemove = "";
		if (removeObj instanceof FormuleElementWithChildren)
		{

			FormuleElementWithChildren fewc = (FormuleElementWithChildren) removeObj;
			boolean empty = true;

			for (int i = 0; i < fewc.children.size(); i++)
			{
				if (fewc.getChild(i).children.size() > 0)
				{
					if (empty == false)
					{
						onRemove = "";
						break;
					}
					empty = false;
					onRemove = fewc.getChild(i).toString();
				}
			}

		}

		if (this.children.elementAt(this.currentPosition + 1).isNumber() == false)
			removeNonNumberChild();

		this.children.removeElementAt(this.currentPosition + 1);
		//this.repaint();
		//this.currentPosition--;

		if (onRemove.equalsIgnoreCase("") == false)
			holder.insert(onRemove);

		this.setChanged(true);
		//resizeEditorWithAnswer();
	}

	@Override
	public FormuleElement getCurrentOnNew()
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor)
			((FormuleHolder) this.holder).setCurrentRegel(this);
		return this;
	}

	public void setIndexAt(int i)
	{
		this.currentPosition = i;
	}

	@Override
	public void setCurrent(boolean current)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;
		FormuleHolder holder = (FormuleHolder) this.holder;
		this.current = current;
		if (current == true)
			holder.setCurrentRegel(this);
	}

	//public void setEditorParent(FormuleEditorWithAnswer editorParent)
	//{
	//	this.editorParent = editorParent;
	//}
	
	@Override
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;

		fm = this.getFont();
		
		defaultwidth = fm.getAscent() / 2;
		defaultheight = fm.getHeight();
		//this.setSize(defaultwidth, defaultheight);
		this.setChanged(true);
		//change all childrens font
		if (this.children.size() == 0)
			return true;
		for (int i = 0; i < this.children.size(); i++)
			this.children.get(i).setFont(fm);

		return true;
	}
	
	public boolean setColor(CssColor c)
	{	if(super.setColor(c) == false)
			return false;
	
		color = c; 
		if(this.children.size() == 0)
			return true;
		for(int i = 0; i < this.children.size(); i++)
			this.children.get(i).setColor(c);
		return true;
		
	}

	@Override
	public FormuleRegel getRegelParent()
	{
		return this;
	}

	public FormuleElement getPrevious(FormuleElement e)
	{
		int pos = this.children.indexOf(e);
		if (pos - 1 < 0)
			return null;
		return this.children.get(pos - 1);
	}

	public FormuleElement getNext(FormuleElement e)
	{
		int pos = this.children.indexOf(e);
		if (pos + 1 > this.children.size())
			return null;
		return this.children.get(pos + 1);
	}

	public FormuleElement getCurrent()
	{
		return this.children.get(this.currentPosition);
	}

	public int getCurrentPosition()
	{
		return this.currentPosition;
	}

	public FormuleElement getElementAt(int i)
	{	
		if(i<0) return null;
		return this.children.get(i);
	}
	
	public void cursorToRight()
	{	if(this.currentPosition < this.children.size() - 1 && holder instanceof FormuleEditor)
		{	FormuleHolder holder = (FormuleHolder) this.holder;
			FormuleElement fe = getElementAt(currentPosition+1);
			if(fe instanceof FormuleElementWithChildren)
			{	FormuleElementWithChildren fewc = (FormuleElementWithChildren)fe;
				int pos = -1 ;
				FormuleRegel fr = fewc.getChild(0);
				holder.setCurrentRegel(fr);
				fr.setIndexAt(pos);
				holder.setCurrentElement(fr);
				fr.drawCursor();
			}
			else
			{	this.currentPosition++;
				holder.setCurrentElement(this.children.get(this.currentPosition));
				clearSelection();
				drawCursor();
			}
			holder.paint();
		}
		else if(!(this.parent==null) && !this.parent.equals(holder) && this.currentPosition == this.children.size() - 1 && holder instanceof FormuleEditor)
		{	FormuleHolder holder = (FormuleHolder) this.holder;
			FormuleRegel parentRegel = null;
			FormuleElementWithChildren parent = (FormuleElementWithChildren)this.parent;
			int index = parent.children.indexOf(this);
			if(parent.children.size() > index+1)
			{	parentRegel = parent.children.get(index+1);
				holder.setCurrentRegel(parentRegel);
				int pos = -1 ;
				parentRegel.setIndexAt(pos);
				holder.setCurrentElement(parentRegel);
			}
			else
			{	parentRegel = this.parent.getRegelParent();
				holder.setCurrentRegel(parentRegel);
				int pos = parentRegel.children.indexOf(this.parent);
				parentRegel.setIndexAt(pos);
				holder.setCurrentElement(this.parent);
			}
			parentRegel.drawCursor();
			holder.paint();
		}
	}
	
	public void cursorToLeft()
	{	if(this.currentPosition > - 1 && holder instanceof FormuleEditor)
		{	FormuleHolder holder = (FormuleHolder) this.holder;
		
			FormuleElement fe = getElementAt(currentPosition);
			if(fe instanceof FormuleElementWithChildren)
			{	FormuleElementWithChildren fewc = (FormuleElementWithChildren)fe;
				FormuleRegel fr = fewc.getChild(fewc.children.size()-1);
				holder.setCurrentRegel(fr);
				int pos = fr.children.size()-1 ;
				fr.setIndexAt(pos);
				if (pos >= 0)
					holder.setCurrentElement(fr.children.get(pos));
				else
					holder.setCurrentElement(fr);
			}
			else
			{	this.currentPosition--;
				if (currentPosition >= 0)
					holder.setCurrentElement(this.getElementAt(currentPosition));
				else
					holder.setCurrentElement(this);
			}
			clearSelection();
			drawCursor();
			holder.paint();
		}
		else if(!(this.parent==null) && !this.parent.equals(holder) && this.currentPosition == - 1 && holder instanceof FormuleEditor)
		{	FormuleHolder holder = (FormuleHolder) this.holder;
			FormuleRegel parentRegel = null;
			FormuleElementWithChildren parent = (FormuleElementWithChildren)this.parent;
			int index = parent.children.indexOf(this);
			if(index>0)
			{	parentRegel = parent.children.get(index-1);
				holder.setCurrentRegel(parentRegel);
				int pos = parentRegel.children.size()-1 ;
				parentRegel.setIndexAt(pos);
				if (pos >= 0)
					holder.setCurrentElement(parentRegel.children.get(pos));
				else
					holder.setCurrentElement(parentRegel);
				
			}
			else
			{	parentRegel = this.parent.getRegelParent();
				holder.setCurrentRegel(parentRegel);
				int pos = parentRegel.children.indexOf(this.parent)-1;
				parentRegel.setIndexAt(pos);
				if (pos >= 0)
					holder.setCurrentElement(parentRegel.children.get(pos));
				else
					holder.setCurrentElement(parentRegel);
			}
			parentRegel.drawCursor();
			holder.paint();
		}
	}

	/*
	 * Converting from string to ui
	 */
	public FormuleElement insert(FormuleElement fe)
	{
		//this method handles things like string "pi" => pi character

		//add to the current formuleregel
		//add(fe,caretPos);
		addElement(fe);

		//simple ignoe this method if the element is not a character
		if (fe instanceof FormuleTeken == false)
			return fe;

		//WiskOpdr.setLaunchDataChanged();
		//if(fe instanceof RegelVak)((RegelVak)fe).setFGColor(fgColor);
		//caretX += getComponent(caretPos) .getSize().width;
		//caretPos++;

		/**/
		int caretPos = getCurrentPosition();
		int nr = caretPos;
		FormuleTeken ft1, ft2, ft3;
		//Component cm1;
		//Component cm2;
		//Component cm3;
		//get the previous elements to check if a string is created like pi or sin etc..
		FormuleElement cm1, cm2, cm3;

		//previous element
		cm3 = fe;//editor.getCurrentRegel().getElementAt(nr - 1);//this.getComponent(nr - 1);
		//we already checked if fe is a formuleteken so we can cast it here
		ft3 = (FormuleTeken) cm3;
		if (ft3.geefChar() == 'e')
		{
			ft3.zetFunctieTeken(true);
		}
		if (nr <= 0)
			return fe;
		cm2 = this.getElementAt(nr - 1);
		if (cm2 instanceof FormuleTeken == false)
			return fe;
		ft2 = (FormuleTeken) cm2;
		if (ft2.geefChar() == 'l' && ft3.geefChar() == 'n')
		{	ft2.zetFunctieTeken(true);
			ft3.zetFunctieTeken(true);
			return fe;
		}
		if (ft2.geefChar() == 'p' && ft3.geefChar() == 'i')
		{
			//caretPos--;
			//caretX -= getComponent(caretPos).getSize().width;
			removePrevious();//remove(cm3);
			//caretPos--;
			//caretX -= getComponent(caretPos).getSize().width;
			removePrevious();//remove(cm2);

			FormuleTeken ft = new FormuleTeken(this, '\u03C0');
			//ft.zetFunctieTeken(true);
			addElement(ft);//add(ft, caretPos);
			//caretX += getComponent(caretPos).getSize().width;
			//caretPos++;
			return ft;
		}
		if (ft2.geefChar() == '>' && ft3.geefChar() == '=')
		{
			removePrevious();
			removePrevious();
			FormuleTeken ft = new FormuleTeken(this, '\u2265');
			addElement(ft);
			return ft;
		}
		if (ft2.geefChar() == '<' && ft3.geefChar() == '=')
		{
			removePrevious();
			removePrevious();
			FormuleTeken ft = new FormuleTeken(this, '\u2264');
			addElement(ft);
			return ft;
		}
		
		if (nr <= 1)
			return fe;
		
		cm1 = this.getElementAt(nr - 2);
		if (cm1 instanceof FormuleTeken && cm2 instanceof FormuleTeken)
		{
			ft1 = (FormuleTeken) cm1;
			if (ft1.geefChar() == 's' && ft2.geefChar() == 'i' && ft3.geefChar() == 'n' || ft1.geefChar() == 'c' && ft2.geefChar() == 'o' && ft3.geefChar() == 's' || ft1.geefChar() == 't' && ft2.geefChar() == 'a' && ft3.geefChar() == 'n' || ft1.geefChar() == 'l' && ft2.geefChar() == 'o' && ft3.geefChar() == 'g')
			{
				ft1.zetFunctieTeken(true);
				ft2.zetFunctieTeken(true);
				ft3.zetFunctieTeken(true);
			}
		}
		return fe;
	}

	public void insert(String s)
	{	if (s == null)
			return;
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
				FormuleElement ne = FormuleDecoder.getElementFromCharacter(ch1, this);
				if (ne != null)
				{
					ne.vulVak(s.substring(2, eind));
					insert(ne);
					s = s.substring(eind);
					if (holder instanceof FormuleEditor)
						((FormuleHolder) holder).setCurrentElement(ne);
					continue;
				}
			}
			else
			{
				//hier iets aanpassen om ook woorden te kunnen gaan maken.
				
				FormuleTeken t = new FormuleTeken(this, s.charAt(0));
				if (holder instanceof FormuleEditor)
					((FormuleHolder) holder).setCurrentElement(t);
				this.insert(t);
				s = s.substring(1);
			}
		}
		//resizeEditorWithAnswer();
		
	}
	
	/*
	public void resizeEditorWithAnswer()
	{
		if(editorParent != null)
			editorParent.resize();
		
		holder.setCurrentRegel(this);
	
		//probleem: nu is de focus weg uit de editor...
	}
	*/
	
	public int getAsHoogte()
	{
		//ashoogtes children bepalen en het maximum daarvan teruggeven.
		if (this.children.size() == 0)
			return super.getAsHoogte();
		int ashoogte = 0;
		for (int i = 0; i < this.children.size(); i++)
		{	if(this.children.get(i).getAsHoogte() > ashoogte)
				ashoogte = this.children.get(i).getAsHoogte();
		}
		return ashoogte;
	}

	/**
	 * Selection
	 */
	@Deprecated
	public void startSelection(int x)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;

		this.selectionStartx = x;
		this.selectionDragStart = this.currentPosition;
	}

	@Deprecated
	public void endSelection(int x)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (x < 0)
		{
			GWT.log("out of bound < 0 " + x);

			//if (this.parent == null)
			//return;
			//TODO: selection from inside an element
			/*
			//remove this element
			FormuleRegel newline = this.parent.getRegelParent();
			editor.setCurrentRegel(newline);
			int abx = this.getAbsoluteX();
			newline.startSelection(this.selectionStartx + abx);
			newline.endSelection(x + abx);
			newline.selectionStartRegel = this;
			//this.parent.setSelected(true);
			*/
			return;
		}
		if (x > this.width)
		{
			GWT.log("out of bound > size " + x);

			//return;
		}
		int lastelement = this.selectionDragStart;
		int startx = this.selectionStartx;
		this.selectionStart = this.selectionDragStart;

		boolean reverse = false;
		if (x < startx)
		{
			//swap
			int temp = x;
			x = startx;
			startx = temp;
			reverse = true;
		}
		FormuleElement e;
		int elx;
		//set selection for each child element
		for (int i = 0; i < this.children.size(); i++)
		{
			e = this.children.elementAt(i);
			elx = e.getX() + (e.getWidth() / 2);
			if (elx < startx || elx > x)
				this.children.elementAt(i).setSelected(false);
			else
			{
				this.children.elementAt(i).setSelected(true);
				//change cursor
				lastelement = i;
				if (reverse == true && (this.selectionStart > i || this.selectionStart == -1))
				{
					this.selectionStart = i;
				}
			}

		}
		if (reverse == false)
		{
			//change cursor
			this.currentPosition = lastelement;
			if (lastelement >= 0)
				holder.setCurrentElement(this.getElementAt(lastelement));
			else
				holder.setCurrentElement(this);
			this.selectionStart = this.selectionDragStart + 1;
		}
		else
		{
			//make sure the element is the last element of the selection
			if (this.currentPosition != this.selectionDragStart)
			{
				this.currentPosition = this.selectionDragStart;
				//change current
				if (this.selectionDragStart >= 0)
					holder.setCurrentElement(this.getElementAt(this.selectionDragStart));
			}

		}

	}

	public void clearSelection()
	{
//		if (this.selectionStart == -1)
//			return;
		for (int i = 0; i < this.children.size(); i++)
			//for (int i = this.selectionStart; i <= this.selectionEnd; i++)
			this.children.get(i).setSelected(false);
		this.selectionStart = -1;
	}

	public String getSelectionString()
	{
		String s = "";
		int start = this.selectionStart;
		if (start < 0)
			start = 0;

		for (int i = start; i <= this.currentPosition; i++)
			s += ((FormuleElement) this.children.get(i)).toString();

		return s;
	}

	public void deleteAll()
	{
		for (int i = children.size()-1 ; i > -1 ; i--)
		{
			children.remove(children.get(i));
			
		}
		currentPosition = -1;
		nonNumberChildern = 0;
	}
	public void deleteSelection()
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (this.selectionStart == -1)
			return;

		//remove selection
		int i;
		for (i = this.currentPosition; i >= this.selectionStart; i--)
		{
			if (this.children.elementAt(i).isNumber() == false)
				removeNonNumberChild();
			this.children.remove(i);
		}
		//clear selection
		this.selectionStart = -1;
		this.currentPosition = i;

		//backspace, so the current element is removed therefor there is a different currentElement
		if (this.currentPosition >= 0)
		{
			holder.setCurrentElement(this.children.get(this.currentPosition));
		}
		else
			holder.setCurrentElement(this);

		this.setChanged(true);
		//resizeEditorWithAnswer();
	}

	public boolean hasSelection()
	{
		return this.selectionStart != -1;
	}

	/**
	 * Editable
	 */

	public boolean isEditable()
	{
		return editable;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	@Override
	public String toString()
	{
		String s = "";
		for (int i = 0; i < this.children.size(); i++)
		{
			s += ((FormuleElement) this.children.get(i)).toString();
		}
		return s;
	}

	public FormuleRegel selection(int selectionStartX, int selectionStartY, int selectionEndX, int selectionEndY)
	{
		if (editable == false)
			return null;
		int lastElement = this.currentPosition;
		int firstElement = this.currentPosition;
		logger.finer( this + " entering selection  "  + selectionStartX + " " +  selectionStartY + " " +  selectionEndX + " " + selectionEndY);
		boolean selectionfound = false;

		//loop through children and set/unset selection
		FormuleElement el;
		FormuleRegel ret = null;
		for (int i = 0; i < this.children.size(); i++)
		{
			el = this.children.elementAt(i);
			//selection inside element?
			if (selectionStartX > el.x && selectionEndX < el.x + el.width && selectionStartY > this.getAsHoogte() - el.getAsHoogte() && selectionEndY < this.getAsHoogte() - el.getAsHoogte() + el.height)
			{
				selectionfound = true;
				el.setSelected(false);
				//find selection regel
				ret = el.selection(selectionStartX - el.x, selectionStartY - (this.getAsHoogte() - el.getAsHoogte()), selectionEndX - el.x, selectionEndY - (this.getAsHoogte() - el.getAsHoogte()));
				//selection does not contain a new line, selection stays in this line
				//this means there is no selection
				if (ret == null)
				{
					//this.children.elementAt(i).setSelected(true);

					firstElement = i;
					lastElement = i;

					if (el.getX() + el.getWidth() / 2 > selectionEndX)
					{
						lastElement--;
						firstElement--;
					}
					if (Math.abs(selectionEndX - selectionStartX)<1 && Math.abs(selectionEndY - selectionStartY)<1) // alleen selectie als hokje > 0x0
						firstElement = -1;
					else
						el.setSelected(true);

					//el.setSelected(true);
					continue;
				}
				//selection is in this child element
				//return ret;
				continue;
			}

			int elx = el.getX();// + (el.getWidth() / 2);

			//if (!(elx >= selectionStartX && elx + el.getWidth() <= selectionEndX))
			if (elx > selectionEndX || elx + el.getWidth() < selectionStartX || this.getAsHoogte() - el.getAsHoogte() > selectionEndY || this.getAsHoogte() - el.getAsHoogte() + el.getHeight() < selectionStartY)
				//outside selection
				el.setSelected(false);
			else
			{
				selectionfound = true;
				//inside selection
				el.setSelected(true);

				//change cursor
				lastElement = i;
				if (firstElement > i)
					firstElement = i;
			}
		}
		if (ret != null)
		{
			logger.finer("exiting selection " + ret + " " + ret.getSelectionString());
			return ret;
		}
		//if(selection == false)
		//this.selectionStart = -1;
		//else
		if (selectionfound == false)
			firstElement = -1;
		this.selectionStart = firstElement;
		this.currentPosition = lastElement;
		if ( holder.getCurrentRegel() != this)
			holder.getCurrentRegel().clearSelection();

		if (lastElement != -1)
			holder.setCurrentElement(this.children.elementAt(lastElement));
		else
			holder.setCurrentElement(this);
		holder.setCurrentRegel(this);
		/*
				selectioncords[0] = selectionStartX;
				selectioncords[1] = selectionStartY;
				selectioncords[2] = selectionEndX;
				selectioncords[3] = selectionEndY;
				setChanged(true);
		*/
		logger.finer("exiting selection2 " + this + " " + this.getSelectionString()  + " " + selectionStart + " " + currentPosition);
		return this;
	}

	@Override
	public boolean isNumber()
	{
		return this.nonNumberChildern == 0;
	}

	public void setMinimumWidth(int minW) {
		this.minW = minW;
		this.setSize(width,height);
	}
	
	public void setMinimumHeight(int minH) {
		this.minH = minH;
		int h = height;
		if(this.children.size() == 0)
		{	h = minH;
		
		}
		this.setSize(width, h);
	}

	/*
	@Override
	public void setAsHoogte(int ashoogte) {
		super.setAsHoogte(ashoogte);
	}
*/
}
