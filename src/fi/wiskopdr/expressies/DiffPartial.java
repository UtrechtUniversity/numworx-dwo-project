package fi.wiskopdr.expressies;

public class DiffPartial extends Expressie
{
	double waarde = Double.NaN;

	public DiffPartial(Expressie e1, Expressie e2)
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
	{
		if (var.equals(kind2.geefVarNaam()))
			return this;
		return new Diff(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		if (var.equals(kind2.geefVarNaam()))
			return this;
		return new Diff(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
		//System.out.println(""+Expressie.evalWithCAS(this.toStringCAS()));
		//return Expressie.evalWithCAS(this.toStringCAS());
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
		return "$D" + kind1.toString() + "$n" + kind2.toString() + "@@";//"$n" + kind3.toString() + 

	}

	public String toStringStrikt()
	{
		return "$D" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";//"$n" + kind3.toString() +

	}

	public String toStringCAS()
	{
		return "D[" + kind1.toStringCAS() + "," + kind2.toStringCAS() + "]";
	}
}
