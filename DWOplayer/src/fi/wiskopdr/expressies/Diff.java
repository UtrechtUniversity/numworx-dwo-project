package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Diff extends Expressie
{
	double waarde = Double.NaN;

	public Diff(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public Expressie evalDiff()
	{	if(kind1!=null && kind2!=null && kind2.isVar() && !kind2.isWaarde() )
		{	return kind1.geefDiff((BasisExpressie)kind2);
		}
		return null;
	}
	 
	public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null && kind2!=null && kind2.isVar() && !kind2.isWaarde() )
		{	return kind1.geefDiff((BasisExpressie)kind2);
		}
		return null;	
	}

	public double geefWaarde()
	{ //if(waarde==Double.NaN) 
		//waarde = geefWaardeViaCAS("N[" + toStringCAS() + ",16]");			
		return Double.NaN;
	}

	public double geefWaarde(double subst)
	{
		return Double.NaN;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		return Double.NaN;
	}

	public Expressie substitueer(double subst, String var)
	{	if(var.equals(kind2.geefVarNaam()))	
		{
			return kind1.geefDiff((BasisExpressie)kind2).substitueer(subst, var);
		}
		return new Diff(kind1.substitueer(subst,var), kind2.substitueer(subst,var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		if (var.equals(kind2.geefVarNaam()))
			return this;
		return new Diff(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
		//System.out.println(""+Expressie.evalWithCAS(this.toStringCAS()));
		//return Expressie.evalWithCAS(this.toStringCAS());
	}
	
	public Expressie vervangDifferentialen(String var)
	{	if(kind1.isVar() && var.equals(kind2.toString()))
			return this;
		
		return new Diff(kind1.vervangDifferentialen(var), kind2.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{	if(var.equals(kind1.toString()))
			return new Diff(subst, kind2.vervangDiffs(subst, var));
	
		return new Diff(kind1.vervangDiffs(subst, var), kind2.vervangDiffs(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return kind1.isWaarde(subst);
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
		return "$d" + kind1.toString() + "$n" + kind2.toString() + "@@";//"$n" + kind3.toString() + 

	}

	public String toStringStrikt()
	{
		return "$d" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";//"$n" + kind3.toString() +

	}

    public Object visit(AbstractConverter converter) {
    	return converter.diff(kind1.visit(converter), kind2.visit(converter));  			
    }
}
