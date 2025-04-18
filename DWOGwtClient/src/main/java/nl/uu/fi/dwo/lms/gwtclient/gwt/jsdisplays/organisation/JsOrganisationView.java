package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.organisation;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.json.client.JSONObject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.*;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUserCodec;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

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
    JsOrganisationDisplay.setHelp(url);
    
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void showPersonen(Map<String, ?> personen, RoleType role) {
    JSONObject json = new JSONObject();
    personen.forEach( (k,v) -> {json.put(k, TaggedDomUserCodec.CODEC.encode((TaggedDomUser) v));});
    JsOrganisationDisplay.showPersons(json.getJavaScriptObject(), role.name());    
  }

  @Override
  public void setEmptyTableMessage() {
    JsOrganisationDisplay.setEmptyTableMessage();
  }

  @Override
  public void setLoadingTableMessage() {
    JsOrganisationDisplay.setLoadingTableMessage();
  }

  
  @Override
  public void initEditModules(boolean flag) {
    JsOrganisationDisplay.initEditModules(flag, false, false);
  }

  @Override
  public void initEditModules(boolean flag, boolean xs, boolean premium) {
    JsOrganisationDisplay.initEditModules(flag, xs, premium);
  }
 
  @Override
  public void initChooseClass(boolean flag) {
    JsOrganisationDisplay.initChooseClass(flag);
    
  }
  
  @Override
  public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
      JSONObject json = new JSONObject();
      schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
      JsOrganisationDisplay.showSchoolClasses(json.getJavaScriptObject());
  }

}
