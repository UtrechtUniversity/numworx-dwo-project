package fi.wiskopdr.expressies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Vermenigvuldiging extends Expressie
{

	public Vermenigvuldiging(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{
		if(kind1!=null && kind2!=null)
		{
			return new Optelling(new Vermenigvuldiging(kind1.geefDiff(basisExp),kind2),new Vermenigvuldiging(kind2.geefDiff(basisExp),kind1));
		}
		return null;	
	}

	public double geefWaarde()
	{
		double waarde;
		if (Algebra.isVector(kind1) && Algebra.isVector(kind2))
			waarde = geefInproduct().geefWaarde();
		else
			waarde = kind1.geefWaarde() * kind2.geefWaarde();
			
		return waarde;
	}

	public Complex geefWaardeComplex()
	{
		Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if (c1 == null || c2 == null)
			return null;
		return Complex.times(c1, c2);
	}

	public double geefWaarde(double subst)
	{
		return kind1.geefWaarde(subst) * kind2.geefWaarde(subst);
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		return Complex.times(kind1.geefWaardeComplex(subst), kind2.geefWaardeComplex(subst));
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		return kind1.geefWaarde(subst, vars) * kind2.geefWaarde(subst, vars);
	}

	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{
		return Complex.times(kind1.geefWaardeComplex(subst, vars), kind2.geefWaardeComplex(subst, vars));
	}

	public Expressie substitueer(double subst, String var)
	{
		return new Vermenigvuldiging(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new Vermenigvuldiging(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new Vermenigvuldiging(kind1.vervangDifferentialen(var), kind2.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new Vermenigvuldiging(kind1.vervangDiffs(subst, var), kind2.vervangDiffs(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}

	public String geefVarNaam()
	{
		String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		if (s1 != null && s2 != null && (s1.equals("") || s2.equals("")))
			return "";
		else if (s1 != null && s2 != null && !s1.equals(s2))
			return "";
		else if (s1 != null && s2 != null && s1.equals(s2))
			return s1;
		else if (s1 != null && s2 == null)
			return s1;
		else if (s1 == null && s2 != null)
			return s2;
		else
			return null;
	}

	public String toString()
	{
		String s1 = kind1.toString();
		String s2 = kind2.toString();
		String op = "";

		Vector v1 = Algebra.geefFactorenBeperkt(kind1, new Vector());
		Vector v2 = Algebra.geefFactorenBeperkt(kind2, new Vector());
		//Expressie g1 = ((Expressie)v1.elementAt(v1.size()-1));
		Expressie g2 = null;
		if (v2.size() > 0)
			g2 = ((Expressie) v2.elementAt(0));
		if (g2 instanceof BasisExpressie && !Double.isNaN(g2.geefWaarde()) && !(g2 instanceof PI) && !(g2 instanceof E) || g2 instanceof Macht && !Double.isNaN(g2.kind1.geefWaarde()) || Algebra.isBreukPlusGetal(g2) || FormuleParser.isWoordFormule() || FormuleParser.isTweeHoofdletterVariabele())
		{
			op = "*";
		}

		//if(kind1.geefWaarde()==1)return s2;
		if (kind1.isVeelterm)
			s1 = "$h" + s1 + "@";
		if (kind2.isVeelterm)
			s2 = "$h" + s2 + "@";

		return s1 + op + s2;
		//return s1 + "*" + s2;
	}

	public String toStringStrikt()
	{
		String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		if (kind1.isVeelterm)
			s1 = "$h" + s1 + "@";
		if (kind2.isVeelterm)
			s2 = "$h" + s2 + "@";
		return "$v" + s1 + "$n" + s2 + "@@";
	}

    public Object visit(AbstractConverter converter)
    {
    	return converter.vermenigvuldiging(this);
    }
    
	/**
	 * Geef het inproduct van de twee vectorkinderen, d.w.z. de som van
	 * kindsgewijs vermenigvuldigen.
	 * 
	 * @return
	 */
	private Expressie geefInproduct()
	{
		Expressie inproduct = null;
		
		if ((Algebra.geefVectorDimensie(kind1)[0] != -1)
			&& (Arrays.equals(Algebra.geefVectorDimensie(kind1), Algebra.geefVectorDimensie(kind2))))
		{
			inproduct = kind1.geefVector().geefInproduct(kind2.geefVector());
		}
		
		return inproduct;
	}

	/**
	 * Als de vermenigvuldiging als uitkomst een matrix heeft, geef deze matrix.
	 * 
	 * @return
	 */
	public Matrix geefMatrix()
	{
		Matrix matrix = null;
		ArrayList<ArrayList<Expressie>> matrixKinderen1 = null;
		ArrayList<ArrayList<Expressie>> matrixKinderen2 = null;
		Expressie scalar = null;
		
		if (kind1 instanceof Matrix && kind2 instanceof Matrix) // matrix * matrix
		{
			ArrayList<ArrayList<Expressie>> list = new ArrayList<ArrayList<Expressie>>();
			Matrix matrix1 = (Matrix) kind1;
			Matrix matrix2 = (Matrix) kind2;
			matrixKinderen1 = matrix1.geefKinderen();
			matrixKinderen2 = matrix2.geefKinderen();
			
			// init list
			for (int i = 0; i < matrix1.geefAantalRijen(); i++)
			{
				list.add(new ArrayList<Expressie>());
			}

			if (isMatrixVermenigvuldigingMogelijk(matrix1, matrix2)) // goede dimensies
			{
				Expressie som = null;
			
				for (int k = 0; k < matrix2.geefAantalKolommen(); k++) // kolommen matrix2
				{
					for (int i = 0; i < matrix1.geefAantalRijen(); i++) // rijen matrix1
					{
						for (int j = 0; j < matrix1.geefAantalKolommen(); j++) // kolommen matrix1
						{
							if (j == 0)
								som = new Vermenigvuldiging(matrixKinderen1.get(i).get(j),
									matrixKinderen2.get(j).get(k));
							else
								som = new Optelling(som, new Vermenigvuldiging(matrixKinderen1.get(i).get(j),
									matrixKinderen2.get(j).get(k)));
						}

						list.get(i).add(k, som);
					}
				}

				// maak de matrix
				matrix = new Matrix(list);
			}
		}
		else
		{
			// matrix * scalar
			if (kind1 instanceof Matrix && !(kind2 instanceof Matrix))
			{
				matrixKinderen1 = ((Matrix) kind1).geefKinderen();
				scalar = kind2;
			}
			else if (kind2 instanceof Matrix && !(kind1 instanceof Matrix))
			{
				scalar = kind1;
				matrixKinderen1 = ((Matrix) kind2).geefKinderen();
			}
			
			ArrayList<ArrayList<Expressie>> list = new ArrayList<ArrayList<Expressie>>();
			
			for (int i = 0; i < matrixKinderen1.size(); i++) // rijen
			{
				ArrayList<Expressie> rij = new ArrayList<Expressie>();
				
				for (int j = 0; j < matrixKinderen1.get(0).size(); j++) // kolommen
				{
					// vermenigvuldig alle kinderen met de scalar
					Expressie kind = new Vermenigvuldiging(scalar, matrixKinderen1.get(i).get(j));
					rij.add(kind);				
				}
				list.add(rij);
			}
			
			matrix = new Matrix(list);
		}
		
		return matrix;
	}
	
	private boolean isMatrixVermenigvuldigingMogelijk(Matrix matrix1, Matrix matrix2)
	{
		boolean isMogelijk = false;
		
		if ((matrix1.geefAantalKolommen() == matrix2.geefAantalRijen())
			&&(matrix1.geefAantalRijen() == matrix2.geefAantalKolommen()))
			isMogelijk = true;

		return isMogelijk;
	}

	/**
	 * Als de vermenigvuldiging als uitkomst een vector heeft, geef deze vector.
	 * 
	 * @return
	 */
	public VectorExpr geefVector()
	{
		VectorExpr vector = null;

		if (isMatrixVectorVermenigvuldiging())
		{
			Matrix matrix = (Matrix) kind1;
			ArrayList<ArrayList<Expressie>> matrixKinderen = matrix.geefKinderen();
			ArrayList<Expressie> vectorKinderen = ((VectorExpr) kind2).geefKinderen();

			ArrayList<Expressie> list = new ArrayList<Expressie>();
			Expressie som = null;
			
			for (int i = 0; i < matrix.geefAantalRijen(); i++)
			{
				for (int j = 0; j < matrix.geefAantalKolommen(); j++)
				{
					if (j == 0)
						som = new Vermenigvuldiging(matrixKinderen.get(i).get(j),
							vectorKinderen.get(j));
					else
						som = new Optelling(som, new Vermenigvuldiging(matrixKinderen.get(i).get(j),
							vectorKinderen.get(j)));
				}
				
				list.add(som);
			}
			
			vector = new VectorExpr(list);
		}
		else if (isVectorScalarVermenigvuldiging())
		{
			ArrayList<Expressie> vectorKinderen = null;
			Expressie scalar = null;

			// vector * scalar
			if (kind1 instanceof VectorExpr)
			{
				vectorKinderen = ((VectorExpr) kind1).geefKinderen();
				scalar = kind2;
			}
			else if (kind2 instanceof VectorExpr)
			{
				scalar = kind1;
				vectorKinderen = ((VectorExpr) kind2).geefKinderen();
			}

			ArrayList<Expressie> list = new ArrayList<Expressie>();
			
			for (int i = 0; i < vectorKinderen.size(); i++)
			{
				// vermenigvuldig alle kinderen met de scalar
				Expressie kind = new Vermenigvuldiging(scalar, vectorKinderen.get(i));
				list.add(kind);
			}
			
			vector = new VectorExpr(list);
		}
		
		return vector;
	}

	/**
	 * Retourneert true als de vermenigvuldiging 
	 * matrix maal vector is (en niet vector maal matrix).
	 * 
	 * @return
	 */
	private boolean isMatrixVectorVermenigvuldiging()
	{
		boolean b = false;
		
		if (kind1 instanceof Matrix && kind2 instanceof VectorExpr) // vector maal matrix is niet toegestaan
		{
			b = true;
		}
		
		return b;
	}

	/**
	 * Retourneert true als de vermenigvuldiging 
	 * vector maal scalar is.
	 * 
	 * @return
	 */
	private boolean isVectorScalarVermenigvuldiging()
	{
		boolean b = false;
		
		if ((kind1 instanceof VectorExpr && !(kind2 instanceof VectorExpr))
			|| (kind2 instanceof VectorExpr && !(kind1 instanceof VectorExpr)))
		{
			b = true;
		}
		
		return b;
	}


}
