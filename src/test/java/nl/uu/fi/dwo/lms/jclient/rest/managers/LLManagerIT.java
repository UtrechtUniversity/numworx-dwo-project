package nl.uu.fi.dwo.lms.jclient.rest.managers;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.LoginManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicUserManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.XapiManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.dom.xapi.Verb;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class LLManagerIT {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	      Dwo2ExceptionTranslator.setTranslator(new DWO2ExceptionTranslatorInterface() {

			@Override
			public String encodeJSON(Dwo2ExceptionCode code, String message) {
				// TODO Auto-generated method stub
				return message;
			}

			@Override
			public String decodeMessageInJSON(String json) {
				// TODO Auto-generated method stub
				return json;
			}

			@Override
			public Dwo2ExceptionCode decodeCodeInJSON(String json) {
				// TODO Auto-generated method stub
				return Dwo2ExceptionCode.Client_InternalError;
			}

			@Override
			public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
				return code.toString();
			}});
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	String username = "";
	String password = "";
	
	@Before
	public void setUp() throws Exception {
		RestAuthenticator authenticator = StoredRestManager.getInstance().getAuthenticator();
		authenticator.setServerUrlPath(new URL("https://test.dwo.nl/dwo/"));
		StoredRestManager.getInstance().setBasicAuthString(username, password, null);
		LoginManager.basicLogin(username, password);
		DomContext context = new DomContext();
		authenticator.setContext(context);
		DomSchoolsRolesAndClassesV2 logins = SecureUserAccountLoginsManager.getSchoolLogins();
		context.setDomHasRole(logins.getActiveSchoolRoleAndClass().getHasRole());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Dwo2Exception, MalformedURLException, InvocationTargetException, InterruptedException {
		DomLRS result = SecureTeacherStudentModelManager.getLRS();
		URL base = StoredRestManager.getInstance().getServerUrlPath();
		XapiManager m = new XapiManager(result, base);
		// Search something
		StatementsQuery query = new StatementsQuery();
		query.relatedActivities = Boolean.TRUE;
		query.activityID = "uuid:371868-0-3fe73047a5e4447f";
		query.verbID = "http://www.dwo.nl/verbs/attempted";
		query.limit = null;
		StatementsResult response = m.queryStatements(query).getValue();
		String id = response.statements.get(0).id;
		System.out.println("found " + id);
		if (true) return;
		Statement out = new Statement();
	    out.verb = new Verb();
	    out.verb.id = "http://adlnet.gov/expapi/verbs/voided";
	    out.verb.display = Collections.singletonMap("en-US", "Voided");
	    out.object = new Activity();
	    out.object.id = id;
	    out.object.objectType = Activity.STATEMENT_REF;
		id = m.saveStatement(out).getValue();
		System.out.println("void statement = " + id);
	}

}
