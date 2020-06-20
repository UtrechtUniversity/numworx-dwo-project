package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.dwojapplet.gui.TeacherStudentModelPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class ImportAction extends AbstractAction {

  private static final Logger LOG = Logger.getLogger(ImportAction.class.getName());
  private JFileChooser chooser;
  private TeacherStudentModelPanel parent;
  private LeerdomeinEditPanel2 panel;
  private Genson genson;
  private JComponent component;

  public ImportAction(TeacherStudentModelPanel parent) {
    super("Import");
    this.chooser = new JFileChooser();
    this.parent = parent;
    this.component = parent;
    this.genson = new GensonBuilder().create();
  }
  
  public ImportAction(LeerdomeinEditPanel2 panel) {
    super("Import");
    this.chooser = new JFileChooser();
    this.genson = new GensonBuilder().create();
    this.panel = panel;
    this.component = panel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (chooser.showOpenDialog(component) == JFileChooser.APPROVE_OPTION) {
      File toImport = chooser.getSelectedFile();
      try {
        FileInputStream input = new FileInputStream(toImport);
        DomStudentModelStructure structure = genson.deserialize(input, DomStudentModelStructure.class);
        input.close();      
        DomStudentModelContext model = new DomStudentModelContext();
        model.setModelStructure(structure);
        if (parent != null) parent.addModel(model);
        if (panel != null) panel.importModel(structure);
      } catch (Exception e1) {
        LOG.log(Level.SEVERE, "import from " + toImport, e1);
      }
    }
    
  }

}
