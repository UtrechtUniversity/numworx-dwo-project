package fi.servlet.dwomaccess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.xmlrpc.applet.XmlRpcException;

import junit.framework.TestCase;

public class DWOmAccessTest extends TestCase {
	DWOmAccess access;

	@Override
	protected void setUp() throws Exception {
		access = new DWOmAccess();
		final ServletContext context = new ServletContext() {

			@Override
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletContext getContext(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInitParameter(String arg0) {
				// TODO Auto-generated method stub
				return "http://dummyone.dwo.nl/dwo/dsaccess"; // to database
			}

			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getMimeType(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public RequestDispatcher getNamedDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRealPath(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getResource(String arg0) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getResourceAsStream(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set getResourcePaths(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServerInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Servlet getServlet(String arg0) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServletContextName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServletNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServlets() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void log(String arg0) {
				System.err.println(arg0);
				
			}

			@Override
			public void log(Exception arg0, String arg1) {
				log(arg1, arg0);
			}

			@Override
			public void log(String arg0, Throwable arg1) {
				System.err.println(arg0);
				if(arg1 != null) arg1.printStackTrace();
				
			}

			@Override
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			/**
			 * @since 2.5
			 * @return the context path
			 */
			// @Override 
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		ServletConfig config = new ServletConfig() {

			@Override
			public String getInitParameter(String arg0) {
				return null;
			}

			@Override
			public Enumeration getInitParameterNames() {
				return null;
			}

			@Override
			public ServletContext getServletContext() {
				return context;
			}

			@Override
			public String getServletName() {
				// TODO Auto-generated method stub
				return "DWOmAccessTest";
			}};
		access.init(config);
	}

	@Override
	protected void tearDown() throws Exception {
		access.destroy();
	}

	public void testGetLaunchDataBytes() throws IOException, XmlRpcException, SQLException {
		int sco = 105645; // wim kladje test
		sco = 10612;
		OutputStream out = new FileOutputStream("test.json.gz");
		out.write(access.getLaunchDataBytes(sco));
		out.close();
		// TODO verity no base64 strings in text.xml.
		access.copy( new GZIPInputStream(new FileInputStream("test.json.gz")), System.out);
	}

}
