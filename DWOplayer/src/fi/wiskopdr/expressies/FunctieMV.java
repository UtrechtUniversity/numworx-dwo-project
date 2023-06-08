package fi.wiskopdr.expressies;

import fi.wiskopdr.expressies.repr.AbstractConverter;

public class FunctieMV extends Expressie  
{	
	public static FunctieMVDefSet functieMVDefSet = new FunctieMVDefSet();
	
	public Expressie[] kinderen;
	
	public static void setFunctieMVDefSet(FunctieMVDefSet functieMVDefSet)
	{	FunctieMV.functieMVDefSet = functieMVDefSet;
		if(FunctieMV.functieMVDefSet==null)
			FunctieMV.functieMVDefSet = new FunctieMVDefSet();
	}
	
	public static FunctieMVDefSet getFunctieMVDefSet()
	{	return functieMVDefSet;
	}
	
	private String functieNaam;
	private String[] functieMVVariabelen;
	private Expressie functieMVExpressie;
	
	public FunctieMV(String functieNaam, Expressie[] e )
	{	this.functieNaam = functieNaam;
		if(functieNaam!=null && functieMVDefSet.functieMVExpressies.containsKey(functieNaam))
			functieMVExpressie = functieMVDefSet.functieMVExpressies.get(functieNaam);
		if(functieNaam!=null && functieMVDefSet.functieMVVariabelen.containsKey(functieNaam))
			functieMVVariabelen = functieMVDefSet.functieMVVariabelen.get(functieNaam);
		
		kinderen = new Expressie[e.length];
		for(int i=0 ; i<e.length ; i++)
		{	kinderen[i] = e[i];
		}
		if(e.length>0) 
			kind1 = e[0];
		if(e.length>1) 
			kind2 = e[1];
		if(e.length>2) 
			kind3 = e[2];
		if(e.length>3) 
			kind4 = e[3];
		
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public String geefFunctieNaam()
	{	return functieNaam;
	}
	
	/*public Expressie geefDiff(BasisExpressie basisExp)
	{	if(kind1!=null)
		{	return new Vermenigvuldiging(kind1.geefDiff(basisExp),functieExpressie.geefDiff(new BasisExpressie(functieVariabele)).substitueer(kind1, functieVariabele));
		}
		return null;	
	}*/
	
	public double geefWaarde()
	{	Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i].geefWaarde(),functieMVVariabelen[i]);
		}
		return e.geefWaarde();
	}
	
	public double geefWaarde(double subst)
	{	return functieMVExpressie.substitueer(kind1.geefWaarde(subst), functieMVVariabelen[0]).geefWaarde();
	}
	
	public Complex geefWaardeComplex()
	{	//Complex c1 = kind1.geefWaardeComplex();
		//if(c1==null) return null;
		//return Complex.sin(c1);
				
		Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i].geefWaardeComplex().getReal(),functieMVVariabelen[i]);
		}
		return e.geefWaardeComplex();
		// nog niet goed
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	//return Complex.sin(kind1.geefWaardeComplex(subst));
		
		return functieMVExpressie.substitueer(kind1.geefWaardeComplex(subst).getReal(), functieMVVariabelen[0]).geefWaardeComplex();
		// nog niet goed
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	
		Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i].geefWaarde(subst,vars),functieMVVariabelen[i]);
		}
		return e.geefWaarde();
	}
	
	public Expressie substitueer(double subst, String var)
	{	//return new Sinus(kind1.substitueer(subst,var));
		//return functieExpressie.substitueer(kind1.substitueer(subst,var), functieVariabele);
		Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i].substitueer(subst,var),functieMVVariabelen[i]);
		}
		return e;
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	//return new Sinus(kind1.substitueer(subst,var));
		//return functieExpressie.substitueer(kind1.substitueer(subst,var), functieVariabele);
		Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i].substitueer(subst,var),functieMVVariabelen[i]);
		}
		return e;
	}
	
	public Expressie vervangDifferentialen(String var)
	{	
		Expressie[] e = new Expressie[kinderen.length];
		for(int i=0 ; i<kinderen.length ; i++)
		{	e[i] = kinderen[i].vervangDifferentialen(var);
		}
		return new FunctieMV(functieNaam, e);
	}
	
	public Expressie vervangDiffs(Expressie subst, String var)
	{
		Expressie[] e = new Expressie[kinderen.length];
		for(int i=0 ; i<kinderen.length ; i++)
		{	e[i] = kinderen[i].vervangDiffs(subst, var);
		}
		return new FunctieMV(functieNaam, e);
	}
		
	public boolean isWaarde(double subst)
	{	
		boolean b = true;
		for(int i=0 ; i<kinderen.length ; i++)
		{	b = b && kinderen[i].isWaarde(subst);
		}
		return b;
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	
		String kindString = "";
		for(int i=0 ; i<kinderen.length ; i++)
		{	if(i>0)
			{	kindString = kindString + ",";
			}
			kindString = kindString + kinderen[i].toString();
		}
		return functieNaam + "$h" + kindString + "@";
	}
	
	public String toStringStrikt()
	{	
		String kindString = "";
		for(int i=0 ; i<kinderen.length ; i++)
		{	if(i>0)
			{	kindString = kindString + ",";
			}
			kindString = kindString + kinderen[i].toStringStrikt();
		}
		return functieNaam + "$h" + kindString + "@";
	}
    
    public String toStringCAS()
    {   
    	String kindString = "";
		for(int i=0 ; i<kinderen.length ; i++)
		{	if(i>0)
			{	kindString = kindString + ",";
			}
			kindString = kindString + kinderen[i].toStringCAS();
		}
    	return functieNaam + "[" + kindString + "]";
    }
    
    public Object visit(AbstractConverter converter) {
    	//return converter.sinus(kind1.visit(converter));
    	//return functieExpressie.substitueer(kind1, functieVariabele).visit(converter);
    	
    	Expressie e = functieMVExpressie;
		for(int i=0 ; i<kinderen.length ; i++)
		{	e = e.substitueer(kinderen[i],functieMVVariabelen[i]);
		}
		return e.visit(converter);
    	
    	//lijkt me ?
    }

}
