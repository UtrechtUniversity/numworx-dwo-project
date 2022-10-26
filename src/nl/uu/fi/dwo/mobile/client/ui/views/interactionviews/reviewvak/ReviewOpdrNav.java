package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class ReviewOpdrNav implements OpdrNavIF {

	private OpdrNavIF delegate;
	private ReviewActivity parent;

	public ReviewOpdrNav(OpdrNavIF delegate, ReviewActivity reviewActivity) {
		this.delegate = delegate;
		parent = reviewActivity;
	}

	public void setChanged(boolean fout) {
		//delegate.setChanged(fout);
	}

	public FormuleKeyboardIF getKeyboard() {
		return delegate.getKeyboard();
	}

	public FormuleClipboardIF getFormuleClipboard() {
		return delegate.getFormuleClipboard();
	}

	public int getMode() {
		return OEFENEN;
	}

	public String getLearnerId() {
		return delegate.getLearnerId();
	}

	public String getLearnerName() {
		return delegate.getLearnerName();
	}

	public CssColor getBackground() {
		return delegate.getBackground();
	}

	public String getUUID() {
		return delegate.getUUID();
	}

	public LessonMode getLessonMode() {
		return parent.getLessonMode();
	}

	public Role getRole() {
		return delegate.getRole();
	}

	public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
		return delegate.addCBookEventListener(command, listener);
	}

	public void fireEvent(CBookEvent event) {
		delegate.fireEvent(event);
	}

	public boolean hasListeners(String command) {
		return delegate.hasListeners(command);
	}

	public void pause() {
		delegate.pause();
	}

	public void unpause() {
		delegate.unpause();
	}

	public ObjectMap getConfiguration() {
		return delegate.getConfiguration();
	}

	public ObjectMap getContext() {
		return delegate.getContext();
	}
	
}
