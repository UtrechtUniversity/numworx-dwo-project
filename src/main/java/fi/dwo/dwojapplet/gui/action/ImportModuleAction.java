package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.form.DWOFile;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import fi.dwo.dwojapplet.persistence.MapperCreator;
import fi.dwo.dwojapplet.persistence.MapperIF;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;




//import fi.dwo.dwojapplet.persistence.MapperCreator;
//import fi.dwo.dwojapplet.persistence.MapperIF;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.SAXException;

public class ImportModuleAction extends GuiAction {
    private static final Logger LOG = Logger.getLogger(ImportModuleAction.class.getName());

    private CourseMap course;
    private FileDialog openDial;
    private String dir;

    {
        if (DwoHelper.isSecure()) {
            dir = System.getProperty("user.dir", ".");
        } else {
            dir = ".";
        }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Frame topFrame = DwoHelper.getFrameForComponent(null);
        openDial = new FileDialog(topFrame, getToolTipText(), FileDialog.LOAD);
        openDial.setDirectory(dir);
        if (course instanceof Course && !((Course) course).isWithChildren()) {
            try {
                importScos((Course) course);
                return;
            } catch (Exception e1) {
                LOG.log(Level.SEVERE,null,e1);
                return;
            }
        }
        if (course != null) {
            try {
                upload(course);
            } catch (Exception e1) {

                LOG.log(Level.SEVERE,null,e1);
            }
            return;
        }
        // no course, try selection
        CourseMap map = Clipboard.getSelection();
        if (map instanceof Course && !((Course) map).isWithChildren()) {
            try {
                importScos((Course) map);
                return;
            } catch (Exception e1) {
                LOG.log(Level.SEVERE,null,e1);
                return;
            }
        }
        if (map != null) {
            try {
                upload(map);
            } catch (Exception e1) {

                LOG.log(Level.SEVERE,null,e1);
            }
            return;
        }

    }

    private void importScos(Course course) throws ParserConfigurationException, SAXException, IOException, DwoXmlRpcException, XmlRpcException, SQLException, PersistenceException {
        String naam;
        openDial.setTitle(getToolTipText());
        openDial.show();
        naam = openDial.getFile();
        if (naam != null) {
            File dir = new File(openDial.getDirectory());
            this.dir = dir.getAbsolutePath();
            File file = new File(dir, naam);
            FileInputStream input = new FileInputStream(file);
            DWOFile zipper = new DWOFile();
            Hashtable result = zipper.inputIMSManifest(input);
            Set names = course.getScoNames();
            int offset = names.size();
            Vector scos = (Vector) result.get("sco");
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
			DWOFile zipper = new DWOFile();
			importModule(map, input, zipper);
			getCenter().updateMap(map);
		}
	}
/**
 * shared method. Input a module from a manifest 
 * @param parent
 * @param input
 * @param zipper
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws IOException
 * @throws DwoXmlRpcException
 * @throws SQLException
 * @throws XmlRpcException
 * @throws PersistenceException 
 * @throws CourseException 
 */
	static void importModule(CourseMap parent, InputStream input, DWOFile zipper)
			throws ParserConfigurationException, SAXException, IOException,
			DwoXmlRpcException, SQLException, XmlRpcException, PersistenceException, CourseException {
		Hashtable result = zipper.inputIMSManifest(input);
		Set names = parent.getChildNames();
		String title = (String)result.get("name");
		title = CourseManagementPanel.replaceDuplicate(title, names);
		result.put("name", title);
		final DwoIF dwo = GuiCreator.instance().getDWO();
		int schoolID = dwo.getUser().getSchool().getSchoolID();
		if(parent.getUserObject() == ModuleTreePanel.STANDAARD_DWO_MODULES)
			schoolID = 0; // import in standaard map 
		int id = 0;
		Course parentCourse = null;
		if(parent instanceof Course) parentCourse = (Course) parent;
		if( parentCourse != null)
		{
			id = parentCourse.getID();
			schoolID = parentCourse.getSchoolID(); // takeover schoolid van parentcourse
		}
		if(schoolID != 0 || dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
		{	id = zipper.addCourse(result, dwo.getDwoProfileID(), schoolID, id);
			MapperIF mapper = MapperCreator.instance(Course.class);
			Course c = (Course) mapper.get(id);
			mapper.put(id, c);
			parent.addChild(c);
		}
	}

    public CourseMap getCourse() {
        return course;
    }

    public void setCourse(CourseMap course) {
        this.course = course;
    }

    @Override
    void setMap(CourseMap map) {
        setEnabled(DwoHelper.isSecure() && canModify(map) && !(map.getUserObject() instanceof Sco));
    }

}
