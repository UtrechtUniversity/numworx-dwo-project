package fi.beans.scorm;

import java.applet.Applet;
import java.util.logging.Level;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

public class JSScormAPI_1484_11 extends ScormAdapter implements SCORM12APIInterface {

    private static final Logger LOG = Logger.getLogger(JSScormAPI_1484_11.class.getName());

    private JSObject window;
    private String launchData;
    private static final String LAUNCH_DATA = "launchData";
    private static final String CMI_LAUNCH_DATA = "cmi.launch_data";

    public JSScormAPI_1484_11(Applet parent) {
        super(false);
        // override empty cmi.launch_data with applet.parameter launchData
        launchData = parent.getParameter(LAUNCH_DATA);
        if (launchData == null) {
            launchData = "";
        }

        try {
            window = JSObject.getWindow(parent);
        } catch (Exception e) {
        }

    }

    @Override
    public String LMSCommit(String iParam) {
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[1];
                args[0] = iParam;
                result = (String) window.call("LMSCommit", args);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
        return result;
    }

    @Override
    public String LMSFinish(String iParam) {
        return null;
    }

    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        return null;
    }

    @Override
    public String LMSGetErrorString(String iErrorCode) {
        return null;
    }

    @Override
    public String LMSGetLastError() {
        return null;
    }

    @Override
    public String LMSInitialize(String iParam) {
        return null;
    }

    @Override
    public String GetValue(String cmiElement) {
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[1];
                args[0] = cmiElement;
                result = (String) window.call("GetValue", args);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }

        if ((result == null || result.length() == 0) && CMI_LAUNCH_DATA.equals(cmiElement)) {
            return launchData;
        }

        return result;
    }

    @Override
    public String SetValue(String cmiElement, String value) {
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[2];
                args[0] = cmiElement;
                args[1] = value;
                result = (String) window.call("SetValue", args);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
        return result;

    }

}
