package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TreeModulePlace extends Place
{

	private String token;

	public TreeModulePlace()
	{
		this("0");
	}

	public TreeModulePlace(Object token)
	{
		this.token = token == null ? null : token.toString();
	}

	public TreeModulePlace(int id)
	{
		this(Integer.toString(id));
	}

	public static class Tokenizer implements PlaceTokenizer<TreeModulePlace>
	{

		@Override
		public TreeModulePlace getPlace(String token)
		{
			return new TreeModulePlace(token);
		}

		@Override
		public String getToken(TreeModulePlace place)
		{
			return place.getToken();
		}

	}

	public String getToken()
	{
		return token;
	}
}
