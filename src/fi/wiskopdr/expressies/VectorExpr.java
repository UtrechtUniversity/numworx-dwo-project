package fi.wiskopdr.expressies;

import java.util.ArrayList;
import java.util.Vector;

import fi.wiskopdr.expressies.Matrix.BerekendeExpressie;
import fi.wiskopdr.expressies.repr.AbstractConverter;

/**
 * Vectorklasse met array van kinderen (expressies).
 * 
 * @author borku102
 *
 */
public class VectorExpr extends Expressie
{
	ArrayList<Expressie> kinderen;
	
	public VectorExpr(ArrayList<Expressie> list)
	{
		kinderen = new ArrayList<Expressie>(list);
	}

	/**
	 * Geef de kinderen van de vector.
	 * 
	 * @return
	 */
	public ArrayList<Expressie> geefKinderen()
	{
		return kinderen;
	}
	
	/**
	 * Geef de dimensie [rijen, kolommen], voor vector
	 * geldt dat het aantal kolommen 1 is.
	 */
	public int[] geefDimensie()
	{
		int[] dimensie = {0, 1};
		
		if (kinderen != null && !kinderen.isEmpty())
		{
			dimensie[0] = kinderen.size();
		}
		
		return dimensie;
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
	public Expressie geefGrootte()
	{
		Expressie expr = null;
				
		for (int i = 0; i < kinderen.size(); i++)
		{
			// tel alle kwadraten op
			if (i == 0)
				expr = new Macht(kinderen.get(i), new BasisExpressie(2));
			else
				expr = new Optelling(expr, kinderen.get(i));
		}
		
		// neem de wortel
		expr = new Wortel(expr);
		
		return expr;
	}

	public VectorExpr substitueer(double subst, String var)
	{
		ArrayList<Expressie> newList = new ArrayList<Expressie>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			Expressie kind = kinderen.get(i).substitueer(subst, var);
			newList.add(kind);
		}
		return new VectorExpr(newList);
	}

