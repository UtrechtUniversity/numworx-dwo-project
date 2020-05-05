package nl.numworx.osiris;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import nl.numworx.osiris.TablePanel.Model;

public class TablePanel extends JPanel implements Iterable<CSVRecord> {

	private static final DefaultTableModel EMPTY_MODEL = new DefaultTableModel();

  private static final String EMPTY = "<empty>";
  String charset = "UTF-8";

  public static class Model extends AbstractTableModel {

		final CSVParser parser;
		final List<CSVRecord> records;
		final List<String> headers;
		

		public Model(CSVParser parser) throws IOException {
			this.parser = parser;
			this.records = parser.getRecords();
			this.headers = parser.getHeaderNames();
		}

		public int getRowCount() {
			return records.size();
		}

		public int getColumnCount() {			
			return headers.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return records.get(rowIndex).get(columnIndex);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			return headers.get(column);
		}
	}

	final Main main;
	
	JButton openBtn, wisBtn;
	JTable table;
	Model model;
	JLabel header;
	
	File file;

  private Col[] headers;

	public Iterator<CSVRecord> iterator() {
		if (model == null) return Collections.emptyIterator();
		return model.records.iterator();
	}
	
	boolean verify(Model model) {
	  boolean verify = model.getColumnCount() == headers.length;
	  if (verify) {
	    Set<String> names = new TreeSet<>(model.headers);
	    for (Col key:headers) {
	      if (!names.contains(key.toString())) return false;	      
	    }
	    int cols = model.getColumnCount();
	    int rows = model.getRowCount();
	    for(int i = 0; i < cols; i++)
	      for(int j = 0; j < rows; j++) {
	        Object v = model.getValueAt(j, i);
	        if (v == null || v.toString().trim().isEmpty()) {
	          return false;
	        }
	      }
	    return true;
	  }
	  return false;
	}
	
	
	public TablePanel(Main main, Col...headers) {
		super(new BorderLayout());
		this.main = main;
		this.headers = headers;
		header = new JLabel(EMPTY);
		add(header, BorderLayout.SOUTH);
		table = new JTable();
		add(new JScrollPane(table), BorderLayout.CENTER);
		Box box = Box.createHorizontalBox();
		openBtn = new JButton("Open file");
		openBtn.addActionListener(this::doOpen);
		wisBtn = new JButton("Empty table");
		wisBtn.addActionListener(this::doDelete);
		box.add(Box.createGlue());
		box.add(wisBtn);
		box.add(Box.createHorizontalStrut(10));
		box.add(openBtn);
		add(box, BorderLayout.NORTH);
	}

	public void doDelete(ActionEvent e) {
	  file = null;
	  model = null;
	  header.setText(EMPTY);
	  table.setModel(EMPTY_MODEL);
	}

	public void doOpen(ActionEvent e) {
		int returnVal = main.chooser.showOpenDialog(main);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = main.chooser.getSelectedFile();
				InputStream in = new FileInputStream(file);
				Excel excel = new Excel();
				CSVParser parser = excel.parse(in);
				charset = excel.charset;
				Model m = new Model(parser);
                in.close();
                if  (verify(m)) {
                  table.setModel(model = m);
                  this.file = file;
                  header.setText(file.getCanonicalPath());
                } else {
                  JOptionPane.showMessageDialog(main, "Wrong format in " + file, "An Error occured",JOptionPane.ERROR_MESSAGE);
                }
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(main, e1.getLocalizedMessage(), "An Error occured",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}



}
