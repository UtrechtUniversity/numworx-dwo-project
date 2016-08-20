package fi.dwo.dwojapplet.system;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(Loader.class.getName());

    private static URL urlPrefix;

    static {
        // set default directory for jars
        setPrefix("https://app.dwo.nl/dwo/jars/");
    }

    /**
     * Set jar directory.
     *
     * @param prefix to set
     */
    public static void setPrefix(String prefix) {
        try {
        	if(!prefix.endsWith("/")) prefix += "/"; // Always with / 
            urlPrefix = new URL(prefix);
            LOG.log(Level.FINE, "URL Prefix set to: {0}.", new Object[]{urlPrefix.toString()});
        } catch (MalformedURLException e) {
        }
    }

    private Loader(URL[] array, ClassLoader parent) {
        super(array, parent);
    }

    public static class LoaderCreator {
    	protected URL getPrefix() {
    		return urlPrefix;
    	}
    	
    	protected ClassLoader create(String jar) {
            URL u = null;
            try {
                u = new URL(urlPrefix, jar);
                 LOG.log(Level.FINE, "URL {0} added.", new Object[]{u});
            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }
            ClassLoader parent = Loader.class.getClassLoader();
            Loader loader = new Loader(new URL[]{u} , parent);
            LOG.log(Level.FINE, "Loader {0} added.", new Object[]{loader.toString()});
            return loader;
        }
    }
    
    public static LoaderCreator instance = new LoaderCreator();

    public static ClassLoader create(String jar) {
        return instance.create(jar);
    }

	final AllPermission all = new AllPermission();

	@Override
	protected PermissionCollection getPermissions(
			CodeSource codesource) {
		PermissionCollection r = super.getPermissions(codesource);
		r.add(all);
		return r;
	}				

}
