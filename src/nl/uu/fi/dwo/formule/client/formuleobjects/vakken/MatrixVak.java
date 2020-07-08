package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Iterator;
import java.util.Vector;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class MatrixVak extends FormuleElementWithChildren
{
	int aantalRijen;
	int aantalKolommen;

	public MatrixVak(FormuleElement holder)
	{
		// default 2x2
		super(holder, 4);
		this.setChanged(true);

		this.aantalKolommen = 2;
		this.aantalRijen = 2;
	}

	public MatrixVak(FormuleElement holder, int aantalRijen, int aantalKolommen)
	{
		super(holder, aantalRijen * aantalKolommen);
		this.setChanged(true);

		this.aantalRijen = aantalRijen;
		this.aantalKolommen = aantalKolommen;
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx)
	{
		super.paintComponent(ctx);
		build(new CanvasBuilder(ctx));		
	}

	public void paintObject()
	{
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{			
				getMatrixChild(i, j).paint();
			}
		}

		zetMaat();
		paintComponent(ctx);
		
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{			
				getMatrixChild(i, j).draw(ctx);
			}
		}
		
		this.drawCursor();
	}
	
	@Override
	protected void paintComponent(OMSVGElement svg)
	{
		super.paintComponent(svg);
		SvgBuilder builder = new SvgBuilder(svg, x, y);
		build(builder);
	}

	@Override
	public void draw(OMSVGElement svg)
	{
		paintComponent(svg);
		OMSVGGElement g = new OMSVGGElement();
		svg.appendChild(g);
		if (x != 0 || y != 0)
		{
			OMSVGTransform transform = getSVGSVGElement(svg).createSVGTransform();
			transform.setTranslate(x, y);
			g.getTransform().getBaseVal().appendItem(transform);
		}
		
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{			
				getMatrixChild(i, j).draw(g);
			}
		}
		
		drawCursor(svg);
	}

	/**
	 * In de children van een matrixvak zitten de matrix-kinderen op een rij
	 * opgeslagen, van boven naar beneden, van links naar rechts.
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private FormuleRegel getMatrixChild(int rowIndex, int columnIndex)
	{
		FormuleRegel kind = null;
		
		if (rowIndex > -1 && columnIndex > -1)
			kind = getChild(rowIndex * aantalKolommen + columnIndex);
		
		return kind;
	}
	
	protected void build(PathBuilder ctx)
	{
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());
		
//		ctx.beginPath();
//
//		// haak ervoor
//		ctx.arc(10, 6, 5, Math.PI, 1.5 * Math.PI, false); // eerste bochtje
//		ctx.moveTo(5, 6);
//		ctx.lineTo(5, height - 6); // 1 lange lijn
//		ctx.arc(10, height - 7, 5, Math.PI, Math.PI / 2, true); // laatste bochtje 
//		
//		ctx.stroke();
//		ctx.beginPath();
//
//		// haak erna
//		ctx.arc(width - 7, 6, 5, 0, 1.5 * Math.PI, true); // eerste bochtje
//		ctx.moveTo(width - 2, 6);
//		ctx.lineTo(width - 2, height - 6); // 1 lange lijn
//		ctx.arc(width - 7, height - 7, 5, 0, Math.PI / 2, false); // laatste bochtje 
//
//		ctx.stroke();
		
		int h = 4 * fm.getAscent() / 3;
		int b = h / 4;
		int c = fm.getAscent() / 6;
		int d = fm.getAscent() / 8;
		
		float bx = (float)(b*Math.sqrt(2)/(Math.sqrt(2)-1));
		float by = (float)(b/(Math.sqrt(2)-1));
		ctx.beginPath();
		ctx.arc(c+bx+3, d+by+2, bx, Math.PI, 5*Math.PI/4, false);
		ctx.moveTo(c+3, d + by+2);
		ctx.lineTo(c+3, height-d-by);
		ctx.arc(c+bx+3, height-d-by, bx, 3*Math.PI/4, Math.PI, false);
		
		ctx.arc(width-c-bx, d+by+2, bx, 0, -Math.PI/4, true);
		ctx.moveTo(width-c, d+2 + by);
		ctx.lineTo(width-c, height-d-by);
		ctx.arc(width-c-bx, height-d-by, bx,  0, Math.PI/4,false);
		ctx.stroke();
	}

	private void maakMaat()
	{
		super.zetMaat();

		height = 5;
		width = 10;
		int[] maxKolomBreedte = new int[aantalKolommen];
		int[] maxRijAshoogte = new int[aantalRijen];
		int[] maxRijHoogte = new int[aantalRijen];
		int rijBreedte = 0;
		
		for (int i = 0; i < aantalRijen; i++) // rijen
		{
			int rijHoogte = 0;
			
			for (int j = 0; j < aantalKolommen; j++) // kolommen
			{
				rijHoogte = Math.max(rijHoogte, getMatrixChild(i, j).height + 5);
				maxRijAshoogte[i] = Math.max(maxRijAshoogte[i], getMatrixChild(i, j).getAsHoogte());
				maxRijHoogte[i] = Math.max(maxRijHoogte[i], getMatrixChild(i, j).height);
				maxKolomBreedte[j] = Math.max(maxKolomBreedte[j], getMatrixChild(i, j).width + 10);
			}

			height += rijHoogte;
//			System.out.println("MatrixVak.maakMaat(): i = " + i + ", rijBreedte = " + rijBreedte + ", width = " + width);
		}

		for (int j = 0; j < aantalKolommen; j++) // kolommen
		{
			rijBreedte += maxKolomBreedte[j];
		}

//		System.out.println("MatrixVak.maakMaat(): rijBreedte = " + rijBreedte);
		width = rijBreedte;

		int[] xPosities = new int[aantalKolommen];
		for (int i = 0; i < aantalKolommen; i++)
		{
			if (i == 0)
				xPosities[i] = 10;
			else
				xPosities[i] = xPosities[i - 1] + maxKolomBreedte[i - 1]; 
		}
		
		// extra breedte voor afsluitende haak
		width = width + 15;

		setSize(width, height);
//		System.out.println("MatrixVak.maakMaat(): na setSize(width, height): width = " + width + ", getSize().width = " + getSize().width);

		setAsHoogte(height / 2 + fm.getAscent()/2 - fm.getDescent()/2);
		//System.out.println("MatrixVak.maakMaat(): setSize(" + width + ", " + height + "), ashoogte = " + getAsHoogte());
		
		int kindY = 5;
		for (int i = 0; i < aantalRijen; i++) // rijen
		{
			for (int j = 0; j < aantalKolommen; j++) // kolommen
			{
				int x = (int) (xPosities[j] + 0.5 * maxKolomBreedte[j] - 0.5 * getMatrixChild(i, j).width);
				int y = kindY + (maxRijAshoogte[i] - getMatrixChild(i, j).getAsHoogte());
				
				getMatrixChild(i, j).setPosition(x, y);
				//getMatrixChild(i, j).setAsHoogte(maxRijAshoogte[i]); // wat helpt dit?
			}

			kindY += maxRijHoogte[i] + 5;
		}
	}

	/**
	 * Geeft de rij- en kolomindex van het kind met focus.
	 * @return
	 */
	public int[] bepaalKindMetFocus()
	{
		int[] kindMetFocus = {0, 0};
		
		for (int i = 0; i < aantalRijen; i++) // rijen
		{
			for (int j = 0; j < aantalKolommen; j++) // kolommen
			{
//				if (getMatrixChild(i, j).hasFocus())
//				{
//					kindMetFocus[0] = i;
//					kindMetFocus[1] = j;
//					break;
//				}
			}
		}
		return kindMetFocus;
	}

	public void focusKindOmhoog()
	{
		int[] kindMetFocus = bepaalKindMetFocus();
//		if (kindMetFocus[0] > 0)
//			matrixChildren.get(kindMetFocus[0] - 1).get(kindMetFocus[1]).neemFocus("rechts");
	}

	public void focusKindOmlaag()
	{
		int[] kindMetFocus = bepaalKindMetFocus();
//		if (kindMetFocus[0] == children.size() - 1) // onderste rij
//		{
//			maakNieuweRij();
//		}
//		matrixChildren.get(kindMetFocus[0] + 1).get(kindMetFocus[1]).neemFocus("rechts");
	}

	public void focusKindLinks()
	{
		int[] kindMetFocus = bepaalKindMetFocus();
//		if (kindMetFocus[1] > 0)
//			matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] - 1).neemFocus("rechts");
	}

	public void focusKindRechts()
	{
		int[] kindMetFocus = bepaalKindMetFocus();
//		if (kindMetFocus[1] == ((MatrixRij) children.get(kindMetFocus[0])).getAantalKolommen() - 1) // laatste kolom
//		{
//			maakNieuweKolom();
//			zetMaat();
//			paint();
//		}
//		matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] + 1).neemFocus("rechts");
	}

