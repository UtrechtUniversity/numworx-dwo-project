package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
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
	private String UUID, UUIDpfx;

	public void pause() {
		comRoot.pause();
	}

	public void unpause() {
		comRoot.unpause();
	}

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
		int k = UUID.lastIndexOf('-');
		UUIDpfx = UUID.substring(0, k+1);
		if(connector != null && connector.widgetId != null)
		{
			UUID = UUIDpfx + connector.widgetId;
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
	
	public FormuleClipboardIF getFormuleClipboard() {
		return comRoot.getFormuleClipboard();
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
	public HandlerRegistration addCBookEventListener(final String command,
			final CBookEventListener listener) {
		if(connector == null) return NULL_REGISTRATION;
		HandlerRegistration registration  = NULL_REGISTRATION;
		Collection<Entry<String, String>> subscriptions = connector.getSubscriptions(command);
		for (Entry<String, String> entry : subscriptions) {
			String rid = entry.getKey();
			String rcmd = entry.getValue();
			String source = UUIDpfx + rid + "." + rcmd;
			CBookEventListener handler = listener;
			//if(! rcmd.equals(command) ) no optimalisation allowed.
			{
				handler = new CBookEventListener() {
					
					@Override
					public void acceptCBookEvent(CBookEvent event) {
						event = new CBookEvent(command, event.toObjectMap());
						listener.acceptCBookEvent(event);
					}
				};
			}
			
			HandlerRegistration r = 
					getEventBus().addHandlerToSource(CBookEvent.TYPE, source, handler);
			if(registration == NULL_REGISTRATION)
				registration = r;
			else {
				// registration = PairRegistration(registration, r);
			}
		}
		return registration;
	}

	private EventBus getEventBus() {
		return OpdrNav.getEventBus();
	}

	/**
	 * @param event
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#fireEvent(nl.uu.fi.dwo.interaction.client.event.CBookEvent)
	 */
	public void fireEvent(CBookEvent event) {
		if(connector != null)
		{
			event.setSource(UUID);
			getEventBus().fireEventFromSource(event, UUID + "." + event.getCommand());
		}
		///comRoot.fireEvent(event); // global event, logging, etc
	}

	@Override
	public String getUUID() {
		return UUID;
	}

	/**
	 * delegate to comRoot.
	 */
	@Override
	public LessonMode getLessonMode() {
		return comRoot.getLessonMode();
	}

	public Role getRole() {
		return comRoot.getRole();
	}

	@Override
	public boolean hasListeners(String command) {
		if(connector == null)
			return false;
		return connector.commands.contains(command);
	}

}
