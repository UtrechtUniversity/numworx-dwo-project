package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.apache.commons.httpclient.URI.DefaultCharsetChanged;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class ExportAction extends AbstractAction {

  interface ExportPanel {
    Component asComponent();
    DomStudentModelStructure getModel();
	void save(DomStudentModelStructure model);
  }
  
  
  
  final static Logger LOG = Logger.getLogger(ExportAction.class.getName());
  private ExportPanel panel;
  private final JFileChooser chooser;
  private Genson genson;

  public ExportAction(ExportPanel leerdomeinEditPanel) {
    super("Export");
    panel = leerdomeinEditPanel;
    chooser = new fi.beans.numworxlf.JFileChooser();
    genson = new GensonBuilder()
        .useIndentation(true)
        .setSkipNull(true)
        .create();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (chooser.showSaveDialog(panel.asComponent()) == JFileChooser.APPROVE_OPTION) {
      File toSave = chooser.getSelectedFile();
      DomStudentModelStructure model = panel.getModel();
      try {
        FileOutputStream out = new FileOutputStream(toSave);
        BufferedOutputStream output = new BufferedOutputStream(out);
        genson.serialize(model, output);
        output.close();
      } catch (IOException e1) {
        LOG.log(Level.SEVERE, "export to " + toSave, e1);
      }
      
    }
    
    
  }

}
