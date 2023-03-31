package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class xc extends c implements HasBack {

	private SelectModuleItem back;

	public xc(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<xc>
	{
		private static final String SEP = ":";

		@Override
		public xc getPlace(String token)
		{
			String[] split = token.split(SEP);
			xc place = new xc(split[0]);
			if (split.length == 2) 
				place.setBack(SelectModuleItemHolder.getItemByID(split[1]));
			return place;
		}

		@Override
		public String getToken(xc place)
		{	String suf = "";
			if (place.back != null) {
				suf = SEP + place.back.getID(); 
			}
			return place.getToken() + suf;
		}

	}

	@Override
	public Type getType() {
		return Type.xc;
	}

	@Override
	public PersistenceId getID() {
		String id = getToken();
		int dot = id.indexOf('.'); 
		if (dot >= 0) id = id.substring(0, dot);
		while(id.length()<20) id = '0' + id;
		return new PersistenceId("MYSQL;PersistentCourse;" + id);
	}

	@Override
	public void setBack(SelectModuleItem item) {
		this.back = item;	
	}

	public SelectModuleItem getBack() {
		return back;
	}

}
