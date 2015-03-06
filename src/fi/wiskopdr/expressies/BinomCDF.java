package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class BinomCDF extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public BinomCDF(Expressie e1, Expressie e2, Expressie e3)
	{
		kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	// distribution function voor standaard normale verdeling
	// let op: de benadering met erf is alleen valide voor z>=0
	// voor negatieve z gebruik phi(z)=1-phi(-z)
	// merk op: erf(-z)=erf(z) 

	public double geefWaarde()
	{
		double n = kind1.geefWaarde();
		double p = kind2.geefWaarde();
		double k = kind3.geefWaarde();
		double waarde = Double.NaN;
		if (Double.isNaN(n) || Double.isNaN(p) || Double.isNaN(k))
			return Double.NaN;
		waarde = 0;
		for (int i = 0; i < k + 1; i++)
		{
			double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(i))).geefWaarde();
			double kans = Math.pow(p, i) * Math.pow(1 - p, n - i);
			waarde = waarde + nBovenK * kans;
		}
		return waarde;
	}

	public double geefWaarde(double subst)
	{
		double n = kind1.geefWaarde(subst);
		double p = kind2.geefWaarde(subst);
		double k = kind3.geefWaarde(subst);
		double waarde = Double.NaN;
		if (Double.isNaN(n) || Double.isNaN(p) || Double.isNaN(k))
			return Double.NaN;
		waarde = 0;
		for (int i = 0; i < k + 1; i++)
		{
			double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(i))).geefWaarde();
			double kans = Math.pow(p, i) * Math.pow(1 - p, n - i);
			waarde = waarde + nBovenK * kans;
		}
		return waarde;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		double n = kind1.geefWaarde(subst, vars);
		double p = kind2.geefWaarde(subst, vars);
		double k = kind3.geefWaarde(subst, vars);
		double waarde = Double.NaN;
		if (Double.isNaN(n) || Double.isNaN(p) || Double.isNaN(k))
			return Double.NaN;
		waarde = 0;
		for (int i = 0; i < k + 1; i++)
		{
			double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(i))).geefWaarde();
			double kans = Math.pow(p, i) * Math.pow(1 - p, n - i);
			waarde = waarde + nBovenK * kans;
		}
		return waarde;
	}

	public Expressie substitueer(double subst, String var)
	{
		return new BinomCDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new BinomCDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return (kind1.isWaarde(subst) && kind2.isWaarde(subst) && kind3.isWaarde(subst));
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
		return "binomcdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "binomcdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "@";
	}

    public Object visit(AbstractConverter converter) 
	{
		return converter.binomcdf( kind1.visit(converter), kind2.visit(converter), kind3.visit(converter));
	}
}