//	/**
//	 * Maak default 3x3 matrix.
//	 */
//	public void maakDefaultMatrix()
//	{
//		aantalKolommen = 3;
//		
//		for (int rij = 0; rij < 3; rij++)
//		{
//			maakNieuweRij();
//		}
//	}

//	/**
//	 * I.p.v. maakNieuwKind()
//	 */
//	public void maakNieuweRij(int rijNummer)
//	{
////		Vector<FormuleRegel> rij = new Vector<FormuleRegel>();
//		MatrixRij rij = null;
//		
//		for (int kolom = 0; kolom < aantalKolommen; kolom++)
//		{
//			FormuleRegel kind = new FormuleRegel(this);
//			kind.setFont(getFont());
//			
//			if (kolom == 0)
//				rij = new MatrixRij(kind);
//			else
//				rij.add(kind);
////			rij.add(kind);
//		}
////		matrixChildren.add(rij);
//		children.add(rij);
//		aantalRijen++;
//
//		zetMaat();
//		paint();
//	}
	
	/**
	 * Gebruikt door vulVak(). Maar een nieuwe rij met de gegeven string.
	 * 
	 * @param s
	 */
	private void maakNieuweRij(String s)
	{
//		Vector<FormuleRegel> rij = new Vector<FormuleRegel>();
//		MatrixRij rij = null;
		int aantalInRij = 0;
		
		while (s.length() > 0)
		{
			char ch0 = s.charAt(0);
			if (ch0 == '@')
			{
				break;
			}
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
				if (ch1 == 'k')
				{
					FormuleRegel kind = new FormuleRegel(this);
					kind.setFont(getFont());
					kind.insert(s.substring(2, eind));
					children.add(kind);
					aantalInRij++;
					s = s.substring(eind);
				}
			}
		}
		
		aantalRijen++;
		aantalKolommen = aantalInRij;
	}

