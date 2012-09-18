package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;

public interface InteractionView {
	
	public HashMap<String, Object> getState();
	
	public void setState(HashMap<String, Object> h);
	
	public int getScore();
	
	public boolean isCorrect();
	
	public void setCommunicationRoot(OpdrNav comRoot);

}
