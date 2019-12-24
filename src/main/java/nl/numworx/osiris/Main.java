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
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * @author peterboon
 *
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	URL base;
	String login_URL =  base + "saml/login.jsp";
	String profileName = "100";
		
	JFileChooser chooser;
	TablePanel cursus, toets, student, docent;
	LoginPanel login;
	InstallPanel install;
	
	Properties config;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws HeadlessException 
	 */
	public static void main(String[] args) throws HeadlessException, IOException {
		
		Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
		
		Main main = new Main();
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
		return config.getProperty(key, "");
	}

	String c(String key, String def) {
		return config.getProperty(key, def);
	}

	private Main() throws HeadlessException, IOException {
		this("");	
		InputStream in = getClass().getResourceAsStream("/config.properties");
		config = new Properties();
		config.load(in);
		in.close();
		base  = url(c("base","https://numworx.acc.uu.nl/dwo/"));
		login_URL =  base + c("login", "saml/login.jsp");
		setTitle(c("title", "Numworx import (acceptatie) ") + c("version") + " " + c("qualifier"));
		chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("CSV UTF-8 (excel)", "csv");
		chooser.setFileFilter(filter );		
		JTabbedPane tabs = new JTabbedPane();
		cursus = new TablePanel(this, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL);
		toets  = new TablePanel(this, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING);		
		student = new TablePanel(this, Col.STUDENTNUMMER, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING);
		docent = new TablePanel(this, Col.COLLEGEJAAR, Col.CURSUS, Col.LDAP_LOGIN);

		login = new LoginPanel(this, login_URL, base);
		install = new InstallPanel(this, base, profileName);
		tabs.addTab("Login", login);
		tabs.addTab("Courses (Classes)", cursus);
		tabs.addTab("Exams (Modules)", toets);
		tabs.addTab("Students", student);
		tabs.addTab("Teachers", docent);
		tabs.addTab("Install", install);
		
		setContentPane(tabs);
	}

	Main(String string) {
		super(string);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
