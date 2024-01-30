package nl.numworx.osiris;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.commons.csv.CSVParser;

import nl.numworx.osiris.servlet.InstallServlet;

public class Main {

	public static void main(String[] args) throws IOException {
		JFileChooser chooser = new JFileChooser();
		int ok = chooser.showOpenDialog(null);
		if (ok == chooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			FileInputStream in = new FileInputStream(f);
			Excel e = new Excel();
			CSVParser o = e.parse(in);
			in.close();
			e.verify(InstallServlet.STUDENTEN);
		}
	}

}
