package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;


public class Prv extends Expressie
{
	double waarde = Double.NaN;

	public Prv(Expressie e1, Expressie e2, Expressie e3, Expressie e4)
	{
		kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		kind4 = e4;
		String[] varnamen = Algebra.geefVarNamen(kind1);
		if (kind4 == null || varnamen == null || varnamen.length == 0 || varnamen.length > 1)
			kind4 = new BasisExpressie("x");
		else
			kind4 = new BasisExpressie(varnamen[0]);
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{
		String[] vars =
		{ kind4.geefVarNaam() };
		double[] subst2 =
		{ kind2.geefWaarde() };
		double[] subst3 =
		{ kind3.geefWaarde() };
		return kind1.geefWaarde(subst3, vars) - kind1.geefWaarde(subst2, vars);
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
		return new Prv(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		if (var.equals(kind4.geefVarNaam()))
			return this;
		return new Prv(kind1.substitueer(subst, var), kind2.substitueer(subst, var), kind3.substitueer(subst, var), kind4.substitueer(subst, var));
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
		return "$q" + kind1.toString() + "$n" + kind2.toString() + "$k" + kind3.toString() + "$l" + kind4.toString() + "@@@@";//"$n" + kind3.toString() + 
	}

	public String toStringStrikt()
	{
		return "$q" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "$k" + kind3.toStringStrikt() + "$l" + kind4.toStringStrikt() + "@@@@";//"$n" + kind3.toString() + 
	}

    public Object visit(AbstractConverter converter) {
    	return converter.prv(kind1.visit(converter), kind2.visit(converter),kind3.visit(converter),kind4.visit(converter));
    }
}
