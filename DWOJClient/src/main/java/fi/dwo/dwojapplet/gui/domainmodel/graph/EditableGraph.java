package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.AWTEventMulticaster;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import fi.beans.numworxlf.JTree;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class EditableGraph extends JPanel implements ActionListener{

	private CardLayout cards;
	private Graph graph;
	private EditGraph editGraph;

	public EditableGraph() {
		super(null);
		cards = new CardLayout();
		setLayout(cards);
		graph = new Graph();
		graph.addActionListener(this);
		editGraph = new EditGraph(graph);
		editGraph.addActionListener(this);
		graph.painter = this;
		add(graph);
		add(editGraph);
		cards.first(this);
		new DropTargetGraph(editGraph);
	}

	public void setEditMode(boolean b) {
		if (b) {
			editGraph.setOrigin(graph.getOrigin());
			editGraph.setFactor(graph.getFactor());
			editGraph.removeTempLocations();
			cards.last(this);
		} else {
			graph.setOrigin(editGraph.getOrigin());
			graph.setFactor(editGraph.getFactor());
			graph.setVoorkennisArea(false);
			cards.first(this);
		}
	}

	public void setModel(TreeModel model, Map<String, Map<String, Set<Integer>>> filter, PersistenceId activeMethod) {
		if (!graph.graphNodes.isEmpty() && !Objects.equals(activeMethod, graph.activeMethod))
			updateModel(model); // extra, save graph that will become invisible.
		graph.setModel(model, filter, activeMethod);
		editGraph.setModelJustSet(true);
	}

	public void updateModel(TreeModel model) {
		graph.updateModel(model);
	}

	public void setScore(DomStudentModelStructureScore scores) {
		HashMap<String, Double> map = new HashMap<>();
		setScoreMap(scores, map);
		for (GNode node : graph.graphNodes) {
			String id = node.getID();
			Double score = map.get(id);
			node.setSuccesFailScore(score);
		}
		repaint();
	}

	private void setScoreMap(DomStudentModelStructureScore scores, HashMap<String, Double> map) {
		map.put(scores.getId(), scores.getScore() * 100.0);
		for (DomStudentModelCategoryScore child : scores.getCategories())
			setScoreMap(child, map);
	}

	private void setScoreMap(DomStudentModelCategoryScore parent, HashMap<String, Double> map) {
		map.put(parent.getId(), parent.getScore() * 100.0);
		for (DomStudentModelObjectiveScore child : parent.getObjectives())
			setScoreMap(child, map);
	}

	private void setScoreMap(DomStudentModelObjectiveScore parent, HashMap<String, Double> map) {
		map.put(parent.getId(), parent.getScore() * 100.0);
		if (parent.getChildren() != null) {
			for (DomStudentModelObjectiveScore child : parent.getChildren())
				setScoreMap(child, map);
		}
	}
	
	//ActionProducer
	private ActionListener actionListener = null;

	public void addActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	public void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	public void produceAction(String command) {
		if (actionListener != null)	{
			actionListener.actionPerformed(new ActionEvent(this, 0, command));
		}
	}

	public void produceThisAction(ActionEvent e)	{
		if (actionListener != null)	{
			actionListener.actionPerformed(e);
		}
	}
	//end ActionProducer

	@Override
	public void actionPerformed(ActionEvent e) {
		produceAction(e.getActionCommand());	
	}

  public Set<String> getVisibleNodes() {
    if (!graph.isFiltered()) return Collections.emptySet();
    return graph.graphNodes.stream()
        .filter(GNode::isVisible)
        .map(GNode::getID)
        .collect(Collectors.toSet());
  }

  public void end() {
    // TODO Auto-generated method stub
    
  }

  public Map<String, Map<String, Set<Integer>>> getFilter() {
    if (!graph.isFiltered()) return Collections.emptyMap();
    return graph.getFilter();
  }

public void setModel(JTree tree, Map<String, Map<String, Set<Integer>>> filter, PersistenceId activeMethod) {
	
	TreeModel model = tree.getModel();
	if (!graph.graphNodes.isEmpty() && !Objects.equals(activeMethod, graph.activeMethod))
		updateModel(model); // extra, save graph that will become invisible.
	graph.setModel(tree, filter, activeMethod);
	editGraph.setModelJustSet(true);

	
}

}
