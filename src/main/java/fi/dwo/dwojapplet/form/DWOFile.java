package fi.dwo.dwojapplet.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.SAXException;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.dwojapplet.persistence.DbAccessIF;
//import fi.dwo.server.persistence.DbAccessLocal;

public class DWOFile {

    // DWO v 1.0
    public static byte[] prefix = {'D', 'W', 'O', 'v', 1, 0};

    ManifestFile m;

    public DWOFile(DbAccessIF dbAccess) {
        m = new ManifestFile(dbAccess);
    }

    /**
     * @param course
     * @param scoid
     * @param out
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SQLException
     * @throws IOException
     * @throws XmlRpcException
     * @see fi.dwo.server.form.ManifestFile#createIMSManifest(int, int,
     * java.io.OutputStream)
     */
    public void createIMSManifest(int course, int scoid, OutputStream out)
            throws ParserConfigurationException, TransformerException,
            SQLException, IOException, XmlRpcException {

        out.write(prefix);
        GZIPOutputStream gout;
        out = gout = new GZIPOutputStream(out);
        m.createIMSManifest(course, scoid, out);
        gout.finish();
    }

    /**
     * @param course
     * @param dwoProfile
     * @param schoolID
     * @param parent
     * @return
     * @throws DwoXmlRpcException
     * @throws SQLException
     * @throws IOException
     * @throws XmlRpcException
     * @see fi.dwo.server.form.ManifestFile#addCourse(java.util.Hashtable, int,
     * int)
     */
    public int addCourse(Hashtable course, int dwoProfile, int schoolID, int parent)
            throws DwoXmlRpcException, SQLException, IOException,
            XmlRpcException {
        return m.addCourse(course, dwoProfile, schoolID, parent);
    }

    /**
     * @param input
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @see
     * fi.dwo.server.form.ManifestFile#inputIMSManifest(java.io.InputStream)
     */
    public Hashtable inputIMSManifest(InputStream input)
            throws ParserConfigurationException, SAXException, IOException {
// slecht één versie, 
        for (int i = 0; i < prefix.length; i++) {
            if (prefix[i] != input.read()) {
                throw new IOException();
            }
        }
        input = new GZIPInputStream(input);
        return m.inputIMSManifest(input);
    }

// TODO Wim. Do we need this?	
//	public static void main(String[] args) throws Exception
//	{
//		DWOFile f = new DWOFile(new DbAccessLocal());
//		File file = new File(System.getProperty("user.home"));
//		file = new File(file, ManifestFile.FILENAME);
//		OutputStream output = new FileOutputStream(file);
//		f.createIMSManifest(226, -1, output);
//		output.close();
//		InputStream input = new FileInputStream(file);
//		Hashtable r = f.inputIMSManifest(input);
//		System.out.println(r);
//		
//		//int c = f.addCourse(r, 3, 0);
//		//System.out.println("courseID="+c);
//	}
    public void appendCourse(int courseID, int offset, Hashtable course) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        m.appendCourse(courseID, offset, course);
    }
}
