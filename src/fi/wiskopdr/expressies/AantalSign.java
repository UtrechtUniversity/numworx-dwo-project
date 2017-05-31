package fi.wiskopdr.expressies;

public class AantalSign extends Expressie
{

	public AantalSign(Expressie e1)
	{
		kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{
		double aantal = Double.NaN;
		if (Double.isNaN(kind1.geefWaarde()))
			return aantal;
		else if (kind1 instanceof BasisExpressie)
		{
			String s = kind1.toString();
			System.out.println("kind1" + s);
			if (s.indexOf('E') > -1)
				s = s.substring(0, s.indexOf('E'));
			//s = StringUtils.replaceStr(s, ".", "");
			//s = StringUtils.replaceStr(s, ",", "");
			s = s.replace(".", "");
			s = s.replace(",", "");
			int lengte = s.length();
			for (int i = 0; i < lengte; i++)
			{
				if (s.charAt(0) == '0')
					s = s.substring(1);
				else
					break;
			}
			return s.length();

		}
		else if (kind1 instanceof Vermenigvuldiging && kind1.kind1 instanceof BasisExpressie && kind1.kind2 instanceof Macht && kind1.kind2.kind1.geefWaarde() == 10)
		{
			String s = kind1.kind1.toString();
			//System.out.println("kind1.kind1" + s);
			//if(s.indexOf('$')>-1) s = s.substring(0,s.indexOf('$'));
			s = s.replace(".", "");
			s = s.replace(",", "");
			int lengte = s.length();
			for (int i = 0; i < lengte; i++)
			{
				if (s.charAt(0) == '0')
					s = s.substring(1);
				else
					break;
			}
			return s.length();

		}
		return aantal;
	}

	public double geefWaarde(double subst)
	{
		return geefWaarde();
	}

	public Complex geefWaardeComplex()
	{
		return new Complex(geefWaarde());
	}

	public Complex geefWaardeComplex(Complex subst)
	{
		return new Complex(geefWaarde());
	}

	public double geefWaarde(double[] subst, String[] vars)
	{
		return geefWaarde();
	}

	public Expressie substitueer(double subst, String var)
	{
		return new AantalSign(kind1.substitueer(subst, var));
	}

	public Expressie substitueer(Expressie subst, String var)
	{
		return new AantalSign(kind1.substitueer(subst, var));
	}
	
	public Expressie vervangDifferentialen(String var)
	{
		return new AantalSign(kind1.vervangDifferentialen(var));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new AantalSign(kind1.vervangDiffs(subst, var));
	}

	public boolean isWaarde(double subst)
	{
		return kind1.isWaarde(subst);
	}

	public String geefVarNaam()
	{
		String s1 = kind1.geefVarNaam();
		if (s1 != null)
			return s1;
		return null;
	}

	public String toString()
	{
		return "sgf" + "$h" + kind1.toString() + "@";
	}

	public String toStringStrikt()
	{
		return "sgf" + "$h" + kind1.toString() + "@";
	}

	public String toStringCAS()
	{
		return "Sgf" + "[" + kind1.toStringCAS() + "]";//TODO
	}
}
