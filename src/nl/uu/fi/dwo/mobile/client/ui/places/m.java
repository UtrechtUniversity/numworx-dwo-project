package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

public class m extends c implements HasBack {


	private SelectModuleItem back;

	public m(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<m>
	{

		@Override
		public m getPlace(String token)
		{
			return new m(token);
		}

		@Override
		public String getToken(m place)
		{
			return place.getToken();
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
