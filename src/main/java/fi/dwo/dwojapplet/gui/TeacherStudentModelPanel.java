package fi.dwo.dwojapplet.gui;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domainmodel.ImportAction;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinResultsPanel2;
import fi.dwo.dwojapplet.gui.domainmodel.SettingsSchoolClassPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 *
 * @author plas0006
 */
public class TeacherStudentModelPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanel.class.getName());

    public final TeacherStudentModelPanelProperties prop;
    private final TeacherStudentModelPanelTableModel tableModel;

    private CenterPanel center;

    private JButton addModelButton, importModelButton;
    private LeerdomeinEditPanel2 textArea;

    private JPanel jtbl;
    private TableRowSorter<TeacherStudentModelPanelTableModel> rowSorter;

    private Image searchImage, removeImage, resultsImage, classImage;
    int row;

    private JScrollPane scrollPane;

    public class ImageRenderer extends JLabel implements TableCellRenderer {

        private ImageIcon icon = new ImageIcon();

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean hasFocus, int row, int col) {
            Image image = (Image) value;
            if (image != null) {
              icon.setImage(image);
              setIcon(icon);
            } else {
              setIcon(null);
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            //Object[] arguments = new Object[]{table.getValueAt(row, 0)};

            if (selected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }

    }

    public class ImageButtonEditor extends AbstractCellEditor implements
            TableCellEditor, ActionListener {

        Object value;
//        ClassTeacherPanelTableModel model;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int aRow, int aCol) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            row = aRow;
            //model = (ClassTeacherPanelTableModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (value == searchImage) {
              String title = (String) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), 0);
              try {
               DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getContextColumn());
               model = prop.getModel(model);
               if (model.getPublishState() == PublishState.edit) {
                  JOptionPane.showMessageDialog(TeacherStudentModelPanel.this, "Er werkt al mogelijk iemand mee!",title, JOptionPane.WARNING_MESSAGE);
               }
                textArea.setEditable(false);
                textArea.setModel(model.getModelStructure(), model.getPublishState());
//                cancelButton.setEnabled(true);
//                addModelButton.setText(TextMapper.getText(TextMapper.BTN_UPDATE));
                int ok = showConfirmDialog(TeacherStudentModelPanel.this, scrollPane, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                model = prop.getCurrent();
                if ( ok == JOptionPane.OK_OPTION && model.getPublishState() == PublishState.edit) {
                  DomStudentModelStructure modelStructure = textArea.getModel();
                  model.setModelStructure(modelStructure);
                  model.setPublishState(PublishState.published);
                  prop.updateModel(model);
                } else {
                  if (textArea.isLock()) {
                    model.setPublishState(PublishState.published);
                    prop.updateModel(model);                    
                  }
                }
              } catch (Dwo2Exception e) {
                LOG.log(Level.SEVERE, "update model " + title, e);
                GuiCreator.instance().ShowErrorDialog(center, e);
              } finally {
                try {
                  textArea.end();
                  tableModel.init(prop.getModelList(), searchImage, removeImage, resultsImage, classImage);
                } catch (Dwo2Exception e) {
                  LOG.log(Level.SEVERE, "refresh list " + title, e);
                  GuiCreator.instance().ShowErrorDialog(center, e);
               }
              }
            }
            if (value == removeImage) {
              DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
              String title = (String) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), 0);
              int ok = JOptionPane.showConfirmDialog(TeacherStudentModelPanel.this, "Zeker '" + title + "' weg?", title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
              if ( ok == JOptionPane.OK_OPTION) {
                try {
                  prop.removeModel(model);
                  tableModel.init(prop.getModelList(), searchImage, removeImage, resultsImage, classImage);
                } catch (Dwo2Exception e) {
                  LOG.log(Level.SEVERE, "remove model " + title, e);
                  GuiCreator.instance().ShowErrorDialog(center, e);

                }
              }
              
              
              
            }
            
            if (value == resultsImage) {
              DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
              //String title = (String) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), 0);
              LeerdomeinResultsPanel2 panel = new LeerdomeinResultsPanel2();
              List<DomSchoolClass> list;
              try {
                  model = prop.getModel(model);
                  panel.setContext(model);
                list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
              } catch (Dwo2Exception e) {
                LOG.log(Level.SEVERE, "get classes for popup", e);
                list = Collections.emptyList();
              }
              panel.setClasses(list);
              ConfirmDialog dialog = new ConfirmDialog(TeacherStudentModelPanel.this, "");
              dialog.setContentPane(panel);
              dialog.pack();
//              int w = dialog.getWidth(), h = dialog.getHeight();
//              Dimension m = dialog.getMaximumSize();
              //dialog.setSize(Math.min(w, m.width), Math.min(h, m.height));
              dialog.center();
              dialog.setVisible(true);
            }
            
            if (value == classImage) {
              DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
              List<DomSchoolClass> list;
              try {
                list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
              } catch (Dwo2Exception e) {
                list = Collections.emptyList();
              }
 
              ConfirmDialog dialog = new ConfirmDialog(TeacherStudentModelPanel.this, "Instelling leerdomein per klas");
              SettingsSchoolClassPanel pane = new SettingsSchoolClassPanel(list, model);
              dialog.getContentPane().add(pane, BorderLayout.CENTER);
              JButton ok = new JButton("OK");
              ok.addActionListener(dialog::ok);
              dialog.getContentPane().add(ok, BorderLayout.SOUTH);
              dialog.pack();
              dialog.center();
              dialog.setVisible(true);
              if (dialog.getOption() == JOptionPane.OK_OPTION) {
                pane.update();
              }
            }
        }
