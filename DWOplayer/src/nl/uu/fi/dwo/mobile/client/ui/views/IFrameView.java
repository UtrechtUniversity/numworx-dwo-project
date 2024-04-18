package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.ResponsiveTextElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;

public class IFrameView extends Composite implements ResponsiveTextElement, IsWidget {

	private int height;
	private int width;
	private int asHoogte;
	private boolean volledigebreedte;
	private float aspect;
	
	public IFrameView(String href, String widthStr, String heightStr, boolean volledigebreedte, int vollebreedte) {
		Frame frame = new Frame(href);
		initWidget(frame);		
		getElement().setAttribute("allowfullscreen", "true"); // op verzoek van youtube
		setStylePrimaryName(".gwt-StubView");
		addStyleDependentName("borderless");
		height = Integer.parseInt(heightStr);
		width = Integer.parseInt(widthStr);
		this.volledigebreedte = volledigebreedte;
		if (volledigebreedte) {
			aspect = ((float) height ) / (float) width;
			width = vollebreedte;
			height = Math.round(vollebreedte * aspect);
		}
		setPixelSize(width, height);
	}
	
	
	
	public IFrameView(String href, String widthStr, String heightStr) {
		this(href, widthStr, heightStr, false, 0);
	}

	@Override
	public int getAsHoogte() {
		return asHoogte;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if (volledigebreedte) {
			this.width = breedte;
			this.height = Math.round(aspect * breedte);
			setPixelSize(width, height);
		}
		
	}

}
