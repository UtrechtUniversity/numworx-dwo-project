package fi.dwo.gwt.lib.rest.CallManagers;

/**
 * Provides the model controllers that process gui-operations a restful interface.
 * 
 * Class names of classes which are on the /secure context path are pre with 
 * 'Secured'. Other classes are on the context path /public and their class names
 * are prefixed with 'public'.
 * 
 * The server-side REST-methods contain the security checks. They often also contain
 * the code for the REST-operation in order to reduce IO/SQL-queries. Common code 
 * between methods that should be placed in the fi.dwo.server.PersistentDataManagers.util 
 * class if there is no significant performance loss by it.
 * <p>
 * @author G.A.J. van der Plas
 * 
 */