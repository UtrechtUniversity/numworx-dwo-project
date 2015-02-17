package fi.beans.scorm;

import java.applet.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import netscape.javascript.*;

public class JSScormAPI implements SCORM12APIInterface {

    private static final Logger log = Logger.getLogger(JSScormAPI.class.getName());

    private JSObject window;
    private String launchData;
    private static final String LAUNCH_DATA = "launchData";
    private static final String CMI_LAUNCH_DATA = "cmi.launch_data";

    /**
     * @author Bastiaan Grutters
     * @author Alexander Elias
     * @param parent
     */
    public JSScormAPI(Applet parent) {
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
    public String LMSInitialize(String iParam) {
        return null;
    }

    @Override
    public String LMSFinish(String iParam) {
        return null;
    }

    @Override
    public String LMSGetValue(String iDataModelElement) {
        String result = "";
        if (window != null) {
            try {
                System.out.println("JSAPI->LMSGetValue(" + iDataModelElement + ")");
                Object[] args = new Object[1];
                args[0] = iDataModelElement;
                result = (String) window.call("LMSGetValue", args);
            } catch (Exception e) {
               log.log(Level.SEVERE,null,e);
            }
        }
        if (result != null && !result.equals("")) {
            if (iDataModelElement.equals("cmi.suspend_data")) {
                result = URLCoder.decode(result);
            }
            //System.out.println( "Result:\r\n" + result );
        }

        if ((result == null || result.length() == 0) && CMI_LAUNCH_DATA.equals(iDataModelElement)) {
            return launchData;
        }

        return result;
    }

    @Override
    public String LMSSetValue(String iDataModelElement, String iValue) {
        String result = "";
        if (iDataModelElement.equals("cmi.suspend_data")) {
            iValue = URLCoder.encode(iValue);
        }
        if (window != null) {
            try {
                Object[] args = new Object[2];
                args[0] = iDataModelElement;
                args[1] = iValue;
                result = (String) window.call("LMSSetValue", args);
            } catch (Exception e) {
                               log.log(Level.SEVERE,null,e);

            }
        }
        return result;
    }

    @Override
    public String LMSCommit(String iParam) {
        return null;
    }

    @Override
    public String LMSGetLastError() {
        return null;
    }

    @Override
    public String LMSGetErrorString(String iErrorCode) {
        return null;
    }

    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        return null;
    }
}
