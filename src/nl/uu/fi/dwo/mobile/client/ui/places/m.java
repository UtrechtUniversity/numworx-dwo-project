package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

public class m extends c implements HasBack {


	private SelectModuleItem back;

	public m(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<m>
	{

		private static final String SEP = ":";

		@Override
		public m getPlace(String token)
		{
			String[] split = token.split(SEP);
			m place = new m(split[0]);
			if (split.length == 2) 
				place.setBack(SelectModuleItemHolder.getItemByID(split[1]));
			return place;
		}

		@Override
		public String getToken(m place)
		{	String suf = "";
			if (place.back != null) {
				suf = SEP + place.back.getID(); 
			}
			return place.getToken() + suf;
		}
	}

	@Override
	public void setBack(SelectModuleItem item) {
		this.back=item;
	}

	public SelectModuleItem getBack() {
		return back;
	}
}
