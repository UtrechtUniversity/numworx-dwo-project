package nl.uu.fi.dwo.interaction.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.IsWidget;

public interface InteractionView extends IsWidget
{

	public HashMap<String, Object> getState();

	public void setState(HashMap<String, Object> h);

	public int getScore();

	public boolean isCorrect();

	public void setCommunicationRoot(OpdrNavIF comRoot);

}
