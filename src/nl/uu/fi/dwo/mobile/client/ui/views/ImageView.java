package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.TekstElement;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ImageView implements IsWidget, TekstElement
{

	private String naam;
	private int ashoogte;
	private static Map<String, Object> map;

	public ImageView(String naam)
	{
		this.naam = naam;
	}

	public boolean exists() {
		return map.containsKey(naam);
	}
	
	public Image getImage()
	{
		// TODO "data:image/png;base64,XXXXXXXxXXXX==" (dataurl) image/png of image/gif is noodzakelijk, uitbreiding in iconan nodig.

		Object object = map.get(naam);
		if(object == null) return null;
		if(object instanceof Map) { // ByteArray
			object = ((Map) object).get("string");
		}
		String data = (String) object;
		if (data.isEmpty())
		{
			String url = (String) map.get(naam + "/u");
			if (url.startsWith("/"))
				url = "http://www.fisme.science.uu.nl" + url; // IS DIT ALTIJD GOED?
			return new Image(url);
		}
		else
		{
			object = map.get(naam + "/f");
			if(object instanceof Map) { // URI
				object = ((Map<String, Object>) object).get("@value");
			}
			String url = (String) object; // is het png,gif,jpg
			if(url == null) url = "x.gif";
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

	@Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		Object height = map.get(naam + "/h");
		if(height instanceof Number)
			return ((Number) height).intValue();
		return 0;
	}

	@Override
	public int getWidth() {
		Object width = map.get(naam + "/w");
		if(width instanceof Number) {
			return ((Number) width).intValue();
		}
		return 0;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		
	}

	
	
}
