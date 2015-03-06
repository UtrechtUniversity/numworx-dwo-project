package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;


public class PoissonPDF extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public PoissonPDF(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double poisson(double lambda, int x)
	{
		double sum = 0;
		for (int i = 1; i < x + 1; i++)
		{
			sum = sum + Math.log(i);
		}
		return Math.exp(x * Math.log(lambda) - lambda - sum);
	}

	public double geefWaarde()
	{
		double lambda = kind1.geefWaarde();
		double x = kind2.geefWaarde();
		double waarde = Double.NaN;
		if (Double.isNaN(lambda) || Double.isNaN(x))
			return Double.NaN;
		waarde = poisson(lambda, (int) Math.rint(x));
		return waarde;
	}

	public double geefWaarde(double subst)
	{
		double lambda = kind1.geefWaarde(subst);
		double x = kind2.geefWaarde(subst);
		double waarde = Double.NaN;
		if (Double.isNaN(lambda) || Double.isNaN(x))
			return Double.NaN;
		waarde = poisson(lambda, (int) Math.rint(x));
		return waarde;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		double lambda = kind1.geefWaarde(subst, vars);
		double x = kind2.geefWaarde(subst, vars);
		double waarde = Double.NaN;
		if (Double.isNaN(lambda) || Double.isNaN(x))
			return Double.NaN;
		waarde = poisson(lambda, (int) Math.rint(x));
		return waarde;
	}

	public Expressie substitueer(double subst, String var)
	{
		return new PoissonPDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new PoissonPDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
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
		return "poissonpdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "poissonpdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

    public Object visit(AbstractConverter converter) {
    	return converter.poissonpdf(kind1.visit(converter), kind2.visit(converter));
    }
}
