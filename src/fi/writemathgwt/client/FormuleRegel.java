package fi.writemathgwt.client;

import java.util.Vector;

/**
 * Formula line
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleRegel extends FormuleElement
{
	protected Vector<FormuleElement> children = new Vector<FormuleElement>();
	
	private int nextx = 0;
	private int nexty = 0;

	private int defaultwidth = 0;
	private int defaultheight = 0;

	private int currentPosition = -1;

	private int selectionDragStart = -1;
	private int selectionStart = -1;
	private int selectionStartx = 0;

	private boolean editable = true;

	//how many children are numbers?
	private int nonNumberChildern = 0;

	private boolean smalltext = false;

	private int[] selectioncords =
	{ 0, 0, 0, 0 };
	
	private boolean stippels = false;

	public FormuleRegel(FormuleElement holder)
	{
		super(holder);
		init();
	}

	private void init()
	{
	}

	private void addElement(FormuleElement e)
	{
		this.children.add(++this.currentPosition, e);

		if (e.isNumber() == false)
			addNonNumberChild();
		//this should be done in the new child, but just to make sure the changed value is set to true.
		this.setChanged(true);
	}

	public void setSmallText(boolean val)
	{
		if (smalltext == val)
			return;
		smalltext = val;
	}

	private void addNonNumberChild()
	{
		this.nonNumberChildern++;
	}

	private void removeNonNumberChild()
	{
		this.nonNumberChildern--;
	}
	
	public void convertToWriteObject()
	{
//System.out.println("FR convertToWriteObject start");		

	for (int i = 0; i < children.size(); i++)
		{	FormuleElement child = (FormuleElement) children.elementAt(i);
			child.convertToWriteObject();
		}
		
//System.out.println("FR convertToWriteObject end");		
	}
	
	public void findSize(int leafWidth, int leafHeight)
	{	
		
		if (children.size() == 0)
			return;
		
		for (int i = 0; i < children.size(); i++)
		{	FormuleElement child = (FormuleElement) children.elementAt(i);
			if ((child.getWidth() == 0) || (child.getHeight() == 0))
				child.findSize(leafWidth, leafHeight);  
		}
		width = Samples20.charHSpace;
	
		setAscent();
/*		
		if (children.size() == 1)
		{	FormuleElement child0 = (FormuleElement) children.elementAt(0);
			height = child0.getHeight();
			if (parent instanceof Machtvak)
			{	width += child0.getWidth() + Samples20.charHSpaceSmall;
			}
			else
			{	width += child0.getWidth() + Samples20.charHSpace;
			}
		}	
		else
		{
*/			
		
			for (int i = 0; i < children.size(); i++)
			{	FormuleElement child = (FormuleElement) children.elementAt(i);
				if (parent instanceof Machtvak)
				{	width += child.getWidth() + Samples20.charHSpaceSmall;
//System.out.println("findSize macht child");			
				}
				else
				{	width += child.getWidth() + Samples20.charHSpace;
//System.out.println("findSize non macht child");			
				}
//System.out.println("findSize non macht child");			
				//height = Math.max(height, child.getHeight() + child.ascent);
				height = Math.max(height, child.getHeight() + ascent - child.ascent);
//System.out.println("child height " + child.getHeight());
//System.out.println("child ascent " + child.ascent);

			}
		//}	
		
		for (int i = 0; i < children.size(); i++)
		{	FormuleElement child = (FormuleElement) children.elementAt(i);
			if (i >= 1)
			{	FormuleElement previousChild = (FormuleElement) children.elementAt(i-1);
				if ((previousChild instanceof FormuleTeken) && (((FormuleTeken) previousChild).character == '(') &&
					(child instanceof Breukvak))
				{	previousChild.ascent = child.ascent; 
					previousChild.height = child.height;
				}
			}
			if (i < children.size()-1)
			{	FormuleElement nextChild = (FormuleElement) children.elementAt(i+1);
				if ((nextChild instanceof FormuleTeken) && (((FormuleTeken) nextChild).character == ')') &&
					(child instanceof Breukvak))
				{	nextChild.ascent = child.ascent; 
					nextChild.height = child.height;
				}
			}
		
		}
		
		
		//setAscent();
		
		
	
