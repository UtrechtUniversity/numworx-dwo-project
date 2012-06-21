package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.base64code.StringCodeObject;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.persistence.ScoMapper;
import fi.dwo.server.persistence.DbAccess;
import fi.dwo.server.persistence.DbAccessLdap;
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

	private static final String PNG = "png";
	private DbAccessIF access;
		
	public void init()
	{
		access = new DbAccessLdap();
		//access = DbAccessCreator.instance();
		//log("inited...");
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
		Loader loader = Loader.create("wiskopdr.jar");
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
		else if(command.endsWith("getLaunchData"))
			doLaunchData(req,resp);
	}

	private void doLaunchData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String s = req.getParameter("s");
		String result = "";
		try {
			int sco = Integer.parseInt(s);
			result = getLaunchData(sco);
		} catch (Exception e) {
			log("doLaunchData", e);
		}
		resp.setContentType("text/xml");
		resp.setHeader("Access-Control-Allow-Origin" ,"*");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(result);
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
			result.add(new Hashtable<Object,Object>(map));
		}
		return result;
	}

	public String getLaunchData(int scoID) throws Exception {
		Hashtable map = getLaunchData_int(scoID);
		transform(map);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(out);
		encoder.writeObject(map);
		encoder.close();
		out.close();
		return new String(out.toByteArray(), "UTF-8");
	}

	private void transform(Map map) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object value = entry.getValue();
			if(value instanceof String && value.toString().startsWith("H4sIA")) {
				value = StringCodeObject.decodeStringToObject(value.toString());
				if(value != null)
				{	
					entry.setValue(value);
				}
			}

			if(value instanceof Map) {
				transform((Map) value);
			}

//			if(value instanceof Font) {
//				value = value.toString();
//				entry.setValue(value);
//			}
//			if(value instanceof java.awt.Color) {
//				value = value.toString();
//				entry.setValue(value);
//			}
// arraytypes TODO List.
			else if (value instanceof Object[]) {
				Object[] array = (Object[])value;
				entry.setValue(transform(array));
			} 
		}
		
	}

	private Object transform(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			if(value instanceof Map) 
				transform( (Map) value);
			if(value instanceof Object[]) 
				value = transform((Object[])value);
			array[i] = value;
		}
		return array;
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
