package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.SAXException;

import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.form.DWOFile;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;

public class ImportMapAction extends GuiAction {

	private Course course;
	private JFileChooser chooser;
	private Logger log;

	public ImportMapAction() {
		super(TextMapper.getText("Import map"));;
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
				importMap( (CourseMap)Clipboard.getSelection());
			}
			else if(course != null)
				importMap(course);
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);
		}

	}

	private void importMap(CourseMap selection) throws IOException, ParserConfigurationException, SAXException, DwoXmlRpcException, SQLException, XmlRpcException, PersistenceException, CourseException {
		int result = chooser.showOpenDialog(instance().getMainPanel());
		if(result == JFileChooser.APPROVE_OPTION)
		try {
			instance().setWait();
			Map<String,CourseMap> directory = new HashMap<String,CourseMap>();
			directory.put("/", selection);
			directory.put("", selection);
			File file = chooser.getSelectedFile();
			ZipFile zipper = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipper.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String name = zipEntry.getName();
				if(name.endsWith(".manifest"))
					continue;
				if(zipEntry.isDirectory()) {
					int len= name.length();
					int slash = name.lastIndexOf('/', len-2);
					String base = name.substring(0,len-1);
					String dir = "";
					if(slash >= 0) {
						base = name.substring(slash+1, len-1);
						dir = name.substring(0, slash+1);
					}
					CourseMap parent = directory.get(dir);
					Course parentCourse = null;
					if(parent instanceof Course) parentCourse = (Course) parent;
					String manifest = name + ".manifest";
					ZipEntry entry = zipper.getEntry(manifest);
					Course child;
					if(entry == null) {
						child = importMap(base, parentCourse, parent.getChildNames());
					} else {
						InputStream in = zipper.getInputStream(entry);
						child = importMap(in,parentCourse, parent.getChildNames());
						in.close();
					}
					if(child == null) {
						directory.put(name, parent);
					} else
					{	parent.addChild(child);
						directory.put(name, child);
					}
				} else {
					int slash = name.lastIndexOf('/');
					String base = name, dir = "";
					if(slash >= 0) {
						base = name.substring(slash+1);
						dir = name.substring(0, slash+1);
					}
					CourseMap parent = directory.get(dir);
					InputStream in = zipper.getInputStream(zipEntry);
					importModule(in,parent);
					in.close();
				}
			}
			getCenter().updateMap(selection);
			zipper.close();
		} finally {
			instance().setReady();
		}
	}

	private Course importMap(String base, Course parentCourse, Set names) {
		base = CourseManagementPanel.replaceDuplicate(base, names);
		return instance().addCourse(base, "", parentCourse, true);
	}

	private Course importMap(InputStream in, Course parent, Set names) throws ParserConfigurationException, SAXException, IOException {
		DWOFile builder = getDWOFile();
		Hashtable map = builder.inputIMSManifest(in);
		String name = (String)map.get("name");
		String description = (String) map.get("description");
		name = CourseManagementPanel.replaceDuplicate(name, names);
		Course child = instance().addCourse(name, description, parent, true);
		return child;
	}

	private DWOFile _builder;
	DWOFile getDWOFile() {
		if(_builder != null) return _builder;
		return _builder = new DWOFile();
	}

	private void importModule(InputStream in, CourseMap map) throws DwoXmlRpcException, ParserConfigurationException, SAXException, IOException, SQLException, XmlRpcException, PersistenceException, CourseException {
		DWOFile builder = getDWOFile();
		ImportModuleAction.importModule(map, in, builder);
	}

}
