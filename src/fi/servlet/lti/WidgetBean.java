package fi.servlet.lti;

import java.io.Serializable;
import java.util.Properties;

public class WidgetBean implements Serializable {
	
	private Properties properties;

	public WidgetBean() {
		properties = new Properties();
		properties.setProperty("widgetsample.SampleWidget", "widgetsample.jar");
	}
	
	public String getArchive(String className) {
		String archive = "cbookinstance.jar";
		archive = properties.getProperty(className, archive);
		return archive;
	}
}
