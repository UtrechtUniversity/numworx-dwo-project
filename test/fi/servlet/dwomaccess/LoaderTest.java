package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Component;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.dwojapplet.persistence.DbAccessBridge;
import fi.servlet.dwomaccess.DWOmAccess.ExtraScoMapper;
import junit.framework.TestCase;

public class LoaderTest extends TestCase {

	protected void setUp() throws Exception {
		Loader.URL_PREFIX = "http://local.dwo.nl/dwo/jars/";
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreate1() throws Exception {
		Loader loader = Loader.create("wiskopdr.jar");
		Class<?> WiskOpdr = loader.loadClass("fi.wiskopdr.WiskOpdr");
		Object o = WiskOpdr.newInstance();
		Applet a = (Applet) o;
		AppletStub stub = new AppletStub() {

			public void appletResize(int width, int height) {
			}

			public AppletContext getAppletContext() {
				return null;
			}

			public URL getCodeBase() {
				return null;
			}

			public URL getDocumentBase() {
				return null;
			}

			public String getParameter(String name) {
				return null;
			}

			public boolean isActive() {
				return false;
			}
			
		};
		a.setStub(stub);
		a.init();
		a.start();
		fi.beans.scorm.PartialScoreIF p = (fi.beans.scorm.PartialScoreIF) o;
		Component c = p.getContentPage();
		assertNotNull(c);
		
	}

	
	public void testCreate2() throws Exception {
		Loader loader = Loader.create("dwo.jar");
		Class<?> geo = loader.loadClass("geogebra.GeoGebra");
		Class<?> g2  = loader.loadClass("v3.geogebra.GeoGebra");
		Object r = geo.newInstance();
		Object r2 = g2.newInstance();
		assertNotNull(r);
		assertNotNull(r2);
	}
	
	public void testCreate3() throws Exception {
		DbAccessIF instance = DbAccessBridge.createClient(null)
//		{
//			@Override
//			public Vector getTable(String tbl, Vector vv, Hashtable key, String value) {
//				Hashtable result = new Hashtable();
//				result.put(key, value);
//				result.put("name", String.valueOf(value));
//				result.put("launchdata", "H4sIAAAAAAAAAD2S246TYBSFcXQyo2Ni4ryAxnhFMhwL5Uqh0KFQoNDSlt5Mfg4tlH+A/hzLhU/kA5j4Ot76DjJGZ2WvnbX3uv2+/8YuS4TdHkED7uoqgXcqKOMK+DC6/fnu80ft14+X2MUUewNzEE5BUOVohr2uYhSVcQ7DrvjyFXvSdXs97JvBVxV25R+CHOZoSJ+mf1Vh7wHIKgCtIkQgiKsoe6Aq7MXgV495GA2RrLC3+b/2gRrai7HKljPxv+6pZUMTK1ea4JqYuwW3jD13kwCYyecUFJQ0lnSHEpWooI8Hwj9HhjAW2NHZW4HCobkK1qqc9yNxJHNmPzmvOxchJTYWQaTRZOyVKYHzNc83qGE0hMdc0pJ4f1TW5akDNR0nXSw4AY2gEyKfEQRESiOp76BhHiU1bvw4DYLNVnfcQGEjn9Ro38A3xbxZMo97AzrFXFtPcNrRT9RERUvqONVVfBy2EYF6VuACo7CtXIb30FQ4Mfc44Ix9j5UZfzMLDOhRi8f2jKZWdVhQ6r3lKryWx7LQbnD7tFVIpVv2wGGWwVZo7aOzK9Np7/upzevIHO33RMyFjTQ3Bdiv7NqqUWHiQplGqzG3pwKuXo1L0TtkokcfWXMy4xULMnRia9y6Cwh5pDSjMJZ8OelkpknXrisJs3a1jSczkvXrcEFo+rYrXTcMTdWSuv0s6TxkEPyuMhtGoFQ7Md1TR3Y6m4e7nZSzfcP3G9UyYGY5yCKlltKV1SlfT5caZYe8IZ/0+aGdM9sJoRPbcwAosNnZaS040aaU2/Se5Bcsz1N02XPCdOamrNCIAWcs92tePIjiM21iUCVNUkXDZCfsG3Y5MAaef08E3lhZGKEwiuCH4bqGIDvU4DDweJHB7g/eI0/yHQMAAA==");
//				result.put("image", "");
//				result.put("dwoProfileID", 1);		
//				Vector  v = new Vector();
//				v.add(result);
//				return v;
//			}
//			
//		}
		;
		DbAccessBridge.setInstance(instance);
		DWOmAccess access = new DWOmAccess();
		access.access = instance;
		ExtraScoMapper mapper = access.new ExtraScoMapper();
		Object result = mapper.getLaunchData(123123123);
		assertNotNull(result);
	}
}
