package fi.dwo.server.persistence;

import java.util.Properties;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Read the configured data-source from the web.xml and configures the
 * {@Link EntityManagerFactory EntityManagerFactory} to use it.
 *
 * @author G.A.J. van der Plas
 */
public class DwoEmfFactory {

    private static final Logger LOG = Logger.getLogger(DwoEmfFactory.class.getName());

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

    /**
	 * @return the _instance
	 */
	public static EntityManagerFactory get_instance() {
		return _instance;
	}

	/**
	 * @param _instance the _instance to set
	 */
	public static void set_instance(EntityManagerFactory _instance) {
		DwoEmfFactory._instance = _instance;
	}

	public static EntityManagerFactory instance() {
        if (_instance == null) {
            synchronized (DwoEmfFactory.class) {
                if (_instance == null) {
                    _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB");
                }
            }

        }
        return _instance;
    }

    public static EntityManager getEntityManager() {
        if (_instance == null) {
             synchronized (DwoEmfFactory.class) {
               if (_instance == null) {
                   _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB");
               }
             }
        }
        return _instance.createEntityManager();
    }

    public static EntityManagerFactory setEntityManagerFactory(String persistenceUnit) {
             synchronized (DwoEmfFactory.class) {
                return _instance = Persistence.createEntityManagerFactory(persistenceUnit);
               }
    }

    public static EntityManagerFactory setEntityManagerFactory(String persistenceUnit, Properties prop) {
        synchronized (DwoEmfFactory.class) {
           return _instance = Persistence.createEntityManagerFactory(persistenceUnit, prop);
          }
}

    public static void setDefaultEntityManagerFactory() {
             synchronized (DwoEmfFactory.class) {
                   _instance = Persistence.createEntityManagerFactory("DWO_MySQLDB");
               }
    }
    
    
}
