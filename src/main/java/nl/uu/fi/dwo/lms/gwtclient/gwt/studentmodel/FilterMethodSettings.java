package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class FilterMethodSettings extends Composite implements LeafValueEditor<Map<String, Map<String, Set<Integer>>>> {

	private Map<String, Map<String, Set<Integer>>> value;
	private final String key;
	private final String[] books;
	private CheckBox gr, rest, gri[][];
	
	public FilterMethodSettings(DomMethod method) {
		key = method.key();
		books = method.books.toArray(new String[method.books.size()]);
		FlowPanel flow = new FlowPanel();
		Label title = new Label(method.getMethod()); flow.add(title);
		int columns = 0;
		for (List<?> list : method.chapters) {
			columns = Math.max(columns, list.size());
		}		
		int rows = method.books.size();
		Grid  grid = new Grid(rows+1, columns+1); flow.add(grid);
		gr = new CheckBox();
		grid.setWidget(0, 0, gr);
		for(int i = 1; i <= columns; i++) {
			grid.setText(0, i, "hfst " + i);
		}
		gri = new CheckBox[rows][];
		
		for(int row = 1; row <= rows; row++) {
			String book = method.books.get(row-1);
			CheckBox grrow = new CheckBox(book);
			grid.setWidget(row, 0, grrow);
			int size = method.chapters.get(row-1).size();
			gri[row-1] = new CheckBox[size+1];
			gri[row-1][0] = grrow;
			for(int i = 1; i <= size; i++) {
				CheckBox gri = new CheckBox();
				this.gri[row-1][i] = gri;
				grid.setWidget(row, i, gri);
			}
		}
		Label alle = new Label("Alle leerdoelen");flow.add(alle); flow.add(alle);
		grid = new Grid(1,1); flow.add(grid);
		rest = new CheckBox("Niet geclassificeerde leerdoelen");
		grid.setWidget(0, 0, rest);

		initWidget(flow);
	}

	@Override
	public void setValue(Map<String, Map<String, Set<Integer>>> value) {
		this.value = value;
		
	}

	@Override
	public Map<String, Map<String, Set<Integer>>> getValue() {
		return value;
	}

}
