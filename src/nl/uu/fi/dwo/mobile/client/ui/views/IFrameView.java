package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.TekstElement;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;

public class IFrameView extends Composite implements TekstElement, IsWidget {

	private int height;
	private int width;
	private int asHoogte;
	private Frame frame;
	
	public IFrameView(String href, String widthStr, String heightStr) {
		frame = new Frame(href);
		height = Integer.parseInt(heightStr);
		width = Integer.parseInt(widthStr);
		frame.setPixelSize(width, height);
		initWidget(frame);
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

}
