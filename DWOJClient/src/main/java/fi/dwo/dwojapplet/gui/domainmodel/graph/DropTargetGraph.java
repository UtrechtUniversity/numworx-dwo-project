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

		new DropTarget(graph, DnDConstants.ACTION_COPY, this, true, null);
	}

	@Override
	public void drop(DropTargetDropEvent event) {
		try {
			Transferable tr = event.getTransferable();
			if (event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				Object id = tr.getTransferData(DataFlavor.stringFlavor);
				event.acceptDrop(DnDConstants.ACTION_COPY);
				Point location = event.getLocation();
				Point origin = graph.getOrigin();
				double factor = graph.getFactor();
				int ex = (int) ((location.x - origin.x) / factor);
				int ey = (int) ((location.y - origin.y) / factor);
				List<GNode> nodes = graph.getGraphNodes();
				for (GNode node : nodes) {
					if (node.getID().equals(id)) {
					    if (node.isVisible()) {
					        node.getVisibleSet().forEach(t -> node.setSelected(t, true));
					    } else {
					        String prefix = String.valueOf(graph.graph.activeRow.key());					        
					        node.getMethodeCodes().stream()
					        .filter (t -> t.startsWith(prefix))
					        .forEach(t -> {
					          node.setSelected(t, true);
					          node.setVisible(t,  true);
					        }
					        );
					    }
						node.setLocation(ex, ey);
						node.setSelected(false);
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
