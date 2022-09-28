package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public interface Hash {
	enum Type { c(new c.Tokenizer()), 
		s(new s.Tokenizer()), /*cs(new cs.Tokenizer()), */ 
		cc(new cc.Tokenizer()), 
		xc(new xc.Tokenizer()),
		xs(new xs.Tokenizer());
		
		final private PlaceTokenizer<?> t;
		<T extends Place & Hash>
		Type(PlaceTokenizer<T> t) {this.t = t; }

		public PlaceTokenizer<Place> getT() {
			return (PlaceTokenizer<Place>) t;
		}
		public Hash getHash(String substring) {
			return (Hash) t.getPlace(substring);
		}
	}
	
	Type getType();
	Place getPlace();
	String getToken();
	PersistenceId getID();
}
