/*
 * Created on Feb 25, 2005
 *
 */

package fi.dwo.client.domain;

import java.applet.Applet;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import fi.dwo.client.gui.MainPanel;
import fi.beans.appletutil.AppletUtil;
import fi.beans.mainframe.MainFrame;


/**
 * Static Helper class for the DWO.
 * 
 * @author M.J.B. Kupers
 *  
 */
public final class DwoHelper {

    private static AppletUtil au;
    
    private static Hashtable loadedImages;
    
    private static String key;

    private static Applet applet;
    
    private static boolean isApplication = true; // default als je setApplet niet aanroept.
    
    public static boolean umpc, contact;
    
    private static boolean scormExportLoggedIn, appletExportLoggedIn, adminLoggedIn;

    /**
     * Returns the current AppletUtil.
     * 
     * @return The AppletUtil.
     */
    public static AppletUtil getAu() {
        return au;
    }
    
    /**
     * Sets the AppletUtil.
     * 
     * @param au The AppletUtil to set.
     */
    public static void setAu(AppletUtil au) {
        DwoHelper.au = au;
    }
    
    /**
     * @deprecated weg ermee
     * @param key
     */
    public static void setKey(String key) {
        DwoHelper.key = key;
    }
    
    public static void setUmpc(boolean b) {
        DwoHelper.umpc = b;
    }
    /**
     * @deprecated weg ermee
     * @return the key
     */
    public static String getKey() {
        return DwoHelper.key;
    }

    /**
     * Returns the current Applet.
     * 
     * @return The current Applet.
     */
    public static Applet getApplet() {
        return applet;
    }

    /**
     * Sets the current Applet.
     * 
     * @param applet The applet to set.
     */
    public static void setApplet(Applet applet) {
// Voor Peter: in comment zetten
    	if (DwoHelper.applet != null)
    	{
    		JOptionPane.showMessageDialog(applet, "Er is al een DWO!");
    		throw new RuntimeException("Er is al een DWO"); // TODO mooier maken?
    	}
// Einde
        DwoHelper.applet = applet;
        if(applet.getParent() instanceof MainFrame) isApplication = true;
        else isApplication = false;
    }
    
    public static void clrApplet(Applet applet) {
    	if( applet == DwoHelper.applet)
    		DwoHelper.applet = null;
    }
    
    public static boolean isApplication()
    {	return isApplication;
    }
    
    public static boolean isAdminLoggedIn()
    {	return adminLoggedIn;
    }
    
    public static void setAdminLoggedIn(boolean b)
    {	adminLoggedIn = b;
    }
    
    public static boolean isScormExportLoggedIn()
    {	return scormExportLoggedIn;
    }
    
    public static void setScormExportLoggedIn(boolean b)
    {	scormExportLoggedIn = b;
    }
    
    public static boolean isAppletExportLoggedIn()
    {	return appletExportLoggedIn;
    }
    
    public static void setAppletExportLoggedIn(boolean b)
    {	appletExportLoggedIn = b;
    }
    
    public static Frame getFrameForComponent(Component owner)
    {
    	if(owner == null)
    		owner = applet;
// Java 1.2
    	return JOptionPane.getFrameForComponent(owner);
// Java 1.1 via Acme.GuiUtils, of zelf doen.
    }
    
    public static Point getComponentLocation(Component c)
    {
    	Container parent = null;
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		parent = c.getParent();
		for(int i=0 ; parent!=null && i<30 ; i++)
		{	if(parent instanceof MainPanel) 
			{	return new Point(x,y);
			}
			else 
			{	
				if(parent==null)return null;
				x += parent.getLocation().x;
				y += parent.getLocation().y;
				parent = parent.getParent();
			}
		}
		return null;
    }
    
    public static Image getResourceImage(String image)
    {
        if(loadedImages==null)loadedImages = new Hashtable();
        if(loadedImages.containsKey(image))return (Image)loadedImages.get(image);
        Image im = au.getImage(image);
        return loadImage(image, im);
    }
    
    static URL applicationBase;
    public static Image getImage(String image) {
        if(loadedImages==null)loadedImages = new Hashtable();
        if(loadedImages.containsKey(image))return (Image)loadedImages.get(image);
        
        Image im = null;
        URL url = getURL(image);
        if(url == null)
        {
        	return null;
        }
        im = applet.getImage(url);
        return loadImage(image, im);
    }

	public static URL getURL(String resource) {
		URL url = null;
        if(isApplication) 
        {	
        	if(applicationBase == null)
				try {
					applicationBase = new URL("http://www.fi.uu.nl/dwo/");
				} catch (MalformedURLException e) {
				}
			try {
				url = new URL(applicationBase, resource);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        }
        else {
        	try {
	          	url = new URL(applet.getCodeBase(), resource);
		     } 
		    catch (MalformedURLException e) {
		    }/**/
	    }
		return url;
	}

	/**
	 * @param image
	 * @param im
	 * @return
	 */
	private static Image loadImage(String image, Image im) {
		MediaTracker tr = new MediaTracker(applet);
        tr.addImage(im, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        if(im!=null)loadedImages.put(image,im);
        return im;
	}

	/**
	 * @return the contact
	 */
	public static boolean isContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public static void setContact(boolean contact) {
		DwoHelper.contact = contact;
	}
	
	public static String getCookie()
    {  	String cookie = null;
    	try {
    		cookie =(String)JSObject.getWindow (applet).eval ("document.cookie");
    	    return cookie;
    	}
    	catch(Exception ex){
    		return null;
    	}
    }
    
    
    public static String getCookie(String name)
    { 	String cookie = getCookie();
    	if(cookie==null) return null;
    	
    	String value = null;
       	String nameIs = name + "=";
    	if (cookie.length()>0){
    		int begin = cookie.indexOf(nameIs);
    		if (begin!=-1){
    			begin += nameIs.length();
    			int einde = cookie.indexOf(";", begin);
    			if (einde==-1){
    					einde = cookie.length();
    				}
    			value = cookie.substring(begin, einde);
    			return value;
    		}
    	}
    	return null;
    }
    
    public static void setCookie(String name, String value)
    { 	if(isApplication())return;
    	try {
    		JSObject.getWindow (applet).eval ("document.cookie ='" + name + "=" + value +"';");
   	    }
    	catch (Exception ex) {
    	}
    }
    
    public static void deleteCookie(String name)
    {	if(isApplication())return;
    	try {
    		JSObject.getWindow (applet).eval ("document.cookie ='" + name + "=dummy" + "';");
   	    }
    	catch (Exception ex) {
    	}
    }
}