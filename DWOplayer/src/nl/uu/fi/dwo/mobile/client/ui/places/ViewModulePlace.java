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
	private String token, hash;

	public ViewModulePlace(Object token)
	{
		this.token = token == null ? "" : token.toString();
		String[] split = this.token.split("#", 2);
		this.token = split[0];
		if (split.length == 2) hash = split[1];
	}

	public ViewModulePlace(Object token, String location) {
		this(token);
		if(location != null) {
			String[] split = location.split("#", 2);
			location = split[0];
			if (split.length==2) hash = split[1];
			this.token += "." + location;
		}
	}
	public ViewModulePlace(Object token, String location, String hash) {
		this(token, location);
		this.hash = hash;
	}
	
	public ViewModulePlace(int id) {
		this(Integer.toString(id));
	}

	public String getToken()
	{
		if (hash == null) return token;
		return token + "#" + hash;
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
	
	public String getHash() {
		return hash;
	}

	public void clrHash() {
		hash = null;
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

	public void setLocation(String location) {
		int dot = token.lastIndexOf('.');
		if (dot >= 0) token = token.substring(0, dot);
		if (location != null) token = token + "." + location;		
	}
}
