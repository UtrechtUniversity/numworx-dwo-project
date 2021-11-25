package nl.numworx.uploadwidget;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import org.cbook.cbookif.CBookWidgetEditIF;

import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JLabel;
import fi.wiskopdr.ObjectiveChoiceButton;

@SuppressWarnings("serial")
public class Editor extends JPanel implements CBookWidgetEditIF {
	
	
	
	
	private JFormattedTextField maxField;
	private ObjectiveChoiceButton objectives;
	
	private JFormattedTextField itemField;	
	private JComboBox<String> mediatypes;
	private JComboBox<String> widgettype;
	
	@Inject Editor() {
		super(null);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(300,300));
		Box vb = Box.createVerticalBox();
		Box hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Score"));hb.add(Box.createHorizontalGlue());
		maxField = new JFormattedTextField(max);
		maxField.setColumns(10);
		maxField.setMaximumSize(maxField.getPreferredSize());
		hb.add(maxField);
		vb.add(hb);
		objectives = new ObjectiveChoiceButton();
		objectives.setEnabled(ObjectiveChoiceButton.hasObjectiveChoices());
		hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(Box.createHorizontalGlue());
		hb.add(objectives);
		vb.add(hb);
		vb.setBorder(BorderFactory.createTitledBorder("Resultaat"));
		add(vb);add(Box.createVerticalStrut(10));
		vb = Box.createVerticalBox();

		hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Max items"));hb.add(Box.createHorizontalGlue());
		
		NumberFormatter nf = new NumberFormatter();
		nf.setMinimum(1);
		nf.setMaximum(10);
		nf.setValueClass(Integer.class);
		itemField = new JFormattedTextField(nf);
		itemField.setValue(items);
		itemField.setColumns(10);
		Dimension pref = itemField.getPreferredSize();
		itemField.setMaximumSize(pref);
		hb.add(itemField);
		vb.add(hb);
		vb.setBorder(BorderFactory.createTitledBorder("Download"));
		add(vb);
		vb = Box.createVerticalBox();
		hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Mediatypes"));hb.add(Box.createHorizontalGlue());
		mediatypes = new JComboBox<>(new String[] { "*", "Text", "Images"} );
		mediatypes.setEditable(true);
		hb.add(mediatypes);
		vb.add(hb);
		hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Stijl"));hb.add(Box.createHorizontalGlue());
		widgettype = new JComboBox<>(new String[] { "anchor", "browser_input", "button", "custom", "dropzone", "label" });
		widgettype.setSelectedIndex(1);
		widgettype.setMaximumSize(pref);widgettype.setPreferredSize(pref);
		mediatypes.setMaximumSize(pref);mediatypes.setPreferredSize(pref);
		
		hb.add(widgettype);
		vb.add(hb);
		vb.setBorder(BorderFactory.createTitledBorder("Upload"));		
		add(vb);
		
		Dimension dim = getPreferredSize();
		dim.width = Math.max(300, dim.width);
		setPreferredSize(dim);
	}

	@Override
	public JComponent asComponent() {
		return this;
	}

	public static final String[] accepted = new String[] { };
	public static final String[] sent = new String[] { };
		
	private int h = 100;
	private int w = 300;
	private int max = 10;
	private int items = 1;
	
	@Override
	public String[] getAcceptedCmds() {
		return accepted;
	}

	@Override
	public Dimension getInstanceSize() {
		return new Dimension(w, h);
	}

	@Override
	public Hashtable<String, ?> getLaunchData() {
		Hashtable<String, ?> launchdata = new Hashtable<>();
		return launchdata;
	}

	@Override
	public String getLocalizedCmd(String cmd) {
		return cmd;
	}

	@Override
	public int getMaxScore() {
		return max;
	}

	@Override
	public String[] getSendCmds() {
		return sent;
	}

	@Override
	public void setInstanceHeight(int arg0) {
		this.h = arg0;

	}

	@Override
	public void setInstanceWidth(int arg0) {
		this.w = arg0;

	}

	@Override
	public void setLaunchData(Map<String, ?> arg0) {
		

	}

	@Override
	public void start() {
		

	}

	@Override
	public void stop() {
		

	}

}
