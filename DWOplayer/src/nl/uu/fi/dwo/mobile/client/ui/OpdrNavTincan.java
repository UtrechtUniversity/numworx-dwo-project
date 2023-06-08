package nl.uu.fi.dwo.mobile.client.ui;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.ModuleMode;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

class OpdrNavTincan extends OpdrNav {

  private static final String HINT_LAYER = "HintLayer";
  private static final String ANSWER_LAYER = "AnswerLayer";
  
  OpdrNavTincan() {}

  @Override
  public void init(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento) {
    ModuleMode mode = memento.getModuleMode();
    @SuppressWarnings("unchecked")
    Map<String,Object> instellingen = (Map<String,Object>) launchData.get("instellingen");
    
    switch(mode) {
      case drill:
    	  setInstellingen(instellingen, ANSWER_LAYER, Boolean.TRUE);
    	  setInstellingen(instellingen, HINT_LAYER, Boolean.TRUE);  	  
    	  launchData.put("mode", Integer.toString(OEFENEN_STRAFPUNTEN));
    	  launchData.put("instellingen", instellingen);
          break;
      case homework:
    	  setInstellingen(instellingen, ANSWER_LAYER, Boolean.FALSE);
    	  setInstellingen(instellingen, HINT_LAYER, Boolean.TRUE);  	  
    	  launchData.put("mode", Integer.toString(OEFENEN_STRAFPUNTEN));
    	  launchData.put("instellingen", instellingen);
    	  break;
      case test:
    	  setInstellingen(instellingen, ANSWER_LAYER, Boolean.FALSE);
    	  setInstellingen(instellingen, HINT_LAYER, Boolean.FALSE);  	  
    	  launchData.put("mode", Integer.toString(EINDTOETS));
    	  launchData.put("instellingen", instellingen);
    	  break;
      default:
    }
    super.init(launchData, ev, memento);
  }

  private void setInstellingen(Map<String,Object> instellingen, String key, Boolean value) {
    Object[] layerNames = (Object[]) instellingen.get("layerNames");
    if(layerNames == null) return;
    Object[] layerVisible = (Object[]) instellingen.get("layerVisible");
    
    for(int i = 0; i < layerNames.length; i++) {
      if (key.equals(layerNames[i]) )
        layerVisible[i] = value;
    }
    instellingen.put("layerVisible", layerVisible);    
  }

  @Override
  public ObjectMap getConfiguration() {
    return super.getConfiguration();
  }

}
