package fi.dwo.dwojapplet.gui.print;

import java.awt.print.Printable;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import fi.dwo.dwojapplet.domain.DwoHelper;

public class PrintPanel extends Box {

	private SetupAction setup;
	private PrintStudent printStudent;
	private PrintStudent printSchoolClass;
	private JMenuBar bar;
	
	
	public PrintPanel() {
		super(BoxLayout.LINE_AXIS);
		ResourceBundle rb = ResourceBundle.getBundle("fi.dwo.dwojapplet.gui.print.print");
// TODO GuiConstants.XXXX
		ImageIcon student = new ImageIcon(DwoHelper.getResourceImage("resources/student.png"));
		ImageIcon schoolClass = new ImageIcon(DwoHelper.getResourceImage("resources/userlist.gif"));
		ImageIcon print = new ImageIcon(DwoHelper.getResourceImage("resources/print.png"));
		setup = new SetupAction(rb.getString("setup"));
		printStudent = new PrintStudent(rb.getString("printStudent"), student, setup);
		printSchoolClass = new PrintStudent(rb.getString("printSchoolClass"), schoolClass, setup);
		
		bar = new JMenuBar();
		JMenu menu = new JMenu();menu.setIcon(print);
		menu.add(printStudent).setHorizontalTextPosition(JMenu.LEADING);
		menu.add(printSchoolClass).setHorizontalTextPosition(JMenu.LEADING);
		menu.addSeparator();
		menu.add(setup);
		bar.add(menu);
		add(bar);
	}

	public void setComponent(Printable component) {
		printStudent.setPrintable(component);
	}
	public Printable getComponent() {
		return printStudent.getPrintable();
	}
	
	
	public void setIterable(Iterable<Printable> collection) {
		printSchoolClass.setPrintable(new PrintIterator(collection));
	}
	

	public void addPrinterListener(PrinterListener listener) {
		printStudent.setListener(listener);
		printSchoolClass.setListener(listener);
	}
	public void removePrinterListener(PrinterListener listener) {
		printStudent.setListener(null);
		printSchoolClass.setListener(null);
	}

	public JMenuBar asComponent() {
		return bar;
	}
	
}
