package fi.dwo.server.persistence.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
 

/**
 * Example how to write your own RealmBase code.
 * 
 * @author G.A.J. van der Plas
 */
public class MyRealm //extends RealmBase 
{
//  @Override
//  protected String getName() {
//    return this.getClass().getSimpleName();
//  }
// 
//  @Override
//  protected String getPassword(final String username) {
//    return "test123";
//  }
// 
//  @Override
//  protected Principal getPrincipal(final String username) {
//    final List<String> roles = new ArrayList<String>();
//    roles.add("tomcat");
//    return new GenericPrincipal(this, username, "test123", roles);
//  }
}
