package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.mobile.client.sco.FacetMemento;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;

import com.google.gwt.user.client.Window;

public class TriforkModuleViewImpl extends ViewModuleViewImpl implements
		ViewModuleView , FacetAware {

	public TriforkModuleViewImpl(Scorm2004IF api) {
		super(false,api);
	}
	
	public void zetMaat() {
		int contentHeight = Window.getClientHeight() - extraHeight;
		//Window.addResizeHandler(new Resizer());
		sb.zetMaat();
		sb.setScrollPanel(this, contentHeight);	
	}

	@Override
	protected Memento createMemento() {
		return new FacetMemento(getApi(), this, this);
	}

	@Override
	public void getResponses(List<String> responses) {
		hoofdPanel.getResponses(responses);
	}

}