//System.out.println("FR findSizes w = " + width + " h = " + height);		
	}
	
	public void setPosition(int xPos, int yPos)
	{	x = xPos;
		y = yPos;
		
//System.out.println("FR setPosition x = " + x + " y = " + y);		
		
		int childX = xPos + Samples20.charHSpace;
		for (int i = 0; i < children.size(); i++)
		{	FormuleElement child = (FormuleElement) children.elementAt(i);
			child.setPosition(childX, yPos);
			if (child instanceof Machtvak)
				childX += child.getWidth() + Samples20.charHSpaceSmall;
			else	
				childX += child.getWidth() + Samples20.charHSpace;;
		}
		
	}	
	
	public void setAscent()
	{
		for (int i = 0; i < children.size(); i++)
		{	FormuleElement child = (FormuleElement) children.elementAt(i);
			ascent = Math.max(ascent, child.ascent); 
		}
		
	}
	
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
		
		//int paintabove = fm.getAscent();
		//int paintbelow = height - fm.getAscent();
	
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

			//if (paintabove < paintabove_e)
			//	paintabove = paintabove_e;
			//if (paintbelow < paintbelow_e)
			//	paintbelow = paintbelow_e;

			//eldrawheight = paintabove + paintbelow;//(e.height / 2 - e.getAsHoogte()) * 2;

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
		
		//this.setAsHoogte(paintabove);

		int elx, ely;
		FormuleElement e;
		for (int i = 0; i < this.children.size(); i++)
		{
			e = this.children.get(i);
			elx = e.getX();
		}
		
		
		int x = this.width;
		if (this.currentPosition == -1 || this.children.size() == 0)
			x = 0;

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

	public int getIndexOf(FormuleElement e)
	{	int index = -1;
		for (int i = 0; i < children.size(); i++)
		{	FormuleElement fe = (FormuleElement) children.elementAt(i);
			if (fe == e)
				index = i;
		}
	
		return index;
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

	public FormuleTeken insertFormuleTekenAt(int index, char tk)
	{
		FormuleTeken ftk = new FormuleTeken(this, tk);
		children.insertElementAt(ftk, index);
		
		return ftk;
	}
	
	
	/*
	 * Converting from string to ui
	 */
	public FormuleElement insert(FormuleElement fe)
	{
		//this method handles things like string "pi" => pi character

		//add to the current formuleregel
		addElement(fe);

		//simple ignore this method if the element is not a character
		if (fe instanceof FormuleTeken == false)
			return fe;
else
{
//System.out.println("FR insert " + ((FormuleTeken) fe).geefChar());
 return fe;
}
		
/*		
		int caretPos = getCurrentPosition();
		int nr = caretPos;
		FormuleTeken ft1, ft2, ft3;
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
			FormuleTeken ft = new FormuleTeken(this, '\u03C0');
			addElement(ft);
			return ft;
		}
		if (ft2.geefChar() == '>' && ft3.geefChar() == '=')
		{
			FormuleTeken ft = new FormuleTeken(this, '\u2265');
			addElement(ft);
			return ft;
		}
		if (ft2.geefChar() == '<' && ft3.geefChar() == '=')
		{
			FormuleTeken ft = new FormuleTeken(this, '\u2264');
			addElement(ft);
			return ft;
		}
		if (ft2.geefChar() == '^' && Character.isDigit(ft3.geefChar()))
		{
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
*/		
	}

/*	
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


	public String getSelectionString()
	{
		String s = "";
		
		if(this.selectionStart <= this.currentPosition)
		{	int start = this.selectionStart;
			if (start < 0)
			start = 0;
			for (int i = start; i <= this.currentPosition; i++)
				s += ((FormuleElement) this.children.get(i)).toString();
		}
		else
		{	int start = currentPosition + 1;
			for(int i = start; i <= this.selectionStart; i++)
				s += ((FormuleElement) this.children.get(i)).toString();
		}
		return s;
	}
	
	public int getSelectionStart()
	{
		return Math.min(selectionStart, currentPosition);
	}
	
	public int getSelectionEnd()
	{
		return Math.max(selectionStart, currentPosition);
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
	
	public void zetStippels(boolean b)
	{
		stippels = b;
		this.setChanged(true);
	}


}
