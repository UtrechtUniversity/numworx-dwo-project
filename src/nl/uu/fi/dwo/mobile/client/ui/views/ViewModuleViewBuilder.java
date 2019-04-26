package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;

public interface ViewModuleViewBuilder extends ViewModuleView {

  void initialize(Scorm2004IF setupAPI);

  ViewModuleView initialize();

}
