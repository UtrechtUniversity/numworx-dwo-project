package fi.wiskopdr.expressies;


public class Integraal extends Expressie
{
	double waarde = Double.NaN;

	public Integraal(Expressie e1, Expressie e2, Expressie e3, Expressie e4)
	{
		kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		kind4 = e4;
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
		return Math.log(kind1.geefWaarde(subst)) / Math.log(kind2.geefWaarde(subst));
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		return Math.log(kind1.geefWaarde(subst, vars)) / Math.log(kind2.geefWaarde(subst, vars));
	}

	public Expressie substitueer(double subst, String var)
	{
		if (var.equals(kind4.geefVarNaam()))
			return this;
		return new Integraal(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		if (var.equals(kind4.geefVarNaam()))
			return this;
		return new Integraal(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
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
		return "$i" + kind1.toString() + "$n" + kind2.toString() + "$k" + kind3.toString() + "$l" + kind4.toString() + "@@@@";//"$n" + kind3.toString() + 
	}

	public String toStringStrikt()
	{
		return "$i" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "$k" + kind3.toStringStrikt() + "$l" + kind4.toStringStrikt() + "@@@@";//"$n" + kind3.toString() + 
	}

	public String toStringCAS()
	{
		if (!kind4.isVar())
		{
			String var = kind4.geefVarNaam();
			return "Integrate[" + kind1.toStringCAS() + "*D[" + kind4.toStringCAS() + "," + var + "]" + ",{" + var + "," + kind2.toStringCAS() + "," + kind3.toStringCAS() + "}]";
		}
		return "Integrate[" + kind1.toStringCAS() + ",{" + kind4.toStringCAS() + "," + kind2.toStringCAS() + "," + kind3.toStringCAS() + "}]";//"$n" + kind3.toString() + 
	}
}
