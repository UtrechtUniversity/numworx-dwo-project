package fi.dwo.dwojapplet.gui.print;

import java.awt.print.Printable;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import fi.dwo.dwojapplet.domain.DwoHelper;

public class PrintPanel extends Box {

	private static final String PDF = "PDF - ";
	private SetupAction setup;
	private PDFSetupAction pdfSetup;
	private PrintStudent printStudent;
	private PrintStudent printSchoolClass;
	private PDFStudent pdfStudent;
	private PDFStudent pdfSchoolClass; // deze is niet goed!
	private JMenuBar bar;
	
	
	public PrintPanel() {
		super(BoxLayout.LINE_AXIS);
		ResourceBundle rb = ResourceBundle.getBundle("fi.dwo.dwojapplet.gui.print.print");
// TODO GuiConstants.XXXX
		ImageIcon student = new ImageIcon(DwoHelper.getResourceImage("resources/student.png"));
		ImageIcon schoolClass = new ImageIcon(DwoHelper.getResourceImage("resources/userlist.gif"));
		ImageIcon print = new ImageIcon(DwoHelper.getResourceImage("resources/print.png"));
		setup = new SetupAction(rb.getString("setup"));
		pdfSetup = new PDFSetupAction(PDF + rb.getString("setup"), setup);
		printStudent = new PrintStudent(rb.getString("printStudent"), student, setup);
		pdfStudent = new PDFStudent(PDF + rb.getString("printStudent"), student, pdfSetup);
		printSchoolClass = new PrintStudent(rb.getString("printSchoolClass"), schoolClass, setup);
		pdfSchoolClass = new PDFStudent(PDF + rb.getString("printSchoolClass"), schoolClass, pdfSetup);
		
		bar = new JMenuBar();
		JMenu menu = new JMenu();menu.setIcon(print);
		menu.add(printStudent).setHorizontalTextPosition(JMenu.LEADING);
		menu.add(printSchoolClass).setHorizontalTextPosition(JMenu.LEADING);
		menu.addSeparator();
		menu.add(setup);
		menu.addSeparator();
		
		menu.add(pdfSetup);
		pdfSetup.enabled = Arrays.asList(pdfStudent, pdfSchoolClass);
		menu.add(pdfStudent).setHorizontalTextPosition(JMenu.LEADING);
		menu.add(pdfSchoolClass).setHorizontalTextPosition(JMenu.LEADING);

		bar.add(menu);
		add(bar);
	}

	public void setComponent(Printable component) {
		printStudent.setPrintable(component);
		pdfStudent.setPrintable(component);
	}
	public Printable getComponent() {
		return printStudent.getPrintable();
	}
		
	public void setIterable(Collection<Printable> collection) {
		printSchoolClass.setPrintable(new PrintIterator(collection));
		pdfSchoolClass.setPrintable((collection));
	}
	

	public void addPrinterListener(PrinterListener listener) {
		printStudent.setListener(listener);
		printSchoolClass.setListener(listener);
		pdfStudent.setListener(listener);
		pdfSchoolClass.setListener(listener);
	}
	public void removePrinterListener(PrinterListener listener) {
		printStudent.setListener(null);
		printSchoolClass.setListener(null);
		pdfStudent.setListener(null);
		pdfSchoolClass.setListener(null);
	}

	public JMenuBar asComponent() {
		return bar;
	}
	
}
