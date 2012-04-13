package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Component;
import java.net.URL;

import junit.framework.TestCase;

public class LoaderTest extends TestCase {

	protected void setUp() throws Exception {
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
		Loader loader = Loader.create("wiskopdr.jar");
		Class<?> geo = loader.loadClass("geogebra.GeoGebra");
		geo.newInstance();
	}
	
	
}
