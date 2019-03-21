package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Collections;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class ImageVak extends FormuleElement {

	private String image = "";
    private OMSVGRectElement selectedRect;
	private static Map<String,Object> images = Collections.emptyMap();
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public ImageVak(FormuleHolder holder) {
		super(holder);
	}

	public ImageVak(FormuleElement parent) {
		super(parent);
	}

	@Override
	public void vulVak(String s) {
	    if(s.endsWith("@"))
	      s = s.substring(0, s.length()-1);
		setImage(s);
	}

	@Override
	public void zetMaat() {
		calculateSize();
		super.zetMaat();
	}

	private Number w() {
		Object o = images.get(image + "/w");
		if (o instanceof Number)
			return (Number) o;
		return null; // default size
	}

	private Number h() {
		Object o = images.get(image + "/h");
		if (o instanceof Number) {
			return (Number) o;
		}
		return null;
	}
	
	private Number a() {
		Object o = images.get(image + "/a"); // ASHOOGTE
		if (o instanceof Number) 
			return (Number) o;
		return null;
	}
	
	private String t() {
		Object o = images.get(image + "/t");
		if (o instanceof String) {
			return (String)o;
		}
		return null;
	}
	
	private String u() {
		Object o = images.get(image);
		if ( o == null) 
			return null;
		if ( o instanceof Map) {
			o = ((Map)o).get("string"); 
		}
		String data = o.toString();
		if (data.isEmpty()) {
			data  = (String) images.get(image + "/u");
			if (data.startsWith("/"))
				data = "//" + getCDN() + data; // IS DIT ALTIJD GOED?
			return data;
		} else {
			o = images.get(image + "/f");
			if (o instanceof Map) {
				o = ((Map)o).get("@value");
			}
			String type = t();
			if (type == null && o != null) {
				String url = o.toString();
				type = "image/" + url.substring(url.length() - 3, url.length());
			}
			return "data:" + type + ";base64," + data;
		}
		
	}
	
	private String getCDN() {
		return "cdn.dwo.nl";
	}

	private void calculateSize() {
		Number w = w();
		Number h = h();
		setSize(w == null ? 10 : w.intValue() , h == null ? fm.getAscent() : h.intValue());
		Number a = a();
		setAsHoogte(a == null ? getHeight() : a.intValue() );
	}

	@Override
	public String toString() {
		return "$I" + image + "@";
	}
	
	public static Map<String,Object> getImages() {
		return images;
	}

	public static void setImages(Map<String,Object> images) {
		ImageVak.images = images;
	}

	@Override
	public void draw(OMSVGElement svg) {
        createSelection(svg);
		OMSVGImageElement image;
		String data = u();
		if (data != null) {
			image = new OMSVGImageElement(x, y, width, height, data);
			svg.appendChild(image);
		}
	}
    @Override
    public void paintObject()
    {
        validate();        
        paintComponent(this.ctx); // TODO draw image on canvas.
        drawCursor();
    }
 
    private void createSelection(OMSVGElement svg) {
      selectedRect = new OMSVGRectElement(x,y,width,height,0, 0);
      paintSelection();
      svg.appendChild(selectedRect);
  }
  
  public void paintSelection() {
      if (isSelected()) {
          selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "#AAAAFF");
      } else {
          selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
      }
      selectedRect.getWidth().getBaseVal().setValue(width);
      selectedRect.getX().getBaseVal().setValue(x);
      
      drawCursor((OMSVGElement)null);
  }
  protected void drawCursor(OMSVGElement svg) {
    drawCursor(width, svg);
}

protected void drawCursor(int width, OMSVGElement notused) {
    if (this.isCurrent() == false || this.isSelected() || this.holder.hasSelection())
        return;
    selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "#00F");
    selectedRect.getWidth().getBaseVal().setValue(2f);
    selectedRect.getX().getBaseVal().setValue(x+width-2);
}

}
