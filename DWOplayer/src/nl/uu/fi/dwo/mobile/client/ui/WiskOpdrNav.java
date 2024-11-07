package nl.uu.fi.dwo.mobile.client.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;



/**
 * update Layer informatie via "dme.layers" scorm variabele.
 * Dit is alleen maar een idee. De launchdata komt ook uit een scormvariable
 * De LMS die deze variabele beheert, kan ook de launchdata zelf aanpassen en hoeft
 * de WiskOpdrPlayer niets te doen.
 * @author wim
 *
 */
class WiskOpdrNav extends OpdrNav {

	private static final String LAYERS = "dme.layers";
	
	WiskOpdrNav() {
	}

	@Override
	public void init(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento) {
		String layers = ev.getApi().GetValue(LAYERS);
		if (!layers.isEmpty()) {
			// "layer1 layer2 !layer3"
			// layer1 en layer2 aan, layer3 uit en layer4 don't care
		    @SuppressWarnings("unchecked")
		    Map<String,Object> instellingen = (Map<String,Object>) launchData.get("instellingen");
		    Object[] layerNames = (Object[]) instellingen.get("layerNames");
		    if(layerNames == null) layerNames = new Object[0];
		    Object[] layerVisible = (Object[]) instellingen.get("layerVisible");
		    if (layerVisible == null) layerVisible = new Object[layerNames.length];
		    
		    StringTokenizer st = new StringTokenizer(layers, " ,");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				Boolean on = Boolean.TRUE;
				if (token.startsWith("!")) { on = Boolean.FALSE; token = token.substring(1); }
				setInstellingen(token, on, layerNames, layerVisible);
			}
		    instellingen.put("layerVisible", layerVisible);    

		}
		super.init(launchData, ev, memento);
	}

	private void setInstellingen( String key, Boolean value, Object[] layerNames, Object[] layerVisible) {
		    
		    for(int i = 0; i < layerNames.length; i++) {
		      if (key.equals(layerNames[i]) )
		        layerVisible[i] = value;
		    }
		  }

}
