package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.gui.GuiCreatorAdmin;
import fi.dwo.dwojapplet.gui.SchoolPanel;
import fi.dwo.dwojapplet.persistence.MapperCreator;
import junit.framework.TestCase;

public class SchoolMapperTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SchoolMapperTest.class);
	}

	/*
	 * Test method for 'fi.dwo.client.persistence.XmlRpcMapper.get()'
	 */
	public void testGet() throws IOException, SQLException, XmlRpcException {
		long tim0 = System.currentTimeMillis();
		MapperCreator.instance(School.class).get();
		long tim1 = System.currentTimeMillis();
		System.out.println(tim1- tim0);
		tim1 = System.currentTimeMillis();
		MapperCreator.instance(School.class).get();
		System.out.println(System.currentTimeMillis()- tim1);
	}

	public void testSchoolPanel() throws Exception {
		DWO.main(new String[0]);
		new GuiCreatorAdmin((DwoIF) DwoHelper.getApplet());
		long tim0 = System.currentTimeMillis();
		new SchoolPanel();
		long tim1 = System.currentTimeMillis();
		System.out.println(tim1- tim0);
		tim1 = System.currentTimeMillis();
		new SchoolPanel();
		System.out.println(System.currentTimeMillis()- tim1);
	}

	public void testSchoolById() throws Exception {
		Object o = MapperCreator.instance(School.class).get(100);
		assertNotNull(o);
		School s = (School)o;
		assertEquals("schoolid", 100, s.getSchoolID());
		System.out.println(s.getPasswd(1));
	}
	
}
