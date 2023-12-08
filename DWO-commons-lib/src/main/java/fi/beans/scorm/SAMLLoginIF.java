package fi.beans.scorm;

import java.awt.Window;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.osgi.util.promise.Promise;

public interface SAMLLoginIF {
    JComponent asComponent();
    Promise<Properties> getPromise();
    void loadURL(String url);
    default void setEndpoint(String endpoint) { }
    default Promise<Properties> popup(JComponent parent, String url) {
    	Window window = SwingUtilities.windowForComponent(parent);
    	JDialog dialog = new JDialog(window);
    	dialog.setModal(true);
    	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	dialog.setContentPane(asComponent());
    	loadURL(url);
    	Promise<Properties> promise = getPromise();
    	promise.onResolve(dialog::dispose);
    	dialog.pack();
    	dialog.setLocationRelativeTo(parent);
    	dialog.show();
		return promise;
    }
    	
}
