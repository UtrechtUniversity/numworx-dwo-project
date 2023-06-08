package fi.wiskopdr.expressies;


import java.util.HashMap;
import fi.wiskopdr.FormuleParser;


public class FunctieMVDefSet  
{	
	public HashMap<String,Expressie> functieMVExpressies = new HashMap<String,Expressie>();
	public HashMap<String,String[]> functieMVVariabelen = new HashMap<String,String[]>();
	public HashMap<String,String> functieMVNamenSubst = new HashMap<String,String>();
	
	public void addFunctieMVExpressie(String functieString)
	{
		String[] functieDelen = functieString.split("=");
		Expressie functieExpressie = FormuleParser.geefExpressie("$f"+functieDelen[1]);
		String functieNaam = functieDelen[0].substring(0, functieDelen[1].indexOf('('));
		String varString = functieDelen[0].substring(functieDelen[1].indexOf('(')+1, functieDelen[1].indexOf(')'));
		String[] functieMVVariabelen = varString.split(",");
		//String functieVariabele = functieDelen[0].substring(functieDelen[1].indexOf('(')+1, functieDelen[1].indexOf('(')+2);
		addFunctieMVExpressie(functieNaam, functieMVVariabelen, functieExpressie);
	}
	
	public void addFunctieMVExpressie(String functieNaam, String[] functieMVVariabelen, Expressie functieExpressie)
	{	functieMVExpressies.put(functieNaam, functieExpressie);
		if(functieMVVariabelen.length==1)
			functieMVExpressies.put(functieNaam+"'", functieExpressie.geefDiff(new BasisExpressie(functieMVVariabelen[0])));
		this.functieMVVariabelen.put(functieNaam, functieMVVariabelen);
		if(functieMVVariabelen.length==1)
			this.functieMVVariabelen.put(functieNaam+"'", functieMVVariabelen);
		
		String fn = functieNaam;
		String fnn = "";
		for(int j=0 ; j<fn.length() ; j++)
		{	fnn = fnn + fn.charAt(j);
			fnn = fnn + '*';
		}
		functieMVNamenSubst.put(functieNaam, fnn);
		//functieMVNamenSubst.put(functieNaam+"'", fnn+"'*");
		//System.out.println("**"+functieNamenSubst.toString());
	}
	
	public void removeFunctieMVExpressie(String functieNaam)
	{	functieMVExpressies.remove(functieNaam);
		functieMVVariabelen.remove(functieNaam);
		functieMVNamenSubst.remove(functieNaam);
	}
	
	public void removeAll()
	{	
		functieMVExpressies.clear();
		functieMVVariabelen.clear();
		functieMVNamenSubst.clear();
	}
	
	public String[] geefFunctieMVNamen()
	{	return functieMVExpressies.keySet().toArray(new String[0]);
	}
	
	public String[] geefFunctieMVNamenSubst()
	{	return functieMVNamenSubst.values().toArray(new String[0]);
	}
	
	public String[] geefFunctieMVVariabele(String functieNaam)
	{	if(functieNaam!=null && functieMVVariabelen.containsKey(functieNaam))
			return functieMVVariabelen.get(functieNaam);
		return null;
	}
	
	public Expressie geefFunctieMVExpressie(String functieNaam)
	{	if(functieNaam!=null && functieMVExpressies.containsKey(functieNaam))
			return functieMVExpressies.get(functieNaam);
		return null;
	}
	
	
}
