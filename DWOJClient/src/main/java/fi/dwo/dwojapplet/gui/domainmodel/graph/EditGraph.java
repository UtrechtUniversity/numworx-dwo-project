package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import fi.beans.numworxlf.JButton;

public class EditGraph extends JPanel implements MouseListener, MouseMotionListener, ActionListener{

	private List<GNode> graphNodes = new ArrayList<>();
	private ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	
	protected ArrayList<ChapterGraphNode> chapterNodes = new ArrayList<ChapterGraphNode>();
	protected ArrayList<ChapterGraphEdge> chapterEdges = new ArrayList<ChapterGraphEdge>();
	
	protected ArrayList<BookGraphNode> bookNodes = new ArrayList<BookGraphNode>();
	protected ArrayList<BookGraphEdge> bookEdges = new ArrayList<BookGraphEdge>();
	
	private GNode activeNode;
	private String activeCode;
	private GNode possibleSourceNode;
	private GNode possibleTargetNode;
	
	private GraphEdge tempEdge;
	
	private JButton zoomFitButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	
	private Point origin = new Point(0,0);
	private double factor = 1.0;
	
	private int startX = 0;
	private int startY = 0;
	
	private int pressedX = 0;
	private int pressedY = 0;
	
	private Rectangle selectieRectangle = new Rectangle(0,0,0,0);
	private boolean selectGroep = false;
	private boolean sleepGroep = false;
	
	private Font buttonFont = new Font("SansSerif", Font.BOLD, 20);
	
	private PopupMenu popup;
    private MenuItem miRemove;
    private GNode editPopupNode;
    
    public boolean modelJustSet = false;
    
    Graph graph;

    public EditGraph() {
		setLayout(null);
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(LeerdomeinGraphPanel.colorBlue3));
		addMouseListener(this);
		addMouseMotionListener(this);
		makeButtons();
		
		popup = new PopupMenu();
        popup.setFont(new Font("SansSerif",Font.PLAIN,13));
        
        miRemove = new MenuItem("Remove");
        miRemove.addActionListener(this);
        popup.add(miRemove);
        add(popup);
        ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	public EditGraph(Graph graph) {
		setLayout(null);
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(LeerdomeinGraphPanel.colorBlue3));
		this.graph = graph;
		graphNodes = graph.getGraphNodes();
		graphEdges = graph.getGraphEdges();
		chapterNodes = graph.getChapterNodes();
		chapterEdges = graph.getChapterEdges();
		bookNodes = graph.getBookNodes();
		bookEdges = graph.getBookEdges();
		origin = graph.getOrigin();
		factor = graph.getFactor();
		addMouseListener(this);
		addMouseMotionListener(this);
		makeButtons();
		
		popup = new PopupMenu();
        popup.setFont(new Font("SansSerif",Font.PLAIN,13));
        
        miRemove = new MenuItem("Remove from graph");
        miRemove.addActionListener(this);
        popup.add(miRemove);
        add(popup);
        ToolTipManager.sharedInstance().registerComponent(this);
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
		if(modelJustSet) {
			zoomFit();
			modelJustSet = false;
		}
		
