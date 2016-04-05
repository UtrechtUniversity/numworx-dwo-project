package fi.dwo.dwojapplet.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(Loader.class.getName());

    private static URL urlPrefix;

    static {
        // set default directory for jars
        setPrefix("https://ws.fisme.science.uu.nl/dwo/jars/");
    }

    /**
     * Set jar directory.
     *
     * @param prefix to set
     */
    public static void setPrefix(String prefix) {
        try {
            urlPrefix = new URL(prefix);
            LOG.log(Level.FINE, "URL Prefix set to: {0}.", new Object[]{urlPrefix.toString()});
        } catch (MalformedURLException e) {
        }
    }

    private Loader(URL[] array, ClassLoader parent) {
        super(array, parent);
    }


    public static Loader create(String jar) {
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
