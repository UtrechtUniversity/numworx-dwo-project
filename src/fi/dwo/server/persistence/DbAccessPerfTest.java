package fi.dwo.server.persistence;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.RandomTimer;
import com.clarkware.junitperf.TimedTest;
import com.clarkware.junitperf.Timer;
import com.jamonapi.proxy.MonProxyFactory;

import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DbAccessPerfTest extends TestCase {

	private static final String COURSE_ID = "CourseID";
	static final int CLASS = 5513; // zomergem
	static final int TEACHER = 66199;
	static final String CLASSCOURSES = "tblClassCourse";
	static DbAccessIF dbAccess;

	static class Proxy extends DbAccessProxy {

		protected DbAccessIF createDelegate() {

			return new DbAccessLocal();
		}
	}

	public DbAccessPerfTest() {
		super();
	}

	public DbAccessPerfTest(String name) {
		super(name);
	}

	public void testGetResults() throws Exception {
		//long start = System.currentTimeMillis();
		Hashtable wheredef;
		wheredef = new Hashtable();
		wheredef.put("classID", new Integer(CLASS));
		Vector courses = dbAccess.getTable(CLASSCOURSES, wheredef, COURSE_ID);
		Vector ids = new Vector(courses.size());
		for (Iterator iterator = courses.iterator(); iterator.hasNext();) {
			Hashtable object = (Hashtable) iterator.next();
			ids.add(object.get(COURSE_ID));
		}
		Object output = dbAccess.getResults(ids, CLASS, TEACHER);
		//System.out.println(output);
		//.out.println(System.currentTimeMillis()-start);
	}
	
	
	//protected void setUp() throws Exception
	static
	{
		dbAccess = new Proxy();
//		try {
//			dbAccess = new DbAccessClient(new URL("http://www.fi.uu.nl" + DbAccessCreator.SERVLET));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public static Test suite() { 
		Test test = new DbAccessPerfTest("testGetResults");
		Test timed = new TimedTest(test, 100);
		Timer timer = new RandomTimer(2,10);
		Test loaded = new LoadTest(timed, 100, timer);
		TestSuite suite = new TestSuite();
		suite.addTest(test);
		suite.addTest(timed);
		suite.addTest(loaded);
		return suite;
	}
	
}
