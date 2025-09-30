package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class Stub<T> implements HasRows {
	public  final Predicate<Entry<String, T>> NULL =  t -> true;
	final public int pagesize = 50;
	private final EventBus eventBus;
	private final PagingView<T> view;
	private final SimplePager pager;

	public Stub(EventBus eventBus, PagingView<T> view) {
		this.eventBus = eventBus;
		this.view = view;
	    pager = new SimplePager();
	    pager.setPageSize(pagesize);
	    pager.setDisplay(this);
	}
	
	public SimplePager getPager() {
	    return pager;
	}

	boolean rowCountExact = false;
	int rowCount = 0;
	Range visibleRange = new Range(0,pagesize);
	public RoleType role;
	Map<String, T> personen = Collections.emptyMap(); 
	private Predicate<Entry<String,T>> filter = NULL;

	@Override
	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration r = eventBus.addHandlerToSource(RangeChangeEvent.getType(), this, handler);
		return r::removeHandler;
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration r = eventBus.addHandlerToSource(RowCountChangeEvent.getType(), this, handler);
		return r::removeHandler;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Range getVisibleRange() {
		return visibleRange;
	}

	@Override
	public boolean isRowCountExact() {
		return rowCountExact;
	}

	@Override
	public void setRowCount(int count) {
		setRowCount(count, true);		
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		rowCount = count;
		rowCountExact = isExact;
		RowCountChangeEvent.fire(this, count, isExact);
	}

	@Override
	public void setVisibleRange(int start, int length) {
		setVisibleRange(new Range(start, length));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setVisibleRange(Range range) {
		visibleRange = range;
		Set<Entry<String,T>> entrySet = personen.entrySet();
		Stream<Entry<String, T>> stream = entrySet.stream();
		view.showPersonen(
				stream
				.filter(filter)
				.skip(range.getStart())
				.limit(range.getLength())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)), 
				role);
		RangeChangeEvent.fire(this, range);
	}

	protected void countPersons(boolean last) {
		int count;
		if (filter == NULL) count = personen.size();
		else count = (int) personen.entrySet().stream().filter(filter).count();
		if (count != rowCount || last != isRowCountExact()) setRowCount(count, last);
	}

	public void setRole(RoleType role) {
		this.role = role;		
	}

	public void limit(Map<String, T> students, boolean first, boolean last) {
		first |= rowCount < pager.getPageSize();
		add(students, last);
		if(first) setVisibleRange(0, pager.getPageSize()); // to front
	}
	
	public void add(Map<String, T> students, boolean last) {
		this.personen = students;
		countPersons(last);
		//pager.setPageSize(Math.min(pagesize, rowCount));
	}

	public void setFilter(Predicate<Entry<String,T>> filter) {
		if (filter == null) filter = NULL;
		this.filter = filter;	
		countPersons(isRowCountExact());
	}

	public void init() {
		filter = NULL;
		personen = Collections.emptyMap();
		rowCountExact = false;
		rowCount = 0;
		visibleRange = new Range(0,pagesize);
		role = null;
	}

	public int getPageSize() {
		return pagesize;
	}
	  
  }