package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class BerekeningVakSettings {
	
	//general settings
	private final ObjectMap launchState;
	private int breedte = 0;
	private int hoogte = 0;
	private boolean volledigeBreedte;
	//logging/nakijken
	private boolean check = true;
	private boolean teltMee = true;
	private boolean logOption = false;
	private String logID = null;
	private String logIDLabel = null;
	private boolean[][] logObjectives = null;
	private String[] smObjectives = null;
	private String[] smForeknowledge = null;
	//settings
	private boolean formuleToolBijFocus = true;
	private boolean rmKnop = true;
	private int aantalDecRm = 10;
	private boolean hasFeedback = false;
	// contextVars
	private String[] antwoordSubStrings = null;
	private String[] antwoordFuncStrings = null;
	//score
	private int scoreMax = 0;
	private boolean scoreCumulatief = false;
	//layout
	private boolean boxMetRand = true;
	private boolean meerregelig = false;
	private boolean pasAanH = true;
	
	
	public BerekeningVakSettings(HashMap<String, Object> launchData) {
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		breedte = map.getInt("breedte");
		hoogte = map.getInt("hoogte");
		volledigeBreedte = map.getBoolean("volledigeBreedte");
		launchState = map.getObjectMap("interactiePanelLaunchState");
		
		if(launchState.containsKey("check"))
			check = launchState.getBoolean("check");
		if(launchState.containsKey("teltMee"))
			teltMee = launchState.getBoolean("teltMee");
		if(launchState.containsKey("logOption"))
			logOption = launchState.getBoolean("logOption");
		if(launchState.containsKey("logID"))
			logID = launchState.getString("logID");
		if(launchState.containsKey("logIDLabel"))
			logID = launchState.getString("logIDLabel");
		
		if(launchState.containsKey("formuleToolBijFocus"))
			formuleToolBijFocus = launchState.getBoolean("formuleToolBijFocus");
		if(launchState.containsKey("rmKnop"))
			rmKnop = launchState.getBoolean("rmKnop");
		if(launchState.containsKey("aantalDecRm"))
			aantalDecRm = launchState.getInt("aantalDecRm");
		
		if(launchState.containsKey("antwoordSubStrings"))
			antwoordSubStrings = launchState.getStringArray("antwoordSubStrings");
		if(launchState.containsKey("antwoordFuncStrings"))
			antwoordFuncStrings = launchState.getStringArray("antwoordFuncStrings");
		
		if(launchState.containsKey("scoreMax"))
			scoreMax = launchState.getInt("scoreMax");
		if(launchState.containsKey("scoreCumulatief"))
			scoreCumulatief = launchState.getBoolean("scoreCumulatief");
		
		if(launchState.containsKey("boxMetRand"))
			boxMetRand = launchState.getBoolean("boxMetRand");
		if(launchState.containsKey("meerregelig"))
			meerregelig = launchState.getBoolean("meerregelig");
		
		if(launchState.containsKey("logObjectives"))
		{	ObjectList logObjectivesList = ( launchState.getObjectList("logObjectives") );
			logObjectives = new boolean[logObjectivesList.size()][];
			for(int i = 0; i < logObjectivesList.size(); i++)
			{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
			}
		}
		if (launchState.containsKey("smObjectives")) {
			  smObjectives = launchState.getStringArray("smObjectives");
		}
		if (launchState.containsKey("smForeknowledge"))
			smForeknowledge = launchState.getStringArray("smForeknowledge");
		
	}
	public ObjectMap launchState() {return launchState;}
	public int breedte() {return breedte;}
	public int hoogte() {return hoogte;}
	public boolean volledigeBreedte() {return volledigeBreedte;}
	
	public boolean check() {return check;}
	public boolean teltMee() {return teltMee;}
	public boolean logOption() {return logOption;}
	public String logID() {return logID;}
	public String logIDLabel() {return logIDLabel;}
	
	public boolean formuleToolBijFocus() {return formuleToolBijFocus;}
	public boolean rmKnop() {return rmKnop;}
	public int aantalDecRm() {return aantalDecRm;}
	
	public String[] antwoordSubStrings() {return antwoordSubStrings;}
	public String[] antwoordFuncStrings() {return antwoordFuncStrings;}
	
	public int scoreMax() {return scoreMax;}
	public boolean scoreCumulatief() {return scoreCumulatief;}
	
	public boolean boxMetRand() {return boxMetRand;}
	public boolean meerregelig() {return meerregelig;}
	
	public boolean[][] logObjectives() {return logObjectives;}
	public String[] smObjectives() {return smObjectives;}
	public String[] smForeknowledge() {return smForeknowledge;}
}
