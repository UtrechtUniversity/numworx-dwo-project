package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;


public class Primitieve extends Expressie
{
	double waarde = Double.NaN;

	public Primitieve(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
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
	{ //if(var.equals(kind2.geefVarNaam()))	return this;			
		return new Primitieve(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{ //if(var.equals(kind2.geefVarNaam()))	return this;			
		return new Primitieve(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
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
		return "$P" + kind1.toString() + "$n" + kind2.toString() + "@@";//"$n" + kind3.toString() + 
	}

	public String toStringStrikt()
	{
		return "$P" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";//"$n" + kind3.toString() + 
	}

    public Object visit(AbstractConverter converter) {
    	return converter.primitieve(kind1.visit(converter), kind2.visit(converter), kind2.geefVarNaam());
    }
}
