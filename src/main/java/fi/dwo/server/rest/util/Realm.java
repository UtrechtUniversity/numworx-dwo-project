package fi.dwo.server.rest.util;

import java.security.Principal;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;

public class Realm {

  public static String of(Principal user) {
    String name = user.getName();
    int split = name.indexOf('@');
    if (split > 0) return name.substring(split);
    return null;
  }

  public static String of(DomContext context) {
    if (context == null) return null;
    String realm = context.getRealm();
    if (realm == null|| realm.isEmpty()) return null;
    return realm;
  }

  public static String of(String name) {
    int split = name.indexOf('@');
    if (split > 0) return name.substring(split);
    return null;
  }
  
}
