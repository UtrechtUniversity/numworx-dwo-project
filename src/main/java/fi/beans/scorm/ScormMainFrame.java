package fi.beans.scorm;

import java.applet.*;
import java.io.*;
import fi.beans.mainframe.*;

public class ScormMainFrame extends MainFrame implements SCORM12APIInterface {

    public ScormMainFrame(Applet applet, int width, int height) {
        super(applet, width, height);
    }

    // implementatie van de SCORM12APIInterface methoden
    @Override
    public String LMSInitialize(String parameter) {
        return null;
    }

    @Override
    public String LMSFinish(String parameter) {
        return null;
    }

    @Override
    public String LMSGetValue(String parameter) {
        String directory = System.getProperty("user.dir", ".");
        String naam = parameter;
        String s = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(directory, naam)));
            s = in.readLine();

        }
        catch (IOException ie) {
        }
        return s;
    }

    @Override
    public String LMSSetValue(String parameter, String value) {
        String directory = System.getProperty("user.dir", ".");
        String naam = parameter;
        try {
            PrintWriter out = new PrintWriter(new FileWriter(new File(directory, naam)));
            out.println((String) value);
            out.close();
        }
        catch (IOException ie) {
            System.out.println(ie.toString());
            return null;
        }
        return null;
    }

    @Override
    public String LMSCommit(String parameter) {
        return null;
    }

    public String LMSGetLastError(String parameter) {
        return null;
    }

    @Override
    public String LMSGetLastError() {
        return null;
    }

    @Override
    public String LMSGetErrorString(String errornumber) {
        return null;
    }

    @Override
    public String LMSGetDiagnostic(String parameter) {
        return null;
    }

}
