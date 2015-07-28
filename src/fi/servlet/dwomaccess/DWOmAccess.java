package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import fi.beans.base64code.StringCodeObject;
import fi.beans.dwomaccess.ByteArray;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.persistence.CourseMapper;
import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.persistence.ScoMapper;
import fi.dwo.server.persistence.DbAccess;
import fi.dwo.server.persistence.DbAccessLdap;
import fi.dwo.server.persistence.DbAccessLocal;
/**
 * Servlet voor het achterhalen van de deelscores en screenshots. 
 * Methoden:
 * <ul>
 * <li>getScoreMapList - XMLRPC interface naar de methode met de zelfde naam in WiskOpdr
 * <li>doImage - screenshot van wiskopdr
 * </ul>
 * @author velth101
 *
 */
public class DWOmAccess extends Servlet implements AppletContext, PartialScoreIF {

	private static final String UTF_8 = "UTF-8";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1809226772104746936L;



	public class Stub implements AppletStub {

		private Hashtable parameters;
		public Stub(Hashtable parameters) {
			this.parameters = parameters;
		}
		
		
		
		public String getParameter(String name) {
			return (String)parameters.get(name);
		}
		
		public boolean isActive() {
			return true;
		}
		public void appletResize(int width, int height) {
		}
		public AppletContext getAppletContext() {
			return DWOmAccess.this;
		}
		public URL getCodeBase() {
			// TODO Auto-generated method stub
			return null;
		}
		public URL getDocumentBase() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public class ExtraScoMapper extends ScoMapper {
		
		public byte[] getLaunchDataBytes(int scoid) throws IOException, XmlRpcException, SQLException {
			Vector v = new Vector();
			v.add("launchdatabytes");
			
			Hashtable wheredef = new Hashtable();
			wheredef.put(getIDCol(), scoid);
			v = access.getTable(getTableName(), v, wheredef, getOrderbyCol());
			if(v.size() > 0) {
				Hashtable h = (Hashtable) v.firstElement();
				Object object = h.get("launchdatabytes");
				if(object instanceof byte[])
					return (byte[]) object;
			}
			return NULL;
		}
		
	}
	
	static byte[] NULL = new byte[0];

	private static final String PNG = "png";

	DbAccessIF access;
		
	public void init() throws ServletException
	{
		access = DbAccessFactory.getDbAccess(getServletContext());
	}
	
	public void sendImage(BufferedImage image, OutputStream out)
	{
		try {
			ImageIO.write(image, PNG, out);
			out.close();
		} catch (IOException e) {
			log("sendimage: "+ e, e);
		}
	}

	public BufferedImage createImage(Component comp) {
		int width = comp.getWidth();
		int height = comp.getHeight();
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	    Graphics2D g2d = bufferedImage.createGraphics();
	    try {
	    	comp.print(g2d);
	    } catch(Exception e)
	    {
	    	log("createImage:" + e, e);
	    }
	    g2d.dispose();

		return bufferedImage;
	}
	
	private Hashtable getLaunchData_int( int scoid ) throws IOException, XmlRpcException, SQLException
	{
		ScoMapper mapper = new ScoMapper();
		Sco sco = (Sco) mapper.get(scoid);
		return sco.getLaunchdata();
	}
	
	byte[] getLaunchDataBytes( int scoid ) throws IOException, XmlRpcException, SQLException 
	{
		byte[] bytes = extraScoMapper.getLaunchDataBytes( scoid );
		if(bytes.length == 0)
		{
// SLOW.....	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream zip = new GZIPOutputStream(bos);
			OutputStreamWriter out = new OutputStreamWriter(zip, UTF_8);
			@SuppressWarnings("rawtypes")
			Map map = getLaunchData_int(scoid);
			JSONEncoder.encode(map, out);
			out.close();
			bytes = bos.toByteArray();
		}
		return bytes;
	}
	
	@SuppressWarnings("unchecked")
	private Hashtable getCourseDescription_int( int courseid) throws IOException, XmlRpcException, SQLException
	{
		CourseMapper mapper = new CourseMapper();
		Course course = (Course) mapper.get(courseid);
		String description = course.getDescription();
		Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(description);
		return map;
	}

	
    private static final String[][] scormDatabaseLink = {
        { "cmi.core.score.raw", "score" },
        { "cmi.suspend_data", "suspendData" },
        { "core.score.raw", "score" }, 
        { "suspend_data", "suspendData" },
        { "cmi.core.session_time", "session_time" }, 
        { "cmi.core.total_time", "total_time" },
        { "core.session_time", "session_time" }, 
        { "core.total_time", "total_time" }
    };
    
