package fi.servlet.dwomaccess;

import fi.servlet.dwomaccess.Loader;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Component;
import java.net.URL;

import junit.framework.TestCase;

public class LoaderTest extends TestCase {

        @Override
	protected void setUp() throws Exception {
		super.setUp();
	}

        @Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreate1() throws Exception {
		Loader loader = Loader.create("wiskopdr.jar");
		Class<?> WiskOpdr = loader.loadClass("fi.wiskopdr.WiskOpdr");
		Object o = WiskOpdr.newInstance();
		Applet a = (Applet) o;
		AppletStub stub = new AppletStub() {

                        @Override
			public void appletResize(int width, int height) {
			}

                        @Override
			public AppletContext getAppletContext() {
				return null;
			}

                        @Override
			public URL getCodeBase() {
				return null;
			}

                        @Override
			public URL getDocumentBase() {
				return null;
			}

                        @Override
			public String getParameter(String name) {
				return null;
			}

                        @Override
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
	
	
}
