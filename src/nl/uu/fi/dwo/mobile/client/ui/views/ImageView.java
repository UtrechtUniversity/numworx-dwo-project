package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.DWOplayer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ImageView implements IsWidget, TekstElement
{
	private Logger logger = Logger.getLogger("ImageView");

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
	
	private ScaledImage scaledImage;

	private String naam;
	private int ashoogte;
	private static Map<String, Object> map;
	private int vollebreedte = -1;

	public ImageView(String naam)
	{
		this.naam = naam;
	}

	public ImageView(String naam2, int vollebreedte) {
		this(naam2);
		this.vollebreedte = vollebreedte;
	}

	public boolean exists() {
		return map.containsKey(strip(naam));
	}

	private static final char SUFFIX = '\f';

	private static String strip(String name) {
		  int i = name.indexOf(SUFFIX);
		  if (i >0) return name.substring(0,i);
		  return name;
		}
	
	public Image getImage()
	{
		// TODO "data:image/png;base64,XXXXXXXxXXXX==" (dataurl) image/png of image/gif is noodzakelijk, uitbreiding in iconan nodig.
		final String snaam = strip(naam);
		Object object = map.get(snaam);
		if(object == null) 
			return null; 
		//was: ImageUtils.newImage("images/resources/antwoordknop.gif");
		//maar dat levert problemen bij weggegooide plaatjes op klaar-knoppen.
		if(object instanceof Map) { // ByteArray
			object = ((Map) object).get("string");
		}
		String data = (String) object;
		if (data.isEmpty())
		{
			String url = (String) map.get(snaam + "/u");
			if (url.startsWith("/"))
				url = "//" + DWOplayer.PARAMETERS.getCDN() + url; // IS DIT ALTIJD GOED?
			Number width = null, height = null;
			object = w();
			if(object instanceof Number) width = (Number) object;
			object = h();
			if(object instanceof Number) height = (Number) object;
			if(width != null && height != null)
			{
				object = map.get(naam + "/v");
				if (Boolean.TRUE.equals(object) && vollebreedte > 0) {
					height = height.intValue() * vollebreedte / width.intValue();
					width  = vollebreedte;
				}
				scaledImage = new ScaledImage(url,width.intValue(), height.intValue());
				return scaledImage;
			}
			return new Image(url);
		}
		else
		{
			object = map.get(snaam + "/f");
			if(object instanceof Map) { // URI
				object = ((Map<String, Object>) object).get("@value");
			}
			String url = (String) object; // is het png,gif,jpg
			if(url == null) url = "x.gif";
			String type = (String) map.get(snaam + "/t");
			if (type == null)
				type = "image/" + url.substring(url.length() - 3, url.length());
			Number width = null, height = null;
			object = w();
			if(object instanceof Number) width = (Number) object;
			object = h();
			if(object instanceof Number) height = (Number) object;
			Image im;
			data = "data:" + type + ";base64," + data;
			if(width != null && height != null)
			{
				object = map.get(naam + "/v");
				if (Boolean.TRUE.equals(object) && vollebreedte > 0) {
					height = height.intValue() * vollebreedte / width.intValue();
					width  = vollebreedte;
				}
				scaledImage = new ScaledImage(data,width.intValue(), height.intValue());
				im = scaledImage;
			}
			else
				im = new Image(data);
			return im;
		}
	}

	private Object h() {
		return map.getOrDefault(naam + "/h", map.get(strip(naam) + "/h"));
	}

	private Object w() {
		return map.getOrDefault(naam + "/w", map.get(strip(naam) + "/w"));
	}
	
	public void zetVolledigeBreedte(int volleBreedte) {
		Object object = map.get(naam + "/v");
		if (Boolean.TRUE.equals(object) && volleBreedte > 0 && scaledImage!=null ) {
			Number width = null, height = null;
			object = w();
			if(object instanceof Number) width = (Number) object;
			object = h();
			if(object instanceof Number) height = (Number) object;
			if(width != null && height != null)	{
				height = height.intValue() * volleBreedte / width.intValue();
				width  = volleBreedte;
				scaledImage.getElement().setPropertyInt("width", (int)width);
				scaledImage.getElement().setPropertyInt("height", (int)height);
				this.vollebreedte = volleBreedte;
				logger.info("In methode zetVolledigeBreedte("+volleBreedte+")  :"+width.toString());
			}
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

	private Widget widget; // idempotent
	@Override
	public Widget asWidget() {
		if( widget != null) return widget;
		return widget = getImage();
	}

	@Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		Object height = h();
		if(height instanceof Number)
		{
			Object object = map.get(naam + "/v");
			if (Boolean.TRUE.equals(object) && vollebreedte > 0) {
				Number width = (Number) map.getOrDefault(naam + "/w", Integer.valueOf(16));
				height = ((Number) height).intValue() * vollebreedte / width.intValue();
			}
			return ((Number) height).intValue();
		}
		return 16;
	}

	@Override
	public int getWidth() {
		Object width = w();
		if(width instanceof Number) {
			Object object = map.get(naam + "/v");
			if (Boolean.TRUE.equals(object) && vollebreedte > 0) {
				return vollebreedte;
			}
			return ((Number) width).intValue();
		}
		return 16;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		
	}

	
	
}
