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
import com.google.gwt.user.client.ui.ResizeComposite;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class FilterTitle extends ResizeComposite implements Consumer<Map<String, Map<String, Set<Integer>>>>, Runnable {

	public static final String ALLE_LEERDOELEN = DwoLocalesForGWT.instance.NUM_LBL_KNOWLEDGE_ALL();
	
	private static final String graphBtnStyle = StudentResultsGraph.bundle.css().graphButton();
	
	DockLayoutPanel root;
	private Button methodeBtn;
	private Button book;
	protected Button close;
	private Label chapter;
	private DomMethod method;
	
	private Consumer<Map<String, Map<String, Set<Integer>>>> filter = this;
	
	private Map<String, Map<String, Set<Integer>>> cfg = Collections.emptyMap();
	
	
	private class Book implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String b = book.getText();
			if (b.isEmpty()) return;
			String m = method.key();
			closeClose();
			cfg = Collections.singletonMap(m, Collections.singletonMap(b, Collections.emptySet()));
			acceptFilter(cfg);
		}
	}
	
	private Runnable closer = this;
	
	private class Close implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			Runnable cmd = closer;
			closeClose();
			cmd.run();
			acceptFilter(cfg);
		}
		
	}
	
	public void showClose(Runnable callback) {
		closer = callback;
		root.setWidgetHidden(close, false);
	}
	
	private class MethodeChange implements ChangeHandler, ClickHandler {

		@Override
		public void onChange(ChangeEvent event) {
			int index = method == null ? 0 : 1;
			Map<String, Map<String,Set<Integer>>> filter = null;
			switch(index) {
			default:
			case 0: filter = Collections.emptyMap(); break;
			case 1: filter = Collections.singletonMap(method.key(), Collections.emptyMap()); break;
			}
			closeClose();
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
		///root.getElement().getStyle().setBackgroundColor("#1B75BB");
		methodeBtn = new Button(ALLE_LEERDOELEN);
		book = new Button("");
		chapter = new Label("");
		initialize();
	}

	protected void initializeX() {
		initClose();
	}
	protected void initialize() {
		if (this.method != null) methodeBtn.setText(this.method.getMethod());
		methodeBtn.setStylePrimaryName(graphBtnStyle);		
		root.addWest(methodeBtn, 10);

		book.setStylePrimaryName(graphBtnStyle);
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
		style = chapter.getElement().getStyle();
		style.setProperty("maxWidth", "initial");
		style.setTextAlign(TextAlign.LEFT);
		chapter.setStylePrimaryName("pseudobutton");

		initClose();

		root.addWest(prebook, 3);
		root.addWest(book, 10);
		root.addWest(postbook, 3);
		root.add(chapter);

		MethodeChange handler = new MethodeChange();
		methodeBtn.addClickHandler(handler);
		book.addClickHandler(new Book());
	}

	protected void initClose() {
		close = new Button("Verberg voorkennis X");
		close.setStylePrimaryName("dwo-Button");
		root.addEast(close, 10);
		close.getElement().getStyle().setProperty("maxWidth", 15, Unit.EM);
		root.setWidgetHidden(close, true);
		close.addClickHandler(new Close());
	}

	public Consumer<Map<String, Map<String, Set<Integer>>>> getFilter() {
		return filter;
	}

	public void setFilter(Consumer<Map<String, Map<String, Set<Integer>>>> filter) {
		if (filter == null) filter = this;
		this.filter = filter;
	}

	private void acceptFilter(Map<String, Map<String, Set<Integer>>> t) {
		cfg = t;
		filter.accept(t);
	}


	public void accept(Map<String, Map<String, Set<Integer>>> f) {
		if (f == null) return;
		cfg = f;
		if (f.size() != 1) {
		    book.setText("");
		    chapter.setText("");
		    return;
		} else {
			String key = f.keySet().iterator().next();
			int index = 0;
			if (method.key().equals(key)) index = 1;
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
		if (method == null) {
			methodeBtn.setText(ALLE_LEERDOELEN);
		} else {
			methodeBtn.setText(method.getMethod());
		}
	}

	@Override
	public void run() {
	}

	private void closeClose() {
		closer = FilterTitle.this;
		root.setWidgetHidden(close, true);
	}

}
