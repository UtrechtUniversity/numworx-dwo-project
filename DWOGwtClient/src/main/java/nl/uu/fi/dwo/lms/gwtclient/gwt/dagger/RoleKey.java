package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.MapKey;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@MapKey
public @interface RoleKey {
  RoleType value();
}