//        private void recursiveUnfocusButtons(Component component) {
//          if (component instanceof JButton) {
//              JButton button = (JButton) component;
//              button.setFocusable(false);
//              return;
//          } else if (component instanceof Container) {
//              for (Component c : ((Container) component).getComponents()) {
//                  recursiveUnfocusButtons(c);
//              }
//          }
//      }

//        private int showConfirmDialogxxx(TeacherStudentModelPanel teacherStudentModelPanel,
//            JScrollPane scrollPane, String title, int okCancelOption, int plainMessage) {
//         return JOptionPane.showConfirmDialog(teacherStudentModelPanel, scrollPane, title, okCancelOption,plainMessage );

//          JOptionPane optionPane = new JOptionPane() {
//            
//          };
//          optionPane.setMessage(scrollPane);
//          optionPane.setMessageType(plainMessage);
//          optionPane.setOptionType(okCancelOption);
//
//          KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
//          optionPane.getInputMap(JComponent.WHEN_FOCUSED).put(enterStroke, enterStroke.toString());
//          optionPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enterStroke, enterStroke.toString());
//          optionPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterStroke, enterStroke.toString());
//          
//          optionPane.getActionMap().put(enterStroke.toString(), new AbstractAction() {
//              @Override
//              public void actionPerformed(ActionEvent e) {
//                  // do nothing
//                System.err.print("ACTION");
//              }
//          });
//
//          //recursiveUnfocusButtons(optionPane);
//          JDialog dialog = optionPane.createDialog(teacherStudentModelPanel, title);
//          recursiveUnfocusButtons(dialog);
//          dialog.show();
//          dialog.dispose();
//          Object        selectedValue = optionPane.getValue();
//
//          if(selectedValue == null)
//              return optionPane.CLOSED_OPTION;         
//          if(selectedValue instanceof Integer)
//              return ((Integer)selectedValue).intValue();
//          return optionPane.CLOSED_OPTION;
//      
//        }
    }

    private void buildJTable() throws Dwo2Exception {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }
        jtbl = new JPanel();

        JTable jtable = new JTable();
        jtable.setForeground(GuiConstants.MAIN_FOREGROUND);
        jtable.getTableHeader().setForeground(GuiConstants.MAIN_FOREGROUND);
        jtable.getTableHeader().setReorderingAllowed(false);
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);

        tableModel.init(prop.getModelList(), searchImage, removeImage, resultsImage, classImage);
        jtable.setModel(tableModel);
        rowSorter = new TableRowSorter<TeacherStudentModelPanelTableModel>(tableModel);
        rowSorter.toggleSortOrder(0);//
        jtable.setRowSorter(rowSorter);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new TeacherStudentModelPanel.ImageRenderer(), new TeacherStudentModelPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

