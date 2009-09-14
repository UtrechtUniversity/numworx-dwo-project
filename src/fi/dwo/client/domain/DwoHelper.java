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

import fi.dwo.client.gui.GuiCreator;
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

    public static AppletUtil au;
    
    private static Hashtable loadedImages;
    
    private static String key;

    public static Applet applet;
    
    private static boolean isApplication = true; // default als je setApplet niet aanroept.
    
    public static boolean umpc, contact;
    
    public static boolean scormExportLoggedIn, appletExportLoggedIn, adminLoggedIn;

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
    
    public static void setKey(String key) {
        DwoHelper.key = key;
    }
    
    public static void setUmpc(boolean b) {
        DwoHelper.umpc = b;
    }
    
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
        DwoHelper.applet = applet;
        if(applet.getParent() instanceof MainFrame) isApplication = true;
        else isApplication = false;
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
    
    public static Image getImage(String image) {
        if(loadedImages==null)loadedImages = new Hashtable();
        if(loadedImages.containsKey(image))return (Image)loadedImages.get(image);
        
        Image im = null;
        
        if(isApplication) im = au.getImage(image);
        else {
        	try {
	          	URL url = new URL(applet.getCodeBase(), image);
	            im = applet.getImage(url);
		     } 
		    catch (MalformedURLException e) {
		          im =  null;
		    }/**/
	    }
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
    { 	try {
    		JSObject.getWindow (applet).eval ("document.cookie ='" + name + "=" + value +"';");
   	    }
    	catch (Exception ex) {
    	}
    }
    
    public static void deleteCookie(String name)
    {
    	try {
    		JSObject.getWindow (applet).eval ("document.cookie ='" + name + "=dummy" + "';");
   	    }
    	catch (Exception ex) {
    	}
    }
}