package fi.servlet.dwomaccess;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.persistence.ScoMapper;
import fi.dwo.server.persistence.DbAccess;
import fi.wiskopdr.WiskOpdr;

public class DWOmAccess extends HttpServlet implements AppletStub, AppletContext {

	private static final String PNG = "png";
	private DbAccessIF access;
	
	private int scoid = 19240;
	private int userid = 70016;
	private Hashtable parameters;
	
	
	
	public DWOmAccess() {
		super();
//		Object t = new com.eteks.awt.PJAToolkit();
//		System.setProperty("awt.toolkit", t.getClass().getName());
//		Object e = new com.eteks.java2d.PJAGraphicsEnvironment();
//		System.setProperty("java.awt.graphicsenv", e.getClass().getName());
//		System.setProperty("java.awt.fonts", "C:/Program Files/Java/jdk1.6.0_20/jre/lib/fonts");
	}
	
	public void init()
	{
		//access = new DbAccess();
		access = DbAccessCreator.instance();	
		
//		Hashtable t = access.login("r.vanalten", "");
//		userid = ((Number) t.get("userID")).intValue();
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

	private static final Object LOCATION = "cmi.core.lesson_location";
	private static final Object LESSON_MODE = "cmi.core.lesson_mode";
    
    
    
	class ScormDecorator extends JPanel implements SCORM12APIInterface 
	{


		public ScormDecorator(Component comp) {
			super(new GridLayout(1,1));
			add(comp);
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
			if(LOCATION.equals(key))
				return "1";
			String result = "";
			key = keymap.getProperty(key, key);
			result = access.LMSGetValue(scoid, userid, key);
			return result;
		}


		public String LMSInitialize(String arg0) {
			return "";
		}


		public String LMSSetValue(String arg0, String arg1) {
			return "";
		}
		
	}
	
	protected void doImage(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
			resp.setContentType("image/png");
			JFrame frame = new JFrame(); // HEADLESS FRAME
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			//frame.setLayout(null);
			frame.setSize(600,400);
			//frame.setVisible(true);
			frame.addNotify();
			WiskOpdr wiskopdr = new WiskOpdr();
			frame.setContentPane(new ScormDecorator(wiskopdr));
			
			parameters = getLauchData(scoid);
			
			wiskopdr.setStub(this);
			wiskopdr.setSize(800,600); // Wat is de juists maat???
			wiskopdr.init();
			//Dimension pref = wiskopdr.getPreferredSize();
			//wiskopdr.setSize(pref);
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

	public void appletResize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	public AppletContext getAppletContext() {
		return this;
	}

	public URL getCodeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getDocumentBase() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameter(String name) {
		return (String)parameters.get(name);
	}
	
	public boolean isActive() {
		return true;
	}

	public Applet getApplet(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration<Applet> getApplets() {
		// TODO Auto-generated method stub
		return null;
	}

	public AudioClip getAudioClip(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getImage(URL url) {
		return null;
	}

	public InputStream getStream(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getStreamKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStream(String key, InputStream stream) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void showDocument(URL url) {
		// TODO Auto-generated method stub
		
	}

	public void showDocument(URL url, String target) {
		// TODO Auto-generated method stub
		
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
	
	
	
}
