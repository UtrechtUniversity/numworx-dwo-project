package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.Context;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;

public interface DwoAdminActions {

  DomScoContextFull update(Context context, DomScoContextFull scoContext, DomScoData scoData,
      Boolean delete);

  DomScoContextFull add(Context context, DomScoContextFull scoContext, DomScoData scoData);

}
