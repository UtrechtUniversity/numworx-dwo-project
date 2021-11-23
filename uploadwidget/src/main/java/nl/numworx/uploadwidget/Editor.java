package nl.numworx.uploadwidget;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import org.cbook.cbookif.CBookWidgetEditIF;

import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JLabel;
import fi.wiskopdr.ObjectiveChoiceButton;

@SuppressWarnings("serial")
public class Editor extends JPanel implements CBookWidgetEditIF {
	
	
	private JFormattedTextField maxField;
	private ObjectiveChoiceButton objectives;
	
	private JFormattedTextField itemField;
	
	@Inject Editor() {
		super(null);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		Box hb = Box.createHorizontalBox();
		hb.add(new JLabel("Score"));
		maxField = new JFormattedTextField(max);
		hb.add(maxField);
		add(hb);		
		objectives = new ObjectiveChoiceButton();
		hb = Box.createHorizontalBox();
		hb.add(Box.createHorizontalGlue());
		hb.add(objectives);
		add(hb);

		hb = Box.createHorizontalBox();
		hb.add(new JLabel("Max uploads"));
		
		NumberFormatter nf = new NumberFormatter();
		nf.setMinimum(1);
		nf.setMaximum(10);
		nf.setValueClass(Integer.class);
		itemField = new JFormattedTextField(nf);
		itemField.setValue(items);
		hb.add(itemField);
		add(hb);
		
	}

	@Override
	public JComponent asComponent() {
		return this;
	}

	public static final String[] accepted = new String[] { };
	public static final String[] sent = new String[] { };
		
	private int h = 50;
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