		if(factor<0.15) {
			for(int i=0 ; i<bookNodes.size() ; i++) {
				bookNodes.get(i).paint(g, origin, factor, true);
			}
			for(int i=0 ; i<bookEdges.size() ; i++) {
				bookEdges.get(i).paint(g, origin, factor, true);
			}
		}
		if(factor<0.05) 
			return;
		
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).paint(g, origin, factor, true);
		}
		for(int i=0 ; i<chapterEdges.size() ; i++) {
			chapterEdges.get(i).paint(g, origin, factor, true);
		}
		
		if(factor<0.15)
			return;
		
		for(int i=0 ; i<graphEdges.size() ; i++)
			graphEdges.get(i).paint(g, origin, factor);
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).getBlur() && graphNodes.get(i).isVisible())
			{	
			  
			  for (String code: graphNodes.get(i).getVisibleSet()) {
			    Rectangle rn = graphNodes.get(i).getTextBB(code);
				if (rn.width == 0) {
					graphNodes.get(i).paint(g, origin, factor);
					rn = graphNodes.get(i).getTextBB(code);
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
				}}
			}
			graphNodes.get(i).paint(g, origin, factor);
		}
		g.setColor(new Color(200,200,200));
		g.drawRect(selectieRectangle.x, selectieRectangle.y, selectieRectangle.width, selectieRectangle.height);
	}
	
	public void setModelJustSet(boolean b) {
		modelJustSet = b;
	}
	
	
	public void setGraphNodes(List<GNode> graphNodes) {
		this.graphNodes = graphNodes;
	}
	
	public void setGraphEdges(ArrayList<GraphEdge> graphEdges) {
		this.graphEdges = graphEdges;
	}
	
	public List<GNode> getGraphNodes() {
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
	
	public GNode findNode(int x, int y) {
		GNode node = null;
		int ex = (int) ((x-origin.x)/factor);
		int ey = (int) ((y-origin.y)/factor);
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).contains(ex, ey)) {
				node = graphNodes.get(i);
				break;
			}
		}
		return node;
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
		
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		if(e.getModifiers()== InputEvent.BUTTON3_MASK || e.isControlDown()) {
			for(int i=0 ; i<graphEdges.size() ; i++) {
				if(graphEdges.get(i).contains(ex, ey)) {
					graphEdges.remove(graphEdges.get(i));
					repaint();
					return;
				}
			}
			
			editPopupNode = findNode(e.getX(),e.getY());
			
			if(editPopupNode!=null) {
			    editPopupNode.selectAround(ex, ey);		  
				popup.show(this, e.getX(), e.getY());
				return;
			}
			
		}
		setToolTipText(null);
		for(int i=0 ; i<graphNodes.size() ; i++) {
			//graphNodes.get(i).setSelected(false);
			if(graphNodes.get(i).contains(ex, ey) && graphNodes.get(i).isVisible()) {
				activeNode = graphNodes.get(i);
				activeNode.selectInside(new Rectangle(e.getX()-(int)(8*factor), e.getY()-(int)(8*factor), (int)(16*factor), (int)(16*factor)), origin, factor);
				activeCode = activeNode.search(ex,ey);
                String variant = activeNode.getVariant(activeCode);
                String tooltip = activeCode;
                if (activeCode != null && variant != null) {
            	   tooltip += "/" + variant;
                }
				setToolTipText(tooltip);
				break;
			}
		}
		if(e.isShiftDown() && activeNode instanceof GraphNode) {
			possibleSourceNode = activeNode;
			GraphNode helpNode = new GraphNode(ex,ey);
			helpNode.setVisible(true);
			helpNode.setSelected(true);
			tempEdge = new GraphEdge((GraphNode) possibleSourceNode, helpNode);
			graphEdges.add(tempEdge);
			activeNode = null;
		}
		else if(e.isShiftDown() && activeNode == null) {
			selectGroep = true;
			selectieRectangle.setLocation(startX, startY);
			activeNode = null;
		}
		repaint();
		
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
			if(possibleTargetNode != possibleSourceNode && possibleTargetNode instanceof GraphNode) {
				graphEdges.add(new GraphEdge((GraphNode) possibleSourceNode, (GraphNode) possibleTargetNode));
				repaint();
			}
		}
		if(activeNode!=null) {
			activeNode.setSelectionOnGrid();
			activeNode.setSelected(false);
			repaint();
			activeNode = null;
			activeCode = null;
		}
		if(tempEdge!=null) {
			graphEdges.remove(tempEdge);
			tempEdge = null;
			repaint();
		}
		if(sleepGroep) {
			sleepGroep = false;
			for(int i=0 ; i<graphNodes.size() ; i++) {
				if(graphNodes.get(i).isSelected()) {
					graphNodes.get(i).setSelectionOnGrid();
					graphNodes.get(i).setSelected(false);
				}	
			}
			repaint();
		}
		if(selectGroep) {
			for(int i=0 ; i<graphNodes.size() ; i++) {
				
//				if(graphNodes.get(i).getLocationOnPanel(origin, factor)!=null && graphNodes.get(i).inside(selectieRectangle,origin, factor))
//					graphNodes.get(i).setSelected(true);	
//				else
//					graphNodes.get(i).setSelected(false);
			    GNode node = graphNodes.get(i);
			    if (node.isVisible()) {
			      node.selectInside(selectieRectangle, origin, factor);
			    }
			
			}
			selectieRectangle = new Rectangle(0,0,0,0);
			repaint();
			selectGroep = false;
			sleepGroep = true;
		}
		
		if(Math.abs(pressedX-e.getX())>2 || Math.abs(pressedY-e.getY())>2)
			return;
		GNode node = null;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(graphNodes.get(i).contains(ex, ey) && graphNodes.get(i).isVisible()) {
				node = graphNodes.get(i);
				break;
			}
		}
		if(node!=null) {
			produceAction(node.getID());
			return;
		}
		
		ChapterGraphNode cNode = null;
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			if(chapterNodes.get(i).contains(ex, ey)) {
				cNode = chapterNodes.get(i);
				break;
			}
		}
		if(cNode!=null) {
		    graph.setChapter(cNode.getHfstCode());
//			for(int i=0 ; i<graphNodes.size() ; i++) {
//				if(!graphNodes.get(i).hasChapterCode(cNode.getHfstCode()))
//					graphNodes.get(i).setVisible(false);
//			}
			for(int i=0 ; i<graphNodes.size() ; i++) {
				graphNodes.get(i).setVisible(false);
			}
			for(int i=0 ; i<graphNodes.size() ; i++) {
				if(!graphNodes.get(i).hasChapterCode(cNode.getHfstCode()))
					graphNodes.get(i).setVisible(false);
				else
					graphNodes.get(i).setVisible(cNode.getHfstCode(), true);
			}
			produceAction("filter");
			zoomFit();
			
			for(int i=0 ; i<chapterNodes.size() ; i++) {
				chapterNodes.get(i).makeLocation(graphNodes);
			}
			for(int i=0 ; i<bookNodes.size() ; i++) {
				bookNodes.get(i).makeLocation(chapterNodes);
			}
//			for(int i=0 ; i<graphNodes.size() ; i++) {
//				graphNodes.get(i).setVisible(true);
//			}
//			for(int i=0 ; i<chapterNodes.size() ; i++) {
//				chapterNodes.get(i).makeLocation(graphNodes);
//			}
			
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
					graphNodes.get(i).move( dx,  dy);	
			}
			repaint();
			startX = e.getX();
			startY = e.getY();
			return;
		}
		if(activeNode!=null) {
			activeNode.setLocation(activeCode, ex, ey);
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
      int ex = (int) ((e.getX()-origin.x)/factor);
      int ey = (int) ((e.getY()-origin.y)/factor);
      boolean set = false;
	     for(int i=0 ; i<graphNodes.size() ; i++) {
           if(graphNodes.get(i).contains(ex, ey) && graphNodes.get(i).isVisible()) {
               GNode activeNode = graphNodes.get(i);
               //activeNode.setSelected(true);
               String activeCode = activeNode.search(ex,ey);
               String variant = activeNode.getVariant(activeCode);
               if (activeCode != null && variant != null) {
            	   activeCode += "/" + variant;
               }
               setToolTipText(activeCode); set = true;
               break;
           }
       }
	   if (!set) setToolTipText(null);

	  
	  
	  
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
//			for(int i=0 ; i<graphNodes.size() ; i++) {
//				graphNodes.get(i).setVisible(true);
//			}
			zoomFit();
//			for(int i=0 ; i<chapterNodes.size() ; i++) {
//				chapterNodes.get(i).makeLocation(graphNodes);
//			}
		}
		if(e.getSource()==miRemove) {
			editPopupNode.setLocation(null);
			editPopupNode = null;
			repaint();
		}
	}
	
	public void removeTempLocations() {
		for (int i = 0; i < graphNodes.size(); i++) {
			graphNodes.get(i).setTempLocation(null);
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
			GNode node = graphNodes.get(i);
            if(node.isVisible()) {
              for(String code: node.getVisibleSet()) {
				Point location = node.getLocation(code);
				if (location == null) continue;
                if(xMax < location.x)
					xMax = location.x;
				if(yMax < location.y)
					yMax = location.y;
				if(xMin > location.x)
					xMin = location.x;
				if(yMin > location.y)
					yMin = location.y;
              }
			}
		}
		factor = Math.min((float)(getWidth()-240)/(float)(xMax-xMin), (float)(getHeight()-80)/(float)(yMax-yMin));
		if(factor<0 || factor>1)
			factor=1;
		int ruimteX = getWidth() - (int)((xMax-xMin)*factor);
		int ruimteY = getHeight() - (int)((yMax-yMin)*factor);
		origin.x = ruimteX/2 + (int)(-xMin*factor);
		origin.y = ruimteY/2 + (int)(-yMin*factor);
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
