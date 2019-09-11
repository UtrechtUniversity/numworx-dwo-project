/**
 * 
 */
package nl.numworx.osiris;

import java.awt.HeadlessException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author peterboon
 *
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	URL base = url("http://uu-dev.dwo.nl/dwo/");
	String login_URL =  base + "saml/login.jsp";

	final JFileChooser chooser;
	TablePanel cursus, toets, student, docent;
	LoginPanel login;
	InstallPanel install;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
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

	private Main() throws HeadlessException {
		super("OSIRIS import");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("CSV files (excel)", "csv");
		chooser.setFileFilter(filter );		
		JTabbedPane tabs = new JTabbedPane();
		cursus = new TablePanel(this);
		toets  = new TablePanel(this);
		student = new TablePanel(this);
		docent = new TablePanel(this);

		login = new LoginPanel(this, login_URL, base);
		install = new InstallPanel(this, base);
		tabs.addTab("Login", login);
		tabs.addTab("Cources", cursus);
		tabs.addTab("Exams", toets);
		tabs.addTab("Students", student);
		tabs.addTab("Teachers", docent);
		tabs.addTab("Install", install);
		
		setContentPane(tabs);
	}

}
