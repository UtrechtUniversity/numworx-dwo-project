package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.Map;

import com.google.web.bindery.event.shared.Event;

import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

public class ResultEvent extends Event<ResultEventHandler> {
	
	public final static Type<ResultEventHandler> TYPE = new Type<>();
	
	private final DomResultStudentScoContext ssc;
	private final Map<String,String> values;
		
	ResultEvent(DomResultStudentScoContext ssc, Map<String, String> values) {
		this.ssc = ssc;
		this.values = values;
	}

	@Override
	public Type<ResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResultEventHandler handler) {
		handler.onResult(this);
	}

	public DomResultStudentScoContext getRSsc() {
		return ssc;
	}

	public Map<String, String> getValues() {
		return values;
	}

}
