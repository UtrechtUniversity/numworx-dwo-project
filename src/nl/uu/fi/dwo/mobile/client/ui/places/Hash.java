package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

interface Hash {
	enum Type { c(new c.Tokenizer()), s(null);
		
		final private PlaceTokenizer<Place> t;
		Type(PlaceTokenizer t) {this.t = t; }
		public PlaceTokenizer<Place> getT() {
			return t;
		}
		public Hash getHash(String substring) {
			return (Hash) t.getPlace(substring);
		}
	}
	
	Type getType();
	Place getPlace();
}
