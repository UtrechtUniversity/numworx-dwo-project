package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

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

}
