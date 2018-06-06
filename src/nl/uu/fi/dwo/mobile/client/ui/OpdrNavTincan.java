package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.ModuleMode;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

class OpdrNavTincan extends OpdrNav {

  private ModuleMode mode;

  
  
  OpdrNavTincan() {}

  @Override
  public void init(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento) {
    ModuleMode mode = memento.getModuleMode();
    @SuppressWarnings("unchecked")
    Map<String,Object> instellingen = (Map<String,Object>) launchData.get("instellingen");
    if(mode != ModuleMode.unknown)
      setInstellingen(instellingen, mode);
    switch(mode) {
      case drill:
      case homework:
        launchData.put("mode", Integer.toString(OEFENEN_STRAFPUNTEN));
        break;
      case test:
        launchData.put("mode", Integer.toString(EINDTOETS));
      default:
        break;
    }
    super.init(launchData, ev, memento);
  }

  private void setInstellingen(Map<String,Object> instellingen, ModuleMode key) {
    Object[] layerNames = (Object[]) instellingen.get("layerNames");
    if(layerNames == null) return;
    Object[] layerVisible = (Object[]) instellingen.get("layerVisible");
    Set<String> set = Arrays.asList(ModuleMode.values()).stream().
        filter(m -> m != key && m != ModuleMode.unknown).
        map(ModuleMode::name).collect(Collectors.toSet());
    String keyName = key.name();
    for(int i = 0; i < layerNames.length; i++) {
      if (keyName.equals(layerNames[i]) )
        layerVisible[i] = Boolean.TRUE;
      else
        if (set.contains(layerNames[i]))
          layerVisible[i] = Boolean.FALSE;
    }
    instellingen.put("layerVisible", layerVisible);    
  }

  @Override
  public ObjectMap getConfiguration() {
    return super.getConfiguration();
  }

}
