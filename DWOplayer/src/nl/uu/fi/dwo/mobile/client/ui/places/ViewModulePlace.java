package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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

	public ViewModulePlace(Object token, String location) {
		this(token);
		if(location != null) {
			this.token += "." + location;
		}
	}
	
	public ViewModulePlace(int id) {
		this(Integer.toString(id));
	}

	public String getToken()
	{
		return this.token;
	}

	public PersistenceId getID() {
		int dot = token.lastIndexOf('.');
		String id = dot > 0 ? token.substring(0, dot) : token;
		while(id.length()<20) id = '0' + id;
		return new PersistenceId("MYSQL;PersistentScoContext;" + id);
	}

	public String getLocation() {
		int dot = token.lastIndexOf('.');
		if(dot > 0)
			return token.substring(dot+1);
		return null;
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
