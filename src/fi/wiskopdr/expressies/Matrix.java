package fi.wiskopdr.expressies;

import java.util.ArrayList;
import java.util.Vector;

import fi.wiskopdr.expressies.repr.AbstractConverter;

/**
 * Matrixklasse met array van array van kinderen (expressies).
 * 
 * @author borku102
 *
 */
public class Matrix extends Expressie
{
	ArrayList<ArrayList<Expressie>> kinderen;
	
	public Matrix(ArrayList<ArrayList<Expressie>> list)
	{
		kinderen = new ArrayList<ArrayList<Expressie>>(list);
	}

	/**
	 * Geef de kinderen van de vector.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Expressie>> geefKinderen()
	{
		return kinderen;
	}
	
	public int[] geefDimensie()
	{
		int[] dimensie = {0, 0};
		
		if (kinderen != null && !kinderen.isEmpty())
		{
			dimensie[0] = kinderen.size();
			dimensie[1] = kinderen.get(0).size();
		}
		
		return dimensie;
	}
	
	public int geefAantalRijen()
	{
		int aantal = 0;
	
		if (kinderen != null && !kinderen.isEmpty())
		{
			aantal = kinderen.size();
		}

		return aantal;
	}
	
	public int geefAantalKolommen()
	{
		int aantal = 0;
	
		if (kinderen != null && !kinderen.isEmpty())
		{
			aantal = kinderen.get(0).size();
		}

		return aantal;
	}
	
	/**
	 * Een vector heeft geen waarde.
	 * 
	 * @return
	 */
	public double geefWaarde()
	{
		return Double.NaN;
	}
	
	/**
	 * Geef de waarde van de grootte (lengte, norm, in het Engels ‘magnitude’).
	 * 
	 * @return
	 */
	public Expressie geefDeterminant()
	{
		Expressie expr = null;
		Expressie vermenigvuldiging = null;
				
		for (int i = 0; i < kinderen.size(); i++) // rijen
		{
			// TODO loop over goede indices
			vermenigvuldiging = new Vermenigvuldiging(kinderen.get(0).get(0), kinderen.get(1).get(1));
			// tel alle kwadraten op
			if (i == 0)
				expr = vermenigvuldiging;
			else
				expr = new Optelling(expr, vermenigvuldiging);
		}
		
		// neem de wortel
		expr = new Wortel(expr);
		
		return expr;
	}

	public Matrix substitueer(double subst, String var)
	{
		ArrayList<ArrayList<Expressie>> newList = new ArrayList<ArrayList<Expressie>>();

		for (int i = 0; i < kinderen.size(); i++)
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int j = 0; j < geefDimensie()[1]; j++)
			{
				Expressie kind = kinderen.get(i).get(j).substitueer(subst, var);
				rij.add(kind);
			}
			newList.add(rij);
		}

		return new Matrix(newList);
	}

	public Matrix substitueer(Expressie subst, String var)
	{
		ArrayList<ArrayList<Expressie>> newList = new ArrayList<ArrayList<Expressie>>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int j = 0; j < geefDimensie()[1]; j++)
			{
				Expressie kind = kinderen.get(i).get(j).substitueer(subst, var);
				rij.add(kind);
			}
			newList.add(rij);
		}

		return new Matrix(newList);
	}

	/**
	 * Maak een nieuwe matrix waarbij in de kinderen de differentialen met de gegeven 
	 * variabele zijn vervangen. 
	 */
	public Matrix vervangDifferentialen(String var)
	{
		ArrayList<ArrayList<Expressie>> newList = new ArrayList<ArrayList<Expressie>>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int j = 0; j < geefDimensie()[1]; j++)
			{
				Expressie kind = kinderen.get(i).get(j).vervangDifferentialen(var);
				rij.add(kind);
			}
			newList.add(rij);
		}
		
		return new Matrix(newList);
	}

	public Matrix vervangDiffs(Expressie subst, String var)
	{
		ArrayList<ArrayList<Expressie>> newList = new ArrayList<ArrayList<Expressie>>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int j = 0; j < geefDimensie()[1]; j++)
			{
				Expressie kind = kinderen.get(i).get(j).vervangDiffs(subst, var);
				rij.add(kind);
			}
			newList.add(rij);
		}
		
		return new Matrix(newList);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isWaarde()
	{
		boolean isWaarde = true;
		for (int i = 0; i < kinderen.size(); i++)
		{
			for (int j = 0; j < kinderen.get(0).size(); j++)
			{
				if (!kinderen.get(i).get(j).isWaarde())
				{
					isWaarde = false;
					break;
				}
			}
		}
		return isWaarde;
	}

	/**
	 * 
	 * @param subst
	 * @return
	 */
	public boolean isWaarde(double subst)
	{
		boolean isWaarde = true;
		for (int i = 0; i < kinderen.size(); i++)
		{
			for (int j = 0; j < kinderen.get(0).size(); j++)
			{
				if (!kinderen.get(i).get(j).isWaarde(subst))
				{
					isWaarde = false;
					break;
				}
			}
		}
		return isWaarde;
	}

	/**
	 * Retourneert null als alle kinderen varnaam null hebben, 
	 * "" als varnaam niet hetzelfde voor alle kinderen, 
	 * varnaam als varnaam hetzelfde voor alle kinderen.
	 * @return
	 */
	public String geefVarNaam()
	{
		String varNaam = null;

		for (int i = 0; i < kinderen.size(); i++)
		{
			if ("".equals(varNaam))
				break;
			
			for (int j = 0; j < kinderen.get(0).size(); j++)
			{
				if (varNaam == null)
				{
					varNaam = kinderen.get(i).get(j).geefVarNaam();
				}
				else if (!varNaam.equals(kinderen.get(i).get(j).geefVarNaam()))
				{
					// als varnaam niet van alle kinderen gelijk is, dan retourneren we ""
					varNaam = "";
					break;
				}
			}
		}
		
		return varNaam;
	}

	public String toString()
	{
		String string = "$M";
		if (kinderen.size() > 0)
		{
			for (int i = 0; i < kinderen.size(); i++) // rijen
			{
				string = string + "$n"; // begin rij
				for (int j = 0; j < geefAantalKolommen(); j++)
				{
					string = string + "$k" + kinderen.get(i).get(j).toString() + "@";
				}
				string = string + "@"; // eind rij
			}
		}
		
		string = string + "@";

		//System.out.println("Matrix.toString(): " + string);

		return string;
	}

	public String toStringStrikt()
	{
		return "";
	}

	public Object visit(AbstractConverter converter)
	{
		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			for (int j = 0; j < kinderen.get(0).size(); j++)
			{
//				list.add(kinderen.get(i).get(j).visit(converter));
			}
		}
		return converter.matrix(list);
	}

	/**
	 * Geef de variabelenamen van alle kinderen.
	 * 
	 * @return
	 */
	public Vector geefVarNamen()
	{
		Vector namen = new Vector();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			for (int j = 0; j < kinderen.get(0).size(); j++)
			{
				Expressie kind = kinderen.get(i).get(j);
				Vector namenKind = Algebra.geefVarN(kind);
				namen.addAll(namenKind);
			}
		}
		
		return namen;
	}

	public Matrix geefMatrix()
	{
		return this;
	}
}
