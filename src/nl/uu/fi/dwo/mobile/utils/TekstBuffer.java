package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordTekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSelectieUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSleepUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckValueUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;
//import fi.kladjegwt.client.KladjeGWT;
//import fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT;

/**
 * Uses information in the launchdata HashMap to create objects that will be
 * used to show on the screen.
 * 
 * @author Evertson Croes
 * 
 */
public class TekstBuffer
{
	String[] randomVarNamen;
	HashMap<String, Object> randomVarWaarden;
	int aantalVakken = 0;
	//TekstVakPanel parent = null;

	public TekstBuffer(String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		aantalVakken = 0;
	}
	
	/*
	public TekstBuffer(String[] randomVarNamen, HashMap randomVarWaarden, TekstVakPanel parent)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		aantalVakken = 0;
		this.parent = parent;
	}
	*/

	public TekstBuffer()
	{
		aantalVakken = 0;
	}

	public ArrayList<Object> convertTekst(HashMap<String, Object> opdracht)
	{
		return convertTekst(opdracht, 0, 0);
	}

	
	
	
	public ArrayList<Object> convertTekst(HashMap<String, Object> opdracht, int row, int column)
	{
		boolean vanTekstVakPanel = false;
		String tekst = (String) opdracht.get("tekst");
		if (tekst == null)
		{
			List<Object> teksten = JSONUtilities.toArrayList( (opdracht.get("teksten")) );
			if (teksten != null)
				tekst = (String) (JSONUtilities.toArrayList(teksten.get(row))).get(column);
			vanTekstVakPanel = true;
		}
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		
		return convertTekst(tekst, opdrachtGegevens, vanTekstVakPanel);
		
	}
	
	
	
	
	//vanTekstVakPanel is standaard false
	//Methodes convertTekst uit elkaar getrokken op 9-4-2014. Zou niets veranderd moeten hebben aan werking, foutjes voorbehouden.
	//maar nu kan ik ook aanroepen: convertTekst(tekst, null, false); Dit in eerste instantie vooral tbv antwoordKeuzeVak.
	
	public ArrayList<Object> convertTekst(String tekst, List<Object> opdrachtGegevens, boolean vanTekstVakPanel)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		

		if(tekst==null) tekst = "";
		
		

		int lastIndex = 0;

		for (int i = 0; i < tekst.length() - 1; i++)
		{
			if (tekst.charAt(i) == '$')
			{
				String identifier = tekst.substring(i, i + 2);
				String normalTekst = tekst.substring(lastIndex, i);
				int endIndex = getEndIndex(tekst.substring(i, tekst.length()));
				lastIndex = lastIndex + normalTekst.length() + endIndex + 1;
				if (normalTekst.length() != 0)
				{
					String[] breaks = getBreaks(normalTekst);
					for (int j = 0; j < breaks.length; j++)
						result.add(breaks[j]);
				}
				//"vakken"
				if (identifier.equals("$V"))
				{ // Hier ook de offset 5 was 1 FIXME Wim
					Object vak = getVak(vanTekstVakPanel ? aantalVakken - 5 : aantalVakken, opdrachtGegevens);//, result); //result toegevoegd tbv checkSelectieUnit
					result.add(vak);
					aantalVakken++;
				}
				//FormuleViewer
				else if (identifier.equals("$f"))
				{
					FormuleViewer fv = getFormuleViewer(tekst, i, endIndex);
					i = i + endIndex;
					result.add(fv);
				}
				else if (identifier.equals("$I"))
				{
					ImageView iv = getImageView(tekst, i, endIndex);
					i = i + endIndex;
					result.add(iv);
				}
				//Not supported
				else
				{	i = i + endIndex; // skip item
					result.add("");
				}

			}
		}
		if (result.size() == 0)
		{
			String[] breaks = getBreaks(tekst);
			for (int j = 0; j < breaks.length; j++)
				result.add(breaks[j]);
		}

		if (lastIndex < tekst.length() && lastIndex != 0)
		{
			String[] breaks = getBreaks(tekst.substring(lastIndex, tekst.length() - 1));
			for (int j = 0; j < breaks.length; j++)
				result.add(breaks[j]);

		}

