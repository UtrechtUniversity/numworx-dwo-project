package fi.wiskopdr.expressies;


public class NormalCDF extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public NormalCDF(Expressie e1, Expressie e2, Expressie e3, Expressie e4)
	{
		kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		kind4 = e4;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double phi(double z)
	{
		return (1 + StatUtil.erf(z / Math.sqrt(2))) / 2;
	}

	public double geefWaarde()
	{
		double grensLinks = kind1.geefWaarde();
		double grensRechts = kind2.geefWaarde();
		double mu = kind3.geefWaarde();
		double sigma = kind4.geefWaarde();
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return waarde;
	}

	public double geefWaarde(double subst)
	{
		double grensLinks = kind1.geefWaarde(subst);
		double grensRechts = kind2.geefWaarde(subst);
		double mu = kind3.geefWaarde(subst);
		double sigma = kind4.geefWaarde(subst);
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return waarde;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		double grensLinks = kind1.geefWaarde(subst, vars);
		double grensRechts = kind2.geefWaarde(subst, vars);
		double mu = kind3.geefWaarde(subst, vars);
		double sigma = kind4.geefWaarde(subst, vars);
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return waarde;
	}

	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{
		double[] substD = new double[subst.length];
		for (int i = 0; i < subst.length; i++)
		{
			substD[i] = subst[i].getReal();
		}
		double grensLinks = kind1.geefWaarde(substD, vars);
		double grensRechts = kind2.geefWaarde(substD, vars);
		double mu = kind3.geefWaarde(substD, vars);
		double sigma = kind4.geefWaarde(substD, vars);
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		double substD = subst.getReal();
		double grensLinks = kind1.geefWaarde(substD);
		double grensRechts = kind2.geefWaarde(substD);
		double mu = kind3.geefWaarde(substD);
		double sigma = kind4.geefWaarde(substD);
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex()
	{
		double grensLinks = kind1.geefWaarde();
		double grensRechts = kind2.geefWaarde();
		double mu = kind3.geefWaarde();
		double sigma = kind4.geefWaarde();
		double waarde = Double.NaN;
		if (Double.isNaN(grensLinks) || Double.isNaN(grensRechts) || Double.isNaN(mu) || Double.isNaN(sigma))
			waarde = Double.NaN;
		else
			waarde = phi((grensRechts - mu) / sigma) - phi((grensLinks - mu) / sigma);
		return new Complex(waarde);
	}

	public Expressie substitueer(double subst, String var)
	{
		return new NormalCDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new NormalCDF(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return (kind1.isWaarde(subst) && kind2.isWaarde(subst) && kind3.isWaarde(subst) && kind4.isWaarde(subst));
	}

	public String geefVarNaam() //TODO: breng in orde!!!
	{
		String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		String s3 = kind3.geefVarNaam();
		String s4 = kind4.geefVarNaam();
		if ("".equals(s1) || "".equals(s2) || "".equals(s3) || "".equals(s4))
			return "";
		else if (s1 != null && s2 != null && !s1.equals(s2))
			return "";
		else if (s1 != null && s3 != null && !s1.equals(s3))
			return "";
		else if (s1 != null && s4 != null && !s1.equals(s4))
			return "";
		else if (s2 != null && s3 != null && !s2.equals(s3))
			return "";
		else if (s2 != null && s4 != null && !s2.equals(s4))
			return "";
		else if (s3 != null && s4 != null && !s3.equals(s4))
			return "";

		else if (s1 != null && s2 != null && s1.equals(s2))
			return s1;
		else if (s1 != null && s3 != null && s1.equals(s3))
			return s1;
		else if (s1 != null && s4 != null && s1.equals(s4))
			return s1;
		else if (s2 != null && s3 != null && s2.equals(s3))
			return s2;
		else if (s2 != null && s4 != null && s2.equals(s4))
			return s2;
		else if (s3 != null && s4 != null && s3.equals(s4))
			return s3;

		else if (s1 != null)
			return s1;
		else if (s2 != null)
			return s2;
		else if (s3 != null)
			return s3;
		else if (s4 != null)
			return s4;

		else
			return null;
	}

	public String toString()
	{
		return "normalcdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "_" + kind4.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "normalcdf" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "_" + kind4.toString() + "@";
	}

	public String toStringCAS()
	{

		return null;
	}
}
