package fi.beans.scorm;

import java.util.Properties;

import javax.swing.JComponent;

import org.osgi.util.promise.Promise;

public interface SAMLLoginIF {
    JComponent asComponent();
    Promise<Properties> getPromise();
    void loadURL(String url);
}
