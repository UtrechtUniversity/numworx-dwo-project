package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModulePlace extends Place
{
	private String token;

	public ViewModulePlace(Object token)
	{
		this.token = token == null ? null : token.toString();
	}

	public ViewModulePlace(int id) {
		this(Integer.toString(id));
	}

	public String getToken()
	{
		return this.token;
	}

	public static class Tokenizer implements PlaceTokenizer<ViewModulePlace>
	{

		@Override
		public ViewModulePlace getPlace(String token)
		{
			return new ViewModulePlace(token);
		}

		@Override
		public String getToken(ViewModulePlace place)
		{
			return place.getToken();
		}

	}
}
