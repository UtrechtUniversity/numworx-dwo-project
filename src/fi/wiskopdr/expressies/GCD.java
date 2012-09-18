package fi.wiskopdr.expressies;


public class GCD extends Expressie
{
	double waarde = Double.NaN;

	static final double NZERO = 1e-5d;

	public GCD(Expressie e1, Expressie e2)
	{
		kind1 = e1;
		kind2 = e2;

		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public long getGCD(long m, long n)
	{
		long hlp;
		if (m < 0)
			m = -m;
		if (n < 0)
			n = -n;
		if (m < n)
		{
			hlp = n;
			n = m;
			m = hlp;
		}
		if (n == 0)
			return m;
		hlp = m % n;
		if (hlp == 0)
			return n;
		else
			return getGCD(n, hlp);

	}

	public double geefWaarde()
	{
		long m = Math.round(kind1.geefWaarde());
		long n = Math.round(kind2.geefWaarde());
		double waarde;
		if (Double.isNaN(kind1.geefWaarde()) || Double.isNaN(kind2.geefWaarde()))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return waarde;
	}

	public double geefWaarde(double subst)
	{
		long m = Math.round(kind1.geefWaarde(subst));
		long n = Math.round(kind2.geefWaarde(subst));
		double waarde;
		if (Double.isNaN(kind1.geefWaarde(subst)) || Double.isNaN(kind2.geefWaarde(subst)))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return waarde;
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		long m = Math.round(kind1.geefWaarde(subst, vars));
		long n = Math.round(kind2.geefWaarde(subst, vars));
		double waarde;
		if (Double.isNaN(kind1.geefWaarde(subst, vars)) || Double.isNaN(kind2.geefWaarde(subst, vars)))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return waarde;
	}

	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{
		double[] substD = new double[subst.length];
		for (int i = 0; i < subst.length; i++)
		{
			substD[i] = subst[i].getReal();
		}
		long m = Math.round(kind1.geefWaarde(substD, vars));
		long n = Math.round(kind2.geefWaarde(substD, vars));
		double waarde;
		if (Double.isNaN(kind1.geefWaarde(substD, vars)) || Double.isNaN(kind2.geefWaarde(substD, vars)))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		double substD = subst.getReal();
		long m = Math.round(kind1.geefWaarde(substD));
		long n = Math.round(kind2.geefWaarde(substD));
		double waarde;
		if (Double.isNaN(kind1.geefWaarde(substD)) || Double.isNaN(kind2.geefWaarde(substD)))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return new Complex(waarde);
	}

	public Complex geefWaardeComplex()
	{
		long m = Math.round(kind1.geefWaarde());
		long n = Math.round(kind2.geefWaarde());
		double waarde;
		if (Double.isNaN(kind1.geefWaarde()) || Double.isNaN(kind2.geefWaarde()))
			waarde = Double.NaN;
		else
			waarde = getGCD(m, n);
		return new Complex(waarde);
	}

	public Expressie substitueer(double subst, String var)
	{
		return new GCD(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new GCD(kind1.substitueer(subst, var), kind2.substitueer(subst, var));
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
		return "gcd" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "gcd" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}

	public String toStringCAS()
	{

		return null;
	}
}
