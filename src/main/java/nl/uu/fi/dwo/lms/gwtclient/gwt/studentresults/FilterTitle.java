package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph.BookNode;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph.ChapterEdge;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph.ChapterNode;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph.Edge;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph.Node;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class FilterTitle extends ResizeComposite implements Consumer<Map<String, Map<String, Set<Integer>>>> {

	public static final String ALLE_LEERDOELEN = "Alle leerdoelen";
	
	
	
	DockLayoutPanel root;
	private ListBox methodeBtn;
	private Button book;
	private Label chapter;
	private DomMethod method;
	
	private Consumer<Map<String, Map<String, Set<Integer>>>> filter = this;
	
	private class Book implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String b = book.getText();
			String m = method.key();
			
			Map<String, Map<String, Set<Integer>>> t = Collections.singletonMap(m, Collections.singletonMap(b, Collections.emptySet()));
			acceptFilter(t);
		}
	}
	private class MethodeChange implements ChangeHandler, ClickHandler {

		@Override
		public void onChange(ChangeEvent event) {
			int index = methodeBtn.getSelectedIndex();
			Map<String, Map<String,Set<Integer>>> filter = null;
			switch(index) {
			default:
			case 0: filter = Collections.emptyMap(); break;
			case 1: filter = Collections.singletonMap(method.key(), Collections.emptyMap()); break;
			}
			acceptFilter(filter);
		}

		@Override
		public void onClick(ClickEvent event) {
			onChange(null);
			event.preventDefault();
		}

	}

	public FilterTitle(DomMethod method) {
		this.method = method;
		root = new DockLayoutPanel(Unit.EM);
		initWidget(root);
		root.setStylePrimaryName("filter-title");
		root.getElement().getStyle().setBackgroundColor("#1B75BB");

		methodeBtn = new ListBox();
		methodeBtn.addItem(ALLE_LEERDOELEN);
		if (method != null) methodeBtn.addItem(method.getMethod());
		methodeBtn.setStylePrimaryName("graph-ListBox");		
		root.addWest(methodeBtn, 10);

		book = new Button("1HV");
		book.setStylePrimaryName("dwo-Button");
		Label prebook = new Label(" > ");
		Style style = prebook.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setTextAlign(TextAlign.CENTER);
		style.setFontSize(20, Unit.PX);
		Label postbook = new Label(" > ");
		style = postbook.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setTextAlign(TextAlign.CENTER);
		style.setFontSize(20, Unit.PX);
		chapter = new Label("h1");
		style = chapter.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setFontSize(20, Unit.PX);
		root.addWest(prebook, 3);
		root.addWest(book, 10);
		root.addWest(postbook, 3);
		root.add(chapter);

		MethodeChange handler = new MethodeChange();
		methodeBtn.addChangeHandler(handler);
		methodeBtn.addClickHandler(handler);
		book.addClickHandler(new Book());
		
	}

	public Consumer<Map<String, Map<String, Set<Integer>>>> getFilter() {
		return filter;
	}

	public void setFilter(Consumer<Map<String, Map<String, Set<Integer>>>> filter) {
		if (filter == null) filter = this;
		this.filter = filter;
	}

	private void acceptFilter(Map<String, Map<String, Set<Integer>>> t) {
		filter.accept(t);
	}


	public void accept(Map<String, Map<String, Set<Integer>>> f) {
		if (f == null) return;
		if (f.size() != 1) {
		    methodeBtn.setSelectedIndex(0);
		    book.setText("");
		    chapter.setText("");
		    return;
		} else {
			String key = f.keySet().iterator().next();
			int index = 0;
			if (method.key().equals(key)) index = 1;
			methodeBtn.setSelectedIndex(index);
			if (f.get(key).size() == 1) {
				book.setText(f.get(key).keySet().iterator().next());
				Set<Integer> chapters = f.get(key).get(book.getText());
				List<String> titles = null;
				for(int i = 0 ; i < method.books.size(); i++) {
					if (method.books.get(i).equals(book.getText())) 
						titles = method.chapters.get(i);
				}
				chapter.setText(FilterUtil.h(chapters, titles));
							
			} else {
				chapter.setText("");
				book.setText("");
			}
		}
	}

	public DomMethod getMethod() {
		return method;
	}

	public void setMethod(DomMethod method) {
		this.method = method;
		if (methodeBtn.getItemCount() == 2) {
			methodeBtn.setItemText(1, method.getMethod());
		} else {
			methodeBtn.addItem(method.getMethod());
		}
	}

}
