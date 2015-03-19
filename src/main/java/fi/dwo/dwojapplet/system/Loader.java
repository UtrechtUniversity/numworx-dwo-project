package fi.dwo.dwojapplet.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader extends URLClassLoader {

    private static final Logger log = Logger.getLogger(Loader.class.getName());

    private static URL urlPrefix;

    static {
        // set default directory for jars
        setPrefix("http://www.fisme.science.uu.nl/dwo/jars/");
    }

    /**
     * Set jar directory.
     *
     * @param prefix to set
     */
    public static void setPrefix(String prefix) {
        try {
            urlPrefix = new URL(prefix);
            log.log(Level.FINE, "URL Prefix set to: {0}.", new Object[]{urlPrefix.toString()});
        } catch (MalformedURLException e) {
        }
    }

    private Loader(URL[] array, ClassLoader parent) {
        super(array, parent);
    }

    public static Loader create(String jar) {
        ArrayList list = new ArrayList();
        try {
            URL u = new URL(urlPrefix, jar);
            addURL(list, u);
            log.log(Level.FINE, "URL {0} added.", new Object[]{u});
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }
        ClassLoader parent = Loader.class.getClassLoader();
        Loader loader = new Loader((URL[]) list.toArray(new URL[list.size()]), parent);
        log.log(Level.FINE, "Loader {0} added.", new Object[]{loader.toString()});
        return loader;
    }

    /**
     * @param list
     * @param u
     * @throws java.io.IOException
     * @pararows IOURLException
     */
    private static void addURL(ArrayList list, URL u)
            throws IOException {
        URL uu = new URL("jar:" + u + "!/");
        if (!list.contains(uu)) {
            JarInputStream jarin;
            try {
                jarin = new JarInputStream(u.openStream());
            } catch (IOException e) {
                System.err.println(u + " exception:");
                log.log(Level.SEVERE, null, e);
                return;
            }
            list.add(uu);
            Manifest manifest = jarin.getManifest();
            if (manifest != null) {
                Attributes attr = manifest.getMainAttributes();
                String path = attr.getValue(Attributes.Name.CLASS_PATH);
                if (path != null) {
                    StringTokenizer st = new StringTokenizer(path);
                    while (st.hasMoreTokens()) {
                        String jar = st.nextToken();
                        addURL(list, new URL(u, jar));
                    }
                }
            }
            JarEntry entry;
            while (null != (entry = jarin.getNextJarEntry())) {
                String name = entry.getName();
                if ("META-INF/INDEX.LIST".equals(name)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarin));
                    String line;
                    while (null != (line = reader.readLine())) {
                        if (line.endsWith(".jar")) {
                            addURL(list, new URL(u, line));
                        }
                    }
                    break;
                }
            }

        }
    }
    /* (non-Javadoc)
     * @see java.net.URLClassLoader#findClass(java.lang.String)
     */

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        try {
            Class clazz = super.findClass(name);
            log.log(Level.FINE, "Class {0} searched and found by parent loaders.", new Object[]{name});
            log.log(Level.FINE, "Class loaded as object {0} .", new Object[]{clazz});

            //System.out.print(name);
            //System.out.println(" found " + clazz + ", loader=" + clazz.getClassLoader());
            return clazz;
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Class with name {0} not found.", new Object[]{name});
            throw e;
        }
    }

}
