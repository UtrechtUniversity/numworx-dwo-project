package fi.dwo.dwojapplet.gui.action;

import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Hashtable;

public class SaveAppletAction extends GuiAction {

    private Sco sco0, sco; // in constructor
    private FileDialog saveDial;

    public SaveAppletAction(Sco sco) {
        super("Export Applet");
        this.sco0 = sco;
    }

    public SaveAppletAction() {
        super("Export Applet");
        setEnabled(false);
        Clipboard.addPropertyChangeListener("selection", this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        sco = sco0;
        if (sco == null) {
            if (Clipboard.getSelection().getUserObject() instanceof Sco) {
                sco = (Sco) Clipboard.getSelection().getUserObject();
            } else {
                return;
            }
        }

        saveDial = new FileDialog(DwoHelper.getFrameForComponent((Component) e.getSource()), "opslaan", FileDialog.SAVE);
        saveDial.setDirectory(System.getProperty("user.dir", "."));
        saveDial.setName("*.htm");

        saveApplet();

    }

    private void saveApplet() {
        String directory, naam;
        saveDial.show();
        directory = saveDial.getDirectory();
        naam = saveDial.getFile();
        if (naam != null) {
            if (naam.indexOf(".") > -1) {
                naam = naam.substring(0, naam.indexOf("."));
            }
            schrijfGrApplet(directory + "" + sco.getScoName() + ".htm");
        }
    }

    private void schrijfGrApplet(String naam) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(naam));
            GRHTML(out);
            out.close();
        } catch (IOException ie) {
        }
    }

    private void GRHTML(PrintWriter out) {
        Hashtable launchData;
        launchData = sco.getEditLaunchdata();
        if (launchData == null) {
            launchData = sco.getLaunchdata();
        }
        String className = sco.getApplet().getClass().getName();
        String jarName = className.substring(3, className.indexOf(".", 3));
        String launchDataString = StringCodeObject.encodeObjectToString(launchData);
        String scoName = sco.getScoName();

        String language = TextMapper.getLanguage();
        //String bgcolor = "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);
        String bgcolor = "#FFFFFF";

        String[] arguments = {scoName, className, jarName, language, bgcolor, launchDataString};

        try {
            URL htmlSource = new URL("http://www.fisme.science.uu.nl/dwo/scorm/applet/applet.htm");
            if (sco.getCourse().getDwoProfile() == 13 || sco.getCourse().getDwoProfile() == 57 || sco.getCourse().getDwoProfile() == 64 || sco.getCourse().getDwoProfile() == 65) {
                htmlSource = new URL("http://www.fisme.science.uu.nl/dwo/scorm/applet/appletGR.htm");
            }
            if (sco.getCourse().getDwoProfile() == 27 || sco.getCourse().getDwoProfile() == 51) {
                htmlSource = new URL("http://www.fisme.science.uu.nl/dwo/scorm/applet/appletMW.htm");
            }
            if (sco.getCourse().getDwoProfile() == 46) {
                htmlSource = new URL("http://www.fisme.science.uu.nl/dwo/scorm/applet/appletNWK.htm");
            }

            URLConnection connection = htmlSource.openConnection();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (FileNotFoundException exception) {
                System.out.println(exception.toString());
            }

            if (in != null) {
                String result = "";
                String tmp = "";
                while ((tmp = in.readLine()) != null) {
                    result += tmp + "\n";
                }
                in.close();
                result = MessageFormat.format(result, arguments);
                out.print(result);
            }
        } catch (IOException e) {
        }

    }

    @Override
    void setMap(CourseMap map) {
        setEnabled(map != null && map.getUserObject() instanceof Sco);
    }
}