//	/**
//	 * I.p.v. maakNieuwKind()
//	 */
//	public void maakNieuweKolom()
//	{
//		for (int rij = 0; rij < aantalRijen; rij++)
//		{
//			FormuleRegel kind = new FormuleRegel(this);
//			kind.setFont(getFont());
////			matrixChildren.get(rij).addElement(kind);
//			((MatrixRij) children.get(rij)).add(kind);
//		}
//		aantalKolommen++;
//		
//		zetMaat();
//		paint();
//	}

	/**
	 * Verwijder de rij die focus heeft en 
	 * verplaats de focus.
	 * 
	 */
	public void deleteRij()
	{
		int[] kindMetFocus = bepaalKindMetFocus();

		for (int j = 0; j < aantalKolommen; j++)
		{
			FormuleRegel kind = (FormuleRegel) getMatrixChild(kindMetFocus[0], j);
		}
		
		children.remove(kindMetFocus[0]);
		aantalRijen--;
		
		// verplaats focus naar boven
//		if (kindMetFocus[0] > 0)
//			matrixChildren.get(kindMetFocus[0] - 1).get(kindMetFocus[1]).neemFocus("rechts");
//		else
//			matrixChildren.get(0).get(kindMetFocus[1]).neemFocus("rechts");
		
		zetMaat();
		paint();
	}
	
	/**
	 * Verwijder de rij met de gegeven index en 
	 * verplaats de focus.
	 * 
	 * @param rijIndex
	 */
	public void deleteRij(int rijIndex)
	{
		int[] kindMetFocus = bepaalKindMetFocus();

		for (int j = 0; j < aantalKolommen; j++)
		{
			FormuleRegel kind = (FormuleRegel) getMatrixChild(rijIndex, j);
		}
		
		children.remove(rijIndex);
		
		aantalRijen--;
		
		// verplaats focus naar boven
//		if (kindMetFocus[0] > 0)
//			matrixChildren.get(kindMetFocus[0] - 1).get(kindMetFocus[1]).neemFocus("rechts");
//		else
//			matrixChildren.get(0).get(kindMetFocus[1]).neemFocus("rechts");
		
		zetMaat();
		paint();
	}
	
