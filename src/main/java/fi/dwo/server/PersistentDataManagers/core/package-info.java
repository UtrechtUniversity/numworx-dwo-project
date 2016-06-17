/**
 * This package contains the persistent entity managers which allow direct and 
 * unrestricted access to the individual persistent entities.
 * 
 * <p>Here be dragons. </p>
 * 
 * <p>These data-managers should only do CRUD operations on the database tables of
 * their entity. Database tables should represent entities. Most current distributed
 * No-SQL databases only support ACID at row level. I.e. MongoDB has collections
 * instead of tables and documents instead of rows. 
 * The entity manages in this package are structured in a specific way and should 
 * not be refactored lightly. They can be refactored in Java 8 as Generics are
 * allowed on static methods. However when
 * opting to use multiple persistence units one should be maintained in the basic design.
 * It allows  to use multiple persistence units is to store large
 * data blobs,jars and media on separate storage servers.
 * </p>
 * 
 * <p> Caching is to be configured 'selectively' and enabled for PersistentUsers, 
 * PersistentSchools and PersistentHasRoles,assuming a single tomcat/servlet. </p>
 * 
 * @author G.A.J. van der Plas
 */
package fi.dwo.server.PersistentDataManagers.core;

