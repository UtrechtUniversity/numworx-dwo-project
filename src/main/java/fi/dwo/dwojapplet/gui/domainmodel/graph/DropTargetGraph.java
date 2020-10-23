package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.List;

public class DropTargetGraph extends DropTargetAdapter {

  private EditGraph graph;

  public DropTargetGraph(EditGraph graph) {
    this.graph = graph;

    new DropTarget(graph, DnDConstants.ACTION_COPY, this,
            true, null);
  }

  @Override
  public void drop(DropTargetDropEvent event) {
    try {
      Transferable tr = event.getTransferable();
      if(event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        Object id = tr.getTransferData(DataFlavor.stringFlavor);
        event.acceptDrop(DnDConstants.ACTION_COPY);
        Point location = event.getLocation();
        List<GraphNode> nodes = graph.getGraphNodes();
        for(GraphNode node: nodes) {
          if (node.getID().equals(id)) {
            node.setLocation(location.x, location.y);
            break;
          }
        }
        graph.repaint();
        event.dropComplete(true);
        return;
      }
      event.rejectDrop();
  } catch (Exception e) {
      e.printStackTrace();
      event.rejectDrop();
  }
  }

}
