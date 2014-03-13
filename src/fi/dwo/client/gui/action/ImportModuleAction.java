package fi.dwo.client.gui.action;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.SAXException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.CourseManagementPanel;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ModuleTreePanel;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.MapperIF;
import fi.dwo.client.system.TextMapper;
import fi.dwo.server.form.DWOFile;
import fi.dwo.server.persistence.DwoXmlRpcException;

public class ImportModuleAction extends GuiAction {

	private CourseMap course;
	private FileDialog openDial;
	private String dir;
	{
		if (DwoHelper.isSecure())
			dir = System.getProperty("user.dir",".");
		else
			dir = ".";
	}

	public ImportModuleAction() {
		super(TextMapper.getText("Import"));
		putValue(LONG_DESCRIPTION, TextMapper.getText(TextMapper.GUIA_INSERT_SCOS));
		setEnabled(DwoHelper.isSecure());  // disable in geval van applet.
		Clipboard.addPropertyChangeListener("selection", this);
		setEnabled(false);
	}

	public ImportModuleAction(Course course) {
		super(TextMapper.getText("Import"));
		putValue(LONG_DESCRIPTION, TextMapper.getText(TextMapper.GUIA_INSERT_SCOS));
		setEnabled(DwoHelper.isSecure());  // disable in geval van applet.
		this.course = course;
	}

	public ImportModuleAction(CourseManagementPanel courseManagementPanel) {
		this();
		this.course = courseManagementPanel.getMap(); // werkt niet!!!
	}

	public void actionPerformed(ActionEvent e) {
    	Frame topFrame = DwoHelper.getFrameForComponent(null);
		openDial = new FileDialog(topFrame, getToolTipText(), FileDialog.LOAD);
    	openDial.setDirectory(dir);
    	if(course instanceof Course && !((Course) course).isWithChildren())
			try {
				importScos((Course)course);
				return;
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			} 
		if(course != null) {
			try {
				upload(course);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
		// no course, try selection
		CourseMap map = Clipboard.getSelection();
		if(map instanceof Course && !((Course) map).isWithChildren())
			try {
				importScos((Course)map);
				return;
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
		if(map != null)
		{
			try {
				upload(map);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
			
			
	}
	
    private void importScos(Course course) throws ParserConfigurationException, SAXException, IOException, DwoXmlRpcException, XmlRpcException, SQLException {
    	String naam;
    	openDial.setTitle(getToolTipText());
		openDial.show();
		naam = openDial.getFile();
		if(naam!=null)
		{	
			File dir = new File(openDial.getDirectory());
			this.dir = dir.getAbsolutePath();
			File file = new File(dir, naam);
			FileInputStream input = new FileInputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			Hashtable result = zipper.inputIMSManifest(input);
			Set names = course.getScoNames();
			int offset = names.size();
			Vector scos = (Vector)result.get("sco");
			Enumeration elements = scos.elements();
			while (elements.hasMoreElements()) {
				Hashtable sco = (Hashtable) elements.nextElement();
				String title = (String) sco.get("sconame");
				title = CourseManagementPanel.replaceDuplicate(title, names);
				names.add(title);
				sco.put("sconame", title);
			}
			zipper.appendCourse(course.getID(), offset, result);
			course.loadScos();
			getCenter().updateCourse(course);
		}
	}

	private String getToolTipText() {
		return getValue(LONG_DESCRIPTION).toString();
	}


	// import a backup into a map
	private void upload(CourseMap map) throws Exception {
		
    	String naam;
    	openDial.setTitle("Restore module backup");
		openDial.show();
		naam = openDial.getFile();
		//CourseMap courses[] = map.getChildren();
		if(naam!=null)
		{	
			File dir = new File(openDial.getDirectory());
			this.dir = dir.getAbsolutePath();
			File file = new File(dir, naam);
			FileInputStream input = new FileInputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			Hashtable result = zipper.inputIMSManifest(input);

// TODO deze code verplaatsen naar DWOFile?
// of ?copieren? naar ScoManagementPanel.
			Set names = map.getChildNames();
			String title = (String)result.get("name");
			title = CourseManagementPanel.replaceDuplicate(title, names);
			result.put("name", title);

			final DwoIF dwo = GuiCreator.instance().getDWO();
			int schoolID = dwo.getUser().getSchool().getSchoolID();
			if(map.getUserObject() == ModuleTreePanel.STANDAARD_DWO_MODULES)
				schoolID = 0; // import in standaard map 
			int id = 0;
			Course parentCourse = null;
			if(map instanceof Course) parentCourse = (Course) map;
			if( parentCourse != null)
			{
				id = parentCourse.getID();
				schoolID = parentCourse.getSchoolID(); // takeover schoolid van parentcourse
			}
			if(schoolID != 0 || dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
			{	id = zipper.addCourse(result, dwo.getDwoProfile().getID(), schoolID, id);
				MapperIF mapper = MapperCreator.instance(Course.class);
				Course c = (Course) mapper.get(id);
				mapper.put(id, c);
				map.addChild(c);
			}
			getCenter().updateMap(map);
		}
	}

	public CourseMap getCourse() {
		return course;
	}

	public void setCourse(CourseMap course) {
		this.course = course;
	}

	void setMap(CourseMap map) {
		setEnabled (DwoHelper.isSecure() && canModify(map) && !(map.getUserObject() instanceof Sco));
	}

	
	
}