//        TableUtil.setDefaults(jtable, false, new ImageRenderer(), new ImageButtonEditor());
//        TableUtil.setJTableSizes(jtable);
// TODO shrink to fit heeft 520 als breedte
//        Dimension size = jtable.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        jtable.setMaximumSize(size);
        jtbl.setLocation(30, addModelButton.getSize().height
                + addModelButton.getLocation().y + 15);
        TableUtil.setBorder(jtable);
        //TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    
    
    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public TeacherStudentModelPanel() throws Dwo2Exception {
      this(new TeacherStudentModelPanelProperties(
        GuiCreator.instance().getStudentModelManager()
        ), new TeacherStudentModelPanelTableModel());
    }
    public TeacherStudentModelPanel(TeacherStudentModelPanelProperties prop, TeacherStudentModelPanelTableModel tmodel) throws Dwo2Exception {
        super(null);
        this.prop = prop;
        tableModel = tmodel;

        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowErrorDialog(this, e);
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(getSubHeaderColor());
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        MediaTracker tr = new MediaTracker(this);
        searchImage = DwoHelper.getResourceImage(GuiConstants.EDIT_STUDENTMODEL_IMAGE);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENTMODEL_IMAGE);
        resultsImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);
        classImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
        tr.addImage(searchImage, 0);
        tr.addImage(removeImage, 0);
        tr.addImage(searchImage, 0);
        tr.addImage(classImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        addModelButton = new JButton(TextMapper.getText(TextMapper.GUIC_STUDENTMODELS_ADD));
        addModelButton.setSize(addModelButton.getPreferredSize());
        addModelButton.addActionListener(this);
        importModelButton = new JButton(new ImportAction(this));
//        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
//        cancelButton.setSize(addModelButton.getPreferredSize());
//        cancelButton.addActionListener(this);
//        cancelButton.setEnabled(false);

        Box header = Box.createHorizontalBox();
        header.add(addModelButton);
        header.add(Box.createRigidArea(new Dimension(10,1)));
        header.add(importModelButton);
        header.add(Box.createHorizontalGlue());
//        header.add(cancelButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.setPreferredSize(header.getMinimumSize());
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createVerticalStrut(15));
        buildJTable();
        this.add(Box.createVerticalStrut(15));
        //textArea = new DomainModelEditor();
        textArea = new LeerdomeinEditPanel2(prop);
        textArea.setEditable(false);
//        this.add(scrollPane);

    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {
      LOG.info("End of " + this);
      try {
        tableModel.init(Collections.emptyList(), searchImage, removeImage, resultsImage, classImage);
      } catch (Dwo2Exception e) {
        LOG.log(Level.WARNING, "should not happen", e);
      }
      prop.end();
    }

    /**
     * Sets the centerpanel to communicate with.
     *
     * @param centerPanel The centerPanel to communicate with.
     */
    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }
    @Override
    public Color getSubHeaderColor() {
      return Constants.COLOR20;
    }

    /**
     * Returns a Panel that can function as a header panel.
     *
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public JComponent getHeaderPanel() {
        HeaderPanel b = new HeaderPanel(TextMapper.getText(TextMapper.GUIMNU_STUDENTMODELS));
        b.setBackground(getSubHeaderColor());
        return b;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addModelButton) {
                textArea.setModel(null, PublishState.published);
                prop.setCurrent(null);
                textArea.setEditable(true);
                int ok = showConfirmDialog(TeacherStudentModelPanel.this, textArea, e.getActionCommand(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (ok == JOptionPane.OK_OPTION) {
                  try {
                    if (textArea.isLock()) {
                      prop.getCurrent().setPublishState(PublishState.published);
                      prop.updateModel(prop.getCurrent());
                    }                   
                    tableModel.init(prop.getModelList(), searchImage, removeImage, resultsImage, classImage);
                  } catch (Dwo2Exception ex) {
                      LOG.log(Level.SEVERE, "new model", ex);
                      GuiCreator.instance().ShowErrorDialog(center, ex);
                  } 
                }
            }
    }

    private int showConfirmDialog(TeacherStudentModelPanel teacherStudentModelPanel,
        JComponent xxx, String title, int okCancelOption, int plainMessage) {
//      if (false)
//        return JOptionPane.showConfirmDialog(teacherStudentModelPanel, scrollPane, title, okCancelOption,plainMessage);

      ConfirmDialog dialog = new ConfirmDialog(teacherStudentModelPanel, "");
      dialog.setContentPane(textArea);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      dialog.addWindowListener(textArea);      
      dialog.pack();
      dialog.center();
      dialog.setVisible(true);
      return dialog.getOption();
    }

    public void addModel(DomStudentModelContext model) throws Dwo2Exception {
      prop.addModel(model);
      tableModel.init(prop.getModelList(), searchImage, removeImage, resultsImage, classImage);
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
