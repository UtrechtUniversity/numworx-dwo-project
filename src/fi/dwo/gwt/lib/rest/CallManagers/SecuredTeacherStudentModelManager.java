package fi.dwo.gwt.lib.rest.CallManagers;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherStudentModelRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredTeacherStudentModelManager {

  private SecuredTeacherStudentModelRestCaller service = GWT.create(SecuredTeacherStudentModelRestCaller.class);
  
  public Promise<DomLRS> getLRS(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getLRS,PathId.getId(context), rest);
  }
}
