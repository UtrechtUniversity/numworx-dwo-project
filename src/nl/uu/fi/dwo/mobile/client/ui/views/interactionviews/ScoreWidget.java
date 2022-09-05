package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;

public class ScoreWidget implements InteractionView{

	private HashMap<String, Object> launchState; 
	
	int breedte = 40;
	int hoogte = 300; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	int choicePageMode = 0;
    int activiteitNr = 0;
    int paginaNr = 0;
    int activiteitID = 0;
    int moduleID = 0;
    boolean score = false;
    boolean goedFout = false; 
	
	public ScoreWidget(ActivityComponent a, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		
		init(breedte, hoogte, launchState);
		
		
	}
	
	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isCorrect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}

	
	public void init(int width, int height, Map<String, Object> launchData) {
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			 if(map.containsKey("choicePageMode"))
			      choicePageMode = map.getInt("choicePageMode");
			 if(map.containsKey("activiteitNr"))
				 activiteitNr = map.getInt("activiteitNr");
			 if(map.containsKey("paginaNr"))
				 paginaNr = map.getInt("paginaNr");
			 if(map.containsKey("activiteitID"))
				 activiteitID = map.getInt("activiteitID");
			 if(map.containsKey("moduleID"))
				 moduleID = map.getInt("moduleID");
			 
			 if(map.containsKey("score"))
				 score = map.getBoolean("score");
			 if(map.containsKey("goedFout"))
				 goedFout = map.getBoolean("goedFout");
		}
	}

}
