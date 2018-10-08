package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

public class MatrixVak extends FormuleElementWithChildren
{
	Vector<Vector<FormuleRegel>> matrixChildren;
	int aantalRijen;
	int aantalKolommen;

	public MatrixVak(FormuleElement holder)
	{
		super(holder, 1);
		this.setChanged(true);
		
//		formuleVak = fv;
//		kinderen = new Vector<Vector<FormuleRegel>>();
//		setLayout(null);
//
//		super.setFont(fv.getFont());
//		fm = getFontMetrics(getFont());
//
//		// default 3x3 kinderen. In vulVak() worden deze weeggehaald.
//		maakDefaultMatrix();
//		maakMaat();
//		kind1 = kinderen.get(0).get(0); // kind 1 moet niet null zijn... consistent met andere vakken
//
//		setOpaque(false);
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
		for (int i = 0; i < getChildrenSize(); i++)
		{
			getChild(i).paint();
		}

		zetMaat();
		paintComponent(ctx);
		
		for (int i = 0; i < getChildrenSize(); i++)
		{
			getChild(i).draw(ctx);
		}
		
		this.drawCursor();
	}
	
	protected void build(PathBuilder ctx)
	{
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());
		
		ctx.beginPath();

		// haak ervoor
		ctx.arc(5, 1, 5, 90, 180, true); // eerste bochtje
//		g.drawArc(5, 1, 10, 10, 90, 90); // eerste bochtje
		ctx.moveTo(5, 6);
		ctx.lineTo(5, height - 6); // 1 lange lijn
//		g.drawLine(5, 6, 5, getSize().height - 6); // 1 lange lijn
		ctx.arc(5, height - 12, 5, 180, 270, true); // laatste bochtje 
//		g.drawArc(5, getSize().height - 12, 10, 10, 180, 90); // laatste bochtje 
		
		// haak erna
		ctx.arc(width - 12, 1, 5, 90, 180, false); // eerste bochtje
//		g.drawArc(getSize().width - 12, 1, 10, 10, 90, -90); // eerste bochtje
		ctx.moveTo(width - 2, 6);
		ctx.lineTo(width - 2, height - 6); // 1 lange lijn
//		g.drawLine(getSize().width - 2, 6, getSize().width - 2, getSize().height - 6); // 1 lange lijn
		ctx.arc(width - 12, height - 12, 5, 0, 90, false); // laatste bochtje 
//		g.drawArc(getSize().width - 12, getSize().height - 12, 10, 10, 0, -90); // laatste bochtje 

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
		
		for (int i = 0; i < matrixChildren.size(); i++) // rijen
		{
			int rijHoogte = 0;
			
			for (int j = 0; j < matrixChildren.get(i).size(); j++) // kolommen
			{
				rijHoogte = Math.max(rijHoogte, matrixChildren.get(i).get(j).height + 5);
				maxRijAshoogte[i] = Math.max(maxRijAshoogte[i], matrixChildren.get(i).get(j).getAsHoogte());
				maxRijHoogte[i] = Math.max(maxRijHoogte[i], matrixChildren.get(i).get(j).height);
				maxKolomBreedte[j] = Math.max(maxKolomBreedte[j], matrixChildren.get(i).get(j).width + 10);
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
		width = width + 10;

		setSize(width, height);
//		System.out.println("MatrixVak.maakMaat(): na setSize(width, height): width = " + width + ", getSize().width = " + getSize().width);

		setAsHoogte(height / 2 - fm.getDescent());
		
		int kindY = 5;
		for (int i = 0; i < matrixChildren.size(); i++) // rijen
		{
			for (int j = 0; j < matrixChildren.get(i).size(); j++) // kolommen
			{
				matrixChildren.get(i).get(j).setPosition(xPosities[j], kindY + (maxRijHoogte[i] - matrixChildren.get(i).get(j).height) / 2);
				matrixChildren.get(i).get(j).setAsHoogte(maxRijAshoogte[i]); // wat helpt dit?

//				System.out.println("MatrixVak.maakMaat(): (" + i + ", " + j + "), kind.width = " + kinderen.get(i).get(j).getSize().width + ", setLocation(" 
//					+ xPosities[j] + ", " + (kindY + (maxRijHoogte[i] - kinderen.get(i).get(j).getSize().height) / 2) + ")");
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
		
		for (int i = 0; i < matrixChildren.size(); i++) // rijen
		{
			for (int j = 0; j < matrixChildren.get(i).size(); j++) // kolommen
			{
//				if (matrixChildren.get(i).get(j).hasFocus())
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
		if (kindMetFocus[0] == matrixChildren.size() - 1) // onderste rij
		{
			maakNieuweRij();
		}
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
		if (kindMetFocus[1] == matrixChildren.get(kindMetFocus[0]).size() - 1) // laatste kolom
		{
			maakNieuweKolom();
			zetMaat();
			paint();
		}
//		matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] + 1).neemFocus("rechts");
	}

	/**
	 * Maak default 3x3 matrix.
	 */
	public void maakDefaultMatrix()
	{
		aantalKolommen = 3;
		
		for (int rij = 0; rij < 3; rij++)
		{
			maakNieuweRij();
		}
	}

	/**
	 * I.p.v. maakNieuwKind()
	 */
	public void maakNieuweRij()
	{
		Vector<FormuleRegel> rij = new Vector<FormuleRegel>();
		
		for (int kolom = 0; kolom < aantalKolommen; kolom++)
		{
			FormuleRegel kind = new FormuleRegel(this);
			kind.setFont(getFont());
			rij.add(kind);
		}
		matrixChildren.add(rij);
		aantalRijen++;

		zetMaat();
		paint();
	}
	
	/**
	 * Gebruikt door vulVak(). Maar een nieuwe rij met de gegeven string.
	 * 
	 * @param s
	 */
	private void maakNieuweRij(String s)
	{
		Vector<FormuleRegel> rij = new Vector<FormuleRegel>();
		
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
					rij.add(kind);
					s = s.substring(eind);
				}
			}
		}
		
		matrixChildren.add(rij);
		aantalRijen++;
		aantalKolommen = rij.size();
	}

	/**
	 * I.p.v. maakNieuwKind()
	 */
	public void maakNieuweKolom()
	{
		for (int rij = 0; rij < matrixChildren.size(); rij++)
		{
			FormuleRegel kind = new FormuleRegel(this);
			kind.setFont(getFont());
			matrixChildren.get(rij).addElement(kind);
		}
		aantalKolommen++;
		
		zetMaat();
		paint();
	}

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
			FormuleRegel kind = (FormuleRegel) matrixChildren.get(kindMetFocus[0]).get(j);
		}
		
		matrixChildren.remove(kindMetFocus[0]);
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
			FormuleRegel kind = (FormuleRegel) matrixChildren.get(rijIndex).get(j);
		}
		
