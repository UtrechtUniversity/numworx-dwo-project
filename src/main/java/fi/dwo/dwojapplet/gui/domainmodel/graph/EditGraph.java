package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fi.beans.numworxlf.JButton;

public class EditGraph extends JPanel implements MouseListener, MouseMotionListener, ActionListener{

	private ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
	private ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	
	private GraphNode activeNode;
	private GraphNode possibleSourceNode;
	private GraphNode possibleTargetNode;
	
	private GraphEdge tempEdge;
	
	private JButton zoomFitButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	
	private Point origin = new Point(0,0);
	private double factor = 1.0;
	
	private int startX = 0;
	private int startY = 0;
	
	private Rectangle selectieRectangle = new Rectangle(0,0,0,0);
	private boolean selectGroep = false;
	private boolean sleepGroep = false;
	
	private Font buttonFont = new Font("SansSerif", Font.BOLD, 20);
	
	public EditGraph() {
		setLayout(null);
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(LeerdomeinGraphPanel.colorBlue3));
		addMouseListener(this);
		addMouseMotionListener(this);
		makeButtons();
	}
	
	public EditGraph(Graph graph) {
		setLayout(null);
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(LeerdomeinGraphPanel.colorBlue3));
		graphNodes = graph.getGraphNodes();
		graphEdges = graph.getGraphEdges();
		addMouseListener(this);
		addMouseMotionListener(this);
		makeButtons();
	}
	
	private void makeButtons() {
		zoomFitButton = new JButton("\u25a2");
		zoomFitButton.setBorder(BorderFactory.createEmptyBorder());
		zoomFitButton.addActionListener(this);
		zoomFitButton.setFont(buttonFont);
		zoomFitButton.setBounds(getWidth() - 35, 5, 30, 30);
		add(zoomFitButton);
		
		zoomInButton = new JButton("+");
		zoomInButton.setBorder(BorderFactory.createEmptyBorder());
		zoomInButton.addActionListener(this);
		zoomInButton.setFont(buttonFont);
		zoomInButton.setBounds(getWidth() - 35, 40, 30, 30);
		add(zoomInButton);
		
		zoomOutButton = new JButton("-");
		zoomOutButton.setBorder(BorderFactory.createEmptyBorder());
		zoomOutButton.addActionListener(this);
		zoomOutButton.setFont(buttonFont);
		zoomOutButton.setBounds(getWidth() - 35, 75, 30, 30);
		add(zoomOutButton);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i=0 ; i<graphEdges.size() ; i++)
			graphEdges.get(i).paint(g, origin, factor);
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).getBlur())
			{	Rectangle rn = graphNodes.get(i).getTextBB();
				if (rn.width == 0) {
					graphNodes.get(i).paint(g, origin, factor);
					rn = graphNodes.get(i).getTextBB();
				}
				int rx = (int)(origin.x+(rn.x)*factor);
				int ry = (int)(origin.y+(rn.y)*factor);
				Rectangle r = new Rectangle(rx, ry, (int)(rn.width*factor), (int)(rn.height*factor));
				int k = 6;
				for(int j=0 ; j<k ; j++) {
					g.setColor(new Color(255, 255, 255, 96+90/k*(k-2*j)));
					if(j==0)
						g.fillRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
					g.drawRect(r.x+k/2-j, r.y+k/2-j, r.width-(k-2*j), r.height-(k-2*j));
				}
			}
			graphNodes.get(i).paint(g, origin, factor);
		}
		g.setColor(new Color(200,200,200));
		g.drawRect(selectieRectangle.x, selectieRectangle.y, selectieRectangle.width, selectieRectangle.height);
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
	
	public double getFactor() {
		return factor;
	}
	
	public Point getOrigin() {
		return origin;
	}
	
	public void setFactor (double factor) {
		this.factor = factor;
	}
	
	public void setOrigin(Point origin) {
		this.origin = origin;
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		int oldWidth = getWidth();
		int oldHeight = getHeight();
		super.setBounds(x, y, width, height);
		zoomFitButton.setBounds(getWidth() - 35, 5, 30, 30);
		zoomInButton.setBounds(getWidth() - 35, 40, 30, 30);
		zoomOutButton.setBounds(getWidth() - 35, 75, 30, 30);
		
		origin.x += (getWidth() - oldWidth)/2;
		origin.y += (getHeight() - oldHeight)/2;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		if(e.getModifiers()== InputEvent.BUTTON3_MASK || e.isControlDown()) {
			for(int i=0 ; i<graphEdges.size() ; i++) {
				if(graphEdges.get(i).contains(ex, ey)) {
					graphEdges.remove(graphEdges.get(i));
					break;
				}
			}
			repaint();
			return;
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(ex, ey)) {
				activeNode = graphNodes.get(i);
				activeNode.setSelected(true);
			}
		}
		if(e.isShiftDown() && activeNode != null) {
			possibleSourceNode = activeNode;
			tempEdge = new GraphEdge(possibleSourceNode, new GraphNode(ex,ey));
			graphEdges.add(tempEdge);
			activeNode = null;
		}
		else if(e.isShiftDown() && activeNode == null) {
			selectGroep = true;
			selectieRectangle.setLocation(startX, startY);
			activeNode = null;
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(ex, ey)) {
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
		if(activeNode!=null) {
			activeNode.setSelected(false);
			activeNode = null;
		}
		if(tempEdge!=null) {
			graphEdges.remove(tempEdge);
			tempEdge = null;
			repaint();
		}
		if(sleepGroep) {
			sleepGroep = false;
			for(int i=0 ; i<graphNodes.size() ; i++) {
				graphNodes.get(i).setSelected(false);
			}
		}
		if(selectGroep) {
			for(int i=0 ; i<graphNodes.size() ; i++) {
				
				if(selectieRectangle.contains(graphNodes.get(i).getLocationOnPanel(origin, factor)))
					graphNodes.get(i).setSelected(true);	
				else
					graphNodes.get(i).setSelected(false);
			}
			selectieRectangle = new Rectangle(0,0,0,0);
			repaint();
			selectGroep = false;
			sleepGroep = true;
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
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		
		int estartx = (int) ((startX-origin.x)/factor);
		int estarty = (int) ((startY-origin.y)/factor);
		
		if(e.isShiftDown() && selectGroep) {
			selectieRectangle.setSize(e.getX()-selectieRectangle.x, e.getY()-selectieRectangle.y);
			repaint();
			return;
		}
		if(sleepGroep) {
			int dx = (int)((e.getX() - startX)/factor);
			int dy = (int)((e.getY() - startY)/factor);
			for(int i=0 ; i<graphNodes.size() ; i++) {
				if(graphNodes.get(i).isSelected())
					graphNodes.get(i).setLocation(graphNodes.get(i).getLocation().x + dx, graphNodes.get(i).getLocation().y + dy);	
			}
			repaint();
			startX = e.getX();
			startY = e.getY();
			return;
		}
		if(activeNode!=null) {
			activeNode.setLocation(ex, ey);
			repaint();
		}
		else if(tempEdge!=null) {
			tempEdge.getTarget().setLocation(ex, ey);
			repaint();
		}
		else {
			int dx = e.getX() - startX;
			int dy = e.getY() - startY;
			
			origin.x += dx;
			origin.y += dy;
			
			repaint();
			
			startX = e.getX();
			startY = e.getY();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==zoomInButton) {
			
			factor = 1.2*factor;
			origin.x = (int)(getWidth()/2 + (origin.x - getWidth()/2)*1.2);
			origin.y = (int)(getHeight()/2 + (origin.y - getHeight()/2)*1.2);
			repaint();
		}
		if(e.getSource()==zoomOutButton) {
			factor = factor/1.2;
			origin.x = (int)(getWidth()/2 + (origin.x - getWidth()/2)/1.2);
			origin.y = (int)(getHeight()/2 + (origin.y - getHeight()/2)/1.2);
			repaint();
		}
		if(e.getSource()==zoomFitButton) {
			if(graphNodes.size()<1)
				return;
			int xMax = graphNodes.get(0).getLocation().x;
			int yMax = graphNodes.get(0).getLocation().y;
			int xMin = graphNodes.get(0).getLocation().x;
			int yMin = graphNodes.get(0).getLocation().y;
			for (int i = 0; i < graphNodes.size(); i++) {
				if(xMax < graphNodes.get(i).getLocation().x)
					xMax = graphNodes.get(i).getLocation().x;
				if(yMax < graphNodes.get(i).getLocation().y)
					yMax = graphNodes.get(i).getLocation().y;
				if(xMin > graphNodes.get(i).getLocation().x)
					xMin = graphNodes.get(i).getLocation().x;
				if(yMin > graphNodes.get(i).getLocation().y)
					yMin = graphNodes.get(i).getLocation().y;
			}
			factor = Math.min((float)(getWidth()-200)/(float)(xMax-xMin), (float)(getHeight()-80)/(float)(yMax-yMin));
			origin.x = 100+(int)(-xMin*factor);
			origin.y = 40+(int)(-yMin*factor);
			repaint();
		}
	}
}
