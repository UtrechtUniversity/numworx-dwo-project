package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import sun.swing.DefaultLookup;

public class LeerdomeinResultsPanel extends JPanel implements TreeSelectionListener, ActionListener {

  private static final Logger LOG = Logger.getLogger(LeerdomeinResultsPanel.class.getName());
  private DomStudentModelContext context;
  
  class SchoolKlas {
    final DomSchoolClass delegate;

    public SchoolKlas(DomSchoolClass delegate) {
      super();
      this.delegate = delegate;
    }

    @Override
    public String toString() {
      if (delegate == null) {
        return "Kies klas";
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

  }

  class ScoreIcon implements Icon {

    float green = 0.24f;
    float red =   0.64f;
    
    ScoreIcon( double score, long count, double part, int size) {
      red = size == 0 ? 0 : (float) part / size;
      green = count == 0 ? 0 : (float) (score/count) * red;
    }
    
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(c.getBackground());
      g.fillRect(x, y, getIconWidth(), getIconHeight());
      x+=2;
      y+=2;
      g.setColor(Color.red);
      g.fillRect(x, y, Math.round(getIconWidth()*red)-2, getIconHeight()-2-3);
      g.setColor(Color.green);
      g.fillRect(x, y, Math.round(getIconWidth()*green)-2, getIconHeight()-2-3);          
      g.setColor(Color.black); g.drawRect(x, y, getIconWidth()-2, getIconHeight()-2-4);
    }

    @Override
    public int getIconWidth() {
      return 150;
    }

    @Override
    public int getIconHeight() {
      return getFontMetrics(getFont()).getHeight()+4+3;
    }
    
  }
  
  
  JComboBox<SchoolKlas> klassen;
  
  JTree tree;

  private DefaultTreeModel model;
  private MutableTreeNode root;
  private JLabel title, title2;
  private JTextArea tekst;
  private JTable table;
  private JLabel score;
  private DomStudentModelScorePerTeacher scores;
  
  
  public LeerdomeinResultsPanel() {
    super(null);
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    
    klassen = new JComboBox<>(new SchoolKlas[] { new SchoolKlas(null) });
    klassen.addActionListener(this);
    root = new DefaultMutableTreeNode("root");
    model = new DefaultTreeModel(root);
    tree = new JTree(model);
    title = new JLabel();
    title2 = new JLabel();
    tekst = new JTextArea(20,20);
    score = new JLabel(new ScoreIcon(0,0,0,0));
    
    Box vbox = Box.createVerticalBox();
    vbox.add(new JScrollPane(tree));
    vbox.add(title);
    vbox.add(score);
    vbox.add(new JScrollPane(tekst));
    
    add(vbox);
    vbox = Box.createVerticalBox();
    Box hbox = Box.createHorizontalBox();
    hbox.add(klassen);
    hbox.add(title2);
    hbox.add(Box.createHorizontalGlue());
    
    vbox.add(hbox);
    
    table = new JTable();
    table.setDefaultRenderer(ScoreIcon.class, new IconRenderer());
    vbox.add(new JScrollPane(table));
    vbox.add(Box.createVerticalGlue());
    
    add(vbox);
    tree.addTreeSelectionListener(this);
    
    
    
  }

  public void setContext(DomStudentModelContext context) {
    this.context = context;
    DomStudentModelStructure model = context.getModelStructure();
    String locale = getLocale().getLanguage();
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));

    this.title.setText(vector.toString());
    this.title2.setText(vector.toString());
    this.tekst.setText(vector.getDescription());
    
