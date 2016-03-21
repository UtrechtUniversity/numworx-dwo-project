package fi.dwo.server.rest;

/**
 * Provides the managers that process gui-operations requests a restful interface.
 * 
 * Class names of classes which are on the /secure context path are pre with 
 * 'Secured'. Other classes are on the context path /public and their class names
 * are prefixed with 'public'.
 * 
 * The REST-methods contain the security checks and often the code for the REST-operation
 * in order to reduce IO/SQL-queries. Common code between methods that should be placed
 * in the fi.dwo.server.PersistentDataManagers.util class if there is no significant
 * performance loss by it.
 * 
 * <p>
 * @author G.A.J. van der Plas
 * 
 */