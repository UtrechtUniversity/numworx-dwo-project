/**
 * This package contains the persistent entity managers which allow direct and 
 * unrestricted access to the individual persistent entities.
 * 
 * <p>Here be dragons. </p>
 * 
 * <p>These data-managers currently do CRUD updates on individual operations. 
 * The entity manages in this package are structured in a specific way and should 
 * not be refactored lightly. They can be refactored when generic (static)
 * methods can be properly used by the persistence framework in use. However when
 * opting to use multiple persistence units one should maintain the basic design.
 * One feature for which one might use multiple persistence units is to store large
 * data blobs,jars and media on separate storage servers.
 * 
 * 
 * </p>
 */
package fi.dwo.server.PersistentDataManagers.core;

