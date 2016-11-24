package nl.uu.fi.dwo.account.client;

import org.osgi.util.promise.Promise;

import com.google.gwt.junit.client.GWTTestCase;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class RPCHandlerV1Test extends GWTTestCase {

	RPCHandlerV1 handler;

	@Override
	public String getModuleName() {
		return "nl.uu.fi.dwo.account.Account";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		handler = new RPCHandlerV1("http://dummytwo.dwo.nl/dwo/xmlrpc", 1);
	}

	public void testGetCoursesForClass() {
		DomSchoolClass schoolclass = new DomSchoolClass();
		schoolclass.setId(new PersistenceId());
		schoolclass.getId().setIdString("MYSQL;" + PersistenceClassType.PersistentSchoolClass + ";" + 1);
		final Promise<DomCoursesOfSchoolClass> promise = handler.getCoursesClass(schoolclass);
		promise.onResolve(
				new Runnable() {

					@Override
					public void run() {
						System.out.println(promise.getFailure());
						DomCoursesOfSchoolClass value = promise.getValue();
						System.out.println(value);
						assertNull(promise.getFailure());
						finishTest();
					}
				}
				);
		delayTestFinish(100000);
	}
	
	
}