		matrixChildren.remove(rijIndex);
		
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
	 * Verwijder de kolom die focus heeft en 
	 * verplaats de focus.
	 *  
	 */
	public void deleteKolom()
	{
		int[] kindMetFocus = bepaalKindMetFocus();

		for (int i = 0; i < aantalRijen; i++)
		{
			FormuleRegel kind = (FormuleRegel) matrixChildren.get(i).get(kindMetFocus[1]);
			matrixChildren.get(i).remove(kindMetFocus[1]);
		}
		
		aantalKolommen--;
		
		// verplaats focus naar links
//		if (kindMetFocus[1] > 0)
//			matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] - 1).neemFocus("rechts");
//		else
//			matrixChildren.get(kindMetFocus[0]).get(0).neemFocus("rechts");
		
		zetMaat();
		paint();
	}
	
	/**
	 * Verwijder de kolom met de gegeven index en 
	 * verplaats de focus.
	 *  
	 * @param kolomIndex
	 */
	public void deleteKolom(int kolomIndex)
	{
		int[] kindMetFocus = bepaalKindMetFocus();

		for (int i = 0; i < aantalRijen; i++)
		{
			FormuleRegel kind = (FormuleRegel) matrixChildren.get(i).get(kolomIndex);
			matrixChildren.get(i).remove(kolomIndex);
		}
		
		aantalKolommen--;
		
		// verplaats focus naar links
//		if (kindMetFocus[1] > 0)
//			matrixChildren.get(kindMetFocus[0]).get(kindMetFocus[1] - 1).neemFocus("rechts");
//		else
//			matrixChildren.get(kindMetFocus[0]).get(0).neemFocus("rechts");
		
		zetMaat();
		paint();
	}
	
	/**
	 * Wordt aangeroepen door FormuleRegel.backspace(). 
	 * Als focus in laatste kolom en alle kinderen in die 
	 * kolom leeg, dan wordt de laatste kolom verwijderd.
	 * Als focus in eerste kolom van laatste rij, dan
	 * wordt de laatste rij verwijderd.  
	 */
	public void delete()
	{
		if (isFocusInLaatsteKolom() && isLegeKolom(aantalKolommen - 1))
			deleteKolom(aantalKolommen - 1);
		else if (isFocusInEersteKolomLaatsteRij() && isLegeRij(aantalRijen - 1))
			deleteRij(aantalRijen - 1);
	}

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

		for (int i = 0; i < matrixChildren.size(); i++) // rijen
		{
			FormuleRegel kind = matrixChildren.get(i).get(kolomIndex);
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
			FormuleRegel kind = matrixChildren.get(rijIndex).get(i);
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
				matrixChildren.get(i).get(j).setEditable(b);
			}
		}
	}

	public void vulVak(String s)
	{
		// hier komt altijd een string in waarin de rijen zijn gescheiden
		// door $n en daarbinnen de kolommen door $k.
		
		// verwijder eerst de default kinderen
		removeKinderen();
		
		matrixChildren = new Vector<Vector<FormuleRegel>>();

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
		
		for (int i = 0; i < matrixChildren.size(); i++)
		{
			for (int j = 0; j < aantalKolommen; j++)
			{
				matrixChildren.get(i).get(j).zetMaat();
			}
		}
	}

	/**
	 * Verwijder alle kinderen.
	 */
	private void removeKinderen()
	{
		Iterator i = matrixChildren.iterator();
		
	    while (i.hasNext())
	    {
	    	Iterator i2 = ((Vector) i.next()).iterator();
	    	while (i2.hasNext())
	    	{
		    	FormuleRegel kind = (FormuleRegel) i2.next();
		    	((Vector) i.next()).remove(kind);
	    	}
	    	
	    	matrixChildren.remove(i.next());
	    }
	    
	    aantalRijen = 0;
	    aantalKolommen = 0;
	}

	public String toString()
	{
		String string = "$M";
		if (matrixChildren.size() > 0)
		{
			for (int i = 0; i < matrixChildren.size(); i++) // rijen
			{
				string = string + "$n"; // begin rij
				for (int j = 0; j < aantalKolommen; j++)
				{
					string = string + "$k" + matrixChildren.get(i).get(j).toString() + "@";
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
}
