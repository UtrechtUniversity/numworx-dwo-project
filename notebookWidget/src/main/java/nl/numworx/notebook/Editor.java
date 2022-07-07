package nl.numworx.notebook;

import java.awt.Dimension;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.CBookWidgetEditIF;

class Editor extends JPanel implements CBookWidgetEditIF {
	
	Dimension instanceSize = new Dimension(600,800);

	Editor(Locale locale) {
	}

	public JComponent asComponent() {
		return this;
	}

	public String[] getAcceptedCmds() {
		return null;
	}

	public Dimension getInstanceSize() {
		return instanceSize;
	}

	public Map<String, ?> getLaunchData() {
		return Collections.emptyMap();
	}

	public String getLocalizedCmd(String arg0) {
		return null;
	}

	public int getMaxScore() {
		return 0;
	}

	public String[] getSendCmds() {
		return null;
	}

	public void setInstanceHeight(int arg0) {
		instanceSize.height = arg0;
	}

	public void setInstanceWidth(int arg0) {
		instanceSize.width = arg0;
	}

	public void setLaunchData(Map<String, ?> arg0) {
		// TODO Auto-generated method stub

	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
