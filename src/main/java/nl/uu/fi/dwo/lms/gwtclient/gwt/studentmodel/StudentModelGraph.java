package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;

public class StudentModelGraph extends StudentResultsGraph {

	final static Logger LOG = Logger.getLogger(StudentModelGraph.class.getName());
	
	
	class GoalNode extends Node {
		
		private boolean goal, check;
		
		String getId() {
			return obj.getInfo().getId();
		}
		private void setClassName(String name, boolean b) {
			if (b) {
				g.addClassNameBaseVal(name);
			} else {
				g.removeClassNameBaseVal(name);
			}
		}

		GoalNode(DomStudentModelObj obj, DomStudentModelMethodInfo info, String parent) {
			super(obj, info, parent);
		}

		@Override
		public void onClick(ClickEvent event) {
			if (goal) {
				LOG.info("on click" + this);
				boolean blur = isCheck();
				setCheck(!blur);
			}
		}

		@Override
		protected void colorize() {
			setClassName(bundle.css().goal(), goal);
			setClassName(bundle.css().check(), check);
			//super.colorize();
		}

		@Override
		protected void setBlur(boolean blur) {
			// TODO Auto-generated method stub
			super.setBlur(blur);
		}

		public void setGoal(boolean b) {
			goal = b;
			colorize();
			
		}

		public void setCheck(boolean b) {
			check = b;
			colorize();			
		}	
		
		public boolean isCheck() {
			return check;
		}
	}
	
	
	
	static private final Promise<DomStudentModelDataScore> DUMMYSCORE = Promises.failed(new IllegalArgumentException());

	@Inject StudentModelGraph(DescriptionPresenter d) {
		super(d);
		addStyleName(bundle.css().goals());
	}
		

	public void setModel(DomStudentModelContext4Student item, DomMethod method) {
		setModelScore(item, DUMMYSCORE, method);		
	}



	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		super.onMouseUp(event);
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		super.onMouseMove(event);
	}


	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		super.onMouseOut(event);
	}


	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		super.onMouseDown(event);
	}


	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
		super.onContextMenu(event);
	}


	@Override
	protected void initHandlers() {
		image.addMouseMoveHandler(this);
		image.addMouseUpHandler(this);
		image.addMouseDownHandler(this);
		image.addMouseOutHandler(this);
		
		zoomFitBtn.addClickHandler(new ZoomFit());
		zoomOutBtn.addClickHandler(new Zoom(true));
		zoomInBtn.addClickHandler(new Zoom(false));
		voorkennisBtn.addClickHandler(new Voorkennis());
		verbergBtn.addClickHandler(new VerbergVoorkennis());
		
		
		//addDomHandler(this, ContextMenuEvent.getType());
	}



	public void setGoals(List<String> ids, List<String> set) {
		ids.stream().map(map::get).filter(Objects::nonNull).flatMap(List::stream).forEach(n -> {
			GoalNode node = (GoalNode) n;
			node.setGoal(true);
			node.setCheck(set.contains(node.uuid()));
		});
	}

	@Override
	protected Node nodeFactory(DomStudentModelObj obj, String p, DomStudentModelMethodInfo info) {
		return new GoalNode(obj, info, p);
	}


	public List<String> getGoals() {
		return map.values().stream()
		.flatMap(List::stream)
		.map(n -> (GoalNode)n)
		.filter(GoalNode::isCheck)
		.map(GoalNode::getId)
		.collect(Collectors.toList());
	}

}
