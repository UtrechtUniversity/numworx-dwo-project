package fi.servlet.dwomaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

public class Loader extends URLClassLoader {

	static final String URL_PREFIX = //"http://www.fisme.science.uu.nl/dwo/jars/";
									"http://www.fisme.uu.nl/dwo/jars/";
	private Loader(URL[] array, ClassLoader parent) {
		super(array, parent);
	}

	static Loader create(String jar) {
		ArrayList<URL> list = new ArrayList<URL>();
		
		jar = URL_PREFIX + jar;
		try {
			URL u = new URL(jar);
			addURL(list, u);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClassLoader parent = Loader.class.getClassLoader(); // TOMCAT classloader
		return new Loader(list.toArray(new URL[list.size()]), parent);
	}

	/**
	 * @param list
	 * @param u
	 * @throws IOURLException
	 */
	private static void addURL(ArrayList<URL> list, URL u)
			throws IOException {
		URL uu = new URL( "jar:" + u + "!/");
		if(!list.contains(uu))
		{	
			JarInputStream jarin;
			try {
				jarin = new JarInputStream(u.openStream());
			} catch (IOException e) {
				System.err.println(u + " exception:");
				e.printStackTrace();
				return;
			}
			list.add(uu);
			Manifest manifest = jarin.getManifest();
			if(manifest != null)
			{
				Attributes attr = manifest.getMainAttributes();
				String path = attr.getValue(Attributes.Name.CLASS_PATH);
				if(path != null) {
					StringTokenizer st = new StringTokenizer(path);
					while (st.hasMoreTokens()) {
						String jar = st.nextToken();
						addURL(list, new URL(u, jar));
					}
				}
			}
			JarEntry entry;
			while (null != (entry= jarin.getNextJarEntry()))
				{
					String name = entry.getName();
					if("META-INF/INDEX.LIST".equals(name) ){
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarin));
						String line; 
						while ( null != (line = reader.readLine())) {
							if( line.endsWith(".jar")) 
							{
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
	protected Class<?> xxfindClass(String name) throws ClassNotFoundException {
		try {
			Class<?> clazz = super.findClass(name);
			System.out.print(name);
			System.out.println(" found " + clazz + ", loader=" + clazz.getClassLoader());
			return clazz;
		} catch (ClassNotFoundException e) {
			System.out.println(name + " not found");
			throw e;
		}
	}
	
}
