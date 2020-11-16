package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.CardLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class EditableGraph extends JPanel {

  private CardLayout cards;
  private Graph graph;
  
  public EditableGraph() {
    super(null);
    cards = new CardLayout();
    setLayout(cards);
    graph = new Graph();
    EditGraph editGraph = new EditGraph(graph);
    graph.painter = this;
    add(graph);
    add(editGraph);    
    cards.first(this);
    new DropTargetGraph(editGraph);
  }

  public void setEditMode(boolean b) {
    if (b) {
      cards.last(this);
    } else {
      cards.first(this);
    }
  }

  public void setModel(TreeModel model) {
    graph.setModel(model);
  }

  public void updateModel(TreeModel model) {
    graph.updateModel(model);
  }

  public void setScore(DomStudentModelStructureScore scores) {
    HashMap<String,Double> map = new HashMap<>();
    setScoreMap(scores, map);
    for( GraphNode node: graph.graphNodes) {
      String id = node.getID();
      Double score = map.get(id);
      node.setSuccesFailScore(score);
    }
    repaint();
  }

  private void setScoreMap(DomStudentModelStructureScore scores, HashMap<String, Double> map) {
    map.put(scores.getId(), scores.getScore()*100.0);
    for( DomStudentModelCategoryScore child: scores.getCategories()) 
      setScoreMap(child, map);   
  }

  private void setScoreMap(DomStudentModelCategoryScore parent, HashMap<String, Double> map) {
    map.put(parent.getId(), parent.getScore()*100.0);
    for( DomStudentModelObjectiveScore child: parent.getObjectives())
      setScoreMap(child, map);
  }

  private void setScoreMap(DomStudentModelObjectiveScore parent, HashMap<String, Double> map) {
    map.put(parent.getId(), parent.getScore()*100.0);
    if (parent.getChildren() != null) {
      for (DomStudentModelObjectiveScore child: parent.getChildren())
        setScoreMap(child, map);
    }   
  }

}
