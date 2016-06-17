/**
 * ****************************************************
 * File: AppletUtil.java created 16-May-00 11:54:46 AM by wim
 */
package fi.beans.appletutil;

import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.DwoLocale;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

/**
 * Standaard Fi Utilities voor applets. Gebruik voor resourceBundles, Images en
 * AudioClips
 *
 * @author Wim van Velthoven
 */
public class AppletUtil {

    private static final Logger LOG = Logger.getLogger(AppletUtil.class.getName());

    private Applet applet;
    private String packageName, language;
    private Locale locale;
    private Hashtable images = new Hashtable();

    /**
     * Geef een Applet een standaard gelocaliseerde omgeving
     *
     * @param applet
     */
    public AppletUtil(Applet applet) {
        this.applet = applet;
        language = applet.getParameter("language");
        if (language == null) {
            language = "nl";
            //Sets global state variable for 
            DwoHelper.setLocale(new DwoLocale("nl-NL"));
        }
        locale = new Locale(language, "");
        DwoHelper.setLocale(new DwoLocale("nl-NL"));
        LOG.log(Level.INFO, "Locale is set to: {0}", DwoHelper.getLocale().toString());
        try {
            Locale.setDefault(locale);
        }
        catch (SecurityException ex) {
        }

        applet.setLocale(locale);
	JComponent.setDefaultLocale(locale);
    }

    /**
     * het l10n deel van een applet.
     *
     * @param prefix bundleprefix inclusief packagenaam.
     * @return
     */
    public ResourceBundle getBundle(String prefix) {	// DIT IS 1.2
        //return ResourceBundle.getBundle(prefix, locale, applet.getClass().getClassLoader());
        // DIT IS 1.1
        return ResourceBundle.getBundle(prefix, locale);
    }

    /**
     * Haal Images als Resources op. Hack via getResourceAsStream als de
     * ClassLoader getResource niet support (Netscape) en via getCodeBase als
     * getResourceAsStream niet gesupport is.
     *
     * @param resourceName
     * @return
     * @see java.lang.ClassLoader#getResource
     */
    public Image getImage(String resourceName) {
        //URL u = applet.getClass().getResource(resourceName);
        //if(u != null) 
        //{
        //   return applet.getImage(u);
        //}
        byte[] buffer = (byte[]) images.get(resourceName);
        if (buffer == null) {
            try {
                InputStream in = applet.getClass().getResourceAsStream(resourceName);
                if (in == null) {
                    //System.err.println(resourceName + " onvindbaar");
// laatste kans via getcodebase
//		return applet.getImage(applet.getCodeBase(), getPackage() + resourceName);
                    return applet.getImage(getCodeBaseResource(resourceName));
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                buffer = new byte[1024];
                int len;
                do {
                    len = in.read(buffer);
//System.out.println("read " + len);
                    if (len > 0) {
                        bos.write(buffer, 0, len);
                    }
                } while (len > 0);
                buffer = bos.toByteArray();
                in.close();
                bos.close();
                images.put(resourceName, buffer);
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
                return null;
            }
        }
        return applet.getToolkit().createImage(buffer);
    }

    private String getPackage() {
        // dit is de fallback via codebase
        if (packageName == null) {
            String name = applet.getClass().getName();
            int i = name.lastIndexOf('.');
            if (i > 0) {
                packageName = name.substring(0, i + 1).replace('.', '/');
            } else {
                packageName = "";
            }
        }
        return packageName;
    }

    private URL getCodeBaseResource(String resource) {
        try {
            if (resource.charAt(0) == '/') {
                return new URL(applet.getCodeBase(), resource.substring(1));
            } else {
                return new URL(applet.getCodeBase(), getPackage() + resource);
            }
        }
        catch (MalformedURLException muex) {
            System.err.println(muex);
            return null;
        }
    }

    /**
     * Haal een AudioClip-Resource op. Via getResource of via getCodeBase
     * (audiofile is dan NIET in JAR file)
     *
     * @param resourceName
     * @return
     */
    public AudioClip getAudioClip(String resourceName) {
        AudioClip audio = null;
        URL u = applet.getClass().getResource(resourceName);
//System.out.println(u);
        if (u != null) {
            audio = applet.getAudioClip(u);
        }
        if (audio != null) {
            return audio;
        }

//	return applet.getAudioClip(applet.getCodeBase(),getPackage() + resourceName);
        return applet.getAudioClip(getCodeBaseResource(resourceName));
    }

    /**
     * geef mij de Locale
     *
     * @return
     * @returns locale via applet parameter "language"
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * InputStream van een willekeurige resource. Probeer via
     * getResourceAsStream of via getCodeBase
     *
     * @param resource naam van de resource
     * @return een InputStream of null
     * @see java.applet.Applet.getCodeBase()
     * @see java.lang.Class.getResourceAsStream(java.lang.String)
     *
     * @exception java.lang.SecurityException in Netscape bij een verkeerde
     * extensie
     */
    public InputStream getStream(String resource) {
        try {
            InputStream in = applet.getClass().getResourceAsStream(resource);
            if (in != null) {
                return in;
            }
        }
        catch (SecurityException sex) {
            System.err.println(sex);
        }
        URL u = getCodeBaseResource(resource);
        if (u != null) {
            try {
                InputStream in = u.openStream();
                if (in != null) {
                    return in;
                }
            }
            catch (IOException ioex) {
                System.err.println(ioex);
            }
        }
        return null;
    }
}
