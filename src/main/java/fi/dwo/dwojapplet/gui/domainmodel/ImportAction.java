package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
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
  private Genson genson;

  public ImportAction(TeacherStudentModelPanel parent) {
    super("Import");
    this.chooser = new JFileChooser();
    this.parent = parent;
    this.genson = new GensonBuilder().create();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      File toImport = chooser.getSelectedFile();
      try {
        FileInputStream input = new FileInputStream(toImport);
        DomStudentModelStructure structure = genson.deserialize(input, DomStudentModelStructure.class);
        input.close();      
        DomStudentModelContext model = new DomStudentModelContext();
        model.setModelStructure(structure);
        parent.addModel(model);
      } catch (Exception e1) {
        LOG.log(Level.SEVERE, "import from " + toImport, e1);
      }
    }
    
  }

}
