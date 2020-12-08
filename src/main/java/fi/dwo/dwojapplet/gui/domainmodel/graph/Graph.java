package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import fi.beans.numworxlf.JButton;
import fi.dwo.dwojapplet.gui.domainmodel.NodeLeaf;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

	final protected ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
	final protected ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();

	private JButton zoomFitButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	
	private Font buttonFont = new Font("SansSerif", Font.BOLD, 20);

	Component painter;
	
	private Point origin = new Point(0,0);
	private double factor = 1;
	
	private int startX = 0;
	private int startY = 0;
	private int pressedX = 0;
	private int pressedY = 0;
	
	private boolean isScoreGraph = false;
	private boolean modelJustSet = false;
	
	

	public Graph() {
		setLayout(null);
		setBackground(LeerdomeinGraphPanel.colorGray3);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		addMouseListener(this);
		addMouseMotionListener(this);
		painter = this;
		
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
		if(modelJustSet) {
			zoomFit();
			modelJustSet = false;
		}
		super.paintComponent(g);
		for (int i = 0; i < graphEdges.size(); i++) {
			GraphEdge edge = graphEdges.get(i);
			GraphNode source = edge.getSource();
			GraphNode target = edge.getTarget();
//			Point p = makeTempLocation(source, target);
//			if(source.getTempLocation()==null)
//				source.setTempLocation(makeTempLocation(source, target));
//			if(edge.getLength() < 600)
//				source.setTempLocation(null);
			graphEdges.get(i).paint(g, origin, factor);
		}
		for (int i = 0; i < graphNodes.size(); i++) {
			if (!graphNodes.get(i).getBlur()) {
				Rectangle rn = graphNodes.get(i).getTextBB();
				if (rn.width == 0) {
					graphNodes.get(i).paint(g, origin, factor);
					rn = graphNodes.get(i).getTextBB();
				}
				int rx = (int)(origin.x+(rn.x)*factor);
				int ry = (int)(origin.y+(rn.y)*factor);
				Rectangle r = new Rectangle(rx, ry, (int)(rn.width*factor), (int)(rn.height*factor));
				int k = 6;
				for (int j = 0; j < k; j++) {
					g.setColor(new Color(237, 239, 241, 96 + 90 / k * (k - 2 * j)));
					if (j == 0)
						g.fillRect(r.x + k / 2 - j, r.y + k / 2 - j, r.width - (k - 2 * j), r.height - (k - 2 * j));
					g.drawRect(r.x + k / 2 - j, r.y + k / 2 - j, r.width - (k - 2 * j), r.height - (k - 2 * j));
				}
			}
			graphNodes.get(i).paint(g, origin, factor);
			//graphNodes.get(i).setTempLocation(null);
		}
		//zoomFitButton.paint(g);
	}
	
	private boolean onPanel(GraphNode node) {
		Point p = node.getLocationOnPanel(origin, factor);
		if(p!=null && p.x > 0 && p.x < getWidth() && p.y > 0 && p.y < getHeight())
			return true;
		return false;
	}
	
	private Point makeTempLocation (GraphNode source, GraphNode target) {
		Point pTemp = null;
		if(source==null)
			return null;
		if(!onPanel(source) && onPanel(target)) {
			int x = 0;
			int y = 0;
			int w = getWidth();
			int h = getHeight();
			int a = source.getLocationOnPanel(origin, factor).x;
			int b = source.getLocationOnPanel(origin, factor).y;
			int c = target.getLocationOnPanel(origin, factor).x;
			int d = target.getLocationOnPanel(origin, factor).y;
			Point py0 = new Point((int)(a+(double)(0-b)/(double)(d-b)*(c-a)), 0);
			if(py0.x > 0 && py0.x < w)
				return py0;
			Point px0 = new Point(0, (int)(b+(double)(0-a)/(double)(c-a)*(d-b)));
			if(px0.y > 0 && px0.y < h)
				return px0;
			return pTemp;
		}
		
		
		return pTemp;
	}

	public void setGraphNodes(ArrayList<GraphNode> graphNodes) {
		if (graphNodes != this.graphNodes) {
			this.graphNodes.clear();
			this.graphNodes.addAll(graphNodes);
		}
	}

	public void setGraphEdges(ArrayList<GraphEdge> graphEdges) {
		if (graphEdges != this.graphEdges) {
			this.graphEdges.clear();
			this.graphEdges.addAll(graphEdges);
		}
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
	
	public void setAsScoreGraph (Boolean isScoreGraph) {
		this.isScoreGraph = isScoreGraph;
	}

	
	
//	@Override
//	public void setSize(int width, int height) {
//		super.setSize(width, height);
//		zoomFitButton.setBounds(getWidth() - 35, 5, 30, 30);
//		zoomInButton.setBounds(getWidth() - 35, 40, 30, 30);
//		zoomOutButton.setBounds(getWidth() - 35, 75, 30, 30);
//		
//		origin = new Point((int)(getWidth()/2), (int)(getHeight()/2));
//		repaint();
//	}
	
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
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - startX;
		int dy = e.getY() - startY;
		
		origin.x += dx;
		origin.y += dy;
		
		repaint();
		
		startX = e.getX();
		startY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		GraphNode mouseOverNode = null;
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).contains(ex, ey)) {
				mouseOverNode = graphNodes.get(i);
				break;
			}
		}
		if (mouseOverNode != null) {
			for (int i = 0; i < graphNodes.size(); i++) {
				if (graphNodes.get(i) != mouseOverNode) {
					graphNodes.get(i).setBlur(true);

				}
			}
			for (int i = 0; i < graphEdges.size(); i++) {
				if (graphEdges.get(i).getTarget() != mouseOverNode) {
					graphEdges.get(i).setBlur(true);
				} else {
					graphEdges.get(i).getSource().setBlur(false);
				}
			}
			repaint();
		} else {
			for (int i = 0; i < graphNodes.size(); i++) {
				graphNodes.get(i).setBlur(false);
			}
			for (int i = 0; i < graphEdges.size(); i++) {
				graphEdges.get(i).setBlur(false);
			}
			repaint();
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
//		int ex = (int) ((e.getX()-origin.x)/factor);
//		int ey = (int) ((e.getY()-origin.y)/factor);
//		GraphNode node = null;
//		for(int i=0 ; i<graphNodes.size() ; i++) {
//			if(graphNodes.get(i).contains(ex, ey)) {
//				node = graphNodes.get(i);
//				break;
//			}
//		}
//		if(node!=null)
//			produceAction(node.getID());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		
		pressedX = e.getX();
		pressedY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(Math.abs(pressedX-e.getX())>2 || Math.abs(pressedY-e.getY())>2)
			return;
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		GraphNode node = null;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(ex, ey)) {
				node = graphNodes.get(i);
				break;
			}
		}
		if(node!=null)
			produceAction(node.getID());

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
		modelJustSet = true;
