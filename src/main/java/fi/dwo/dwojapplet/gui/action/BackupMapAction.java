package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.form.DWOFile;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;

public class BackupMapAction extends GuiAction {

	private CourseMap course;
	private JFileChooser chooser;
	private Logger log;
	
	public BackupMapAction() {
		super(TextMapper.getText("Backup map"));
		Clipboard.addPropertyChangeListener("selection", this);
		setEnabled(false);
		chooser = new JFileChooser();
		log = Logger.getLogger(getClass().getName());
	}

	void setMap(CourseMap map) {
		boolean enabled = false;
		if( DwoHelper.isSecure() && canModify(map)) {
			if(map instanceof Course)
			{ 
				enabled = ((Course)map).isWithChildren();
			} else {
				enabled = map.getUserObject() == ModuleTreePanel.SCHOOL_MODULES;
			}
		}  
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			if(course == null && Clipboard.getSelection() instanceof CourseMap)
			{
				export( (CourseMap)Clipboard.getSelection());
			}
			else if(course != null)
				export(course);
		} catch (Exception e) {
			log.log(Level.WARNING, null, e);
		}

}

	private void export(CourseMap selection) throws Exception {
		int result = chooser.showSaveDialog(instance().getMainPanel());
		if(result == JFileChooser.APPROVE_OPTION)
		{
			try {
				instance().setWait();
				File file = chooser.getSelectedFile();
				ZipOutputStream zipper = new ZipOutputStream(new FileOutputStream(file));
				export(selection.getChildren(), "", selection.getChildNames(), zipper);
				zipper.close();
			} finally {
				instance().setReady();
			}
		}
	}

	private void export(CourseMap[] children, String pfx, Set<String> names,
			ZipOutputStream zipper)
					throws IOException, ParserConfigurationException, TransformerException, SQLException, XmlRpcException, PersistenceException {
		for(CourseMap item: children) {
			if(item instanceof Course) {
				Course course = (Course) item;
				
				String name = course.getName();
				names.remove(name);
				names.add(".manifest");
				name = name.replace('/', '%');
				if(!names.add(name)) {
					int cnt=1;
					while (! names.add(name + ";" + cnt)) cnt++;
					name = name + ";" + cnt;
				}
				String longname = pfx + "/" + name;
				if(longname.startsWith("/")) longname = longname.substring(1);
				if(course.isWithChildren()) {
					ZipEntry entry = new ZipEntry(longname + "/");
					zipper.putNextEntry(entry); zipper.closeEntry();
					entry = new ZipEntry(entry.getName() + ".manifest");
					zipper.putNextEntry(entry);
					exportCourse(course, zipper);
					zipper.closeEntry();
					export(course.getChildren(), longname, course.getChildNames(), zipper);
				} else {
					ZipEntry entry = new ZipEntry(longname);
					zipper.putNextEntry(entry);
					exportCourse(course, zipper);
					zipper.closeEntry();
				}	
			}
		}
		
	}

	private void exportCourse(Course map, OutputStream out) throws ParserConfigurationException, TransformerException, SQLException, IOException, XmlRpcException, PersistenceException {
		DWOFile builder = new DWOFile();
		builder.createIMSManifest(map.getID(), -1, out);
	}
}