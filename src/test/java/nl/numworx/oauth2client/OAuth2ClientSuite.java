package nl.numworx.oauth2client;

import nl.numworx.oauth2client.client.OAuth2ClientTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class OAuth2ClientSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for OAuth2Client");
		suite.addTestSuite(OAuth2ClientTest.class);
		return suite;
	}
}