	public VectorExpr substitueer(Expressie subst, String var)
	{
		ArrayList<Expressie> newList = new ArrayList<Expressie>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			Expressie kind = kinderen.get(i).substitueer(subst, var);
			newList.add(kind);
		}
		return new VectorExpr(newList);
	}

	/**
	 * Maak een nieuwe vector waarbij in de kinderen de differentialen met de gegeven 
	 * variabele zijn vervangen. 
	 */
	public VectorExpr vervangDifferentialen(String var)
	{
		ArrayList<Expressie> newList = new ArrayList<Expressie>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			Expressie kind = kinderen.get(i).vervangDifferentialen(var);
			newList.add(kind);
		}
		return new VectorExpr(newList);
	}

	public VectorExpr vervangDiffs(Expressie subst, String var)
	{
		ArrayList<Expressie> newList = new ArrayList<Expressie>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			Expressie kind = kinderen.get(i).vervangDiffs(subst, var);
			newList.add(kind);
		}
		return new VectorExpr(newList);
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
			if (!kinderen.get(i).isWaarde())
			{
				isWaarde = false;
				break;
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
			if (!kinderen.get(i).isWaarde(subst))
			{
				isWaarde = false;
				break;
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
			if (i == 0)
			{
				varNaam = kinderen.get(i).geefVarNaam();
			}
			else if (varNaam == null)
			{
				varNaam = kinderen.get(i).geefVarNaam();
			}
			else if (varNaam != null && !varNaam.equals(kinderen.get(i).geefVarNaam()))
			{
				// als varnaam niet van alle kinderen gelijk is, dan retourneren we ""
				varNaam = "";
				break;
			}
		}
		
		return varNaam;
	}

	public String toString()
	{
		String string = "$Y";
		if (kinderen.size() > 0)
		{
			for (int i = 0; i < kinderen.size(); i++)
				string = string + "$n" + kinderen.get(i).toString() + "@";
		}

		return string + "@";
	}

	public String toStringStrikt()
	{
		String string = "$Y";
		if (kinderen.size() > 0)
		{
			for (int i = 0; i < kinderen.size(); i++)
				string = string + "$n" + kinderen.get(i).toStringStrikt() + "@";
		}

		return string + "@";
	}

	public Object visit(AbstractConverter converter)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			list.add(kinderen.get(i).visit(converter));
		}
		return converter.vectorExpr(list);
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
			Expressie kind = kinderen.get(i);
			Vector namenKind = Algebra.geefVarN(kind);
			namen.addAll(namenKind);
		}
		
		return namen;
	}

	/**
	 * Geef het inproduct van de vector met de gegeven vector, d.w.z. de som van
	 * kindsgewijs vermenigvuldigen.
	 * De dimensies van de vectoren moet gelijk zijn.
	 * 
	 * @param geefVector
	 * @return
	 */
	public Expressie geefInproduct(VectorExpr expr)
	{
		Expressie inproduct = null;
		ArrayList<Expressie> kinderen2 = expr.geefKinderen();
		Expressie term;

		for (int i = 0; i < kinderen.size(); i++)
		{
			term = new Vermenigvuldiging(kinderen.get(i), kinderen2.get(i));
			if (i == 0)
				inproduct = term;
			else
				inproduct = new Optelling(inproduct, term);
		}
		
		return inproduct;
	}
	
	public VectorExpr geefVector()
	{
		return this;
	}

	/**
	 * Retourneert true als ieder kind een variabele is.
	 * 
	 * @return
	 */
	public boolean isVariabelenVector()
	{
		boolean isVariabelenVector = true;
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			Expressie kind = kinderen.get(i);
			if (kind.isWaarde())
			{
				isVariabelenVector = false;
				break;
			}
		}
		
		return isVariabelenVector;
	}

	/**
	 * Geef de lengte (absolute waarde, norm) van de vector.
	 * @return
	 */
	public Expressie geefLengte()
	{
		Expressie som = null;
		Expressie lengte = null;
		Expressie term;

		for (int i = 0; i < kinderen.size(); i++)
		{
			term = new Macht(kinderen.get(i), new BasisExpressie(2));
			if (i == 0)
				som = term;
			else
				som = new Optelling(som, term);
		}
		
		lengte = new Wortel(som);
		return lengte;
	}

	/**
	 * Bereken de kinderen van de vector (als in rekenmachine).
	 * @param aantalDecRm 
	 * @return
	 */
	public BerekendeVectorExpr berekenVector(int aantalDecRm)
	{
		BerekendeVectorExpr berekendeVector = new BerekendeVectorExpr(null, false); // initialiseer
		VectorExpr vector;
		ArrayList<Expressie> list = new ArrayList<Expressie>();
		
		for (int i = 0; i < kinderen.size(); i++)
		{
			BerekendeExpressie berekendeExpressie = Matrix.berekenExpressie(kinderen.get(i), aantalDecRm);
			list.add(berekendeExpressie.getExpressie());
 
			if (berekendeExpressie.isAfgerond())
				berekendeVector.setIsAfgerond(true);
		}
		
		vector = new VectorExpr(list);
		berekendeVector.setVector(vector);
		
		return berekendeVector;
	}
	
	/**
	 * Klasse voor een al dan niet afgeronde berekende vector (als in rekenmachine).
	 * 
	 * @author borku102
	 *
	 */
	public class BerekendeVectorExpr
	{
		VectorExpr vector;
		boolean isAfgerond;
		
		BerekendeVectorExpr(VectorExpr vector, boolean isAfgerond)
		{
			this.vector = vector;
			this.isAfgerond = isAfgerond;
		}
		
		public VectorExpr getVector()
		{
			return vector;
		}
		
		public boolean isAfgerond()
		{
			return isAfgerond;
		}
		
		private void setVector(VectorExpr v)
		{
			this.vector = v;
		}
		
		private void setIsAfgerond(boolean b)
		{
			this.isAfgerond = b;
		}

		/* 
		 * Geef de matrix-string.
		 * 
		 */
		@Override
		public String toString()
		{
			return vector.toString();
		}
	}
}
