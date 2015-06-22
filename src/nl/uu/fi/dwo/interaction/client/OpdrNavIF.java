package nl.uu.fi.dwo.interaction.client;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.web.bindery.event.shared.HandlerRegistration;

public interface OpdrNavIF
{
	int OEFENEN = 0;
	int OEFENEN_STRAFPUNTEN = 1;
	int ZELFTOETS = 2;
	int EINDTOETS = 3;
	
	void setChanged(boolean fout);
	FormuleKeyboardIF getKeyboard();
	FormuleClipboardIF getFormuleClipboard();
	
	int getMode();

	String getLearnerId();
	String getLearnerName();
	CssColor getBackground();
	String getUUID();
	LessonMode getLessonMode();
	
	Role ROLE_LEARNER = Role.Learner;
	Role ROLE_INSTRUCTOR = Role.Instructor;
	Role getRole();
	
	HandlerRegistration addCBookEventListener(String command, CBookEventListener listener);
	void fireEvent(CBookEvent event);
}