//	/**
//	 * Verwijder de kolom die focus heeft en 
//	 * verplaats de focus.
//	 *  
//	 */
//	public void deleteKolom()
//	{
//		int[] kindMetFocus = bepaalKindMetFocus();
//
//		for (int i = 0; i < aantalRijen; i++)
//		{
//			FormuleRegel kind = (FormuleRegel) getMatrixChild(i, kindMetFocus[1]);
//			((MatrixRij) children.get(i)).remove(kindMetFocus[1]);
//		}
//		
//		aantalKolommen--;
//		
//		// verplaats focus naar links
////		if (kindMetFocus[1] > 0)
////			matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] - 1).neemFocus("rechts");
////		else
////			matrixChildren.get(kindMetFocus[0]).get(0).neemFocus("rechts");
//		
//		zetMaat();
//		paint();
//	}
	
//	/**
//	 * Verwijder de kolom met de gegeven index en 
//	 * verplaats de focus.
//	 *  
//	 * @param kolomIndex
//	 */
//	public void deleteKolom(int kolomIndex)
//	{
//		int[] kindMetFocus = bepaalKindMetFocus();
//
//		for (int i = 0; i < aantalRijen; i++)
//		{
//			FormuleRegel kind = (FormuleRegel) getMatrixChild(i, kolomIndex);
//			((MatrixRij) children.get(i)).remove(kolomIndex);
//		}
//		
//		aantalKolommen--;
//		
//		// verplaats focus naar links
////		if (kindMetFocus[1] > 0)
////			matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] - 1).neemFocus("rechts");
////		else
////			matrixChildren.get(kindMetFocus[0]).get(0).neemFocus("rechts");
//		
//		zetMaat();
//		paint();
//	}
	
