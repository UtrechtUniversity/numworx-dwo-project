package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.s;

public class CourseScoActivity extends ScoActivity {

  public CourseScoActivity(ClientFactory clientFactory, SelectModuleItem item, s where) {
    super(clientFactory, item, where);
  }

  public CourseScoActivity(s where) {
    super(where);
  }

}
