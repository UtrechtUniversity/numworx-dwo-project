package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrEditPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdomeinEditPanel2 extends JPanel implements ActionListener, TreeSelectionListener {
  static final String WISKOPDR_SIG = "H4sIAAAAAA";

  private JButton okButton;
  private JButton cancelButton;
  private DomStudentModelStructure structure;
  private JMenuBar bar = new JMenuBar();
  private JLabel title;
  private JTextField subtitle;
  private JButton bewerken;
  private Box south;
  JTree tree;
  DefaultTreeModel model;
  DynamicUtilTreeNode root;
  private JPanel settings;
  JFormattedTextField slip, init, learn;

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

  }

  public LeerdomeinEditPanel2() {
    super(new BorderLayout());
  south = Box.createHorizontalBox();
  south.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
  south.setOpaque(true);
  south.setBackground(Constants.COLOR21);
  okButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_SAVE));
  okButton.setPreferredSize(new Dimension(100, 24));
  okButton.setBackground(Constants.COLOR15);
  okButton.setForeground(Constants.COLOR20);
  cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
  cancelButton.setPreferredSize(new Dimension(100, 24));
  cancelButton.setBackground(Constants.COLOR15);
  cancelButton.setForeground(Constants.COLOR20);           
  south.add(Box.createHorizontalGlue());
  south.add(okButton); 
  south.add(Box.createHorizontalStrut(20));
  south.add(cancelButton);
  south.add(Box.createHorizontalGlue());

  add(south, BorderLayout.SOUTH);

  Box north = Box.createHorizontalBox();
  north.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
  north.setOpaque(true);north.setBackground(Constants.COLOR15);
  bewerken = new JButton(TextMapper.getText(TextMapper.GUIH_EDIT));
  title = new JLabel(getTitle());
  title.setForeground(Constants.COLOR20);
  title.setFont(title.getFont().deriveFont(24f));
  north.add(bewerken);
  north.add(Box.createHorizontalGlue());
  north.add(title);
  north.add(Box.createHorizontalGlue());
  bewerken.addActionListener(
    e -> {
      if (TextMapper.getText(TextMapper.GUIH_STOP_EDIT) != bewerken.getText()) {
        setEditable(true);
        //textArea.OPSLAAN_ACTION.bewerken();
      } else {
        setEditable(false);
        //textArea.OPSLAAN_ACTION.opslaan();
      }
    }
  );
  add(north, BorderLayout.NORTH);
  
  JSplitPane split = new JSplitPane();
  split.setDividerSize(20);
  split.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  split.setBackground(Constants.COLOR20);
  setBackground(Constants.COLOR20);
  JMenu Bestand = new JMenu(TextMapper.getText("file"));
  JMenu Bewerken = new JMenu(TextMapper.getText("edit"));
  bar.setBackground(Constants.COLOR21);
  Bestand.setBackground(Constants.COLOR21);
  Bewerken.setBackground(Constants.COLOR21);
  bar.setOpaque(true);
  bar.setUI(new BasicMenuBarUI());
  bar.add(Bestand);
  //  Bestand.add(new JMenuItem(new SubdomeinAction()));
  //  Bestand.add(new JMenuItem(new LeerdoelAction()));
  //  Bestand.addSeparator();
  //  Bestand.add(new JMenuItem(new ExportAction(this)));
  bar.add(Bewerken);
  //  Bewerken.add(new JMenuItem(new Knippen()));
  //  Bewerken.add(new JMenuItem(new Kopieren()));
  //  Bewerken.add(new JMenuItem(new Plakken()));
  //  Bewerken.add(new JMenuItem(new Wijzigen()));
  //  Bewerken.add(new JMenuItem(new Omhoog()));
  //  Bewerken.add(new JMenuItem(new Omlaag()));
  //  Bewerken.add(new JMenuItem(new Verwijderen()));
  bar.add(Box.createHorizontalGlue());
  
  add(split, BorderLayout.CENTER);
  String locale = JComponent.getDefaultLocale().getLanguage();
  NodeVector v = new NodeVector(locale);
  v.setTitle("Leerdomein");
  root = new DynamicUtilTreeNode(v,v);
  model = new DefaultTreeModel(root);   
  tree = new JTree(model);
  JPanel leftBox = new JPanel(new BorderLayout());
  leftBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR10));
  leftBox.add(bar, BorderLayout.NORTH);
  leftBox.add(new JScrollPane(tree), BorderLayout.CENTER);
  //leftBox.add(comp)
  split.setLeftComponent(leftBox);
  
  JPanel rightBox = new JPanel(new BorderLayout());
  subtitle = new JTextField();
  container = new JPanel(new GridLayout(1,1));
  container.setPreferredSize(new Dimension(500 , 325));
  rightBox.add(subtitle, BorderLayout.NORTH);
  rightBox.add(container, BorderLayout.CENTER);

  init = new JFormattedTextField(NumberFormat.getInstance());
  slip = new JFormattedTextField(NumberFormat.getInstance());
  learn = new JFormattedTextField(NumberFormat.getInstance());
  
  
  settings = new JPanel();
  settings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constants.COLOR15), "Instellingen", TitledBorder.CENTER, TitledBorder.CENTER, null, Constants.COLOR15));    
  rightBox.add(settings, BorderLayout.SOUTH);
  split.setRightComponent(rightBox);

  JTextField leerdoelTitelEditor = subtitle;
  leerdoelTitelEditor.setForeground(Color.WHITE);
  leerdoelTitelEditor.setBackground(Constants.COLOR13);
  //leerdoelTitelEditor.setMaximumSize(new Dimension(800,30));
  leerdoelTitelEditor.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
  leerdoelTitelEditor.setFont(leerdoelTitelEditor.getFont().deriveFont(Font.BOLD, 14));
  leerdoelTitelEditor.setOpaque(true);

  tree.addTreeSelectionListener(this);
  
  
  }

  private String getTitle() {
    if (structure != null) {
      return structure.getInfo().getTitle().get(getLocale().getLanguage());
    }
    return "Leerdomein";
  }

  private boolean editable;
  private WiskOpdrEditPanel wiskOpdrEditPanel;
  private JPanel container;
  public void setEditable(boolean b) {
    editable = b;
    fillSelection();
    if (b) {
      bewerken.setText(TextMapper.getText(TextMapper.GUIH_STOP_EDIT));
      title.setText("Editor " + getTitle());
      bar.show();
      south.show();
      subtitle.setEditable(true);
    } else {
      bewerken.setText(TextMapper.getText(TextMapper.GUIH_EDIT));
      title.setText(getTitle());
      bar.hide();
      south.hide();
      subtitle.setEditable(false);
    }
  }

  public void setModel(DomStudentModelStructure model) {
    String locale = getLocale().getLanguage();
    if (model == null) {
      model = new DomStudentModelStructure();
      model.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
      model.getInfo().getTitle().put(locale, "Model");
      model.getInfo().getDescription().put(locale, "");      
      model.setCategories(new ArrayList<>());
    }
    this.structure = model; 
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));
    this.subtitle.setText("");
    //text.setEditable(false);
    //OPSLAAN_ACTION.setDescription("");
    this.model.nodeStructureChanged(root);
    this.structure = model;
    setEditable(editable);
    //OPSLAAN_ACTION.left();
  }

  public DomStudentModelStructure getModel() {    
    return structure;
  }

  public JButton ok() { return okButton; }
  public JButton cancel() { return cancelButton; }

  void fillSelection() {
    TreePath path = tree.getSelectionPath();
    if (path == null) {
      subtitle.setText("");
      setDescription("");
      settings.setVisible(false);
      return;
    }
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    Object u = node.getUserObject();
    subtitle.setText(u.toString()); 
    setDescription(u);
    if (u instanceof NodeLeaf) {
      DomStudentModelContextInfo info = ((NodeLeaf) u).getInfo();
      Double d = info.getSlip(); if (d == null) d = 0.05; // DEFAULT SLIP
      slip.setValue(d);
      d = info.getInit(); if (d == null) d = 0.5; // DEFAULT INIT;
      init.setValue(d);
      d = info.getLearn(); if (d == null) d = 0.2; // DEFAULT LEARN;
      learn.setValue(d);
      settings.setVisible(true);
    } else {
      settings.setVisible(false);
    }
  }

  void setDescription(Object u) {
    if (u instanceof Node) {
      String description = ((Node) u).getDescription();
      if (description == null || description.startsWith(WISKOPDR_SIG)||description.isEmpty()) {
        if (editable) {
          Locale locale = getLocale();
          wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(description, locale, container.getWidth(), container.getHeight(), 425, 300);
          wiskOpdrEditPanel.setBackground(Color.WHITE);
          container.removeAll();
          container.add(wiskOpdrEditPanel);
          wiskOpdrEditPanel.setRequestFocusEnabled(true);
          wiskOpdrEditPanel.setFocusable(true);
          wiskOpdrEditPanel.requestFocusInWindow();
        } else {
          WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description);
          panel.setBackground(Color.WHITE);
          container.removeAll();
          container.add(panel);
        }
      } else {
        wiskOpdrEditPanel = null;
        container.removeAll();
      }
    } else {
      wiskOpdrEditPanel = null;
      container.removeAll();
    }
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      fillSelection();
      validate();
    }
  }
  
}
