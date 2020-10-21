package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public abstract class AbstractScoContextManager {
  protected final DomContext context;

  protected DomContext getContext() {
    return context;
  }

  AbstractScoContextManager(DomContext context) {
    this.context = context;
  }

  public abstract DomScoContextFull update(DomScoContextFull edit, DomScoData data,
      DomDwoProfile dwoProfile) throws Dwo2Exception;

  public abstract DomScoContextFull add(DomScoContextFull edit, DomScoData data,
      DomDwoProfile dwoProfile) throws Dwo2Exception;

  public abstract Boolean remove(DomScoContext sco, DomDwoProfile profile) throws Dwo2Exception;

  public Boolean trash(DomScoContext scoContext, DomDwoProfile dwoProfile) throws Dwo2Exception {
	return Boolean.FALSE;
  }

}
