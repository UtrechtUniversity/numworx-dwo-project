package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.geogebraview.client.GeogebraView;
import fi.kladjegwt.client.KladjeGWT;
import fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT;

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

	public TekstBuffer(String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		aantalVakken = 0;
	}

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
		ArrayList<Object> result = new ArrayList<Object>();
		boolean vanTeksVakPanel = false;

		String tekst = (String) opdracht.get("tekst");
		if (tekst == null)
		{
			ArrayList<ArrayList<Object>> teksten = (ArrayList<ArrayList<Object>>) (opdracht.get("teksten"));
			if (teksten != null)
				tekst = (String) (teksten.get(row)).get(column);
			vanTeksVakPanel = true;
		}

		ArrayList<Object> opdrachtGegevens = (ArrayList<Object>) opdracht.get("interactiePanelLaunchData");

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
					Object vak = getVak(vanTeksVakPanel ? aantalVakken - 5 : aantalVakken, opdrachtGegevens);
					result.add(vak);
					aantalVakken++;
				}
				//FormuleViewer
				else if (identifier.equals("$f"))
				{
					FormuleViewer fv = getFormuleViewer(opdracht, tekst, i, endIndex);
					i = i + endIndex;
					result.add(fv);
				}
				else if (identifier.equals("$I"))
				{
					ImageView iv = getImageView(opdracht, tekst, i, endIndex);
					i = i + endIndex;
					result.add(iv);
				}
				//Not supported
				else
				{
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

	private ImageView getImageView(HashMap<String, Object> opdracht, String tekst, int i, int endIndex)
	{
		String naam = tekst.substring(i + 2, i + endIndex);

		return new ImageView(naam);
	}

	private FormuleViewer getFormuleViewer(HashMap<String, Object> opdracht, String tekst, int i, int endIndex)
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

	private Object getVak(int index, ArrayList<Object> opdrachtGegevens)
	{
		Object result = null;
		HashMap<String, Object> currentVakGegevens = null;
		// ik denk dat het +5 is en niet +1
		if (opdrachtGegevens.size() > index + 5) // FIXME size() = 6, index = 0 get(0)= null
			currentVakGegevens = (HashMap<String, Object>) opdrachtGegevens.get(index + 5);
		if (currentVakGegevens == null) // FIXME Komt voor in g4test
			return "";
		int soortVak = (Integer) currentVakGegevens.get("soortInteractiePanel");

		switch (soortVak)
		{
		case 39:
			return new GeogebraView(currentVakGegevens, randomVarNamen, randomVarWaarden);
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
			result = new NabouwenAanzichtenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 9)
		{
			result = new TekstVakPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 41)
		{
			result = new KladjeGWT(currentVakGegevens, randomVarNamen, randomVarWaarden);
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