	private static final Properties keymap = new Properties();
	static {
		for (int i = 0; i < scormDatabaseLink.length; i++) {
			keymap.put(scormDatabaseLink[i][0], scormDatabaseLink[i][1]);
		}
	}

	static final Object LESSON_LOCATION = "cmi.core.lesson_location";
	static final Object LESSON_MODE = "cmi.core.lesson_mode";
	static final Object LAUNCH_DATA = "cmi.launch_data";

    
    
	class ScormDecorator extends JPanel implements SCORM12APIInterface 
	{

		private int scoid, userid;
		private String location;
		public ScormDecorator(Component comp, int scoid, int userid, String location) {
			super(new GridLayout(1,1));
			add(comp);
			this.scoid = scoid;
			this.userid= userid;
			this.location = location;
		}


		public String LMSCommit(String arg0) {
			return "";
		}

		public String LMSFinish(String arg0) {
			return "";
		}


		public String LMSGetDiagnostic(String arg0) {
			return "";
		}


		public String LMSGetErrorString(String arg0) {
			return "";
		}


		public String LMSGetLastError() {
			return "101";
		}


		public String LMSGetValue(String key) {
			if(LESSON_MODE.equals(key))
				return "review";
			if(LESSON_LOCATION.equals(key))
				return location;
			if(LAUNCH_DATA.equals(key))
				try {
					return getLauchDataString();
				} catch (Exception e) {
					e.printStackTrace();
					return "";
				} 
			String result = "";
			key = keymap.getProperty(key, key);
			try {
				result = access.LMSGetValue(scoid, userid, key);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return result;
		}

		public String getLauchDataString() throws IOException, XmlRpcException, SQLException
		{
			ScoMapper mapper = new ScoMapper();
			Sco sco = (Sco) mapper.get(scoid);
			return sco.getLaunchdataString();
		}

		public String LMSInitialize(String arg0) {
			return "";
		}


		public String LMSSetValue(String arg0, String arg1) {
			return "";
		}
		
	}
	
	/**
	 * Parameter voor doImage. 
	 */
	final static String SCOID = "s";
	/**
	 * Parameter voor doImage. 
	 */
	final static String USERID = "u";
	/**
	 * Parameter voor doImage. 
	 */
	final static String LOCATION = "l";
	/**
	 * Parameter voor doImage. 
	 */
	final static String WIDTH = "w";
	/**
	 * Parameter voor doImage. 
	 */
	final static String HEIGHT = "h";
	/**
	 * Parameter voor doImage.
	 */
	final static String APPLET = "a";
	
