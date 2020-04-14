package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class s extends Place implements Hash {

	private String token;
	public s(Object token, String location ) {
	  this(token == null ? null : token.toString());
      if(location != null) {
        this.token += "." + location;
    }
	  
	}
	
	s(String token) {
		this.token = token;
	}
	public static class Tokenizer implements PlaceTokenizer<s>
	{

		@Override
		public s getPlace(String token)
		{
			return new s(token);
		}

		@Override
		public String getToken(s place)
		{
			return place.getToken();
		}
	}

	public String getToken()
	{
		return token;
	}

	@Override
	public Type getType() {
		return Type.s;
	}

	@Override
	public Place getPlace() {
		return this;
	}

	public String getLocation() {
		int dot = token.lastIndexOf('.');
		if(dot > 0)
			return token.substring(dot+1);
		return null;
	}

	@Override
	public PersistenceId getID() {
		int dot = token.lastIndexOf('.');
		String id = dot > 0 ? token.substring(0, dot) : token;
		while(id.length()<20) id = '0' + id;
		return new PersistenceId("MYSQL;PersistentScoContext;" + id);
	}

}
