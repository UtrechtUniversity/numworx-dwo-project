package fi.dwo.server.mysql;

/**
 * Classes and resources to initialize empty databases for testing and new installs.
 * 
 * <ul>
 * <li> InitDatabaseNoJars.sql: Install tables in an empty MySQL database with a NullSchool and a
 * DwoAdmin school. The DwoAdmin school has a user in the (DWO)ADMIN role with username 'dwoadmin' and password
 * 'dwoadmin'.
 * <li> InitTestDatabase.sql: Install tables in an empty MySQL database with a 
 * DwoAdmin school and a user in the (DWO)ADMIN role with username 'dwoadmin' and password
 * 'dwoadmin'. It also adds test users, courses and more.
 * </ul>
 * <li> ClearTestDatabase.sql: Cleans all tables and views in an empty MySQL database.
 * </ul>
 * 
 * <p>
 * @since 1.0.3
 * @author G.A.J. van der Plas
 * 
 */