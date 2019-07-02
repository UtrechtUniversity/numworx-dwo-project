package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.system.TextMapper;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class ExportAction extends AbstractAction {

  final static Logger LOG = Logger.getLogger(ExportAction.class.getName());
  private LeerdomeinEditPanel panel;
  private final JFileChooser chooser;
  private Genson genson;

  public ExportAction(LeerdomeinEditPanel leerdomeinEditPanel) {
    super("Export");
    panel = leerdomeinEditPanel;
    chooser = new JFileChooser();
    genson = new GensonBuilder()
        .useIndentation(true)
        .setSkipNull(true)
        .create();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (chooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
      File toSave = chooser.getSelectedFile();
      DomStudentModelStructure model = panel.getModel();
      try {
        FileOutputStream output = new FileOutputStream(toSave);
        genson.serialize(model, output);
        output.close();
      } catch (IOException e1) {
        LOG.log(Level.SEVERE, "export to " + toSave, e1);
      }
      
    }
    
    
  }

}
