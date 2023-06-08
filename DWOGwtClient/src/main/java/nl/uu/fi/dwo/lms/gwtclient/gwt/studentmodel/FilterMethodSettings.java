package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.tapestry.form.Checkbox;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.FilterTitle;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class FilterMethodSettings extends Composite implements LeafValueEditor<Map<String, Map<String, Set<Integer>>>> {

	static class P extends Label {
		private P(String text) {
			super(Document.get().createPElement());
			setStylePrimaryName("filter");
			setText(text);
		}
	}
	static class L extends Label {
		private L(String text) {
			super(Document.get().createLabelElement());
			setStylePrimaryName("filter");
			getElement().getStyle().setFontSize(12, Unit.PX);
			setText(text);
		}
	}
	
	
	private Map<String, Map<String, Set<Integer>>> value;
	private final String key;
	private final String[] books;
	private CheckBox gr, rest, gri[][];

	private static void setValue(CheckBox gri[], Boolean value) {
		for (int i = 0; i < gri.length; i++) {
			CheckBox checkBox = gri[i];
			checkBox.setValue(value, false);
		}
	}
	
	class GRHandler implements ValueChangeHandler<Boolean> {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			for (int i = 0; i < gri.length; i++) {
				CheckBox[] g = gri[i];
				setValue(g, event.getValue());
			}	
		}		
	}
	
	static class GRiHandler implements ValueChangeHandler<Boolean> {
		private CheckBox[] gri;

		private GRiHandler(CheckBox[] gri) {
			this.gri = gri;
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			setValue(gri, event.getValue());
		}
	}
	
	
	public FilterMethodSettings(DomMethod method) {
		key = method.key();
		books = method.books.toArray(new String[method.books.size()]);
		FlowPanel flow = new FlowPanel();
		Label title = new P(method.getMethod()); flow.add(title);
		int columns = 0;
		for (List<?> list : method.chapters) {
			columns = Math.max(columns, list.size());
		}		
		int rows = method.books.size();
		Grid  grid = new Grid(rows+1, columns+1); flow.add(grid);
		grid.getElement().getStyle().setMarginBottom(18, Unit.PX);
		gr = new CheckBox();
		grid.setWidget(0, 0, gr);
		for(int i = 1; i <= columns; i++) {
			grid.setWidget(0, i, new L("hfst " + i));
			grid.getColumnFormatter().getElement(i).getStyle().setWidth(40, Unit.PX);
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
			grrow.addValueChangeHandler(new GRiHandler(gri[row-1]));
		}
		gr.addValueChangeHandler(new GRHandler());
		Label alle = new P(FilterTitle.ALLE_LEERDOELEN);flow.add(alle); flow.add(alle);
		grid = new Grid(1,1); flow.add(grid);
		grid.getElement().getStyle().setMarginBottom(18, Unit.PX);
		rest = new CheckBox(DwoLocalesForGWT.instance.NUM_LBL_KNOWLEDGE_UNCLASSIFIED());
		grid.setWidget(0, 0, rest);

		initWidget(flow);
	}

	private void setValue(Map<String, Set<Integer>> GenR, String hfst, CheckBox[] g) {
		Set<Integer> h1 = GenR.get(hfst);
		for(CheckBox gr: g) gr.setValue(Boolean.FALSE);
		if (h1 == null) return;
		if (h1.isEmpty()) {
			setValue(g,Boolean.TRUE);
			return;
		}
		h1.forEach(item -> g[item.intValue()].setValue(Boolean.TRUE));
		g[0].setValue(h1.size() == g.length-1);
	}

	@Override
	public void setValue(Map<String, Map<String, Set<Integer>>> value) {
		this.value = value;
		rest.setValue(value.containsKey("")||value.containsKey(null));
		Map<String, Set<Integer>> GenR = value.getOrDefault(key, Collections.emptyMap());
		gr.setValue(Boolean.FALSE, true);
		boolean sum = true;
		for(int i = 0; i < books.length; i++ ) 
		{
			setValue(GenR, books[i], gri[i]);
			sum = sum & gri[i][0].isChecked();
		}
		gr.setValue(sum);
	}

	private Set<Integer> getValue(CheckBox[] g) {
		Set<Integer> set = new TreeSet<>();
		for (int i = 1; i < g.length; i++) {
			if (g[i].isChecked()) set.add(i);
		}
		return set;
	}

	@Override
	public Map<String, Map<String, Set<Integer>>> getValue() {
		Map<String, Map<String,Set<Integer>>> result = new HashMap<>();
		Map<String, Set<Integer>> GenR = new HashMap<>();
		Set<Integer> h;
		for (int i = 0; i < books.length; i++ ) {
			CheckBox[] gr1s = gri[i];
			h = getValue(gr1s);if (!h.isEmpty()) {
				if (h.size() == gr1s.length-1) h = Collections.emptySet();
				GenR.put(books[i], h);
			}		
		}
		if (!GenR.isEmpty()) result.put(key, GenR);
		if (rest.isChecked()) result.put("", Collections.emptyMap());		
		return result;
	}

}
