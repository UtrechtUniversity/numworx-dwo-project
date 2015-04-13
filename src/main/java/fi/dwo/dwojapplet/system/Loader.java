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

    private static final Logger log = Logger.getLogger(Loader.class.getName());

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
            log.log(Level.FINE, "URL Prefix set to: {0}.", new Object[]{urlPrefix.toString()});
        } catch (MalformedURLException e) {
        }
    }

    private Loader(URL[] array, ClassLoader parent) {
        super(array, parent);
        List<URL> asList = Arrays.asList(array);
		urls.addAll(asList);
    }

    @Override
	protected void addURL(URL url) {
		if(urls.add(url))
			super.addURL(url);
	}

	private Map<String, Collection<URL>> packages = new HashMap<String, Collection<URL>>();
    private Set<URL> work = new HashSet<URL>(), workDone = new HashSet<URL>();
    private Set<URL> urls = new HashSet<URL>();
    
    public static Loader create(String jar) {
        ArrayList list = new ArrayList();
        URL u = null;
        try {
            u = new URL(urlPrefix, jar);
            addURL(list, u);
            log.log(Level.FINE, "URL {0} added.", new Object[]{u});
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }
        ClassLoader parent = Loader.class.getClassLoader();
        Loader loader = new Loader((URL[]) list.toArray(new URL[list.size()]), parent);
        loader.work.add(u);
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
        URL uu = toJarURL(u);
        if (!list.contains(uu)) {
            JarInputStream jarin;
            try {
                jarin = new JarInputStream(u.openStream());
            } catch (IOException e) {
                System.err.println(u + " exception:" + e);
                log.log(Level.SEVERE, null, e);
                return;
            }
            list.add(uu);
            if(true)return;
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
            try {
            	return retryFindClass(name);
            } catch (ClassNotFoundException ignore) {} 
            
            
            throw e;
        }
    }

	private Class retryFindClass(String name) throws ClassNotFoundException {
		int k = name.lastIndexOf('.');
		String packageName = k < 0 ?"" : name.substring(0, k);
		Collection<URL> set = packages.remove(packageName); 
		if(set != null)
		{
			for( URL u: set) {
				addURL(toJarURL(u));
				addWork(u);
			}
		} else if( ! work.isEmpty())
		{
			ArrayList<URL> copy = new ArrayList<URL>(work);
			work.clear();
			workDone.addAll(copy);
			Iterator<URL> iter = copy.iterator();
			while (iter.hasNext()) {
				URL url = (URL) iter.next();
				doWork(url);
			}	
			return retryFindClass(name);
		}
		
		return super.findClass(name);
	}

	private void doWork(URL u) {
        JarInputStream jarin;
        try {
            jarin = new JarInputStream(u.openStream());
        Manifest manifest = jarin.getManifest();
        if (manifest != null) {
            Attributes attr = manifest.getMainAttributes();
            String path = attr.getValue(Attributes.Name.CLASS_PATH);
            if (path != null) {
                StringTokenizer st = new StringTokenizer(path);
                while (st.hasMoreTokens()) {
                    String jar = st.nextToken();
                    addWork(new URL(u, jar));
                }
            }
        }
        JarEntry entry;
        while (null != (entry = jarin.getNextJarEntry())) {
            String name = entry.getName();
            if ("META-INF/INDEX.LIST".equals(name)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(jarin));
                String line;
                URL last = null;
                while (null != (line = reader.readLine())) {
                    if (line.endsWith(".jar")) {
                        addWork(last = new URL(u, line));                        
                    } else if( !line.isEmpty() && last != null) {
                    	addPackage(line, last);
                    }
                }
                break;
            }
        }
        jarin.close();
        } catch (IOException e) {
            System.err.println(u + " exception:" + e);
            log.log(Level.SEVERE, null, e);
            return;
        }

    }
		
		
	private void addPackage(String name, URL last) {
		name = name.replace('/', '.');
		Collection<URL> urls = packages.get(name);
		if(urls == null) {
			urls = new LinkedHashSet<URL>();
			packages.put(name, urls);
		}
		urls.add(last);
	}

	private void addWork(URL url) {
		if(! workDone.contains(url))
			work.add(url);
	}

	private static URL toJarURL(URL u) {
		try {
			return new URL("jar:" + u + "!/");
		} catch (MalformedURLException ignore) {
			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		// TODO Auto-generated method stub
		return super.getResourceAsStream(name);
	}

	@Override
	public URL findResource(String name) {
		// TODO Auto-generated method stub
		return super.findResource(name);
	}

	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.findResources(name);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadClass(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadClass(name, resolve);
	}

	@Override
	public URL getResource(String name) {
		// TODO Auto-generated method stub
		return super.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.getResources(name);
	}

}