//	/**
//	 * Wordt aangeroepen door FormuleRegel.backspace(). 
//	 * Als focus in laatste kolom en alle kinderen in die 
//	 * kolom leeg, dan wordt de laatste kolom verwijderd.
//	 * Als focus in eerste kolom van laatste rij, dan
//	 * wordt de laatste rij verwijderd.  
//	 */
//	public void delete()
//	{
//		if (isFocusInLaatsteKolom() && isLegeKolom(aantalKolommen - 1))
//			deleteKolom(aantalKolommen - 1);
//		else if (isFocusInEersteKolomLaatsteRij() && isLegeRij(aantalRijen - 1))
//			deleteRij(aantalRijen - 1);
//	}

	/**
	 * Retourneert true als alle kinderen in de kolom met de gegeven index
	 * leeg zijn, anders false.
	 *  
	 * @param kolomIndex
	 * @return
	 */
	private boolean isLegeKolom(int kolomIndex)
	{
		boolean isLeeg = true;

		for (int i = 0; i < aantalRijen; i++) // rijen
		{
			FormuleRegel kind = getMatrixChild(i, kolomIndex);
			if (kind.toString().length() > 0)
			{
				isLeeg = false;
				break;
			}
		}
		
		return isLeeg;
	}

	/**
	 * Retourneert true als alle kinderen in de rij met de gegeven index
	 * leeg zijn, anders false.
	 *  
	 * @param rijIndex
	 * @return
	 */
	private boolean isLegeRij(int rijIndex)
	{
		boolean isLeeg = true;

		for (int i = 0; i < aantalKolommen; i++) // loop door de kolommen
		{
			FormuleRegel kind = getMatrixChild(rijIndex, i);
			if (kind.toString().length() > 0)
			{
				isLeeg = false;
				break;
			}
		}
		
		return isLeeg;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isFocusInLaatsteKolom()
	{
		boolean b = false;
		
		if (bepaalKindMetFocus()[1] == aantalKolommen - 1)
			b = true;
		
		return b;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isFocusInEersteKolomLaatsteRij()
	{
		boolean b = false;
		
		if (bepaalKindMetFocus()[1] == 0 && bepaalKindMetFocus()[0] == aantalRijen - 1)
			b = true;
		
		return b;
	}

	public void zetMaat()
	{
		maakMaat();
		if (getParent() instanceof FormuleElement)
			((FormuleElement) getParent()).zetMaat();
	}

	public void setEditable(boolean b)
	{
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{
				getMatrixChild(i, j).setEditable(b);
			}
		}
	}

	public void vulVak(String s)
	{
		// hier komt altijd een string in waarin de rijen zijn gescheiden
		// door $n en daarbinnen de kolommen door $k.
		
		// verwijder eerst de default kinderen
		removeKinderen();
		
		children = new Vector<FormuleRegel>();

		while (s.length() > 0)
		{
			char ch0 = s.charAt(0);
			if (ch0 == '@')
			{
				break;
			}
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
				if (ch1 == 'n')
				{
					maakNieuweRij(s.substring(2, eind));
					s = s.substring(eind);
				}
			}
		}
		
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{
				getMatrixChild(i, j).zetMaat();
			}
		}
	}

	/**
	 * Verwijder alle kinderen.
	 */
	private void removeKinderen()
	{
		if (children != null)
		{
			children.removeAllElements();
//			Iterator i = children.iterator();
//			
//		    while (i.hasNext())
//		    {
//		    	FormuleRegel kind = (FormuleRegel) i.next();
//		    	children.remove(kind);
//		    }
		}
		
	    aantalRijen = 0;
	    aantalKolommen = 0;
	}

	public String toString()
	{
		String string = "$M";
		if (children.size() > 0)
		{
			for (int i = 0; i < aantalRijen; i++) // rijen
			{
				string = string + "$n"; // begin rij
				for (int j = 0; j < aantalKolommen; j++)
				{
					string = string + "$k" + getMatrixChild(i, j).toString() + "@";
				}
				string = string + "@"; // eind rij
			}
		}
		
		string = string + "@";

		//System.out.println("MatrixVak.toString(): " + string);
		
		return string;
	}
	
	public void neemFocus(String richting)
	{
//		if (matrixChildren != null && !matrixChildren.isEmpty())
//			matrixChildren.get(0).get(0).neemFocus(richting);
	}
	
	/* 
	 * Set font overriden zodat matrixkinderen het goede font krijgen.
	 * TODO matrixChildren niet Vector<Vector<FormuleRegel>, 
	 * maar Vector<MatrixRij> 
	 * waarbij MatrixRij extends FormuleRegel.
	 */
	@Override
	public boolean setFont(FormuleFont fm)
	{
		boolean b = super.setFont(fm);
		
		for (int i = 0; i < aantalRijen; i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{
				getMatrixChild(i, j).setFont(fm);
			}
		}

		return b;
	}

	public int getAantalKolommen()
	{
		return this.aantalKolommen;
	}
	
}
