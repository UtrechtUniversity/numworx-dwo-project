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
	 * Geef de determinant van de vierkant matrix.
	 * 
	 * @return
	 */
	public Expressie geefDeterminant()
	{
		Expressie expr = null;
		Expressie vermenigvuldiging = null;

		if (geefDimensie()[0] == geefDimensie()[1]) // matrix moet vierkant zijn
		{
			Matrix temporary;
			ArrayList<ArrayList<Expressie>> tempKinderen;
			expr = new BasisExpressie(0);

			if (geefDimensie()[0] == 1)
			{
				expr = kinderen.get(0).get(0);
			}
			else if (geefDimensie()[0] == 2)
			{
				expr = new Aftrekking(new Vermenigvuldiging(kinderen.get(0).get(0), kinderen.get(1).get(1)), 
					new Vermenigvuldiging(kinderen.get(0).get(1), kinderen.get(1).get(0)));
			}
			else
			{
				for (int i = 0; i < kinderen.get(0).size(); i++)
				{
					tempKinderen = initializeMatrixKinderen(kinderen.size() - 1, kinderen.get(0).size() - 1);
	
					for (int j = 1; j < kinderen.size(); j++)
					{
						for (int k = 0; k < kinderen.get(0).size(); k++)
						{
							if (k < i)
							{
								tempKinderen.get(j - 1).set(k, kinderen.get(j).get(k));
							}
							else if (k > i)
							{
								tempKinderen.get(j - 1).set(k - 1, kinderen.get(j).get(k));
							}
						}
					}
	
					temporary = new Matrix(tempKinderen);
					
					expr = new Optelling(expr, new Vermenigvuldiging(
						new Vermenigvuldiging(kinderen.get(0).get(i), new BasisExpressie(Math.pow(-1, (double) i))), temporary.geefDeterminant()));
				}
			}
		}
		
		return expr;
	}
	
	/**
	 * 
	 * @param aantalRijen
	 * @param aantalKolommen
	 * @return
	 */
	private ArrayList<ArrayList<Expressie>> initializeMatrixKinderen(int aantalRijen, int aantalKolommen)
	{
		ArrayList<ArrayList<Expressie>> list = new ArrayList<ArrayList<Expressie>>();
		
		for (int i = 0; i < aantalRijen; i++) // loop over de rijen
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int j = 0; j < aantalKolommen; j++) // loop over kolommen
			{
				Expressie expr = new Expressie();
				rij.add(expr);
			}
			
			list.add(rij);
		}
		
		return list;
	}

	/**
	 * Geef de getransponeerde matrix.
	 * 
	 * @return
	 */
	public Matrix geefGetransponeerde()
	{
		Matrix getransponeerde = null;
		
		ArrayList<ArrayList<Expressie>> newKinderen = new ArrayList<ArrayList<Expressie>>();

		for (int j = 0; j < geefDimensie()[1]; j++) // loop over kolommen
		{
			ArrayList<Expressie> rij = new ArrayList<Expressie>();
			for (int i = 0; i < kinderen.size(); i++) // loop over de rijen
			{
				// kolommen worden rijen
				Expressie kind = kinderen.get(i).get(j);
				rij.add(kind);
			}
			newKinderen.add(rij);
		}

		getransponeerde = new Matrix(newKinderen);
		
		return getransponeerde;
	}
	
	/**
	 * Geef de inverse matrix.
	 * 
	 * @return
	 */
	public Matrix geefInverse()
	{
		Matrix inverse = null;
		ArrayList<ArrayList<Expressie>> inverseKinderen = new ArrayList<ArrayList<Expressie>>();
		
		if (geefDimensie()[0] == geefDimensie()[1]) // matrix moet vierkant zijn
		{
			for (int i = 0; i < kinderen.size(); i++) // loop over de rijen
			{
				ArrayList<Expressie> rij = new ArrayList<Expressie>();
				for (int j = 0; j < geefDimensie()[1]; j++) // loop over kolommen
				{
					Expressie expr = new Vermenigvuldiging(new BasisExpressie(Math.pow(-1, i + j)), geefDeterminant(geefMinor(i, j)));
					rij.add(expr);
				}
				
				inverseKinderen.add(rij);
			}
			
			// adjugate and determinant
			Expressie det = new Deling(new BasisExpressie(1.0), geefDeterminant());
			for (int i = 0; i < inverseKinderen.size(); i++)
			{
				for (int j = 0; j <= i; j++)
				{
					Expressie temp = inverseKinderen.get(i).get(j);
					inverseKinderen.get(i).set(j, new Vermenigvuldiging(inverseKinderen.get(j).get(i), det));
					inverseKinderen.get(j).set(i, new Vermenigvuldiging(temp, det));
				}
			}
			
			inverse = new Matrix(inverseKinderen);
		}
		
		return inverse;
	}
	
	private Matrix geefMinor(int row, int column)
	{
		Matrix minor = null;
		ArrayList<ArrayList<Expressie>> minorKinderen = initializeMatrixKinderen(kinderen.size() - 1, kinderen.size() - 1);

		for (int i = 0; i < kinderen.size(); i++)
		{
			for (int j = 0; i != row && j < kinderen.get(i).size(); j++)
			{
				if (j != column)
				{
					int rowIndex = i < row ? i : i - 1;
					int columnIndex = j < column ? j : j - 1;
						
					minorKinderen.get(rowIndex).set(columnIndex, kinderen.get(i).get(j));
				}
			}
		}
		
		minor = new Matrix(minorKinderen);
		
		return minor;
	}

	/**
	 * Geef de determinant van de gegeven matrix.
	 * 
	 * @param matrix
	 * @return
	 */
	private Expressie geefDeterminant(Matrix matrix)
	{
		Expressie det = new BasisExpressie(0);
		
		if (matrix.geefKinderen().size() != matrix.geefKinderen().get(0).size())
			throw new IllegalStateException("invalid dimensions");

		if (matrix.geefKinderen().size() == 2)
		{
			det = new Aftrekking(
				new Vermenigvuldiging(matrix.geefKinderen().get(0).get(0), matrix.geefKinderen().get(1).get(1)), 
				new Vermenigvuldiging(matrix.geefKinderen().get(0).get(1), matrix.geefKinderen().get(1).get(0)));
		}
		else
		{
			for (int i = 0; i < matrix.geefKinderen().size(); i++)
			{
				Expressie exp = new Vermenigvuldiging(new BasisExpressie(Math.pow(-1, i)), matrix.geefKinderen().get(0).get(i));
				exp = new Vermenigvuldiging(exp, geefDeterminant(matrix.geefMinor(0, i)));
				det = new Optelling(det, exp);
			}
		}
		
		return det;
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
