package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class LeerdomeinMockupResultPanel extends JPanel implements ActionListener {
	private Locale locale;
	private JPanel topPanel;
	private JLabel titleLabel;

	private JPanel mainPanel;
	private JPanel treePanel, wiskOpdrPanel;
	private Image treeImage;
	private JScrollPane treeScrollPane;
	private JButton filterButton;

	private JScrollPane wiskOpdrScrollPane;
	private Image wiskOpdrImage;
	private JLabel leerdoelTitelLabel;
	
	private JLabel klasResultLabel;
	private JLabel klasGemiddeldeLabel;

	private JPanel bottomPanel;
	private JScrollPane tableScrollPane;

	private JButton okButton, cancelButton;
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private Color colorBlue1 = new Color(49, 71, 112);
	private Color colorBlue2 = new Color(38, 115, 182);
	private Color colorBlue3 = new Color(120, 150, 202);
	private Color colorBlue4 = new Color(180, 195, 228);
	private Color colorBlue5 = new Color(211, 229, 244);
	private Color colorBlue6 = new Color(231, 242, 250);
	private Color colorGray1 = new Color(206, 207, 208);
	private Color colorGray2 = new Color(221, 223, 225);
	private Color colorGray3 = new Color(237, 239, 241);
	
	private Color colorRed = new Color(200, 0, 0);
	private Color colorGreen = new Color(0, 180, 0);
	
	public LeerdomeinMockupResultPanel() {
		setLayout(new BorderLayout());
		Locale locale = Locale.forLanguageTag("nl");
		treeImage = loadImage("resources/tree-gray.png");
		wiskOpdrImage = loadImage("resources/wiskOpdrPanel.png");
		makeGUI();
		// makeEditGUI();
	}

	private Image loadImage(String imagePath) {
		Image image = null;
		try {
			URL url = getClass().getResource(imagePath);
			image = ImageIO.read(url);
		} catch (Exception ex) {
			System.out.println("plaatje niet gevonden h");
		}
		return image;
	}

	public void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(colorGray3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// topPanel
		titleLabel = new JLabel("Klasresultaten op leerdoelen");
		titleLabel.setForeground(colorGray3);
		titleLabel.setFont(font.deriveFont(Font.PLAIN, 24));

		// mainPanel
		treePanel = new JPanel();
		treePanel.setBackground(colorGray3);
		treePanel.setSize(new Dimension(treeImage.getWidth(null), treeImage.getHeight(null)));
		treePanel.add(new JLabel(new ImageIcon(treeImage)));

		treeScrollPane = new JScrollPane(treePanel);
		treeScrollPane.setPreferredSize(new Dimension(treeImage.getWidth(null) + 40, 300));
		treeScrollPane.setMaximumSize(new Dimension(3000, 1200));
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder());

		filterButton = new JButton("Filter leerdoelen");
		filterButton.setPreferredSize(new Dimension(140, 24));

		wiskOpdrPanel = new JPanel(null);
		wiskOpdrPanel.setBackground(Color.white);
		wiskOpdrPanel.setPreferredSize(new Dimension(wiskOpdrImage.getWidth(null) + 10, wiskOpdrImage.getHeight(null)));
		JLabel wiskOpdrImageLabel = new JLabel(new ImageIcon(wiskOpdrImage));
		wiskOpdrImageLabel.setBounds(5, 0, wiskOpdrImage.getWidth(null), wiskOpdrImage.getHeight(null));
		wiskOpdrPanel.add(wiskOpdrImageLabel);

		wiskOpdrScrollPane = new JScrollPane(wiskOpdrPanel);
		wiskOpdrScrollPane.setPreferredSize(new Dimension(wiskOpdrImage.getWidth(null) + 40, 250));
		wiskOpdrScrollPane.setMinimumSize(new Dimension(wiskOpdrImage.getWidth(null) + 40, 100));
		wiskOpdrScrollPane.setMaximumSize(new Dimension(3000, 1200));
		wiskOpdrScrollPane.setBorder(BorderFactory.createEmptyBorder());

		leerdoelTitelLabel = new JLabel("Handig haakjes wegwerken bij merkwaardige producten");
		leerdoelTitelLabel.setForeground(Color.WHITE);
		leerdoelTitelLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
		leerdoelTitelLabel.setFont(font.deriveFont(Font.BOLD, 14));

		klasResultLabel = new JLabel("Resultaten klas:");
		klasResultLabel.setForeground(colorBlue1);
		klasResultLabel.setFont(new Font("SansSerif",Font.BOLD,16));
		
		klasGemiddeldeLabel = new JLabel("Klasgemiddelde:");
		klasGemiddeldeLabel.setForeground(colorBlue1);
		klasGemiddeldeLabel.setFont(new Font("SansSerif",Font.BOLD,16));
		
		klasKeuze = new JComboBox<>(new SchoolKlas[] { new SchoolKlas(null) });
		klasKeuze.setMaximumSize(new Dimension(300, 24));
		klasKeuze.addActionListener(this);
		
		table = new JTable();
		table.setDefaultRenderer(ScoreIcon.class, new IconRenderer());
		//table.setPreferredSize(new Dimension(400, 800));
		
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(430, 550));
		tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
		tableScrollPane.getComponent(0).setBackground(colorGray3);
		
		root = new DefaultMutableTreeNode("root");
	    model = new DefaultTreeModel(root);
	    title = new JLabel();
	    title2 = new JLabel();
	    tekst = new JTextArea(20,20);
	    score = new JLabel(new ScoreIcon(0,0,0,0));

		plaatsGUI();
	}

	private void plaatsGUI() {

		// topPanel
		Component[] compTop = { hgl(), titleLabel, hgl() };
		topPanel.add(hb(compTop));

		// mainPanel
		//
		Component[] r11 = { treeScrollPane };
		Component[] r12 = { hgl(), filterButton, hgl() };

		Component[] r13 = { leerdoelTitelLabel, hgl() };
		Component[] r14 = { wiskOpdrScrollPane };

		Box leerdoelTitel = hb(r13);
		leerdoelTitel.setOpaque(true);
		leerdoelTitel.setBackground(colorBlue3);

		Component[] compLeerdoel = { leerdoelTitel, ra(0, 20), hb(r14) };

		Box leerdoelBox = vb(compLeerdoel);
		leerdoelBox.setBorder(BorderFactory.createLineBorder(colorBlue3, 1));
		leerdoelBox.setOpaque(true);
		leerdoelBox.setBackground(Color.white);

		Component[] compLinks = { hb(r12), ra(0, 10), hb(r11), ra(0, 30), leerdoelBox };

		Component[] r21 = { klasResultLabel, ra(20,0), klasKeuze , hgl()};
		Component[] r22 = { tableScrollPane, hgl() };
		Component[] r23 = { klasGemiddeldeLabel, ra(20,0),score, hgl() };

		Component[] compRechts = { hb(r21), ra(0, 10), hb(r22), ra(0, 30), hb(r23),vgl() };

		// Component[] compRechts = {hb(compRechtsA)};

		Component[] compMain = { vb(compLinks), hst(20), vb(compRechts) };
		mainPanel.add(hb(compMain));

		add(topPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);

	}

	private Box hb(Component[] c) {
		Box box = Box.createHorizontalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

	private Box vb(Component[] c) {
		Box box = Box.createVerticalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

	private Component hgl() {
		return Box.createHorizontalGlue();
	}

	private Component vgl() {
		return Box.createVerticalGlue();
	}

	private Component hst(int n) {
		return Box.createHorizontalStrut(n);
	}

	private Component vst(int n) {
		return Box.createVerticalStrut(n);
	}

	private Component ra(int w, int h) {
		return Box.createRigidArea(new Dimension(w, h));
	}

	private Component ln(int w, int h) {
		Component c = ln(h);
		c.setPreferredSize(new Dimension(w, h));
		c.setMinimumSize(new Dimension(w, h));
		c.setMaximumSize(new Dimension(w, h));
		return c;
	}

	private Component ln(int h) {
		JPanel p = new JPanel() {
			public void paintComponent(Graphics g) {
				g.setColor(colorBlue4);
				g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
				// g.drawLine(0, getHeight()/2+1, getWidth(), getHeight()/2+1);
			}
		};
		p.setPreferredSize(new Dimension(1, h));
		p.setMaximumSize(new Dimension(1000, h));
		return p;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == klasKeuze) {
			SchoolKlas klas = klasKeuze.getItemAt(klasKeuze.getSelectedIndex());
			if (klas != null && klas.delegate != null) {
				DomSchoolClass dom = klas.delegate;
				scores = new DomStudentModelScorePerTeacher();
				scores.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(dom.getId(), dom)));
				scores.setStudentModelContexts(Collections.singletonList(new DomMapEntry<>(context.getId(), context)));
				try {
				// fetch students of class         
		          List<DomStudent> studentsInSchoolClass = SecureTeacherSchoolClassManager.getStudentsInSchoolClass(dom);
		          List<DomMapEntry<PersistenceId, DomStudent>> aStudents = studentsInSchoolClass.stream().map(s -> new DomMapEntry<>(s.getId(),s)).collect(Collectors.toList());
		          scores.setStudents(aStudents);
		          List<DomStudentModelDataStudentScore> aScores = studentsInSchoolClass.stream().map(s -> {
		            DomStudentModelDataStudentScore domScore = new DomStudentModelDataStudentScore();
		            domScore.setStudentId(s.getId());
		            domScore.setModelId(context);
		            return domScore;
		          }).collect(Collectors.toList());
		          scores.setStudentScores(aScores);

					if (context.getModelStructure().getInfo().getId() != null
							&& context.getModelStructure().getInfo().getId().startsWith(AdviseMeResultManager.KEY)) {
						try {
							scores = new AdviseMeResultManager().fromAdviseMe(scores).getValue();
						} catch (InvocationTargetException e1) {
							LOG.log(Level.SEVERE, "fromAdviseMe", e1.getCause());
						} catch (InterruptedException e1) {
							LOG.log(Level.WARNING, "interrupted", e1);
						}
					} else {

						DomLRS lrs = SecureTeacherStudentModelManager.getLRS();
						XapiResultsManager xapi = new XapiResultsManager(lrs);
						try {
							scores = xapi.fromXAPI(scores).getValue();
						} catch (InvocationTargetException e1) {
							LOG.log(Level.SEVERE, "fromXAPI", e1.getCause());
						} catch (InterruptedException e1) {
							LOG.log(Level.WARNING, "interrupted", e1);
						}

					}

					TableModel tmodel = new DefaultTableModel(scores.getStudents().size(), 2);
					for (int i = 0; i < tmodel.getRowCount(); i++) {
						String u = scores.getStudents().get(i).getValue().getDisplayName();
						tmodel.setValueAt(u, i, 0);
					}
					
					calculateROOT(scores, tmodel);
					table.setModel(tmodel);
					table.getTableHeader().setUI(null);
					table.setBackground(colorGray3);
					table.setForeground(colorBlue1);
					table.setSelectionBackground(colorBlue4);
					table.getColumnModel().getColumn(1).setCellRenderer(new IconRenderer());
					table.setRowHeight(score.getPreferredSize().height + 2);
					table.clearSelection();
				} catch (Dwo2Exception e1) {
					LOG.log(Level.SEVERE, "getScores", e1);
				}
			} else {
				table.setModel(new DefaultTableModel(0, 2));
			}
		}

	}

	private void calculateROOT(DomStudentModelScorePerTeacher scores, TableModel tmodel) {
		if (scores == null)
			return;
		double nzl = 0.0;
		double sumScore = 0.0;
		long sumCount = 0;
		List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
		for (int i = 0; i < tmodel.getRowCount(); i++) {
			DomStudentModelStructureScore v = studentScores.get(i).getDomStudentModelStructureScore();
			double nz = 0;
			int count = v.getCategories().size();
			for (DomStudentModelCategoryScore item : v.getCategories()) {
				if (item.getCount() != 0)
					nz += 1;
			}
			ScoreIcon result = new ScoreIcon(v.getScore(), v.getCount(), nz, count);
			//tmodel.setValueAt(result.getPercentage(), i, 1);
			tmodel.setValueAt(result, i, 1);
			if (v.getCount() != 0)
				nzl++;
			sumScore += v.getScore();
			sumCount += v.getCount();
		}
		ScoreIcon icon = new ScoreIcon(sumScore, sumCount, nzl, tmodel.getRowCount());
		score.setIcon(icon);
		score.setText(icon.getPercentage());
	}

	private static final Logger LOG = Logger.getLogger(LeerdomeinResultsPanel2.class.getName());

	private JComboBox<SchoolKlas> klasKeuze;
	private DomStudentModelContext context;

	JTree tree;

	private DefaultTreeModel model;
	private MutableTreeNode root;
	private JLabel title, title2;
	private JTextArea tekst;
	private JTable table;
	private JLabel score;
	private DomStudentModelScorePerTeacher scores;
	private JScrollPane scroll;

	public void setClasses(List<DomSchoolClass> list) {
		for (DomSchoolClass i : list) {
			klasKeuze.addItem(new SchoolKlas(i));
		}
		klasKeuze.setMaximumSize(klasKeuze.getPreferredSize());
	}

	public void setContext(DomStudentModelContext context) {
		this.context = context;
		DomStudentModelStructure model = context.getModelStructure();
		String locale = getLocale().getLanguage();

		model = AdviseMeResultManager.restructure(model, locale, context);

		NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
		this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));

		this.title.setText(vector.toString());
		this.title2.setText(vector.toString());
		this.tekst.setText(vector.getDescription());

		this.model.nodeStructureChanged(root);
	}

	class SchoolKlas {
		final DomSchoolClass delegate;

		public SchoolKlas(DomSchoolClass delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		public String toString() {
			if (delegate == null) {
				return TextMapper.getText(TextMapper.LBL_CLICK_TO_SELECT_A_SCHOOLCLASS);
			}
			return delegate.getSchoolClassName();
		}
	}

	static class IconRenderer extends DefaultTableCellRenderer {

		@Override
		protected void setValue(Object value) {
			if (value instanceof Icon) {
				super.setIcon((Icon) value);
			} else
				super.setValue(value);
		}
		
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//		return new JLabel();
//		}
		
	}

	class ScoreIcon implements Icon {
		float green = 0.64f;
		float red = 0.24f;
		float score = 0.5f;

		ScoreIcon(double score, long count, double part, int size) {
			if (count == 0L || size == 0) {
				this.score = 0.5f;
				red = 0.49f;
				green = 0.51f;
			} else {
				this.score = red = green = (float) (((float) score / count * part + (size - part) * 0.5f)
						/ (float) size);
				if (green <= 0.49f) {
					green = 0.5f;
				} else if (green >= 0.51f) {
					red = 0.5f;
				} else {
					green += 0.01f;
					red -= 0.01f;
				}
			}
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.white);
			
			red = (float)(Math.random());
			green = (float)Math.random();
			//g.fillRect(x, y, getIconWidth(), getIconHeight());
			x += 30;
			y += 2;
			int w = getIconWidth() - 60;
			g.fillRect(x, y, getIconWidth() - 60, getIconHeight() - 2 - 4);

			g.setColor(colorRed);
			g.fillRect(x + Math.round(red * w), y, Math.round((0.5f - red) * w), getIconHeight() - 2 - 4);
			int perc = Math.max(0, ((int)(100-red*100)-50)*2);
			g.drawString(""+perc+"%", 5, 2*getIconHeight()/3);
			
			g.setColor(colorGreen);
			g.fillRect(x + Math.round(w / 2.0f), y, Math.round(w * (green - 0.5f)), getIconHeight() - 2 - 4);
			perc = Math.max(0, ((int)(green*100)-50)*2);
			g.drawString(""+perc+"%", 175, 2*getIconHeight()/3);
			
			g.setColor(colorBlue1);
			g.drawRect(x, y, getIconWidth() - 60, getIconHeight() - 2 - 4);
			
			
		}

		@Override
		public int getIconWidth() {
			return 200;
		}

		@Override
		public int getIconHeight() {
			return getFontMetrics(getFont()).getHeight() + 4 + 3;
		}

		public String getPercentage() {
			return Math.round(score * 200 - 100) + "%";
		}
	}
}
