package nl.numworx.notebook;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
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
import fi.beans.numworxlf.JFileChooser;
import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JLabel;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.wiskopdr.ObjectiveChoiceButton;

@SuppressWarnings("serial")
class Editor extends JPanel implements CBookWidgetEditIF {
	
	URI uri = URI.create("https://hub-dev.dwo.nl/");

	public class NotesAction extends AbstractAction implements Action {

		
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

//		private int cnt;
		private JFileChooser chooser;
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			chooser.setCurrentDirectory(JFileChooser.getGlobalDirectory());
			int ok = chooser.showOpenDialog(asComponent());
			if (ok == JFileChooser.APPROVE_OPTION) { 
				File[] files = chooser.getSelectedFiles();
				if (files == null || files.length == 0) files = new File[] { chooser.getSelectedFile() };
				
				JFileChooser.setGlobalDirectory(chooser.getCurrentDirectory());
				for(File f : files) {
					FileInputStream in;
					try {
						in = new FileInputStream(f);
					} catch (FileNotFoundException e2) {
						continue;
					}
					try {
						String name = f.getName();
						int size = (int) f.length();
						Bestand b = new Bestand(name);
						b.data = new byte[size];
						b.type = "data"; // just bytes;
						in.read(b.data);
						uploadModel.removeElement(b);
						uploadModel.addElement(b);
					} catch (FileNotFoundException e1) {
					} catch (IOException e1) {
					} finally {
						try {
							in.close();
						} catch (IOException e1) {
						}
					}
				}
			}
//			Bestand b = new Bestand("Untitled " + cnt++);
//			uploadModel.addElement(b); // should b sorted
//			uploadList.setSelectedIndex(uploadModel.indexOf(b));
		}
		
