package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            scormChooser.html5.setSelected(true);
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
            makeScos(course, variant, out, au, runner, new StringBuilder());
   // manifest
            if(!zip) {
	            out.putNextEntry(new ZipEntry("imsmanifest.xml"));
	            runner.copy(au.getStream("resources/imsmanifestHtml5.txt"), out);
	            out.closeEntry();
	
	         // metadata
	            out.putNextEntry(new ZipEntry("metadata.xml"));
	            runner.copy(au.getStream("resources/metadata.txt"), out);
	            out.closeEntry();
	            Save2004Action.makeCopies(out, runner, runner.getBase());
            }
            out.close(); out = null;
   // TODO xsd
		} catch(Exception e) {
			LOG.log(Level.SEVERE, "createHTML5", e);
		} finally { 
			try {
				out.close();
			} catch (Exception e) { // silently close Java8 constructie
			}
		}
		
	}

	static boolean zip = true;
	void makeScos(Course course2, String variant, ZipOutputStream out, AppletUtil au, ScormParameters runner, StringBuilder prefix)
			throws IOException, UnsupportedEncodingException {
		int length = prefix.length();
		if(course2.isWithChildren())
		{
			CourseMap[] children = course2.getChildren();
			try {
				prefix.append(Save2004Action.en_code_(course2.getName())).append('/');
				for (int i = 0; i < children.length; i++) {
					makeScos((Course) children[i].getUserObject(), variant, out, au, runner, prefix);
				}
			} finally {
				prefix.setLength(length);
			}
			return;
		}
		Sco[] scos = course2.getScoList();
		if(scos == null) {
			course2.loadScos();
			scos = course2.getScoList();
		}
		if(scos == null || scos.length == 0) return; // early out.
		
		try {
			prefix.append(Save2004Action.en_code_(course2.getName())).append('/');
            if(!zip)Save2004Action.makeAuxilaryCopies(out, runner, runner.getBase(), prefix.toString());

		for(Sco sco: scos) {
			if(zip) {
				String name = Save2004Action.en_code_(sco.getScoName()) + ".zip";
				out.putNextEntry(new ZipEntry(prefix + name));
				ZipOutputStream inner = new ZipOutputStream(out);
				Save2004Action.createHtml5(variant, inner, runner, sco);
				inner.finish();
				inner.flush();
				out.closeEntry();
			} else {
				Save2004Action.createHTML_XML(variant, out, runner, sco, prefix.toString());
			}
		}
		} finally {
			prefix.setLength(length);
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
