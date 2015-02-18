package fi.beans.scorm;

import java.applet.Applet;
import java.util.logging.Level;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

/**
 * Scormvariant voor gebruik bij Wolters-Noordhoff.
 *
 * @author Wim
 *
 */
public class WNScormAPI implements SCORM12APIInterface {
    private static final Logger log = Logger.getLogger(WNScormAPI.class.getName());

    public static final String GUID = "GUID";
    public static final String SCORE = "cmi.core.score.raw";
    public static final String SUSPENDDATA = "cmi.suspend_data";
    public static final String LESSONSTATUS = "cmi.core.lesson_status";
    public static final String COMPLETED = "completed";
    public static final String INCOMPLETE = "incomplete";

    private JSObject window;
    private boolean inited;
    private String GUIDString;

    public WNScormAPI(Applet parent) {

        GUIDString = parent.getParameter(GUID);
        if (GUIDString == null) {
            GUIDString = "";
        }
        try {
            window = JSObject.getWindow(parent);
        } catch (Exception e) {
        }
        LMSInitialize("");
    }

    @Override
    public String LMSCommit(String param) {
        return "";
    }

    @Override
    public String LMSFinish(String param) {
        return "";
    }

    @Override
    public String LMSGetDiagnostic(String errorCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String LMSGetErrorString(String errorCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String LMSGetLastError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String LMSGetValue(String dataModelElement) {
        if (SUSPENDDATA.equals(dataModelElement)) {
            return getAssetData();
        }
        return "";
    }

    @Override
    public String LMSInitialize(String param) {
        String result = "";
        if (!inited && window != null) {
            inited = true;
            try {
                Object[] args = new Object[2];
                args[0] = GUIDString;
                args[1] = Boolean.TRUE;
                result = (String) window.call("SetInitialized", args);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }
        }
        return result;
    }

    private String setCompleted(String value) {
        String result = "";
        boolean completed = COMPLETED.equals(value);
        if (window != null) {
            try {
                Object[] args = new Object[2];
                args[0] = GUIDString;
                if (completed) {
                    args[1] = Boolean.TRUE;
                } else {
                    args[1] = Boolean.FALSE;
                }
                result = (String) window.call("SetCompleted", args);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }
        }
        return result;
    }

    private String setAssetData(String value) {
        if (value == null) {
            return "";
        }
        value = URLCoder.encode(value);
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[2];
                args[0] = GUIDString;
                args[1] = value;
                result = (String) window.call("SetAssetData", args);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }
        }
        return result;
    }

    private String getAssetData() {
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[1];
                args[0] = GUIDString;
                result = (String) window.call("GetAssetData", args);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }
        }
        if (result != null && !result.equals("")) {
            result = URLCoder.decode(result);
        }
        return result;
    }

    private String setScore(String score) {
        String result = "";
        if (window != null) {
            try {
                Object[] args = new Object[2];
                args[0] = GUIDString;
                args[1] = score;
                result = (String) window.call("SetScore", args);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }
        }
        return result;
    }

    @Override
    public String LMSSetValue(String dataModelElement, String value) {
        if (SCORE.equals(dataModelElement)) {
            return setScore(value);
        }
        if (SUSPENDDATA.equals(dataModelElement)) {
            return setAssetData(value);
        }
        if (LESSONSTATUS.equals(dataModelElement)) {
            return setCompleted(value);
        }
        return "";
    }

}
