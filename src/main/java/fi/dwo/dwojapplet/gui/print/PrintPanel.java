package fi.dwo.dwojapplet.gui.print;

import java.awt.FlowLayout;
import java.awt.print.Printable;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class PrintPanel extends Box {

	private SetupAction setup;
	private PrintStudent printStudent;
	private PrintStudent printSchoolClass;
	private JMenuBar bar;
	
	
	public PrintPanel() {
		super(BoxLayout.LINE_AXIS);
		ResourceBundle rb = ResourceBundle.getBundle("fi.dwo.dwojapplet.gui.print.print");
		ImageIcon student = new ImageIcon(getClass().getResource("../../domain/resources/student.png"));
		ImageIcon schoolClass = new ImageIcon(getClass().getResource("../../domain/resources/userlist.gif"));
		ImageIcon print = new ImageIcon(getClass().getResource("../../domain/resources/print.png"));
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
	
	
	public static void main(String[] args) {
		PrintPanel panel = new PrintPanel();
		Printable p = new PrintComponent(panel);
		
		panel.setComponent(p);
		panel.setIterable(Arrays.asList( p,p,p ));
		
		JFrame f = new JFrame("test L&F");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER,20, 20));
		f.getContentPane().add(panel);
		f.pack();
		f.show();
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