//		if(getParent()!=null) {
//			zoomFit();
//		}
			
		painter.repaint();
	}

	private void searchEdges(List<NodeLeaf> leaves, Map<String, GraphNode> graphMap, ArrayList<GraphEdge> edges) {
		for (NodeLeaf leaf : leaves) {
			String dest = leaf.getId();
			GraphNode gnd = graphMap.get(dest);
			List<String> sources = leaf.getVoorkennis();
			if (sources != null)
				for (String source : sources) {
					GraphNode gns = graphMap.get(source);
					if (gns != null) {
						GraphEdge edge = new GraphEdge(gns, gnd);
						edges.add(edge);
					}
				}
		}
	}

	private void searchNodes(TreeModel model, Object node, Map<String, GraphNode> graphMap, String parent,
			List<NodeLeaf> leaves) {
		if (model.isLeaf(node)) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) node;
			node = object.getUserObject();
			if (node instanceof NodeLeaf) {
				NodeLeaf leaf = (NodeLeaf) node;
				String id = leaf.getId();
				Integer x = leaf.getX();
				Integer y = leaf.getY();
//				if (x == null)
//					x = (int) (Math.random() * 600);
//				if (y == null)
//					y = (int) (Math.random() * 600);
				if(x==null || y==null) {
					GraphNode g = new GraphNode(id, parent, leaf.toString(), null);
					graphMap.put(id, g);
					leaves.add(leaf);
				}
				else {
					GraphNode g = new GraphNode(id, parent, leaf.toString(), x.intValue(), y.intValue());
					g.setMethodeInfo(leaf.getMethode());
					graphMap.put(id, g);
					leaves.add(leaf);
				}
				//System.out.println("Methode: "+leaf.getMethode().get("Getal&Ruimte"));
			}
			return;
		}
		// Non leaf
		int count = model.getChildCount(node);
		parent = node.toString(); // Of zo iets
		int col = parent.indexOf(':');
		if (col < 0)
			parent = "";
		else
			parent = parent.substring(0, col);
		for (int i = 0; i < count; i++) {
			Object child = model.getChild(node, i);
			searchNodes(model, child, graphMap, parent, leaves);
		}
	}

	public void updateModel(TreeModel model) {
		Map<String, GraphNode> graphMap = new HashMap<>();
		Map<String, Set<String>> edgeMap = new HashMap<>();
		for (GraphEdge edge : graphEdges) {
			String source = edge.getSource().getID();
			String dest = edge.getTarget().getID();
			Set<String> sources = edgeMap.computeIfAbsent(dest, k -> new TreeSet<>());
			sources.add(source);
		}
		for (GraphNode node : graphNodes) {
			graphMap.put(node.getID(), node);
		}
		updateNodes(model, model.getRoot(), graphMap, edgeMap);
	}

	private void updateNodes(TreeModel model, Object node, Map<String, GraphNode> graphMap,
			Map<String, Set<String>> edgeMap) {
		if (model.isLeaf(node)) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) node;
			node = object.getUserObject();
			if (node instanceof NodeLeaf) {
				NodeLeaf leaf = (NodeLeaf) node;
				String id = leaf.getId();
				GraphNode gn = graphMap.get(id);
				if (gn != null && gn.getLocation()!=null) {
					leaf.setX(gn.getLocation().x);
					leaf.setY(gn.getLocation().y);
				}
				else if(gn!=null) {
					leaf.setX(null);
					leaf.setY(null);
				}
				List<String> voorkennis = leaf.getVoorkennis();
				if (voorkennis == null)
					voorkennis = new ArrayList<>();
				voorkennis.removeAll(graphMap.keySet());
				voorkennis.addAll(edgeMap.getOrDefault(id, Collections.emptySet()));
				leaf.setVoorkennis(voorkennis);
			}
			return;
		}
		int count = model.getChildCount(node);
		for (int i = 0; i < count; i++) {
			Object child = model.getChild(node, i);
			updateNodes(model, child, graphMap, edgeMap);
		}

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
			zoomFit();
		}
	}
	
	public void zoomFit() {
		if(graphNodes.size()<1)
			return;
		
		int xMax = -10000;//graphNodes.get(0).getLocation().x;
		int yMax = -10000;//graphNodes.get(0).getLocation().y;
		int xMin = 10000;//graphNodes.get(0).getLocation().x;
		int yMin = 10000;//graphNodes.get(0).getLocation().y;
		
		for (int i = 0; i < graphNodes.size(); i++) {
			if(graphNodes.get(i).getLocation()!=null) {
				if(xMax < graphNodes.get(i).getLocation().x)
					xMax = graphNodes.get(i).getLocation().x;
				if(yMax < graphNodes.get(i).getLocation().y)
					yMax = graphNodes.get(i).getLocation().y;
				if(xMin > graphNodes.get(i).getLocation().x)
					xMin = graphNodes.get(i).getLocation().x;
				if(yMin > graphNodes.get(i).getLocation().y)
					yMin = graphNodes.get(i).getLocation().y;
			}
		}
		factor = Math.min((float)(getWidth()-200)/(float)(xMax-xMin), (float)(getHeight()-80)/(float)(yMax-yMin));
		if(factor<0 || factor>1)
			factor=1;
		origin.x = 100+(int)(-xMin*factor);
		origin.y = 40+(int)(-yMin*factor);
		repaint();
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

}
