package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Map;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ImageView implements IsWidget
{

	private String naam;
	private static Map<String, Object> map;

	public ImageView(String naam)
	{
		this.naam = naam;
	}

	public Image getImage()
	{
		// TODO "data:image/png;base64,XXXXXXXxXXXX==" (dataurl) image/png of image/gif is noodzakelijk, uitbreiding in iconan nodig.

		String data = (String) map.get(naam);
		if (data.isEmpty())
		{
			String url = (String) map.get(naam + "/u");
			if (url.startsWith("/"))
				url = "http://www.fisme.science.uu.nl" + url; // IS DIT ALTIJD GOED?
			return new Image(url);
		}
		else
		{
			String url = (String) map.get(naam + "/f"); // is het png,gif,jpg
			String type = (String) map.get(naam + "/t");
			if (type == null)
				type = "image/" + url.substring(url.length() - 3, url.length());
			data = "data:" + type + ";base64," + data;
			Image im = new Image(data);
			return im;
		}
	}

	public static Map<String, Object> getMap()
	{
		return map;
	}

	public static void setMap(Map<String, Object> map)
	{
		ImageView.map = map;
	}

	@Override
	public Widget asWidget() {
		return getImage();
	}

}
