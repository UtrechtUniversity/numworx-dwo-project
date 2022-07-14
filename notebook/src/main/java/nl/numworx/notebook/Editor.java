package nl.numworx.notebook;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.cbook.cbookif.CBookWidgetEditIF;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JLabel;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.wiskopdr.ObjectiveChoiceButton;

@SuppressWarnings("serial")
class Editor extends JPanel implements CBookWidgetEditIF {
	
	public class NotesAction extends AbstractAction implements Action {

		URI uri = URI.create("https://hub-dev.dwo.nl/");
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e1) {
			}

		}
		NotesAction() { super("Open Notebook in browser"); 
			setEnabled(Desktop.isDesktopSupported());
		}
	}

	class MinAction extends AbstractAction implements Action {

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Bestand> selected = uploadList.getSelectedValuesList();
			selected.forEach(uploadModel::removeElement);
		}
		
		MinAction() { super("-"); }
	}

	class PlusAction extends AbstractAction implements Action {

		private int cnt;

		@Override
		public void actionPerformed(ActionEvent e) {
			Bestand b = new Bestand("Untitled " + cnt++);
			uploadModel.addElement(b); // should b sorted
			uploadList.setSelectedIndex(uploadModel.indexOf(b));
		}
		
		PlusAction() { super("+"); }
	}

	static class Bestand implements Comparable<Bestand> {
		final String name;
		String data;
		String type;
		
		public String toString() { return name; }

		Bestand(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Bestand other = (Bestand) obj;
			return Objects.equals(name, other.name);
		}

		@Override
		public int compareTo(Bestand o) {
			return name.compareToIgnoreCase(o.name);
		}
		
	}
	
	
	
	static final String SCORE_MAX = "scoreMax";
	static final String CHECK_DOCENT = "checkDocent";
	
	Dimension instanceSize = new Dimension(600,800);
	
	private int max = 10;

	private JFormattedTextField maxField;
	private ObjectiveChoiceButton objBtn;
	private JCheckBox projectCB;
	private JTextField projectField;
	private JCheckBox documentCB;
	private JTextField documentField;
	private JList<Bestand> uploadList;
	private JButton plusBtn, minBtn, notesBtn;
	private DefaultListModel<Bestand> uploadModel;

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
		if (ObjectiveChoiceButton.hasObjectiveChoices() ) {
			JPanel p = new JPanel();
			objBtn = new ObjectiveChoiceButton();
			p.add(objBtn);
			vb.add(p);
		}
		vb.setBorder(BorderFactory.createEtchedBorder());
		add(vb);
		vb = Box.createVerticalBox();
		hb = Box.createHorizontalBox();
		projectCB = new JCheckBox("Project");
		projectField = new JTextField();
		hb.add(projectCB); hb.add(projectField);
		vb.add(hb);
		hb = Box.createHorizontalBox();
		documentCB = new JCheckBox("Notebook");
		documentField = new JTextField();
		hb.add(documentCB); hb.add(documentField);
		vb.add(hb);
		vb.setBorder(BorderFactory.createEtchedBorder());
		add(vb);
		vb = Box.createVerticalBox();
		hb = Box.createHorizontalBox();
		uploadModel = new DefaultListModel<Bestand>();
		uploadList = new JList<>(uploadModel);
		uploadList.setVisibleRowCount(4);
		JScrollPane scroll = new JScrollPane(uploadList);
		vb.add(scroll);
		plusBtn = new JButton(new PlusAction());
		minBtn = new JButton(new MinAction());
		hb.add(plusBtn); hb.add(minBtn);
		vb.add(hb);
		notesBtn = new JButton(new NotesAction());
		vb.add(notesBtn);
		vb.setBorder(BorderFactory.createEtchedBorder());
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
