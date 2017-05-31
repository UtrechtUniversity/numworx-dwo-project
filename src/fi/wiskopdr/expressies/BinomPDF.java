package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class BinomPDF extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public BinomPDF(Expressie e1, Expressie e2, Expressie e3)
	{
		kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{
		double n = kind1.geefWaarde();
		double p = kind2.geefWaarde();
		double k = kind3.geefWaarde();
		double waarde = Double.NaN;
		if (Double.isNaN(n) || Double.isNaN(p) || Double.isNaN(k))
			return Double.NaN;
		double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(k))).geefWaarde();
		double kans = Math.pow(p, k) * Math.pow(1 - p, n - k);
		waarde = nBovenK * kans;
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
		double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(k))).geefWaarde();
		double kans = Math.pow(p, k) * Math.pow(1 - p, n - k);
		waarde = nBovenK * kans;
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
		double nBovenK = (new Bin(new BasisExpressie(n), new BasisExpressie(k))).geefWaarde();
		double kans = Math.pow(p, k) * Math.pow(1 - p, n - k);
		waarde = nBovenK * kans;
		return waarde;
	}

	public Expressie substitueer(double subst, String var)
	{
		return new BinomPDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new BinomPDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{	return new BinomPDF(kind1.vervangDifferentialen(var), kind2.vervangDifferentialen(var), kind3.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new BinomPDF(kind1.vervangDiffs(subst, var), kind2.vervangDiffs(subst, var), kind3.vervangDiffs(subst, var));
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
		return "binompdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "binompdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "@";
	}

    public Object visit(AbstractConverter converter) 
	{
		return converter.binompdf( kind1.visit(converter), kind2.visit(converter), kind3.visit(converter));
	}
}
