package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentMFA;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class MFAManagerPIT {

	private static DatabaseManager instance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void test() {
		EntityManager em = DwoEmfFactory.getEntityManager();
		PersistentMFA mfa = new PersistentMFA();
		mfa.setUserID(1L);
		mfa.setSecret("geheim");
		mfa.setRecovery(Collections.singletonList("oops"));
		em.getTransaction().begin();
		em.persist(mfa);
		
		mfa.setSecret("public");
		mfa.changeTimestamp();
		mfa = em.merge(mfa);
		em.getTransaction().commit();
		em.close();
	}

}
