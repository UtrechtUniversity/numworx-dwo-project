package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

import fi.beans.appletutil.AppletUtil;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ScormChooser;
import fi.dwo.dwojapplet.gui.ScormParameters;

public class SaveScosAction extends GuiAction {

	final Logger LOG = Logger.getLogger(getClass().getName());
	
	private static final String EXPORT_SCO_S = "Export SCO's";
	private Course course, course0;
	private ScormChooser scormChooser;
	
	public SaveScosAction() {
        super(EXPORT_SCO_S);
        Clipboard.addPropertyChangeListener("selection", this);
        setEnabled(false);
	}

	public SaveScosAction(Course course) {
        super(EXPORT_SCO_S);
        this.course0 = course;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        if (course0 == null) {
            CourseMap selection = Clipboard.getSelection();
            if (selection != null && selection.getUserObject() instanceof Course) {
                save2004((Course) selection.getUserObject());
            }
            return;
        }
        save2004(course0);

	}

	private void save2004(Course userObject) {
        if (scormChooser == null) {
            scormChooser = new ScormChooser();
            scormChooser.html5.setText("Noordhoff HTML5");
        }
        course = userObject;
        int result = scormChooser.showSaveDialog(GuiCreator.instance().getMainPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = scormChooser.getSelectedFile();
            boolean is2004 = scormChooser.isScorm2004();
            boolean ishtml5 = scormChooser.isHTML5();
            String naam = file.getName();
            if (naam.lastIndexOf(".") > -1) {
                naam = naam.substring(0, naam.indexOf("."));
            }
            if (ishtml5) {
                createHtml5(file, "2014_v1_0");
            } else if (is2004) {
                createScorm2004(file);
            } else {
                createZip(file);
            }
        }
		
	}

	private void createZip(File file) {
		// TODO Auto-generated method stub
		
	}

	private void createScorm2004(File file) {
		// TODO Auto-generated method stub
		
	}

	private void createHtml5(File file, String variant) {
		ZipOutputStream out = null;
		try {
            out = new ZipOutputStream(new FileOutputStream(file));
            AppletUtil au = DwoHelper.getAu();
            // ugly string creation
            String scormURL = DwoHelper.getAppURLPath().toString() + variant + "/";
            ScormParameters runner = new ScormParameters();
            runner.setBase(scormURL);
            runner.setUser(instance().getUser());
            makeScos(course, variant, out, au, runner);
            //runner.setSco(scos[0]); // FIXME NIET GOED
   // manifest
            out.putNextEntry(new ZipEntry("imsmanifest.xml"));
            runner.copy(au.getStream("resources/imsmanifestHtml5.txt"), out);
            out.closeEntry();

         // metadata
            out.putNextEntry(new ZipEntry("metadata.xml"));
            runner.copy(au.getStream("resources/metadata.txt"), out);
            out.closeEntry();
            Save2004Action.makeCopies(out, runner, scormURL);
            out.close(); out = null;
		} catch(Exception e) {
			LOG.log(Level.SEVERE, "createHTML5", e);
		} finally { 
			try {
				out.close();
			} catch (Exception e) { // silently close Java8 constructie
			}
		}
		
	}

	void makeScos(Course course2, String variant, ZipOutputStream out, AppletUtil au, ScormParameters runner)
			throws IOException, UnsupportedEncodingException {
		if(course2.isWithChildren())
		{
			CourseMap[] children = course2.getChildren();
			for (int i = 0; i < children.length; i++) {
				makeScos((Course) children[i].getUserObject(), variant, out, au, runner);
			}
			return;
		}
		Sco[] scos = course2.getScoList();
		if(scos == null) {
			course2.loadScos();
			scos = course2.getScoList();
		}
		if(scos == null || scos.length == 0) return; // early out.
		
		 
		for(Sco sco: scos) {
		    runner.setSco(sco);
		    String id;
		    //id = Integer.toString(sco.getID());
		    id = sco.getScoName();
   // verboden characters worden _	: space, /, ? , #	
		    id = id.replace(' ', '_');
		    id = id.replace('/', '_');
		    id = id.replace('?', '_');
		    id = id.replace('#', '_');
		    runner.setId(id);
		 // sco
		    out.putNextEntry(new ZipEntry(id + ".html"));
   // sco.txt is profiel afhankelijk!
		    int profile = sco.getCourse().getDwoProfile();
		    InputStream in = au.getStream("resources/" + variant + "-" + profile + ".txt");
		    if (in == null) {
		        in = au.getStream("resources/" + variant + ".txt");
		    }
		    runner.copy(in, out);
		    out.closeEntry();

		    out.putNextEntry(new ZipEntry(id + ".xml"));
		    Map<?, ?> ld = new HashMap<Object, Object>(runner.getLaunchData());
		    OutputStreamWriter wr = new OutputStreamWriter(out, ScormParameters.UTF8);
		    sco.jsonEncode(ld, wr);
		    wr.flush();
		    out.closeEntry();
		}
	}

	@Override
    void setMap(CourseMap map) {
        if (map != null) {
            setEnabled(map.getUserObject() instanceof Course);
        } else {
            setEnabled(false);
        }
    }

}
