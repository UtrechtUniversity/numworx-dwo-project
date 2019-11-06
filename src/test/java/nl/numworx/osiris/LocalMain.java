/**
 * 
 */
package nl.numworx.osiris;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.osgi.util.promise.Promises;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.system.MD5;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * @author peterboon
 *
 */
@SuppressWarnings("serial")
public class LocalMain extends Main {
				
	/**
	 * @param args
	 * @throws IOException 
	 * @throws HeadlessException 
	 */
	public static void main(String[] args) throws HeadlessException, IOException {
		
		Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
		
		Main main = new LocalMain();
		main.setSize(1024,768);
		main.setPreferredSize(main.getSize());
		main.pack();
		main.setVisible(true);

	}

	private URL url(String string) {
		try {
			return new URL(string);
		} catch (MalformedURLException e) {
			throw new Error("fatal",e);
		}
	}
	
	String c(String key) {
		return config.getProperty(key);
	}
  
  @SuppressWarnings("deprecation")
  private LocalMain() throws HeadlessException, IOException {
		super("TEST import");

		profileName = "77";
		
		InputStream in = getClass().getResourceAsStream("/test.properties");
		config = new Properties();
		config.load(in);
		in.close();
		base = url(config.getProperty("url", "http://localhost:8080/dwo/"));

		chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("CSV files (excel)", "csv");
		chooser.setFileFilter(filter );		
		JTabbedPane tabs = new JTabbedPane();
        cursus = new TablePanel(this, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL);
        toets  = new TablePanel(this, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING);        
        student = new TablePanel(this, Col.STUDENTNUMMER, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING);
        docent = new TablePanel(this, Col.COLLEGEJAAR, Col.CURSUS, Col.LDAP_LOGIN);

		login = new LoginPanel(this, login_URL, base);
		DomUserFullwLoginContext value = new DomUserFullwLoginContext();
		value.setDomLoginContext(new DomLoginContext());
		value.setDomUserFull(new DomUserFull());
		value.getDomUserFull().setUserName(c("username"));
		value.getDomUserFull().setPassword(MD5.getHashString(c("password")));
		login.complete = Promises.resolved(value);
		install = new InstallPanel(this, base, profileName);
		tabs.addTab("Login", login);
		tabs.addTab("Courses", cursus);
		tabs.addTab("Exams", toets);
		tabs.addTab("Students", student);
		tabs.addTab("Teachers", docent);
		tabs.addTab("Install", install);
		
		setContentPane(tabs);
	}

}
