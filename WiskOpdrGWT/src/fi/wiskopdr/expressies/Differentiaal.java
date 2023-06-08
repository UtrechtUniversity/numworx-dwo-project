package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class Differentiaal extends Expressie
{	private double waarde = 0.8765432;

	public Differentiaal(Expressie e1)
	{   kind1 = e1;
	    isVeelterm = false;
	    isProdukt = false;
	    isBasis = false;
	}
	
	public double geefWaarde()
	{	return waarde*kind1.geefWaarde();
		
		//return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	
		return waarde*kind1.geefWaarde(subst);
		
		//return Double.NaN;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return waarde*kind1.geefWaarde(subst, vars);
		
		//return Double.NaN;
	}
	
	public boolean isLeeg()
	{
		return kind1 == null;
	}
	
	public Expressie substitueer(double subst, String var)
	{	//onderstaande komt uit diff. Hier wel of niet?
		//if(var.equals(kind1.geefVarNaam()))	return this;			
	
		return new Differentiaal(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	//onderstaande komt uit diff. Hier wel of niet?
		//if(var.equals(kind1.geefVarNaam()))	return this;
		
		return new Differentiaal(kind1.substitueer(subst,var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}	
	
	public Expressie vervangDifferentialen(String var) {
		
		if(var.equals(kind1.toString()))
			return this;
		return new Vermenigvuldiging(new Diff(kind1, new BasisExpressie(var)), new Differentiaal(new BasisExpressie(var)));
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		return new Differentiaal(kind1.vervangDiffs(subst, var));
	}
	
	public String toString()
	{	if(kind1.toString().length() == 1 || kind1.toString().contains("$s") && kind1.toString().length() == 5)
		{
			return "d" + kind1.toString();
		}
		else
			return "d" + "$h" + kind1.toString() + "@";
	   
	}
	
	
	public String toStringStrikt()
	{	if(kind1.toString().length() == 1 || kind1.toString().contains("$s") && kind1.toString().length() == 5)
		{
			return "d" + kind1.toString();
		}
		else
			return "d" + "$h" + kind1.toStringStrikt() + "@";
	   
	}
	
	public String toStringCAS() //weet niet of 'Differential' klopt of handig is.
	{   return "Differential" + "[" + kind1.toStringCAS() + "]";
	}
	 
	public Object visit(AbstractConverter converter) 
	{   	return converter.differentiaal(kind1.visit(converter));  			
	}
}
