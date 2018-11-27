package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.organisation;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.*;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUserFull;

@Singleton
public class JsOrganisationView implements OrganisationPresenter.Display {

  @Inject JsOrganisationView() {
  }

  @Override
  public void init() {
    JsOrganisationDisplay.init();
  }

  @Override
  public void clear() {
    JsOrganisationDisplay.clear();
    
  }

  @Override
  public void setHelp(String url) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showPersonen(Map<String, TaggedDomUserFull> personen) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setEmptyTableMessage() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setLoadingTableMessage() {
    // TODO Auto-generated method stub
    
  }

}
