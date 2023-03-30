package fi.beans.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to implement more ClassLoader features than the default
 * ClassLoader
 * 
 * @author Wim
 *
 */

public class Loader {

	private static final Logger LOG = Logger.getLogger(Loader.class.getName());

	static URL urlPrefix;

	static {
		setPrefix("https://cdn.dwo.nl/jars/");
	}

	/**
	 * Set jar directory.
	 *
	 * @param prefix
	 *            to set
	 */
	public static void setPrefix(String prefix) {
		try {
			if (!prefix.endsWith("/"))
				prefix += "/"; // Always with /
			urlPrefix = new URL(prefix);
			LOG.log(Level.FINE, "URL Prefix set to: {0}.", new Object[] { urlPrefix.toString() });
		} catch (MalformedURLException e) {
			LOG.log(Level.SEVERE, "URL Prefix not set to: {0}.", new Object[] { prefix });
		}
	}

	/**
	 * Create a classloader that knows how to use <em>jar</em>.
	 * <br><b>Note</b>: Use this one for WiskOpdr
	 * @param jar String
	 * @param parent the parent classloader
	 * @return a classloader
	 */
	public static ClassLoader create(String jar, ClassLoader parent) {
		URL u = null;
		ClassLoader loader;
		try {
			if(jar != null) 
				u = new URL(Loader.urlPrefix, jar);
			LOG.log(Level.FINE, "URL {0} added.", new Object[] { u });
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
		if( u != null)
			loader = new LoaderImpl(new URL[] { u }, parent);
		else 
			loader = parent;
		LOG.log(Level.FINE, "Loader {0} added.", new Object[] { loader });
		return loader;
	}

	/**
	 * Create a classloader using a default parent 
	 * @param jar
	 * @return
	 */
	public static ClassLoader create(String jar) {
// FIXME find classloader of calling class!
		ClassLoader parent = Loader.class.getClassLoader(); // select the best classloader, this one is not!
		return create(jar, parent);
	}
	
	
}
