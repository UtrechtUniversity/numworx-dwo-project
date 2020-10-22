package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class EditGraph extends JPanel implements MouseListener, MouseMotionListener{

	private ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
	private ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	
	private GraphNode activeNode;
	private GraphNode possibleSourceNode;
	private GraphNode possibleTargetNode;
	
	private GraphEdge tempEdge;
	
	public EditGraph() {
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public EditGraph(Graph graph) {
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		graphNodes = graph.getGraphNodes();
		graphEdges = graph.getGraphEdges();
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
					g.setColor(new Color(255, 255, 255, 96+90/k*(k-2*j)));
					if(j==0)
						g.fillRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
					g.drawRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
						
								
				}
			}
			graphNodes.get(i).paint(g);
		}
			
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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getModifiers()== InputEvent.BUTTON3_MASK || e.isControlDown()) {
			for(int i=0 ; i<graphEdges.size() ; i++) {
				if(graphEdges.get(i).contains(e.getX(), e.getY())) {
					graphEdges.remove(graphEdges.get(i));
					break;
				}
			}
			repaint();
			return;
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(e.getX(), e.getY()))
				activeNode = graphNodes.get(i);
		}
		if(e.isShiftDown() && activeNode != null) {
			possibleSourceNode = activeNode;
			tempEdge = new GraphEdge(possibleSourceNode, new GraphNode(e.getX(),e.getY()));
			graphEdges.add(tempEdge);
			activeNode = null;
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(e.getX(), e.getY())) {
				activeNode = graphNodes.get(i);
				break;
			}
		}
		if(e.isShiftDown()) {
			possibleTargetNode = activeNode;
			if(possibleTargetNode != possibleSourceNode && possibleTargetNode != null) {
				graphEdges.add(new GraphEdge(possibleSourceNode, possibleTargetNode));
				repaint();
			}
				
		}
		activeNode = null;
		if(tempEdge!=null) {
			graphEdges.remove(tempEdge);
			tempEdge = null;
			repaint();
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(activeNode!=null) {
			activeNode.setLocation(e.getX(), e.getY());
			repaint();
		}
		else if(tempEdge!=null) {
			tempEdge.getTarget().setLocation(e.getX(), e.getY());
			repaint();
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
//		GraphNode mouseOverNode = null;
//		for(int i=0 ; i<graphNodes.size() ; i++) {
//			if(graphNodes.get(i).contains(e.getX(), e.getY())) {
//				mouseOverNode = graphNodes.get(i);
//				break;
//			}
//		}
//		if(mouseOverNode!=null && activeNode==null) {
//			for(int i=0 ; i<graphNodes.size() ; i++) {
//				if(graphNodes.get(i)!= mouseOverNode) {
//					graphNodes.get(i).setBlur(true);
//					
//				}
//			}
//			for(int i=0 ; i<graphEdges.size() ; i++) {
//				if(graphEdges.get(i).getTarget()!= mouseOverNode) {
//					graphEdges.get(i).setBlur(true);
//				}
//				else  {
//					graphEdges.get(i).getSource().setBlur(false);
//				}
//			}
//			repaint();
//		}
//		else {
//			for(int i=0 ; i<graphNodes.size() ; i++) {
//				graphNodes.get(i).setBlur(false);
//			}
//			for(int i=0 ; i<graphEdges.size() ; i++) {
//				graphEdges.get(i).setBlur(false);
//			}
//			repaint();
//		}
//		
	}
}
