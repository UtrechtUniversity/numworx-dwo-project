package fi.servlet.dwomaccess;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.TestCase;


public class PartialScoreTest extends TestCase {

	private static final String ICONAN = "$IMAGE$MAP$";
	private int sco = 19240;
	private static final int user = 70016;
	PartialScoreIF getter;

	public void testGetScoreMapList() throws Exception {
		DWOmAccess dwOmAccess = new DWOmAccess();
		dwOmAccess.init();
		getter = dwOmAccess;
		doGetScoreMapList();
	}
	
	
	@SuppressWarnings("unchecked")
	public void doGetScoreMapList() throws Exception {
		Vector result = getter.getScoreMapList(sco, user);
		assertNotNull("geen result?", result);
		System.out.println(result);
	}

	public void testLocalhost() throws Exception {
		URL url = new URL("http://localhost:8080/DWOmAccess/partialScore");
		getter = new PartialScoreClient(url );
		doGetScoreMapList();
		doGetLaunchData();
	}
	
	public void doLaunchData(String u) throws Exception {
		URL url = new URL(u + "?s=" + sco);
		InputStream in = url.openStream();
		launchdata(in);
	}
	
	public void testDoLaunchData() throws Exception {
		doLaunchData("http://localhost:8080/DWOmAccess/getLaunchData");
	}
	
	public void testRemote() throws Exception {
		URL url = new URL("http://delta.fi.uu.nl/DWOmAccess/partialScore");
		getter = new PartialScoreClient(url );
		doGetScoreMapList();
		doGetLaunchData();
	}
	
	public void testGetLaunchData() throws Exception {
		getter = new DWOmAccess();
		doGetLaunchData();
	}
	public void testGetLaunchDataIconan() throws Exception {
		getter = new DWOmAccess();
		sco = 42736;
		String result = getter.getLaunchData(sco);
		assertNotNull(result);
		System.out.println(result);
		InputStream reader = new ByteArrayInputStream(result.getBytes());
		XMLDecoder decoder = new XMLDecoder(reader);
		Hashtable r = (Hashtable) decoder.readObject();
		System.out.println(r);
		assertNotNull(r.get(ICONAN));
		Object o = r.get(ICONAN);
		ByteArrayOutputStream bos;
		ObjectOutputStream dos = new ObjectOutputStream(bos = new ByteArrayOutputStream());
		dos.writeObject(o);
		dos.close();
		ObjectInputStream dis = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Object o2 = dis.readObject();
		assertEquals("iconan stuff", o, o2);
		
	}


	private void doGetLaunchData() throws Exception {
		
		String result = getter.getLaunchData(sco);
		assertNotNull(result);
		InputStream reader = new ByteArrayInputStream(result.getBytes());
		launchdata(reader);
	}


	private void launchdata(InputStream reader) {
		XMLDecoder decoder = new XMLDecoder(reader);
		Hashtable r = (Hashtable) decoder.readObject();
		assertEquals(20, r.size());
		System.out.println(r);
	}
	
	
}