	/**
	 * standaard paramater: taal.
	 */
	final static String LANGUAGE = "language";
	/**
	 * standaard parameter: achtergrondkleur
	 */
	final static String BGCOLOR  = "bgcolor";
	
	
	
	
	
	
	/**
	 * Genereer een screenshot van WiskOpdr via het HTTP protocol.
	 * Aanroep:<br>
	 * http://..../image.png?s=<i>scoid</i>&u=<i>userid</i>&l=<i>location</i>&w=<i>width</i>&h=<i>height</i>
	 * <p>Parameters: 
	 * <dl>
	 * <dt>scoid
	 * <dd>nummer van sco
	 * <dt>userid
	 * <dd>id van gebruiker
	 * <dt>location
	 * <dd>identificatie van pagina, zoals verkregen via @{link {@link #getScoreMapList(int, int)}. Default "0"
	 * <dt>width
	 * <dd>breedte van de screenshot, default "800"
	 * <dt>height
	 * <dd>hoogte van de screenshot, default "500"
	 * </dl>
	 * @param req http request
	 * @param resp een PNG image.
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doImage(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int scoid = 19240;
	 	int userid = 70016;
		int width = 800;
		int height = 500;
		String location = "0";
		String language = "nl";
		String bgcolor = "#FFFFFF";
		boolean full = false;
		String param;
		
		param = req.getParameter(SCOID);
		if(notEmpty(param))
			scoid = Integer.parseInt(param);
		param = req.getParameter(USERID);
		if(notEmpty(param))
			userid = Integer.parseInt(param);
		param = req.getParameter(LOCATION);
		if(param != null)
			location = param;
		param = req.getParameter(HEIGHT);
		if(notEmpty(param))
			height = Integer.parseInt(param);
		param = req.getParameter(WIDTH);
		if(notEmpty(param))
			width = Integer.parseInt(param);
		param = req.getParameter(APPLET);
		if(notEmpty(param))
			full = true;
		param = req.getParameter(LANGUAGE);
		if(notEmpty(param))
			language = param;
		param = req.getParameter(BGCOLOR);
		if(notEmpty(param))
			bgcolor = param;		
		log("bcolor = " + bgcolor);
		Hashtable parameters;

		resp.setContentType("image/png");
		JFrame frame = new JFrame(); // HEADLESS FRAME
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(width,height);
		frame.addNotify();
		Applet wiskopdr = null;//new WiskOpdr();
		wiskopdr = createApplet(scoid); 
		if(wiskopdr == null)
			return;
		frame.setContentPane(new ScormDecorator(wiskopdr, scoid, userid, location));
		
		try {
			parameters = getLaunchData_int(scoid);
		} catch (XmlRpcException e) {
			throw new IOException(e.getMessage());
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
// standaard parameters: background color is white, language = nl
		parameters.put("bgcolor", bgcolor);
		parameters.put("language", language);
		wiskopdr.setStub(new Stub(parameters));
		wiskopdr.setSize(width,height); // Wat is de juists maat???
		wiskopdr.init();
		wiskopdr.validate();
		wiskopdr.doLayout();
		wiskopdr.start();
		BufferedImage f;
		if(full)
			f = createImage(wiskopdr);
		else
			f = createImage(((fi.beans.scorm.PartialScoreIF) wiskopdr).getContentPage());
		OutputStream out = resp.getOutputStream();
		sendImage(f, out);
		
		wiskopdr.stop();
		wiskopdr.destroy();
		frame.dispose(); // on close ....
		
	}

	private Class<Applet> lastClass;
	private String lastName;
	private final ExtraScoMapper extraScoMapper = new ExtraScoMapper();
	/**
	 * Laad applet die bij sco hoort.
	 * Caching last class d.m.v. lastClass/lastName.
	 * @param scoid
	 * @return the applet.
	 */	
	@SuppressWarnings("unchecked")
	private synchronized Applet createApplet(int scoid) {
		String name = "fi.wiskopdr.WiskOpdr"; // uitzoeken
		if (lastClass != null && name.equals(lastName)) 
			try {
				log("use " + lastClass  + " for " + name);
				return lastClass.newInstance();
			} catch (Exception e1) {
				log("newInstance of " + name, e1);
				e1.printStackTrace();
			}		
		Loader loader = Loader.create("dwo.jar"); // Helaas, wiskopdr.jar geen goede index.
		try {
			Class<?> clazz =  loader.loadClass(name);
			lastClass = (Class<Applet>) clazz;
			lastName = name;
			return lastClass.newInstance();
		} catch (Exception e) {
			log("loading of " + name, e);
			return null;
		}
	}

	/**
	 * @param param
	 * @return
	 */
	private static boolean notEmpty(String param) {
		return param != null && param.length()!= 0;
	}


	public void showStatus(String status) {
		log(status);
	}

