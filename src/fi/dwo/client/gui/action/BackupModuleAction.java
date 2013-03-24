package fi.dwo.client.gui.action;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.Action;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.server.form.DWOFile;

public class BackupModuleAction extends GuiAction {

	
	private FileDialog saveDial;
	private String tip;
	private String dir;
	
	public BackupModuleAction(Course course) {
		this();
		this.course = course;
		tip = "Backup activiteiten van module " + course;
		putValue(Action.LONG_DESCRIPTION, tip);
	}

	private Course course;
	
	public BackupModuleAction() {
		super("Backup module");
		setEnabled(DwoHelper.isSecure());
		if( isEnabled() ) dir = System.getProperty("user.dir", ".");
		tip = "Backup module";
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			if(course == null && Clipboard.getSelection() instanceof Course)
			{
				tip = "Backup activiteiten van module " + Clipboard.getSelection();
				export( (Course)Clipboard.getSelection());
			}
			else if(course != null)
				export(course);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void export(Course course) throws ParserConfigurationException, TransformerException, SQLException, IOException, XmlRpcException {
    	String naam;
    	final Frame topFrame = DwoHelper.getFrameForComponent(null);		
    	saveDial = new FileDialog(topFrame, tip, FileDialog.SAVE);
    	saveDial.setDirectory(dir);
		saveDial.show();
		naam = saveDial.getFile();
		if(naam!=null)
		{	
			File dir = new File(saveDial.getDirectory());
			this.dir = dir.getAbsolutePath();
			File file = new File(dir, naam);
			FileOutputStream out = new FileOutputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			zipper.createIMSManifest(course.getID(), -1, out);
		}
	}

	
	
}
