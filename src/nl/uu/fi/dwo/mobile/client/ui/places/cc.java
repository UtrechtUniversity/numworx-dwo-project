package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class cc extends Place implements Hash {

	private String token;
	
	cc(String token) {
		this.token = token;
	}
	public static class Tokenizer implements PlaceTokenizer<cc>
	{

		@Override
		public cc getPlace(String token)
		{
			return new cc(token);
		}

		@Override
		public String getToken(cc place)
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
		return Type.c;
	}

	@Override
	public Place getPlace() {
		return this;
	}

	@Override
	public PersistenceId getID() {
		String id = token;
		while(id.length()<20) id = '0' + id;
		return new PersistenceId("MYSQL;PersistentClassCourse;" + id);
	}

}
