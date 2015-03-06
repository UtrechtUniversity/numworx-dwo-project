package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;


public class Limiet extends Expressie
{
	double waarde = Double.NaN;
	//private int richting = 0;
	static int NAAR = 0;
	static int VANBOVEN = 1;
	static int VANONDER = 2;

	public Limiet(Expressie e1, Expressie e2, Expressie e3, Expressie e4)
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
		return new Limiet(kind1.substitueer(subst, var), kind2, kind3.substitueer(subst, var), kind4);
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		if (var.equals(kind2.geefVarNaam()))
			return this;
		return new Limiet(kind1.substitueer(subst, var), kind2, kind3.substitueer(subst, var), kind4);
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
		else if (s1 != null && s2 != null && !s1.equals(s2) && !s1.equals(s2))
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
		return "$T" + kind1.toString() + "$n" + kind2.toString() + "$k" + kind3.toString() + "$l" + kind4.toString() + "@@@@";//"$n" + kind3.toString() + 
	}

	public String toStringStrikt()
	{
		return "$T" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "$k" + kind3.toStringStrikt() + "$l" + kind4.toStringStrikt() + "@@@@";//"$n" + kind3.toString() + 
	}

    public Object visit(AbstractConverter converter ) {
    	return converter.limit( kind1.visit(converter),kind2.visit(converter),kind3.visit(converter),kind4.visit(converter));
    }
}
