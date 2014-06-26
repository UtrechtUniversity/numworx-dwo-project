package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class FlatModulePlace extends Place
{

	private String token;

	public FlatModulePlace()
	{
		this("0");
	}

	public FlatModulePlace(String token)
	{
		this.token = token;
	}

	public FlatModulePlace(int id)
	{
		this(Integer.toString(id));
	}

	public static class Tokenizer implements PlaceTokenizer<FlatModulePlace>
	{

		@Override
		public FlatModulePlace getPlace(String token)
		{
			return new FlatModulePlace(token);
		}

		@Override
		public String getToken(FlatModulePlace place)
		{
			return place.getToken();
		}

	}

	public String getToken()
	{
		return token;
	}
}
