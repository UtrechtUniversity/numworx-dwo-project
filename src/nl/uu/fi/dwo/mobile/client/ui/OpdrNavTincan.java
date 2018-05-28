package nl.uu.fi.dwo.mobile.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

class OpdrNavTincan extends OpdrNav {

  private static String MODE_PARAMETER = "mode";

  enum Mode { drill, homework, test }
  
  OpdrNavTincan() {}

  @Override
  protected int createMode(HashMap<String, Object> launchData) {
    String mode = Window.Location.getParameter(MODE_PARAMETER);
    if(mode != null) {
        try {
          return Integer.parseInt(mode); // bijvoorbeeld.
          //return Mode.valueOf(mode).ordinal(); // of zo
        } catch (Exception e) {
            // JAMMER DAN
        }
    }
    return super.createMode(launchData);
  }

  @Override
  public ObjectMap getConfiguration() {
    return super.getConfiguration();
  }

}
