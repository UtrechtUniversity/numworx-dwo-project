/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Read the configured datasource from the web.xml and configures the
 * {@Link EntityManagerFactory EntityManagerFactory} to use it.
 *
 * @author G.A.J. van der Plas
 */
public class DwoEmfFactory {

    private static final Logger log = Logger.getLogger(DwoEmfFactory.class.getName());

    private volatile static EntityManagerFactory _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB");
//uses config below, but does not load persistent classes. Needs to generate xml or so.
//<persistence-unit name="DWO_MySQLDB" transaction-type="NON_JTA_DATASOURCE">
//<!-- include all and any required persistent entity in here exclude-unlisted-classes is being ignored!-->
//    <non-jta-data-source>jdbc/dwodb</non-jta-data-source>
//    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
//    <class>fi.dwo.commons.persistence.entities.PersistentHasRole</class>
//    <class>fi.dwo.commons.persistence.entities.PersistentUser</class>
//    <class>fi.dwo.commons.persistence.entities.DwoSystemParameters</class>
//    <exclude-unlisted-classes>false</exclude-unlisted-classes>
//    <shared-cache-mode>NONE</shared-cache-mode>
//  </persistence-unit>

    public static EntityManagerFactory instance() {
        if (_instance == null) {
            synchronized (DwoEmfFactory.class) {
                if (_instance == null) {
                    try {
                        Map properties = new HashMap();
                        DataSource dataSource = getDataSource();
                        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
//                        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "FINE");
//                        properties.put(PersistenceUnitProperties.LOGGING_TIMESTAMP, "false");
//                        properties.put(PersistenceUnitProperties.LOGGING_THREAD, "false");
//                        properties.put(PersistenceUnitProperties.LOGGING_SESSION, "false");
                        _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB", properties);
                    } catch (NamingException ex) {
                        log.log(Level.SEVERE, null, ex);
                        return null;
                    }
                }
            }

        }
        return _instance;
    }

    public static EntityManager createEntityManager() {
        if(_instance==null){
             _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB");
        }
        return _instance.createEntityManager();
    }

    /**
     * Looks up the default data source in the tomcat context.xml.
     *
     * @return
     * @throws NamingException
     */
    private static DataSource getDataSource() throws NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource dataSource = (DataSource) envContext.lookup("jdbc/dwodb");
        log.log(Level.FINE, "Datasource jdbc/dwodb is: {0}.", new Object[]{dataSource.toString()});
        return dataSource;
    }

}
