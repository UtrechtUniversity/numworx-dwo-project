package nl.numworx.osiris;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import nl.numworx.osiris.TablePanel.Model;

public class TablePanel extends JPanel {

	public class Model extends AbstractTableModel {

		private final CSVParser parser;
		private final List<CSVRecord> records;
		private final List<String> headers;
		

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





	private static final CSVFormat EXCEL = CSVFormat.EXCEL.withHeader().withDelimiter(';');

	final Main main;
	
	JButton openBtn;
	JTable table;
	JLabel header;
	
	File file;
	
	public TablePanel(Main main) {
		super(new BorderLayout());
		this.main = main;
		header = new JLabel("<empty>");
		add(header, BorderLayout.SOUTH);
		table = new JTable();
		add(new JScrollPane(table), BorderLayout.CENTER);
		Box box = Box.createHorizontalBox();
		openBtn = new JButton("Open file");
		openBtn.addActionListener(this::doOpen);
		box.add(Box.createGlue());
		box.add(openBtn);
		add(box, BorderLayout.NORTH);
	}


	public void doOpen(ActionEvent e) {
		int returnVal = main.chooser.showOpenDialog(main);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = main.chooser.getSelectedFile();
				InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in, "UTF-8");
				CSVParser parser = CSVParser.parse(reader, EXCEL);
				table.setModel(new Model(parser));
				in.close();
				this.file = file;
				header.setText(file.getCanonicalPath());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(main, e1.getLocalizedMessage(), "An Error occured",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}


}
