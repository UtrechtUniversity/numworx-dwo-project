package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModulePlace extends Place
{
	private String token;

	public SelectModulePlace(String token)
	{
		this.token = token;
	}

	public String getToken()
	{
		return this.token;
	}

	public static class Tokenizer implements PlaceTokenizer<SelectModulePlace>
	{

		@Override
		public SelectModulePlace getPlace(String token)
		{
			return new SelectModulePlace(token);
		}

		@Override
		public String getToken(SelectModulePlace place)
		{
			return place.getToken();
		}

	}
}
