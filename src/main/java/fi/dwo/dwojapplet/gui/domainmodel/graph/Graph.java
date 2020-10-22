package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import fi.dwo.dwojapplet.gui.domainmodel.NodeLeaf;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

public class Graph extends JPanel implements MouseListener, MouseMotionListener{

	protected ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
	protected ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	
	public Graph() {
		setBackground(LeerdomeinGraphPanel.colorGray3);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i=0 ; i<graphEdges.size() ; i++)
			graphEdges.get(i).paint(g);
		
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).getBlur())
			{				Rectangle r = graphNodes.get(i).getTextBB();
				if(r.width==0) {
					graphNodes.get(i).paint(g);
					r = graphNodes.get(i).getTextBB();
				}
				int k = 6;
				for(int j=0 ; j<k ; j++) {
					g.setColor(new Color(237, 239, 241, 96+90/k*(k-2*j)));
					if(j==0)
						g.fillRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
					g.drawRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
						
								
				}
			}
			graphNodes.get(i).paint(g);
		}
	}
	
	public ArrayList<GraphNode> transformFromGephi(ArrayList<GraphNode> graphNodes) {
		ArrayList<GraphNode> graphNodesNew = new ArrayList<GraphNode>();
		for(int i=0 ; i<graphNodes.size() ; i++) {
			GraphNode gn = graphNodes.get(i);
			double factor = 0.5;
			int x = (int)(factor*(gn.getLocation().x+1200));
			int y = (int)(factor*(-gn.getLocation().y+900));
			graphNodesNew.add(new GraphNode(gn.getID(), gn.getSubdomein(), gn.getDescription(), x, y));
		}
		return graphNodesNew;
	}
	
	public void setGraphNodes(ArrayList<GraphNode> graphNodes) {
		this.graphNodes = graphNodes;
	}
	
	public void setGraphEdges(ArrayList<GraphEdge> graphEdges) {
		this.graphEdges = graphEdges;
	}
	
	public ArrayList<GraphNode> getGraphNodes() {
		return graphNodes;
	}
	
	public ArrayList<GraphEdge> getGraphEdges() {
		return graphEdges;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		GraphNode mouseOverNode = null;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(e.getX(), e.getY())) {
				mouseOverNode = graphNodes.get(i);
				break;
			}
		}
		if(mouseOverNode!=null) {
			for(int i=0 ; i<graphNodes.size() ; i++) {
				if(graphNodes.get(i)!= mouseOverNode) {
					graphNodes.get(i).setBlur(true);
					
				}
			}
			for(int i=0 ; i<graphEdges.size() ; i++) {
				if(graphEdges.get(i).getTarget()!= mouseOverNode) {
					graphEdges.get(i).setBlur(true);
				}
				else  {
					graphEdges.get(i).getSource().setBlur(false);
				}
			}
			repaint();
		}
		else {
			for(int i=0 ; i<graphNodes.size() ; i++) {
				graphNodes.get(i).setBlur(false);
			}
			for(int i=0 ; i<graphEdges.size() ; i++) {
				graphEdges.get(i).setBlur(false);
			}
			repaint();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

  public void setModel(TreeModel model) {
    Map<String, GraphNode> graphMap = new LinkedHashMap<>();
    List<NodeLeaf> leaves = new ArrayList<>();
    ArrayList<GraphEdge> edges = new ArrayList<>();
    searchNodes(model, model.getRoot(), graphMap, "", leaves);
    setGraphNodes(new ArrayList<>(graphMap.values()));
    searchEdges(leaves, graphMap, edges);
    setGraphEdges(edges);
  }

  private void searchEdges(List<NodeLeaf> leaves, Map<String, GraphNode> graphMap,
      ArrayList<GraphEdge> edges) {
    for(NodeLeaf leaf: leaves) {
        String dest = leaf.getId();
        GraphNode gnd = graphMap.get(dest);
        List<String> sources = leaf.getVoorkennis();
        if (sources != null)
        for(String source: sources) {
          GraphNode gns = graphMap.get(source);
          if(gns != null) {
            GraphEdge edge = new GraphEdge(gns, gnd);
            edges.add(edge);
          }
        }
      }
  }

  private void searchNodes(TreeModel model, Object node, Map<String, GraphNode> graphMap, String parent, List<NodeLeaf> leaves) {
    if (model.isLeaf(node)) {
      DefaultMutableTreeNode object = (DefaultMutableTreeNode) node;
      node = object.getUserObject();
      if (node instanceof NodeLeaf) {
        NodeLeaf leaf = (NodeLeaf) node;
        String id = leaf.getId();
        Integer x = leaf.getX();
        Integer y = leaf.getY();
        if (x == null) x = (int)(Math.random()*600);
        if (y == null) y = (int)(Math.random()*600);
        GraphNode g = new GraphNode(id, parent, leaf.toString(), x.intValue(), y.intValue());
        graphMap.put(id, g);
        leaves.add(leaf);
      }
      return;
    }
// Non leaf
    int count = model.getChildCount(node);
    parent = node.toString(); // Of zo iets
    int col = parent.indexOf(':');
    if (col < 0) parent = "";
    else parent = parent.substring(0,col);
    for(int i = 0; i < count; i++) {
      Object child = model.getChild(node, i);
      searchNodes(model, child, graphMap, parent,leaves);
    }
    
  }
}