	/* (non-Javadoc)
	 * @see com.eteks.awt.servlet.PJAServlet#doGetPJA(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String command = req.getServletPath();
		if(command.endsWith("image.png"))
			doImage(req, resp);
//		else if(command.endsWith("getLaunchData"))
//			doLaunchData(req,resp);
		else if(command.endsWith("getCourseDescription"))
			doCourseDescription(req,resp);
		else if(command.endsWith("getJSONLaunchData"))
			doJSONLaunchData(req, resp);
		else if(command.endsWith("getJSONLaunchDataBytes"))
			doJSONLaunchData_fast(req, resp);
	}

	void doCourseDescription(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String c = req.getParameter("c");
		OutputStream out = getOutputStream(req, resp);
		try {
			int course = Integer.parseInt(c);
			getCourseDescription(course, out);

		} catch (Exception e) {
			log("doCourseDescription", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		
	}

	
//	private void doLaunchData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		String s = req.getParameter("s");
//		OutputStream out = getOutputStream(req, resp);
//		try {
//			int sco = Integer.parseInt(s);
//			getLaunchData(sco, out);
//		} catch (Exception e) {
//			log("doLaunchData", e);
//			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
//		
//	}

	private void doJSONLaunchData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String s = req.getParameter("s");
		Writer out = getWriter(req, resp);
		try {
			int sco = Integer.parseInt(s);
			getJSONLaunchData(sco, out);
		} catch (Exception e) {
			log("doLaunchData", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
	}
	
	private void doJSONLaunchData_fast(HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		String s = req.getParameter("s");
		String encoding = req.getHeader("Accept-Encoding");		
		resp.setContentType("application/json");
		resp.setHeader("Access-Control-Allow-Origin" ,"*");
		resp.setCharacterEncoding(UTF_8);
		OutputStream out = resp.getOutputStream();
		try {
			byte[] data = getLaunchDataBytes(Integer.parseInt(s));
			if( encoding != null && encoding.contains("gzip")) {
				resp.setHeader("Content-Encoding", "gzip");
				out.write(data);
			} else {
				InputStream in = new GZIPInputStream(new ByteArrayInputStream(data));
				if (encoding != null && encoding.contains("deflate")) {
					resp.setHeader("Content-Encoding", "deflate");
					out = new DeflaterOutputStream(resp.getOutputStream());
				}
				copy(in,out);
				in.close();			
			}
			out.close();
		} catch (Exception e) {
			log("doLaunchData", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} 
	}
	
	

	void copy(InputStream in, OutputStream out) throws IOException {
		byte buf[] = new byte[1024];
		int len;
		while( -1 != (len = in.read(buf))) out.write(buf, 0, len);
	}

	private Writer getWriter(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		resp.setHeader("Access-Control-Allow-Origin" ,"*");
		resp.setCharacterEncoding(UTF_8);
		return resp.getWriter();
	}

	private OutputStream getOutputStream(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String encoding = req.getHeader("Accept-Encoding");		
		resp.setContentType("text/xml");
		resp.setHeader("Access-Control-Allow-Origin" ,"*");
		resp.setCharacterEncoding(UTF_8);
		OutputStream out;
// insert compressor		
		if( encoding != null && encoding.contains("gzip")) {
			resp.setHeader("Content-Encoding", "gzip");
			out = new GZIPOutputStream(resp.getOutputStream());
		} else if (encoding != null && encoding.contains("deflate")) {
			resp.setHeader("Content-Encoding", "deflate");
			out = new DeflaterOutputStream(resp.getOutputStream());
		} else 
// no compression
			out = resp.getOutputStream();
		return out;
	}

//	void getLaunchData(int sco, OutputStream out) throws IOException {
//		Hashtable<?,?> map;
//		try {
//			map = getLaunchData_int(sco);
//			XmlEncoder.encode(map, out);
//		} catch (XmlRpcException e) {
//			throw new IOException(e.getMessage());
//		} catch (SQLException e) {
//			throw new IOException(e.getMessage());
//		}
//		out.close();
//	}
	
	void getJSONLaunchData(int sco, Writer out) throws IOException {
		Hashtable<?,?> map;
		try {
			map = getLaunchData_int(sco);
			JSONEncoder.encode(map, out);
		} catch (XmlRpcException e) {
			throw new IOException(e.getMessage());
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
		out.close();
	}
	


	void getCourseDescription(int course, OutputStream out) throws IOException {
		Hashtable<?,?> map;
		try {
			map = getCourseDescription_int(course);
			OutputStreamWriter w = new OutputStreamWriter(out,"UTF-8");
			if(map != null)
				JSONEncoder.encode(map, w);
			w.flush();
		} catch (XmlRpcException e) {
			throw new IOException(e.getMessage());
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
		out.close();
	}
	
	
	// AppletContext Dummies
	
	public Applet getApplet(String name) {
		return null;
	}

	public Enumeration<Applet> getApplets() {
		return null;
	}

    // AppletContext methodes
    public AudioClip getAudioClip( URL url )
    {
    	return java.applet.Applet.newAudioClip(url);
    }
    
    public Image getImage( URL url )
    {	
    	try
		{	
		    return Toolkit.getDefaultToolkit().createImage( url );
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	public InputStream getStream(String key) {
		return null;
	}

	public Iterator<String> getStreamKeys() {
		return null;
	}

	public void setStream(String key, InputStream stream) throws IOException {
	}

	public void showDocument(URL url) {
	}

	public void showDocument(URL url, String target) {
	}

	@SuppressWarnings("unchecked")
	public Vector getScoreMapList(int sco, int user) throws Exception {
		Applet wiskopdr = createApplet(sco);
		ScormDecorator api = new ScormDecorator(wiskopdr, sco, user, "");
		List list = ((fi.beans.scorm.PartialScoreIF) wiskopdr).getScoreMapList(api);
// convert to vector of hashtable
		Vector result = new Vector(list.size());
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Map map = (Map) iter.next();
			for(Object item: map.entrySet())
			{ Entry entry = (Entry) item;
			  if(entry.getValue() == null) entry.setValue("");
			}
			result.add(new Hashtable<Object,Object>(map));
		}
		return result;
	}

	public String getLaunchData(int scoID) throws Exception {
		Hashtable map = getLaunchData_int(scoID);
		StringWriter out = new StringWriter();
		JSONEncoder.encode(map, out);
		out.close();
		return out.toString();
	}


	public String getCourseDescription(int courseID) throws Exception {
		Hashtable map = getCourseDescription_int(courseID);
		StringWriter out = new StringWriter();
		JSONEncoder.encode(map, out);
		out.close();
		return out.toString();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * Access-Control-Allow-Origin: http://foo.example
		 * Access-Control-Allow-Methods: POST, GET, OPTIONS
		 */
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		resp.setContentType("text/plain");
		resp.getOutputStream().close();
	}

}
