package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTree;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.TeacherStudentModelPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class LeerdomeinResultsPanel2 extends JPanel implements Constants, ActionListener, TreeSelectionListener {
  private static final Logger LOG = Logger.getLogger(LeerdomeinResultsPanel2.class.getName());
  static final Color RED = new Color(200, 0, 0);
  static final Color GREEN = new Color(0, 180, 0);

  
//  public static void main(String[] args) {
//    LeerdomeinResultsPanel2 p = new LeerdomeinResultsPanel2();
//    ConfirmDialog d = new ConfirmDialog(null, "sample");
//    d.setContentPane(p);
//    d.pack();d.show();
//    System.exit(0);
//  }
  
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
  }
  static class ColorRenderer extends DefaultTableCellRenderer {
    final Color color;
    final Color selColor;

    ColorRenderer(Color color) {
      this.color = color;
      this.selColor = color;
    }
    ColorRenderer(Color color, Color selected) {
      this.color = color;
      this.selColor = selected;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
      Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      component.setForeground(isSelected? selColor: color);
      return component;
    }
    
  }
  public class ImageButtonEditor extends AbstractCellEditor implements
  TableCellEditor, ActionListener {

Object value;
private int row;
TableModel model;
//ClassTeacherPanelTableModel model;

@Override
public Component getTableCellEditorComponent(JTable table, Object value,
      boolean arg2, int aRow, int aCol) {
  this.value = value;
  JButton button = new JButton((Icon) value);
  button.addActionListener(this);
  row = aRow;
  model = table.getModel();
  return button;
}

@Override
public Object getCellEditorValue() {
  return value;
}


Window getWindowForComponent(Component parentComponent)
    throws HeadlessException {
    if (parentComponent == null)
        return JOptionPane.getRootFrame();
    if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
        return (Window)parentComponent;
    return getWindowForComponent(parentComponent.getParent());
}
private JDialog createDialog(Component parentComponent, String title)
                             throws HeadlessException {

                         final JDialog dialog;

                         Window window = getWindowForComponent(parentComponent);
                         if (window instanceof Frame) {
                             dialog = new JDialog((Frame)window, title, true);
                         } else {
                             dialog = new JDialog((Dialog)window, title, true);
                         }
                         return dialog;
                     }

@Override
public void actionPerformed(ActionEvent event) {
  if (value == lens) {
    String student = (String) model.getValueAt(row, 0);
    String title = "";
    StudentResultsPanel message = new StudentResultsPanel(student);
    message.setContext(context);
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    DomStudentModelStructureScore v = studentScores.get(row).getDomStudentModelStructureScore();
    message.setScore(v);
    JDialog dialog = createDialog(LeerdomeinResultsPanel2.this, title);
    dialog.setContentPane(message);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.pack();
    dialog.setLocationRelativeTo(LeerdomeinResultsPanel2.this);
    dialog.show();    
  }
}

}

  
  private JLabel titleLabel;
  private JTree  tree;
  private Font font = GuiConstants.NORMAL_TEXT;
  private DefaultMutableTreeNode root;
  private InvisibleTreeModel model;
  private JLabel subtitle;
  private JTextArea tekst;
  private JScrollPane scroll;
  private JComboBox<SchoolKlas> klassen;
  private JTable results;
  private JScrollPane resultsPane;
  private DomStudentModelContext context;
  private DomStudentModelScorePerTeacher scores;
  private JLabel red, score, green;
  private Icon lens;
  
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
    Image searchImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);
    lens = new ImageIcon(searchImage);
 
    JSplitPane leftBox;
    Box vb = Box.createVerticalBox();
    JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Consumer<Map<String, Map<String, Set<Integer>>>> consumer = this::filter;
    FilterAction fa = new FilterAction(this, consumer);
    JButton filter = new JButton(fa);
    filterBox.add(filter);
    vb.add(filterBox);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setResizeWeight(0.9);
    BasicSplitPaneUI sui = (BasicSplitPaneUI) BasicSplitPaneUI.createUI(split);
    split.setUI(sui);
    BasicSplitPaneDivider divider = sui.getDivider();
    divider.setBorder(BorderFactory.createEmptyBorder());
    divider.setBackground(Constants.COLOR20);
    split.setDividerSize(20);
    //split.setBorder(BorderFactory.createEmptyBorder());
    leftBox = (split);
    leftBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
    root = new DefaultMutableTreeNode("Handig haakjes wegwerken bij merkwaardige producten");
    model = new InvisibleTreeModel(root);
    tree = new JTree(model);
    TreeCellRenderer renderer = new TreeCellRenderer();
    renderer.updateUI();
    tree.setCellRenderer(renderer);
    tree.updateUI();
    tree.setBackground(COLOR20);
    tree.addTreeSelectionListener(this);
    renderer.setBackgroundNonSelectionColor(COLOR20);

    JScrollPane pane = new JScrollPane(tree);
    pane.setBorder(BorderFactory.createEmptyBorder(20,20,0,0));
    pane.setViewportBorder(BorderFactory.createEmptyBorder());
    pane.setBackground(COLOR20);
    pane.setPreferredSize(new Dimension(580,300));
    vb.add(pane);
    split.setTopComponent(vb);
    subtitle = new JLabel("Handig haakjes wegwerken bij merkwaardige producten");
    subtitle.setForeground(Color.WHITE);
    subtitle.setFont(font.deriveFont(Font.BOLD, 14f));
    subtitle.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
    vb = Box.createVerticalBox();
    Box b = hb(ra(10,0), subtitle, hgl());
    b.setBackground(COLOR13);
    b.setOpaque(true);

    vb.add(b);
    
    tekst = new JTextArea(5,20);tekst.setEditable(false);
    scroll = new JScrollPane(tekst);
    Dimension min = new Dimension(480, 250);
    scroll.setMinimumSize(min);
    scroll.setPreferredSize(min);
    vb.add(scroll);
    split.setBottomComponent(vb);
    leftBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 00));   
    add(leftBox, BorderLayout.CENTER);
    
    Box rightBox = Box.createVerticalBox();
    
    JLabel kies = new JLabel("Resultaten klas:");
    kies.setFont(font.deriveFont(Font.BOLD, 16));
    kies.setForeground(COLOR15);
    kies.setMaximumSize(kies.getPreferredSize());
    klassen = new fi.beans.numworxlf.JComboBox<>(new SchoolKlas[] {new SchoolKlas(null)});
    klassen.setSelectedIndex(0);
    klassen.addActionListener(this);
    
    b = hb( kies, ra(20,0), klassen, hgl());
    b.setOpaque(true); b.setBackground(COLOR20);
    b.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    Dimension pref = b.getPreferredSize();pref.width = Short.MAX_VALUE;
    b.setMaximumSize(pref);
    rightBox.add(b);
    
    results = new JTable();
    results.setTableHeader(null);
    results.setBackground(COLOR20);
    results.setForeground(COLOR13);
    resultsPane = new JScrollPane(results);
    resultsPane.getViewport().setBackground(COLOR20);
    resultsPane.setBackground(COLOR20);
    resultsPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
    resultsPane.setPreferredSize(new Dimension(440, 550));
    rightBox.add(resultsPane);
    
    JLabel l = new JLabel("Klasgemiddelde: ");
    l.setFont(font.deriveFont(Font.BOLD, 16));
    l.setForeground(COLOR15);
    JComponent gemiddelde = score = new JLabel(new ScoreIcon(0, 0, 0, 0, l.getFontMetrics(l.getFont())));
    red = new JLabel("0%"); red.setForeground(RED);
    green = new JLabel("0%"); green.setForeground(GREEN);
    b = hb(ra(10,0), l, ra(10,0), red, gemiddelde, green, hgl());   
    rightBox.add(b);    
    rightBox.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
    add(rightBox, BorderLayout.EAST);
    setOpaque(true);
    setBackground(COLOR20);
  }

  private void setDescription(Node n) {
    String description = n.getDescription();
    if (description == null) description = "";
    if (description.startsWith(LeerdomeinEditPanel2.WISKOPDR_SIG))
    {
      WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description, getLocale());
      panel.setBackground(Color.white);
      scroll.setViewportView(panel);
    } else {
      tekst.setText(description);
      scroll.setViewportView(tekst);
    }
  }

  public void setContext(DomStudentModelContext context) {
    this.context = context;
    DomStudentModelStructure model = context.getModelStructure();
    String locale = getLocale().getLanguage();
    
    model = AdviseMeResultManager.restructure(model, locale, context);
      
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));

    this.subtitle.setText(vector.toString());
    //this.title2.setText(vector.toString());
    //this.tekst.setText(vector.getDescription());
    setDescription(vector);
    this.model.nodeStructureChanged(root);

  }

  public void setClasses(List<DomSchoolClass> list) {
    for(DomSchoolClass i : list) {
      klassen.addItem(new SchoolKlas(i));
    }
    klassen.setMaximumSize(klassen.getPreferredSize());
  }

  static Box hb(Component... c) {
    Box box = Box.createHorizontalBox();
    for (int i = 0; c != null && i < c.length; i++)
        box.add(c[i]);
    return box;
}
   static Component hgl() {
    return Box.createHorizontalGlue();
}
   static Component ra(int w, int h) {
    return Box.createRigidArea(new Dimension(w, h));
}

  private int[] getPath(TreePath path) {
    if (path == null) return null;
    Object[] o = path.getPath();
    int[] result = new int[o.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((Node) ((DefaultMutableTreeNode) o[i]).getUserObject()).getPath();
    }
    return result;
  }

  
  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      TreePath path = tree.getSelectionPath();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Node n = (Node)node.getUserObject();
      subtitle.setText(n.toString());
      //title2.setText(n.toString());
      setDescription(n);     
      calculatePath(path,n);     
    }

    
  }

  private void calculatePath(TreePath path, Node n) {
    int[] ipath = getPath(path);
    if (ipath == null || ipath.length == 1) {
      calculateROOT(scores, results.getModel(),n);
    } else if (ipath.length == 2) {
      calculateCategories(scores, results.getModel(), ipath[1],n);
    } else if (ipath.length >= 3) {
      calculateObjectives(scores, results.getModel(), ipath, n);
    }
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
          //scores = SecureTeacherStudentModelManager.getScores(scores);

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

          if (context.getModelStructure().getInfo().getId() != null &&
              context.getModelStructure().getInfo().getId().startsWith(AdviseMeResultManager.KEY))
          {
            try {
              scores = new AdviseMeResultManager().fromAdviseMe(scores).getValue();
            } catch (InvocationTargetException e1) {
              LOG.log(Level.SEVERE, "fromAdviseMe", e1.getCause());
            } catch (InterruptedException e1) {
              LOG.log(Level.WARNING, "interrupted", e1);
            }
          } else {
// fetch students of class         

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
          
          TableModel tmodel = new DefaultTableModel(scores.getStudents().size(), 5) {
            public boolean isCellEditable(int row, int column) {
              return column == 1;
            } 
          } ;
          for (int i = 0; i < tmodel.getRowCount(); i++ ) {
            String u = scores.getStudents().get(i).getValue().getDisplayName();
            tmodel.setValueAt(u, i, 0);
            tmodel.setValueAt(lens, i, 1);
          }
          //calculateROOT(scores, tmodel);
          JTable table = results;
          table.setModel(tmodel);
          table.setSelectionBackground(COLOR14);
          calculatePath(tree.getSelectionPath(), null);
          TableColumnModel columnModel = table.getColumnModel();
          TableColumn c0 = columnModel.getColumn(0);
          ColorRenderer studentRenderer = new ColorRenderer(COLOR15, table.getSelectionForeground());
          c0.setCellRenderer(studentRenderer);
          
          TableColumn c1 = columnModel.getColumn(2);
          ColorRenderer redRenderer = new ColorRenderer(RED);
          redRenderer.setHorizontalAlignment(SwingConstants.TRAILING);
          c1.setCellRenderer(redRenderer);
          int width = new JLabel("100%").getPreferredSize().width;
          c1.setPreferredWidth(width);
          c1.setMaxWidth(width);
          TableColumn c3 = columnModel.getColumn(4);
          c3.setCellRenderer(new ColorRenderer(GREEN));
          c3.setPreferredWidth(width);
          c3.setMaxWidth(width);
          TableColumn c2 = columnModel.getColumn(3);
          c2.setCellRenderer(new IconRenderer());
          width = score.getIcon().getIconWidth()+3;
          c2.setPreferredWidth(width);
          c2.setMaxWidth(width);
          table.setRowHeight(score.getPreferredSize().height+2);
          table.clearSelection();
          
          c0 = columnModel.getColumn(1);
          c0.setCellRenderer(new IconRenderer());
          c0.setCellEditor(new ImageButtonEditor());
          c0.setMaxWidth(new JLabel(lens).getPreferredSize().width+4);
          
        } catch (Dwo2Exception e1) {
          LOG.log(Level.SEVERE, "getScores", e1);
        }
      } else {
        results.setModel(new DefaultTableModel(0,5));
      }
    }
    
  }

  private void calculateROOT(DomStudentModelScorePerTeacher scores, TableModel tmodel, Node n) {
    if (scores == null) return;
    double greenScore = 0.0, redScore = 0.0;
    long greenCount = 0, redCount = 0, totalCount = 0;
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++ ) {
      DomStudentModelStructureScore v = studentScores.get(i).getDomStudentModelStructureScore();
      if (v == null)
        v = new DomStudentModelStructureScore();
      List<DomStudentModelCategoryScore> categories = v.getCategories();
      if (categories == null) 
        categories = Collections.emptyList();
      PIcon result;// = new ScoreIcon (v.getScore()  , v.getCount() , nz , count, results.getFontMetrics(results.getFont()));
      result = StudentResultsPanel.createIcon(n, v, results.getFontMetrics(results.getFont()));
      tmodel.setValueAt(result.getRedPercentage(), i, 2);
      tmodel.setValueAt(result, i, 3);
      tmodel.setValueAt(result.getGreenPercentage(), i, 4);
      totalCount += v.getTotalCount();
      redCount += v.getRedCount();
      greenCount += v.getGreenCount();
      if (v.getRedCount()>0) redScore += v.getRedScore();
      if (v.getGreenCount()>0) greenScore += v.getGreenScore();
    }
    
    DomStudentModelScore<?> s = new DomStudentModelScore<>();
    s.setScore(greenScore, greenCount, redScore, redCount, totalCount);
    PIcon icon = new SummaryIcon(s, results.getFontMetrics(results.getFont()));
        //new ScoreIcon (sumScore , sumCount , nzl , tmodel.getRowCount(), results.getFontMetrics(results.getFont()));
    score.setIcon( icon);
    red.setText(icon.getRedPercentage());
    green.setText(icon.getGreenPercentage());
  }

  private void calculateCategories(DomStudentModelScorePerTeacher scores, TableModel tmodel,
      int cat, Node n) {
    if (scores == null) return;
    double greenScore = 0.0, redScore = 0.0;
    long greenCount = 0, redCount = 0, totalCount = 0;
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++) {
      DomStudentModelStructureScore c = studentScores.get(i).getDomStudentModelStructureScore();
      if(c == null) c = new DomStudentModelStructureScore();
      DomStudentModelCategoryScore v = 
          cat < c.getCategories().size() ? c.getCategories().get(cat) : new DomStudentModelCategoryScore();

      PIcon result;// = new ScoreIcon(v.getScore(), v.getCount(), nz, count, results.getFontMetrics(results.getFont()));
      result = StudentResultsPanel.createIcon(n, v, results.getFontMetrics(results.getFont()));
      tmodel.setValueAt(result, i, 3);
      tmodel.setValueAt(result.getGreenPercentage(), i, 2);
      tmodel.setValueAt(result.getGreenPercentage(), i, 4);
      totalCount += v.getTotalCount();
      redCount += v.getRedCount();
      greenCount += v.getGreenCount();
      if (v.getRedCount()>0) redScore += v.getRedScore();
      if (v.getGreenCount()>0) greenScore += v.getGreenScore();
    }
    // score.setText( sumScore + "/" + sumCount + " " + nzl + "/" + tmodel.getRowCount() );
    DomStudentModelScore<?> s = new DomStudentModelScore<>();
    s.setScore(greenScore, greenCount, redScore, redCount, totalCount);
    PIcon icon = new SummaryIcon(s, results.getFontMetrics(results.getFont()));
    score.setIcon(icon);
    red.setText(icon.getGreenPercentage());
    green.setText(icon.getGreenPercentage());

  }

  private void calculateObjectives(DomStudentModelScorePerTeacher scores, TableModel tmodel, int[] path, Node n) {
    if (scores == null) return;
    double greenScore = 0.0, redScore = 0.0;
    long greenCount = 0, redCount = 0, totalCount = 0;
    final int cat = path[1];
    final int obj = path[2];
    List<DomStudentModelDataStudentScore> studentScores = scores.getStudentScores();
    for (int i = 0; i < tmodel.getRowCount(); i++ ) {
      DomStudentModelStructureScore c = studentScores.get(i).getDomStudentModelStructureScore();
      if (c == null) c = new DomStudentModelStructureScore();
      DomStudentModelCategoryScore o = c.getCategories().size()> cat ? c.getCategories().get(cat): new DomStudentModelCategoryScore();
      DomStudentModelObjectiveScore v = o.getObjectives().size() > obj ? o.getObjectives().get(obj): new DomStudentModelObjectiveScore();
      if (path.length >= 4) {
        for (int index = 3; index < path.length; index++) {
          int sub = path[index];
          if (v.getChildren() != null && v.getChildren().size() > sub)
            v = v.getChildren().get(sub);
          else
            v = new DomStudentModelObjectiveScore();
        }
      }
           
      PIcon result;
      result = StudentResultsPanel.createIcon(n, v, results.getFontMetrics(results.getFont()));
      tmodel.setValueAt(result, i, 3);
      tmodel.setValueAt(result.getRedPercentage(), i, 2);
      tmodel.setValueAt(result.getGreenPercentage(), i, 4);
      totalCount += v.getTotalCount();
      redCount += v.getRedCount();
      greenCount += v.getGreenCount();
      if (v.getRedCount()>0) redScore += v.getRedScore();
      if (v.getGreenCount()>0) greenScore += v.getGreenScore();
    }
    DomStudentModelScore<?> s = new DomStudentModelScore<>();
    s.setScore(greenScore, greenCount, redScore, redCount, totalCount);
    PIcon icon = new SummaryIcon(s, results.getFontMetrics(results.getFont()));
    score.setIcon( icon);
    red.setText( icon.getRedPercentage());
    green.setText(icon.getGreenPercentage());
    
  }
  public void filter(Map<String,Map<String,Set<Integer>>> filter) {
    if (filter.isEmpty()) {
      model.activateFilter(false);
      if (model.getRoot() != root) model.setRoot(root);
    } else {
      model.activateFilter(true);
      model.setRoot(LeerdomeinEditPanel2.filter(root, filter));      
    }
    if(scores != null)
      recalculateAncestors(root, scores.getStudentScores());
  }

  private void recalculateAncestors(DefaultMutableTreeNode node,
      List<DomStudentModelDataStudentScore> studentScores) {
    for(DomStudentModelDataStudentScore item:studentScores) {
      DomStudentModelScore<?> top = item.getDomStudentModelStructureScore();
      if (top!=null) recalculateAncestors(node, top);
    }
  }


  private void recalculateAncestors(DefaultMutableTreeNode node, DomStudentModelScore<?> top) {
    @SuppressWarnings("rawtypes")
    List<? extends DomStudentModelScore> children = top.getChildren();
    if(children != null) {
      // interior node
      int redCount = 0, greenCount = 0, totalCount = 0;
      double redScore = 0.0, greenScore = 0.0;
      Enumeration<?> nodes = node.children();
      for(DomStudentModelScore<?> child: children) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) nodes.nextElement();
        if (!invisible(n) && child != null) {
          recalculateAncestors(n, child);
          redCount += child.getRedCount();
          redScore += child.getRedScore();
          greenCount += child.getGreenCount();
          greenScore += child.getGreenScore();
          totalCount += child.getTotalCount();
        }
      }
      top.setScore(greenScore, greenCount, redScore, redCount, totalCount);
    }
  }

  private boolean invisible(DefaultMutableTreeNode child) {
    return child instanceof InvisibleNode && ! ((InvisibleNode)child).isVisible();
  }

  
}
