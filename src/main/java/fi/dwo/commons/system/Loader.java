package fi.dwo.commons.system;

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

public class Loader extends URLClassLoader {

	private static URL urlPrefix;
	static {
		// set default directory for jars
		setPrefix("http://www.fisme.science.uu.nl/dwo/jars/");
	}
	
	/**
	 * Set jar directory.
	 * @param prefix to set
	 */
	public static void setPrefix(String prefix) {
		try {
			urlPrefix = new URL(prefix);
		} catch (MalformedURLException _) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClassLoader parent = Loader.class.getClassLoader();
		return new Loader((URL[]) list.toArray(new URL[list.size()]), parent);
	}

	/**
	 * @param list
	 * @param u
	 * @throws IOURLException
	 */
	private static void addURL(ArrayList list, URL u)
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
	protected Class findClass(String name) throws ClassNotFoundException {
		try {
			Class clazz = super.findClass(name);
			//System.out.print(name);
			//System.out.println(" found " + clazz + ", loader=" + clazz.getClassLoader());
			return clazz;
		} catch (ClassNotFoundException e) {
			System.out.println(name + " not found");
			throw e;
		}
	}
	
}
