package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.utils.ImageUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ImageView implements IsWidget, TekstElement
{

	static class ScaledImage extends Image {

		public ScaledImage(String element) {
			super(element);
		}
		
		public ScaledImage(String url, int width, int height) {
			super( createScaledElement(url, width, height));
		}
		
		static ImageElement createScaledElement(String url, int width,
				int height) {
			ImageElement element = ImageElement.as(DOM.createImg());
			element.setWidth(width);
			element.setHeight(height);
			element.setSrc(url);
			return element;
		}
	}

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
		if(object == null) 
			return null; //ImageUtils.newImage("images/resources/antwoordknop.gif");
		if(object instanceof Map) { // ByteArray
			object = ((Map) object).get("string");
		}
		String data = (String) object;
		if (data.isEmpty())
		{
			String url = (String) map.get(naam + "/u");
			if (url.startsWith("/"))
				url = "https://ws.fisme.science.uu.nl" + url; // IS DIT ALTIJD GOED?
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
			Number width = null, height = null;
			object = map.get(naam + "/w");
			if(object instanceof Number) width = (Number) object;
			object = map.get(naam + "/h");
			if(object instanceof Number) height = (Number) object;
			Image im;
			data = "data:" + type + ";base64," + data;
			if(width != null && height != null)
				im = new ScaledImage(data,width.intValue(), height.intValue());
			else
				im = new Image(data);
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
		return 16;
	}

	@Override
	public int getWidth() {
		Object width = map.get(naam + "/w");
		if(width instanceof Number) {
			return ((Number) width).intValue();
		}
		return 16;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		
	}

	
	
}
