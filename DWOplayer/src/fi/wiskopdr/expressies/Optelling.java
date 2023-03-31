package fi.wiskopdr.expressies;

import java.util.ArrayList;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Optelling extends Expressie  
{	
	
	public Optelling(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = true;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Optelling(kind1.geefDiff(basisExp), kind2.geefDiff(basisExp));
		}
		return null;	
	}
	
	public double geefWaarde()
	{	return kind1.geefWaarde()+kind2.geefWaarde();
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if(c1==null || c2==null) return null;
		return Complex.plus(c1,c2);
	}
		
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)+kind2.geefWaarde(subst);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.plus(kind1.geefWaardeComplex(subst),kind2.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)+kind2.geefWaarde(subst,vars);
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return Complex.plus(kind1.geefWaardeComplex(subst,vars),kind2.geefWaardeComplex(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Optelling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Optelling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new Optelling(kind1.vervangDifferentialen(var), kind2.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new Optelling(kind1.vervangDiffs(subst, var), kind2.vervangDiffs(subst, var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		if(s1!=null && s2!=null && (s1.equals("") || s2.equals("")))return "";
		else if(s1!=null && s2!=null && !s1.equals(s2))return "";
		else if(s1!=null && s2!=null && s1.equals(s2))return s1;
		else if(s1!=null && s2==null)return s1;
		else if(s1==null && s2!=null)return s2;
		else return null;
	}
	
	public String toString()
	{	if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return kind1.toString() + "+" + kind2.toString();
	}
	
	public String toStringStrikt()
	{	if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return "$o" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";
	}
    
    public Object visit(AbstractConverter converter) {
    	return converter.optelling(kind1.visit(converter), kind2.visit(converter));
    }
    
	/**
	 * Als de optelling als uitkomst een vector heeft,
	 * geef de dimensie van deze vector. Anders [-1, -1].
	 * 
	 */
	public int[] geefDimensie()
	{
		int[] dimensie = {-1, -1};
		
		int[] dimensieKind1 = Algebra.geefVectorDimensie(kind1);
		int[] dimensieKind2 = Algebra.geefVectorDimensie(kind2);
		
		if (dimensieKind1 == dimensieKind2)
			dimensie = dimensieKind1;
		
		return dimensie;
	}
	
	/**
	 * Als de optelling als uitkomst een vector heeft, geef deze vector.
	 * 
	 * @return
	 */
	public VectorExpr geefVector()
	{
		VectorExpr vector = null;
		ArrayList<Expressie> kinderen1, kinderen2;
		
		if (kind1 instanceof VectorExpr)
		{
			kinderen1 = ((VectorExpr) kind1).geefKinderen();
		}
		else
		{
			kinderen1 = kind1.geefVector().geefKinderen();
		}
		
		if (kind2 instanceof VectorExpr)
		{
			kinderen2 = ((VectorExpr) kind2).geefKinderen();
		}
		else
		{
			kinderen2 = kind2.geefVector().geefKinderen();
		}
		
		if (kinderen1.size() == kinderen2.size()) // zelfde dimensie
		{
			ArrayList<Expressie> list = new ArrayList<Expressie>();
			
			for (int i = 0; i < kinderen1.size(); i++)
			{
				Expressie kind = new Optelling(kinderen1.get(i), kinderen2.get(i));
				list.add(kind);
			}
			vector = new VectorExpr(list);
		}
		
		return vector;
	}
	
	/**
	 * Als de optelling als uitkomst een matrix heeft, geef deze matrix.
	 * 
	 * @return
	 */
	public Matrix geefMatrix()
	{
		Matrix matrix = null;
		ArrayList<ArrayList<Expressie>> matrixKinderen1, matrixKinderen2;
		Expressie scalar;
		
		if (kind1 instanceof Matrix)
		{
			matrixKinderen1 = ((Matrix) kind1).geefKinderen();
		}
		else
		{
			matrixKinderen1 = kind1.geefMatrix().geefKinderen();
		}
		
		if (kind2 instanceof Matrix)
		{
			matrixKinderen2 = ((Matrix) kind2).geefKinderen();
		}
		else
		{
			matrixKinderen2 = kind2.geefMatrix().geefKinderen();
		}
		
		ArrayList<ArrayList<Expressie>> list = new ArrayList<ArrayList<Expressie>>();
		
		if ((matrixKinderen1.size() == matrixKinderen2.size())
			&& (matrixKinderen1.get(0).size() == matrixKinderen2.get(0).size())) // zelfde dimensies
		{
			for (int i = 0; i < matrixKinderen1.size(); i++) // rijen
			{
				ArrayList<Expressie> rij = new ArrayList<Expressie>();
				
				for (int j = 0; j < matrixKinderen1.get(0).size(); j++) // kolommen
				{
					// vermenigvuldig alle kinderen met de scalar
					Expressie kind = new Optelling(matrixKinderen1.get(i).get(j), matrixKinderen2.get(i).get(j));
					rij.add(kind);				
				}
				list.add(rij);
			}
			
			matrix = new Matrix(list);
		}
		
		return matrix;
	}
	
}
