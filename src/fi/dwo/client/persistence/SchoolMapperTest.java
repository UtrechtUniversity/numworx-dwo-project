package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.gui.GuiCreatorAdmin;
import fi.dwo.client.gui.SchoolPanel;
import junit.framework.TestCase;

public class SchoolMapperTest extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(SchoolMapperTest.class);
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

}
