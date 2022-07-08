package nl.numworx.notebook;

import java.awt.Dimension;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.CBookWidgetEditIF;

import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JLabel;

@SuppressWarnings("serial")
class Editor extends JPanel implements CBookWidgetEditIF {
	static final String SCORE_MAX = "scoreMax";
	static final String CHECK_DOCENT = "checkDocent";
	
	Dimension instanceSize = new Dimension(600,800);
	
	private int max = 10;

	private JFormattedTextField maxField;

	Editor(Locale locale) {
		super(null);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setLocale(locale);
		setMinimumSize(new Dimension(300,300));
		Box vb = Box.createVerticalBox();
		Box hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Score"));hb.add(Box.createHorizontalGlue());
		maxField = new JFormattedTextField(max);
		maxField.setColumns(10);
		maxField.setMaximumSize(maxField.getPreferredSize());
		hb.add(maxField);
		vb.add(hb);
		
		add(vb);
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
		Map<String, Object> launchData = new HashMap<>();
		try {
			maxField.commitEdit();
		} catch (ParseException e) {
		}
		max = ((Number) maxField.getValue()).intValue();
		launchData.put(SCORE_MAX, max);
		launchData.put(CHECK_DOCENT, max > 0);
		return launchData;
	}

	public String getLocalizedCmd(String arg0) {
		return null;
	}

	public int getMaxScore() {
		return max;
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

	public void setLaunchData(Map<String, ?> h) {
		if (h.containsKey(SCORE_MAX)) {
			max = ((Number) h.get(SCORE_MAX)).intValue();
			maxField.setValue(max);
		}
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
