package nl.numworx.osiris;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.csv.CSVRecord;
import org.xml.sax.InputSource;

import nl.numworx.edexml.OsirisBuilder;
import nl.numworx.edexml.ServerBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

@SuppressWarnings("serial")
public class InstallPanel extends JPanel {
	
	Main main;
	JButton install, delete;
	
	OsirisBuilder osiris;
	ServerBuilder numworx;
	URL base;
	String profile;
	
	public InstallPanel(Main main, URL base, String profile) {
		this.main = main;
		this.base = base;
		this.profile = profile;

		install = new JButton("IMPORT IN NUMWORX");
		install.addActionListener(this::doInstall);
		add(install);
		delete = new JButton("Empty all tables");
		delete.addActionListener(this::doDelete);
		add(delete);
		
		osiris = new OsirisBuilder();
		numworx = new ServerBuilder();
	}

	void doDelete(ActionEvent e) {
	  main.cursus.doDelete(e);
	  main.docent.doDelete(e);
	  main.student.doDelete(e);
	  main.toets.doDelete(e);
	}
	
	public void doInstall(ActionEvent e) {
		if (! main.login.complete.isDone()) {
			showConfirmDialog(main, "login first");
			return;
		}
		try {
			if (main.login.complete.getFailure() != null) {
				showConfirmDialog(main, "login failed");
				return;
			}
		} catch (InterruptedException e1) {
		}
		
//		if (main.student.file == null || main.docent.file == null || main.toets.file == null | main.cursus.file == null) {
//				showConfirmDialog(main, "need more csv files");
//				return;
//		}
	
		String message  = "";
		int toets = 0;
		
		InputSource is;
		try {
			DomUserFullwLoginContext user = main.login.complete.getValue();
			String realm = user.getDomLoginContext().getRealm();
			if (realm == null) realm = "";
			String userName = user.getDomUserFull().getUserName() + realm;
			numworx.setSource(userName, user.getDomUserFull().getPassword(), base);
			numworx.setRealm(user.getDomLoginContext().getRealm());
			
			Map<String, DomSchoolClassFull> initial = numworx.parseGroepen();
			osiris.setGroepenSource(initial.values());
			int initialSize = initial.size();
			
			if (main.cursus.file != null) {
				is = new InputSource(new FileInputStream(main.cursus.file));
				is.setEncoding(main.cursus.charset);
				osiris.setGroepenSource(is);
				close(is);
			}
			if (main.toets.file != null) {
				is = new InputSource(new FileInputStream(main.toets.file));
				is.setEncoding(main.toets.charset);
				osiris.setGroepenSource(is);
				close(is);
			}
			if (main.student.file != null) {
				is = new InputSource(new FileInputStream(main.student.file));
				is.setEncoding(main.student.charset);
				osiris.setGroepenSource(is);
				close(is);
				is = new InputSource(new FileInputStream(main.student.file));
				is.setEncoding(main.student.charset);
				osiris.setLeerlingenSource(is);				
				close(is);
			}
			if (main.docent.file != null) {
				is = new InputSource(new FileInputStream(main.docent.file));
				is.setEncoding(main.docent.charset);
				osiris.setLeerkrachtenSource(is);
				close(is);
			}
			
		      Map<String, DomUserFull> leerlingen = osiris.parseLeerlingen();
		      message += leerlingen.size() + " students\n";
		      Map<String, DomSchoolClassFull> groepen = osiris.parseGroepen();
		      message += (groepen.size()-initialSize) + " courses\n";
		      Map<String, DomUserFull> leerkrachten = osiris.parseLeerkrachten();
		      message += leerkrachten.size() + " teachers\n";
		      Map<String, Collection<String>> members = osiris.memberships();
		      
		      numworx.addSchoolClasses(groepen);
		      numworx.addStudents(leerlingen, members, groepen);
		      numworx.addTeachers(leerkrachten, members, groepen);

		      CourseManager man = new CourseManager(profile, numworx.getSchool(), groepen);
		      man.initTemplate();
			  for (CSVRecord record: main.toets) {
				if (man.createToets(record))
				  toets ++;
			  }
			  for (CSVRecord record: main.student) {
			    if (man.createToets(record))
			      toets ++;
			  }
			  
			  int folders = 0; 
			  for (DomUserFull u: leerkrachten.values()) {
				  if (man.createTeacher(u))
					  folders++;
			  }
			  if (folders > 0) {
				  message += folders + " folders\n";
			  }
			  
			  message += toets + " exams\n";
			
			message += "Installation done";
		} catch (Exception e1) {
		    e1.printStackTrace();
			String error = e1.getLocalizedMessage();
			if (error == null) error = e1.toString();
			message +=  "\n" + error;
		}
		
		showConfirmDialog(main, message);
	}

	private void close(InputSource is) {
		try {
			is.getByteStream().close();
		} catch (IOException e) {
		}
		
	}

	private void showConfirmDialog(Main main, String message) {
		JOptionPane.showMessageDialog(main, message, "Installation", JOptionPane.PLAIN_MESSAGE);
		
	}

}
