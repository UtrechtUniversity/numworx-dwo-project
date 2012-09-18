package fi.wiskopdr.expressies;


public class Max extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public Max(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;

		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{
		double m = kind1.geefWaarde();
		double n = kind2.geefWaarde();
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return waarde;
	}

	public double geefWaarde(double subst)
	{
		double m = kind1.geefWaarde(subst);
		double n = kind2.geefWaarde(subst);
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return waarde;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		double m = kind1.geefWaarde(subst, vars);
		double n = kind2.geefWaarde(subst, vars);
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return waarde;
	}

	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{
		double[] substD = new double[subst.length];
		for (int i = 0; i < subst.length; i++)
		{
			substD[i] = subst[i].getReal();
		}
		double m = kind1.geefWaarde(substD, vars);
		double n = kind2.geefWaarde(substD, vars);
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		double substD = subst.getReal();
		double m = kind1.geefWaarde(substD);
		double n = kind2.geefWaarde(substD);
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex()
	{
		double m = kind1.geefWaarde();
		double n = kind2.geefWaarde();
		double waarde;
		if (Double.isNaN(m) || Double.isNaN(n))
			waarde = Double.NaN;
		else
			waarde = Math.max(m, n);
		return new Complex(waarde);
	}

	public Expressie substitueer(double subst, String var)
	{
		return new Max(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new Max(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return (kind1.isWaarde(subst) && kind2.isWaarde(subst));
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
		return "max" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "max" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

	public String toStringCAS()
	{

		return null;
	}
}
