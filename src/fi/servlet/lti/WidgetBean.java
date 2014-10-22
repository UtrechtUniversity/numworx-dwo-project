package fi.servlet.lti;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class WidgetBean implements Serializable {
	
	private Properties properties;

	public WidgetBean() {
		properties = new Properties();
		properties.setProperty("widgetsample.SampleWidget", "widgetsample.jar");
		try {
			InputStream in = getClass().getResourceAsStream("index.properties");
			properties.load(in);
			in.close();
		} catch (Exception _) {
		}
	}
	
	public String getArchive(String className) {
		String archive = "cbookinstance.jar";
		archive = properties.getProperty(className, archive);
		return archive;
	}
	
	public static void main(String[] args) { 
		WidgetBean wb = new WidgetBean();
		System.out.println(wb.getArchive("xxx"));
		
	}
}
