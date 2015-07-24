/**
 * This package contains the persistent entity managers which allow direct and 
 * unrestricted access to the individual persistent entities.
 * 
 * <p>Here be dragons. </p>
 * 
 * <p>These data-managers currently do CRUD updates on individual operations. 
 * The entity manages in this package are structured in a specific way and should 
 * not be refactored lightly. They are designed to be refactored when generic (static)
 * methods can be properly used by the persistence framework in use.</p>
 */
package fi.dwo.server.PersistentEntityManagers;