		return result;
	}

	private ImageView getImageView(String tekst, int i, int endIndex)
	{
		String naam = tekst.substring(i + 2, i + endIndex);

		return new ImageView(naam);
	}

	private FormuleViewer getFormuleViewer(String tekst, int i, int endIndex)
	{
		FormuleViewer fv;
		fv = new FormuleViewer(tekst.substring(i + 2, i + endIndex), randomVarNamen, randomVarWaarden);
		return fv;
	}
	
	private String[] getBreaks(String normalTekst)
	{
		normalTekst = normalTekst.replaceAll("\n", "\n ");
		String[] result = normalTekst.split("\n");
		return result;
	}

	private int getEndIndex(String currentTekst)
	{
		int result = -1;
		ArrayList<Integer> begins = new ArrayList<Integer>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		for (int i = 0; i < currentTekst.length(); i++)
		{
			if (currentTekst.charAt(i) == '$')
			{
				begins.add(i);
			}
			if (currentTekst.charAt(i) == '@')
			{
				ends.add(i);
			}
		}

		for (int i = 0; i < begins.size(); i++)
		{
			for (int j = 0; j < ends.size(); j++)
			{
				if (begins.get(i) > ends.get(j))
				{
					if ((i - j) == 1)
					{
						result = ends.get(i - 1);
						break;
					}
				}
			}
			if (result != -1)
			{
				break;
			}
		}

		if (result == -1)
		{
			result = ends.get(ends.size() - 1);
		}

		return result;
	}

	
	private Object getVak(int index, List<Object> opdrachtGegevens)//, List<Object> objectenLijst)
	{
		Object result = null;
		HashMap<String, Object> currentVakGegevens = null;
		// ik denk dat het +5 is en niet +1
		if (opdrachtGegevens.size() > index + 5) // FIXME size() = 6, index = 0 get(0)= null
			currentVakGegevens = (HashMap<String, Object>) opdrachtGegevens.get(index + 5);
		else
			return ""; // 1 gegeven, 2 $V
		if (currentVakGegevens == null) // FIXME Komt voor in kladje
			return new TekstVakPanel(new HashMap(), randomVarNamen, randomVarWaarden); // was ""

		int soortVak = ((Number) currentVakGegevens.get("soortInteractiePanel")).intValue();

		
		
		switch (soortVak)
		{
		case 4: 
			return new PopupFacade(currentVakGegevens, new TextEditor( currentVakGegevens, randomVarNamen, randomVarWaarden ));
		
		
		case 39: case 10: // geogebra3
			return new GeogebraView(currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 45: // GraphTool
			return 
					//new StubView("GraphToolGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
					new PopupFacade( 
						currentVakGegevens,
							new fi.graphtoolgwt.client.GraphToolGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)
					);
			
		case 15: 
			return new StubView("DoorzienGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		    //return new PopupFacade( currentVakGegevens, new fi.doorziengwt.client.DoorzienGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 20: 
// Een eerste pesterij: gooi currentVakGegevens door JSONUtilities heen.
			//currentVakGegevens = JSONUtilities.fromJSONObject( JSONUtilities.toJSONObject(currentVakGegevens).isObject());

			return new StubView("GeomAlgGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade( currentVakGegevens, new fi.geomalggwt.client.GeomAlgGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 5 :
			//return new StubView("AlgebraPijlenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return new StubView("AlgebraPijlenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 35 :
			return new StubView("AlgebraExprGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.algebraexprgwt.client.AlgebraExprGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));

		case 46 :
			return new StubView("DraaibankGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.draaibankgwt.client.DraaibankGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		
		}
		

		if (soortVak == 0)
		{
			result = new FormuleEditorWithSteps(currentVakGegevens, false, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 1)
		{
			result = new FormuleEditorWithSteps(currentVakGegevens, true, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 2)
		{
			result = new FormuleEditorWithAnswer(currentVakGegevens, false, null, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 3)
		{
			result = new FormuleEditorWithAnswer(currentVakGegevens, true, null, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 6)
		{
			result = //new NabouwenAanzichtenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden);
					new StubView("NabouwenAanzichtenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 9)
		{
			result = new TekstVakPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 11) 
		{
			result = //new BalansFruitGWT(currentVakGegevens);
					new StubView("BalansFruitGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 12)
		{	
			result = new CheckSelectieUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
			
		}
		else if(soortVak == 49)
		{
			result = new CheckButton(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 14) 
		{
// Deze werkt niet als stub.
			result = //new StubView("AntwoordKeuzeVakGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
					 new PopupFacade(currentVakGegevens,new fi.antwoordkeuzevakgwt.client.AntwoordKeuzeVakGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if(soortVak == 16)
		{	
			result = new CheckSleepUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 33)
		{	result = new CheckValueUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 41)
		{
			//result = new PopupFacade(currentVakGegevens, new fi.kladjegwt.client.KladjeGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
			result = new StubView("KladjeGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 13)
		{
			result = new AntwoordTekstVak(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else
		{
			result = "";
		}

		return result;
	}

	public String[] getVarNamen()
	{
		return randomVarNamen;
	}

	public HashMap getVarWaarden()
	{
		return randomVarWaarden;
	}
}
