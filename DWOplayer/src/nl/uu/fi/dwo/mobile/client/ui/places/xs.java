package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;

public class xs extends s {

	private SelectModuleItem item;

	public xs(Object token, String location) {
		super(token, location);
	}

	public xs(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<xs>
	{
		private static final String SEP = ":";

		@Override
		public xs getPlace(String token)
		{
			String[] split = token.split(SEP);
			token = split[0];
			xs xs = new xs(token);
			if (split.length >= 3) {
				SelectModuleItem.Type type = SelectModuleItem.Type.valueOf(split[1]);
				String id = split[2];
				switch(type) {
				case SCO: xs.item = SelectModuleItemHolder.getScoByID(id); break;
				default: xs.item = SelectModuleItemHolder.getItemByID(id); break;
				}
			}
			return xs;
		}

		@Override
		public String getToken(xs place)
		{
			String token = place.getToken();
			if (place.item != null) {
				token += SEP + place.item.getType().name() + SEP + place.item.getID();
			}
			return token;
		}
	}

	@Override
	public Type getType() {
		return Type.xs;
	}

	public void setBack(SelectModuleItem item) {
		this.item = item;		
	}

	public SelectModuleItem getBack() {
		return item;
	}

}
