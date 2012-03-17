package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.persistence.ScoMapper;
import fi.dwo.server.persistence.DbAccess;
import fi.wiskopdr.WiskOpdr;

public class DWOmAccess extends Servlet implements AppletContext, PartialScoreIF {

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
		//access = new DbAccess();
		access = DbAccessCreator.instance();	
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
	
	public Hashtable getLauchData( int scoid )
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
				return getLauchDataString();
			String result = "";
			key = keymap.getProperty(key, key);
			result = access.LMSGetValue(scoid, userid, key);
			return result;
		}

		public String getLauchDataString()
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
	
	final static String SCOID = "s";
	final static String USERID = "u";
	final static String LOCATION = "l";
	final static String WIDTH = "w";
	final static String HEIGHT = "h";
	
	
	
	protected void doImage(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int scoid = 19240;
	 	int userid = 70016;
		int width = 800;
		int height = 600;
		String location = "0";
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

		Hashtable parameters;

		resp.setContentType("image/png");
		JFrame frame = new JFrame(); // HEADLESS FRAME
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(width,height);
		frame.addNotify();
		WiskOpdr wiskopdr = new WiskOpdr();
		frame.setContentPane(new ScormDecorator(wiskopdr, scoid, userid, location));
		
		parameters = getLauchData(scoid);
		
		wiskopdr.setStub(new Stub(parameters));
		wiskopdr.setSize(width,height); // Wat is de juists maat???
		wiskopdr.init();
		wiskopdr.validate();
		wiskopdr.doLayout();
		wiskopdr.start();
		BufferedImage f = createImage(wiskopdr.getContentPane());
		OutputStream out = resp.getOutputStream();
		sendImage(f, out);
		
		wiskopdr.stop();
		wiskopdr.destroy();
		frame.dispose(); // on close ....
		
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
		doImage(req, resp);
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
		WiskOpdr wiskopdr = new WiskOpdr();
		ScormDecorator api = new ScormDecorator(wiskopdr, sco, user, "");
		List list = wiskopdr.getScoreMapList(api);
// convert to vector of hashtable
		Vector result = new Vector(list.size());
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Map map = (Map) iter.next();
			result.add(new Hashtable<Object,Object>(map));
		}
		return result;
	}
	
	
	
}