		PlusAction() {
			super("+"); 
			chooser = new JFileChooser();
		}
	}

	static class Bestand implements Comparable<Bestand>  {
		final String name;
		byte[] data;
		String type, content;
		
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
	static final String UPLOAD = "upload";
	static final String PROJECT = "project";
	static final String NOTEBOOK = "notebook";
	
	Dimension instanceSize = new Dimension(600,800);
	
	private int max = 0;

	private JFormattedTextField maxField;
	private ObjectiveChoiceButton objBtn;
	private JCheckBox projectCB;
	private JTextField projectField;
	private JCheckBox documentCB;
	private JTextField documentField;
	private JList<Bestand> uploadList;
	private JButton plusBtn, minBtn, notesBtn;
	private DefaultListModel<Bestand> uploadModel;

	Editor(Locale locale, URI base) {
		super(null);
		this.uri = base;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setLocale(locale);
		//setMinimumSize(new Dimension(300,300));
		Box vb = Box.createVerticalBox();
		Box hb = Box.createHorizontalBox();hb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		hb.add(new JLabel("Score"));hb.add(Box.createHorizontalGlue());
		maxField = new JFormattedTextField(max); fixHeight(maxField);
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
		vb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		add(vb);
		Dimension mingap = new Dimension(0, 10);
		Dimension maxgap = new Dimension(Short.MAX_VALUE, 20);
		add(new Box.Filler(mingap, mingap, maxgap));
		vb = Box.createVerticalBox();
		hb = Box.createHorizontalBox();
		projectCB = new JCheckBox("Project");
		projectField = new JTextField(); fixHeight(projectField);
		hb.add(projectCB); hb.add(projectField);
		vb.add(hb);
		vb.add(Box.createVerticalStrut(10));
		hb = Box.createHorizontalBox();
		documentCB = new JCheckBox("Notebook");
		documentField = new JTextField(); fixHeight(documentField);
		hb.add(documentCB); hb.add(documentField);
		vb.add(hb);
		vb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		add(vb);
		add(new Box.Filler(mingap, mingap, maxgap));
		vb = Box.createVerticalBox();
		JLabel label = new JLabel("Upload bestanden");
		label.setAlignmentX(1);
		vb.add(label);
		
		hb = Box.createHorizontalBox();
		uploadModel = new DefaultListModel<Bestand>();
		uploadList = new JList<>(uploadModel);
		uploadList.setVisibleRowCount(4);
		JScrollPane scroll = new JScrollPane(uploadList);
		scroll.setAlignmentX(1);
		vb.add(scroll);
		vb.add(Box.createVerticalStrut(3));
		plusBtn = new JButton(new PlusAction());
		minBtn = new JButton(new MinAction());
		hb.add(plusBtn); hb.add(minBtn); hb.add(Box.createHorizontalGlue());
		hb.setAlignmentX(1);
		vb.add(hb);
		vb.add(Box.createVerticalStrut(10));
		notesBtn = new JButton(new NotesAction());
		notesBtn.setAlignmentX(1);
		vb.add(notesBtn);
		vb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		add(vb);
		add(Box.createGlue());
	}

	private void fixHeight(JComponent component) {
		Dimension pref = component.getPreferredSize();
		Dimension max  = component.getMaximumSize();
		max.height = pref.height;
		component.setMaximumSize(max);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, ?> getLaunchData() {
		Map<String, Object> launchData = new HashMap<>();
		try {
			maxField.commitEdit();
		} catch (ParseException e) {
		}
		max = ((Number) maxField.getValue()).intValue();
		launchData.put(SCORE_MAX, max);
		launchData.put(CHECK_DOCENT, max > 0);
		if (objBtn != null) {
			launchData.putAll((Map<String,?>)objBtn.getEditState(max));
		}
		if (projectCB.isSelected()) {
			launchData.put(PROJECT, projectField.getText());
		}
		if (documentCB.isSelected()) {
			launchData.put(NOTEBOOK, documentField.getText());
		}
		
		Map[] bestanden = new Hashtable[uploadModel.size()];
		for (int i = 0; i < bestanden.length; i++) {
			Bestand b = uploadModel.elementAt(i);
			Hashtable bestand = new Hashtable(3);
			bestanden[i] = bestand;
			bestand.put("name", b.name);
			if (b.content == null) {
				boolean ascii = false; 
				if (b.data != null) {
					ascii = true;
					for (byte data : b.data) {
						if (data <= 0 || data >= 127) { ascii = false; break; }
					}
				} else {
					b.data = new byte[0];
				}
				if (ascii) {
					b.content = new String(b.data, StandardCharsets.US_ASCII);
					b.type = "text";
				} else {
					b.content = Base64.getEncoder().encodeToString(b.data);
					b.type = "base64";
				}
				b.data = null;
			}
			bestand.put("type", b.type);
			bestand.put("content", b.content);
		}
		launchData.put(UPLOAD, Arrays.asList(bestanden));
		
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setLaunchData(Map<String, ?> h) {
		if (h.containsKey(SCORE_MAX)) {
			max = ((Number) h.get(SCORE_MAX)).intValue();
			maxField.setValue(max);
		}
		if (objBtn != null) {
			objBtn.setEditState(h);
		}
		if (h.containsKey(UPLOAD)) {
			List<Map> bestanden = (List<Map>) h.get(UPLOAD);
			uploadModel.removeAllElements();
			for( Map map: bestanden) {
				Bestand b = new Bestand(map.get("name").toString());
				b.type = map.get("type").toString();
				b.content = map.get("content").toString();
				uploadModel.addElement(b);
			}
		}
		if (h.containsKey(PROJECT)) {
			projectCB.setSelected(true);
			projectField.setText(h.get(PROJECT).toString());
		} else 
			projectCB.setSelected(false);
		if (h.containsKey(NOTEBOOK)) {
			documentCB.setSelected(true);
			documentField.setText(h.get(NOTEBOOK).toString());
		} else 
			documentCB.setSelected(false);
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
