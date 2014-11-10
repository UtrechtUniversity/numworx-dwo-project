package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.utils.Connector;

public class OpdrNavContext implements OpdrNavIF {
	private static final HandlerRegistration NULL_REGISTRATION = new HandlerRegistration() {
			
			@Override
			public void removeHandler() {
				
			}
		};
	private OpdrNavIF comRoot;
	private String UUID;
	private Connector connector;
	private CssColor background;
	
	public OpdrNavContext(OpdrNavIF comRoot, Connector connector, CssColor bgColor) {
		this.comRoot = comRoot;
		this.connector = connector;
		this.background = bgColor;
		buildUUID();
	}
	
	public OpdrNavContext(OpdrNavIF comRoot, Connector connector) {
		this(comRoot, connector, comRoot.getBackground());
	}

	private void buildUUID() {
		UUID = comRoot.getUUID();
		if(connector != null && connector.widgetId != null)
		{
			int k = UUID.lastIndexOf('-');
			UUID = UUID.substring(0, k+1) + connector.widgetId;
		}
	}

	/**
	 * @param fout
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#setChanged(boolean)
	 */
	public void setChanged(boolean fout) {
		comRoot.setChanged(fout);
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getKeyboard()
	 */
	public FormuleKeyboardIF getKeyboard() {
		return comRoot.getKeyboard();
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getMode()
	 */
	public int getMode() {
		return comRoot.getMode();
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getLearnerId()
	 */
	public String getLearnerId() {
		return comRoot.getLearnerId();
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getLearnerName()
	 */
	public String getLearnerName() {
		return comRoot.getLearnerName();
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getBackground()
	 */
	public CssColor getBackground() {
		return background;
	}

	/**
	 * @param command
	 * @param listener
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#addCBookEventListener(java.lang.String, nl.uu.fi.dwo.interaction.client.event.CBookEventListener)
	 */
	public HandlerRegistration addCBookEventListener(String command,
			CBookEventListener listener) {
		if(connector == null) return NULL_REGISTRATION;
		String source = command + "." + connector.widgetId;
		return getEventBus().addHandlerToSource(CBookEvent.TYPE, source, listener);
	}

	private EventBus getEventBus() {
		return OpdrNav.getEventBus();
	}

	/**
	 * @param event
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#fireEvent(nl.uu.fi.dwo.interaction.client.event.CBookEvent)
	 */
	public void fireEvent(CBookEvent event) {
		if(connector != null) {
			java.util.List<String> wids = connector.getDest(event.getCommand());
			for(String wid: wids)
			{
				String source = event.getCommand() + "." + wid;
				getEventBus().fireEventFromSource(event, source);
			}
			event.setSource(connector.v);
		}
		comRoot.fireEvent(event); // global event, logging, etc
	}

	@Override
	public String getUUID() {
		return UUID;
	}

}
