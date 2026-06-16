package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Time;
import java.util.Date;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class StudentScoContextManagerTest {

    static DatabaseManager instance = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	public void testCreatePersistentStudentScoContext() {		
		PersistentStudentScoContext context = createContext();
		assertEquals(3L, context.getStudentSco().longValue());
	}

	private PersistentStudentScoContext createContext() {
		PersistentStudentScoContext context = new PersistentStudentScoContext();
		Date now = new Date();
		context.setCreateDate(now);
		context.setCreateTime(new Time(now.getTime()));
		context.setScore(0f);
		
		
		context.setScoID(1L);
		PersistentHasRolePK pk = new PersistentHasRolePK(1L, 1L);
		context.setPersistentHasRolePK(pk);
		StudentScoContextManager.create(context);
		return context;
	}
	
	@Test
	public void testDualCreate() throws InterruptedException {
		Vector<Long> ids = new Vector();
		Runnable run = () -> {
			PersistentStudentScoContext context = createContext();
			ids.add(context.getStudentSco());
		};

		Vector<Throwable> v = new Vector<>();
		Thread t1 = new Thread(run);
		Thread t2 = new Thread(run);
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				v.add(e);
			} };
		t1.setUncaughtExceptionHandler(eh);
		t2.setUncaughtExceptionHandler(eh);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		assertTrue(v.isEmpty());
		System.out.println(ids);
		assertEquals(ids.firstElement(), ids.lastElement());
	}

}
