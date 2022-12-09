package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public interface Hash {
	@SuppressWarnings({ "unchecked" })
	enum Type { c(new c.Tokenizer()), 
		s(new s.Tokenizer()), /*cs(new cs.Tokenizer()), */ 
		cc(new cc.Tokenizer()), 
		xc(new xc.Tokenizer()),
		m(new m.Tokenizer()),
		last(new last.Tokenizer()),
		xs(new xs.Tokenizer());
		
		@SuppressWarnings("rawtypes")
		final private PlaceTokenizer t;
		@SuppressWarnings("rawtypes")
		Type(PlaceTokenizer t) {this.t = t; }

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