    this.model.nodeStructureChanged(root);

  }
  
  public void setClasses(List<DomSchoolClass> list) {
    for(DomSchoolClass i : list) {
      klassen.addItem(new SchoolKlas(i));
    }
    klassen.setMaximumSize(klassen.getPreferredSize());
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      TreePath path = tree.getSelectionPath();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Node n = (Node)node.getUserObject();
      title.setText(n.toString());
      title2.setText(n.toString());
      tekst.setText(n.getDescription());
      
      int[] ipath = getPath(path);
      if (ipath == null || ipath.length == 1) {
        calculateROOT(scores, table.getModel());
      } else if (ipath.length == 2) {
        calculateCategories(scores, table.getModel(), ipath[1]);
      } else if (ipath.length == 3) {
        calculateObjectives(scores, table.getModel(), ipath[1], ipath[2]);
      }
      
    }

    
  }

  private void calculateObjectives(DomStudentModelScorePerTeacher scores, TableModel tmodel, int cat,
      int obj) {
    if (scores == null) return;
    double nzl = 0.0;
    double sumScore = 0.0;
    long sumCount = 0;
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++ ) {
      DomStudentModelStructureScore c = studentScores.get(i).getDomStudentModelStructureScore();
      DomStudentModelCategoryScore o = c.getCategories().get(cat);
      DomStudentModelObjectiveScore v = o.getObjectives().get(obj);
      
      double nz = 0;
      int count = 1;      
      if (v.getCount() != 0) nz += 1;      
      
      Icon result = new ScoreIcon (v.getScore()  , v.getCount() , nz , count);
      tmodel.setValueAt(result, i, 1);
      if (v.getCount() != 0) nzl ++;
      sumScore += v.getScore();
      sumCount += v.getCount();
    }
    score.setIcon( new ScoreIcon (sumScore , sumCount , nzl , tmodel.getRowCount()  ));
    //score.setText( sumScore + "/" + sumCount + " " + nzl + "/" + tmodel.getRowCount()  );
    
    
  }

  private int[] getPath(TreePath path) {
    Object[] o = path.getPath();
    int[] result = new int[o.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((Node) ((DefaultMutableTreeNode) o[i]).getUserObject()).getPath();
    }
    return result;
  }

  private void calculateCategories(DomStudentModelScorePerTeacher scores, TableModel tmodel,
      int cat) {
    if (scores == null) return;
    double nzl = 0.0;
    double sumScore = 0.0;
    long sumCount = 0;
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++ ) {
      DomStudentModelStructureScore c = studentScores.get(i).getDomStudentModelStructureScore();
      DomStudentModelCategoryScore v = c.getCategories().get(cat);
      
      double nz = 0;
      int count = v.getObjectives().size();
      for( DomStudentModelObjectiveScore item: v.getObjectives()) {
        if (item.getCount() != 0) nz += 1;      
      }
      Icon result = new ScoreIcon (v.getScore()  , v.getCount() , nz , count);
      tmodel.setValueAt(result, i, 1);
      if (v.getCount() != 0) nzl ++;
      sumScore += v.getScore();
      sumCount += v.getCount();
    }
   // score.setText( sumScore + "/" + sumCount + " " + nzl + "/" + tmodel.getRowCount()  );
    score.setIcon( new ScoreIcon (sumScore , sumCount , nzl , tmodel.getRowCount()  ));
   
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==klassen) {
      SchoolKlas klas = klassen.getItemAt(klassen.getSelectedIndex());
      if (klas != null && klas.delegate != null) {
        DomSchoolClass dom = klas.delegate;
        scores = new DomStudentModelScorePerTeacher();
        scores.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(dom.getId(), dom)));
        scores.setStudentModelContexts(Collections.singletonList(new DomMapEntry<>(context.getId(), context)));
        try {
          scores = SecureTeacherStudentModelManager.getScores(scores);
          TableModel tmodel = new DefaultTableModel(scores.getStudents().size(), 2);
          for (int i = 0; i < tmodel.getRowCount(); i++ ) {
            String u = scores.getStudents().get(i).getValue().getDisplayName();
            tmodel.setValueAt(u, i, 0);
          }
          calculateROOT(scores, tmodel);
          table.setModel(tmodel);
          table.getColumnModel().getColumn(1).setCellRenderer(new IconRenderer());
          table.setRowHeight(score.getPreferredSize().height+2);
          table.clearSelection();
        } catch (Dwo2Exception e1) {
          LOG.log(Level.SEVERE, "getScores", e1);
        }
      } else {
        table.setModel(new DefaultTableModel(0,2));
      }
    }
    
  }

  private void calculateROOT(DomStudentModelScorePerTeacher scores, TableModel tmodel) {
    if (scores == null) return;
    double nzl = 0.0;
    double sumScore = 0.0;
    long sumCount = 0;
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++ ) {
      DomStudentModelStructureScore v = studentScores.get(i).getDomStudentModelStructureScore();
      double nz = 0;
      int count = v.getCategories().size();
      for( DomStudentModelCategoryScore item: v.getCategories()) {
        if (item.getCount() != 0) nz += 1;      
      }
      Icon result = new ScoreIcon (v.getScore()  , v.getCount() , nz , count);
      tmodel.setValueAt(result, i, 1);
      if (v.getCount() != 0) nzl ++;
      sumScore += v.getScore();
      sumCount += v.getCount();
    }
   // score.setIcon( new ScoreIcon (sumScore , sumCount , nzl , tmodel.getRowCount()  ));
    score.setText( sumScore + "/" + sumCount + " " + nzl + "/" + tmodel.getRowCount()  );
  }
  
}
