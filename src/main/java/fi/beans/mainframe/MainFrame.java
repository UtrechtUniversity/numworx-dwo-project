package fi.beans.mainframe;

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public class MainFrame extends Frame   implements WindowListener, AppletStub, AppletContext
{
    // Check that we are on Mac OS X.  This is crucial to loading and using the OSXAdapter class.
    public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    // Generic registration with the Mac OS X application menu
    // Checks the platform, then attempts to register with the Apple EAWT
    // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
    private void registerForMacOSXEvents() {
        if (MAC_OS_X) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));
               // OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
               // OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
               // OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
            }
        }
    }
	
	private Applet applet;

    public MainFrame( Applet applet, int width, int height )
	{	this.applet = applet;

		registerForMacOSXEvents();
		addWindowListener(this);
		applet.setStub( this );
		setLayout( new BorderLayout() );
		add( "Center", applet );
		applet.setSize( width, height );
		applet.init();
		applet.start();
	}
        
    @Override
	public void windowClosing(WindowEvent e)
	{	
		quit();
	}

	void quit() {
		applet.stop();
		applet.destroy();
		dispose();
		System.exit(0);
	}
    @Override
	public void windowOpened(WindowEvent e){}
    @Override
	public void windowIconified(WindowEvent e){doLayout();}
    @Override
	public void windowDeiconified(WindowEvent e){doLayout();}
    @Override
	public void windowClosed(WindowEvent e){
		
	}
    @Override
	public void windowActivated(WindowEvent e){}
    @Override
	public void windowDeactivated(WindowEvent e){}
    
    // AppletStub methodes
    @Override
    public boolean isActive(){return true;}
    @Override
    public URL getDocumentBase(){return null;}
    @Override
	public URL getCodeBase(){return null;}
    @Override
	public String getParameter(String name){return null;}
    @Override
    public void appletResize( int width, int height ){}
    @Override
    public AppletContext getAppletContext(){return this;}
    	
    // AppletContext methodes
    @Override
    public AudioClip getAudioClip( URL url )
    {
    	return java.applet.Applet.newAudioClip(url);
    }
    
    @Override
    public Image getImage( URL url )
    {	Toolkit tk = Toolkit.getDefaultToolkit();
		try
		{	ImageProducer prod = (ImageProducer) url.getContent();
		    return tk.createImage( prod );
		}
		catch ( Exception e )
		{
			return null;
		}
	}
    @Override
	public Applet getApplet( String name ){return null;}
    @Override
    public Enumeration getApplets(){return null;}
    @Override
    public void setStream(String s, InputStream is){}
    @Override
    public InputStream getStream(String s){return null;}
    @Override
    public Iterator getStreamKeys(){return null;}
    @Override
	public void showDocument( URL url ){}
    @Override
    public void showDocument( URL url, String target ){}
    @Override
    public void showStatus( String status ){}
}