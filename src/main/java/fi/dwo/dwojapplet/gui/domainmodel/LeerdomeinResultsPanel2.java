package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JTree;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

public class LeerdomeinResultsPanel2 extends JPanel implements Constants {

  
  public static void main(String[] args) {
    LeerdomeinResultsPanel2 p = new LeerdomeinResultsPanel2();
    ConfirmDialog d = new ConfirmDialog(null, "sample");
    d.setContentPane(p);
    d.pack();d.show();
    System.exit(0);
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

  
  
  private JLabel titleLabel;
  private JTree  tree;
  private Font font = GuiConstants.NORMAL_TEXT;
  private DefaultMutableTreeNode root;
  private DefaultTreeModel model;
  private JLabel subtitle;
  private JTextArea tekst;
  private JScrollPane scroll;
  private JComboBox<SchoolKlas> klassen;
  private JTable results;
  private JScrollPane resultsPane;
  
  public LeerdomeinResultsPanel2() {
    super(new BorderLayout());
    titleLabel = new JLabel("Klasresultaten op leerdoelen");
    add(titleLabel, BorderLayout.NORTH);
    titleLabel.setBackground(COLOR15);
    titleLabel.setForeground(COLOR20);
    titleLabel.setFont(font.deriveFont(24f));
    titleLabel.setOpaque(true);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
 
    Box leftBox = Box.createVerticalBox();
    JPanel filterBox = new JPanel();
    JButton filter = new JButton("Filter leerdoelen");
    filterBox.add(filter);
    leftBox.add(filterBox);
 
    root = new DefaultMutableTreeNode("Handig haakjes wegwerken bij merkwaardige producten");
    model = new DefaultTreeModel(root);
    tree = new JTree(model);
    tree.setBackground(COLOR20);

    JScrollPane pane = new JScrollPane(tree);
    pane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    pane.setViewportBorder(BorderFactory.createEmptyBorder());
    pane.setBackground(COLOR20);
    leftBox.add(pane);
    
    subtitle = new JLabel("Handig haakjes wegwerken bij merkwaardige producten");
    subtitle.setForeground(Color.WHITE);
    subtitle.setFont(font.deriveFont(14f));
    Box b = hb(ra(10,0), subtitle, hgl());
    b.setBackground(COLOR13);
    b.setOpaque(true);

    leftBox.add(b);
    
    tekst = new JTextArea(5,20);tekst.setEditable(false);
    scroll = new JScrollPane(tekst);
    
    leftBox.add(scroll);
    leftBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));   
    add(leftBox, BorderLayout.CENTER);
    
    Box rightBox = Box.createVerticalBox();
    
    JLabel kies = new JLabel("Resultaten klas:");
    kies.setFont(font.deriveFont(Font.BOLD, 16));
    kies.setForeground(COLOR15);
    kies.setMaximumSize(kies.getPreferredSize());
    klassen = new fi.beans.numworxlf.JComboBox<>(new SchoolKlas[] {new SchoolKlas(null)});
    klassen.setSelectedIndex(0);
    
    b = hb( kies, ra(20,0), klassen, hgl());
    b.setOpaque(true); b.setBackground(COLOR20);
    b.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    Dimension pref = b.getPreferredSize();pref.width = Short.MAX_VALUE;
    b.setMaximumSize(pref);
    rightBox.add(b);
    
    results = new JTable();
    results.setBackground(COLOR20);
    resultsPane = new JScrollPane(results);
    resultsPane.getViewport().setBackground(COLOR20);
    resultsPane.setBackground(COLOR20);
    resultsPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    rightBox.add(resultsPane);
    
    JLabel l = new JLabel("Klasgemiddelde: ");
    l.setFont(font.deriveFont(Font.BOLD, 16));
    l.setForeground(COLOR15);
    JPanel gemiddelde = new JPanel();
    gemiddelde.setBackground(Color.RED);
    Dimension size = new Dimension(60,20);
    gemiddelde.setPreferredSize(size);
    gemiddelde.setMaximumSize(size);
    gemiddelde.setMinimumSize(size);
    b = hb(ra(10,0), l, ra(10,0), gemiddelde, hgl());   
    rightBox.add(b);    
    rightBox.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
    add(rightBox, BorderLayout.EAST);
    setOpaque(true);
    setBackground(COLOR20);
  }

  public void setContext(DomStudentModelContext model) {
    // TODO Auto-generated method stub
    
  }

  public void setClasses(List<DomSchoolClass> list) {
    // TODO Auto-generated method stub
    
  }

  private static Box hb(Component... c) {
    Box box = Box.createHorizontalBox();
    for (int i = 0; c != null && i < c.length; i++)
        box.add(c[i]);
    return box;
}
  private static Component hgl() {
    return Box.createHorizontalGlue();
}
  private static Component ra(int w, int h) {
    return Box.createRigidArea(new Dimension(w, h));
}

}
