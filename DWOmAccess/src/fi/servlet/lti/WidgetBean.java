package fi.servlet.lti;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WidgetBean implements Serializable {
	
	private Properties properties;
	private Logger log = Logger.getLogger(getClass().getName());

	public WidgetBean() {
		properties = new Properties();
		properties.setProperty("widgetsample.SampleWidget", "widgetsample.jar");
		try {
			InputStream in = getClass().getResourceAsStream("resources/index.properties");
			properties.load(in);
			in.close();
		
		} catch (Exception _) {
			_.printStackTrace();
		}
	}
	
	public String getArchive(String className, String version, String userid ) {
		String archive = "cbookinstance.jar";
		archive = getProperties(version, userid).getProperty(className, archive);
		return archive;
	}

	private Properties getProperties(String version, String learner_id) {
		Properties properties = new Properties(this.properties);
		Set<String> done = new HashSet<String>();
		String url = "https://" + version + "-dot-mc2dme.appspot.com/dwo/widgets/index.properties";
		try {
			InputStream in = new URL(url).openStream();
			properties.load(in);
			in.close();
		} catch (MalformedURLException e1) {
			log(e1);
		} catch (IOException e1) {
			log(e1);
		}
		done.add(url);
		done.add("");
		List<String> work = new LinkedList<String>();
		String index;
// patch
		do {
			String includes = (String) properties.remove("includes");
			if(includes != null) {
				work.addAll(Arrays.asList(includes.split(" ")));
			}
			if (work.isEmpty()) break;
			index = work.remove(0);
			if(done.contains(index)) continue;
			done.add(index);
			Properties p = new Properties();
			try {
                index += "?applicationVersion=" + version;
                if(learner_id != null && learner_id.length()>0)
                	index += "&user_id=" + URLEncoder.encode(learner_id); // What a hack?	
				InputStream openStream = new URL(index).openStream();
				p.load(openStream);
				openStream.close();
			} catch (MalformedURLException e) {
				log(e);
			} catch (IOException e) {
				log(e);
			}
			//p.keySet().removeAll(properties.keySet());
			properties.putAll(p);			
		} while (true);
		return properties;
	}
	
	private void log(Throwable e1) {
		log.log(Level.SEVERE, null, e1);		
	}

	public static void main(String[] args) { 
		WidgetBean wb = new WidgetBean();
		System.out.println(wb.getArchive("de.cinderella.CindyWidget", "6", "wimvvv"));
		
	}
	public String toString() { 
		return super.toString() + 
				"[properties=" + 	
				properties.keySet().toString() + "]";
	}
}
