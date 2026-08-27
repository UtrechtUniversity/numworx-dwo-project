package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JTree;
import fi.dwo.dwojapplet.gui.domainmodel.InvisibleNode;
import fi.dwo.dwojapplet.gui.domainmodel.InvisibleTreeModel;
import fi.dwo.dwojapplet.gui.domainmodel.NodeLeaf;
import fi.dwo.dwojapplet.gui.domainmodel.NodeVector;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.StudentModelUtil;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, ActionListener, TreeExpansionListener {

	final protected ArrayList<GNode> graphNodes = new ArrayList<GNode>();
	final protected ArrayList<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	
	protected ArrayList<ChapterGraphNode> chapterNodes = new ArrayList<ChapterGraphNode>();
	protected ArrayList<ChapterGraphEdge> chapterEdges = new ArrayList<ChapterGraphEdge>();
	
	protected ArrayList<BookGraphNode> bookNodes = new ArrayList<BookGraphNode>();
	protected ArrayList<BookGraphEdge> bookEdges = new ArrayList<BookGraphEdge>();

	private JPanel topPanel;
	private JLabel methodeLabel;
	private JLabel bookLabel;
	private JLabel chapterLabel;
	private JLabel tussenLabel1;
	private JLabel tussenLabel2;
	//private JComboBox methodeChoice;
	private JButton zoomFitButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton voorkennisButton;
	private JButton voorkennisWegButton;
//	private JButton methodeChoiceButton;
//	private PopupMenu methodeChoicePopup;
//	private MenuItem menuItemAll;

	//private MenuItem menuItemGR;
	//private MenuItem menuItemMW;
	
	private PopupMenu voorkennisPopupMenu;
    private MenuItem miVoorkennis;
    private GNode voorkennisPopupNode;
    private boolean voorkennisTree;
	
	private Font buttonFont = new Font("SansSerif", Font.BOLD, 20);
	private Font font = new Font("SansSerif", Font.BOLD, 16);

	Component painter;
	
	private Point origin = new Point(0,0);
	private double factor = 1;
	
	private int startX = 0;
	private int startY = 0;
	private int pressedX = 0;
	private int pressedY = 0;
	
	private boolean isScoreGraph = false;
	private boolean modelJustSet = false;
	
	private boolean voorkennisArea = false;
	//private boolean bookSelected = false;
	
	//private String selectedChapterTitle = "";
	//private String selectedBookTitle = "";
	
	private String selectedChapter;
	private String selectedBook;
	private String selectedMethod;
	
	private HashMap<String,String> methodeLabels = new HashMap();
    PersistenceId activeMethod;
    DomMethod activeRow;
	
	private Map<String,Map<String,Set<Integer>>> filter = Collections.emptyMap();
  private MenuItem diVoorkennis;
private String voorkennisPopupVariant;

	public Map<String,Map<String,Set<Integer>>> getFilter() { 
	  return filter;
	}
	
	
	public Graph() {
		setLayout(null);
		setBackground(LeerdomeinGraphPanel.colorGray3);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		painter = this;
		
		
		
		topPanel = new JPanel();
		topPanel.setBackground(LeerdomeinGraphPanel.colorBlue3);
		topPanel.setBounds(0, 0, getWidth(), 24);
		topPanel.setLayout(null);
		
		Box hb = Box.createHorizontalBox();
		
		tussenLabel1 = new JLabel("");
		tussenLabel1.setFont(font);
		tussenLabel1.setForeground(Color.white);
				
		methodeLabel = new JLabel("Alle leerdoelen");
		methodeLabel.setFont(font);
		methodeLabel.setForeground(Color.white);
		methodeLabel.addMouseListener(this);
		
		bookLabel = new JLabel("");
		bookLabel.setFont(font);
		bookLabel.setForeground(Color.white);
		bookLabel.addMouseListener(this);
		
		tussenLabel2 = new JLabel("");
		tussenLabel2.setFont(font);
		tussenLabel2.setForeground(Color.white);
		
		chapterLabel = new JLabel("");
		chapterLabel.setFont(font);
		chapterLabel.setForeground(Color.white);
		chapterLabel.addMouseListener(this);
		
		hb.setBounds(20,0,1000, 26);
		
//		methodeChoiceButton = new JButton("\u25be");
//		methodeChoiceButton.addActionListener(this);
//		
//		methodeChoicePopup = new PopupMenu();
//		methodeChoicePopup.setFont(new Font("SansSerif",Font.PLAIN,13));
//		
//		menuItemAll = new MenuItem("Alle leerdoelen");
//		menuItemAll.addActionListener(this);
//		methodeChoicePopup.add(menuItemAll);
//		
//		menuItemGR = new MenuItem("Geen Methode");
//		menuItemGR.setEnabled(false);
//		menuItemGR.addActionListener(this);
//		methodeChoicePopup.add(menuItemGR);
		
//		menuItemMW = new MenuItem("Moderne Wiskunde");
//        menuItemMW.addActionListener(this);
//        methodeChoicePopup.add(menuItemMW);
        
//        add(methodeChoicePopup);
        
//        methodeChoice = new JComboBox();
//		methodeChoice.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//		methodeChoice.addItem("Getal en Ruimte");
//		methodeChoice.addItem("Moderne Wiskunde");
//		//methodeChoice.addMouseListener(this);
//		//methodeChoice.addActionListener(this);
//		methodeChoice.setForeground(Color.white);
//		methodeChoice.setBackground(LeerdomeinGraphPanel.colorBlue3);
//		methodeChoice.setFont(font);
//		methodeChoice.setBounds(20, 5, 20, 24);
//		methodeChoice.setMaximumSize(new Dimension(20,24));
//		methodeChoice.setPreferredSize(new Dimension(20,24));
		
//		hb.add(methodeChoiceButton);
		hb.add(methodeLabel);
		hb.add(Box.createHorizontalStrut(40));
		hb.add(tussenLabel1);
		hb.add(Box.createHorizontalStrut(40));
		hb.add(bookLabel);
		hb.add(Box.createHorizontalStrut(40));
		hb.add(tussenLabel2);
		hb.add(Box.createHorizontalStrut(40));
		hb.add(chapterLabel);
		hb.add(Box.createHorizontalGlue());
		
		topPanel.add(hb);
		add(topPanel);
		
		
		zoomFitButton = new JButton("\u25a2");
		zoomFitButton.setBorder(BorderFactory.createEmptyBorder());
		zoomFitButton.addActionListener(this);
		zoomFitButton.setFont(buttonFont);
		zoomFitButton.setBounds(getWidth() - 35, 35, 30, 30);
		add(zoomFitButton);
		
		zoomInButton = new JButton("+");
		zoomInButton.setBorder(BorderFactory.createEmptyBorder());
		zoomInButton.addActionListener(this);
		zoomInButton.setFont(buttonFont);
		zoomInButton.setBounds(getWidth() - 35, 70, 30, 30);
		add(zoomInButton);
		
		zoomOutButton = new JButton("-");
		zoomOutButton.setBorder(BorderFactory.createEmptyBorder());
		zoomOutButton.addActionListener(this);
		zoomOutButton.setFont(buttonFont);
		zoomOutButton.setBounds(getWidth() - 35, 105, 30, 30);
		add(zoomOutButton);
		
		voorkennisButton = new JButton("Voorkennis");
		voorkennisButton.setBackground(LeerdomeinGraphPanel.colorBlue3);
		voorkennisButton.setBorder(BorderFactory.createEmptyBorder());
		voorkennisButton.addActionListener(this);
		voorkennisButton.setFont(font);
		voorkennisButton.setBounds(getWidth()/2 - 60, 30, 120, 24);
		voorkennisButton.setVisible(false);
		add(voorkennisButton);
		
		voorkennisWegButton = new JButton("Verberg voorkennis  X");
		voorkennisWegButton.setBackground(LeerdomeinGraphPanel.colorBlue3);
		voorkennisWegButton.setBorder(BorderFactory.createEmptyBorder());
		voorkennisWegButton.addActionListener(this);
		voorkennisWegButton.setFont(font);
		voorkennisWegButton.setBounds(getWidth() - 220, 1, 200, 24);
		voorkennisWegButton.setVisible(false);
		add(voorkennisWegButton);
		
		voorkennisPopupMenu = new PopupMenu();
		voorkennisPopupMenu.setFont(new Font("SansSerif",Font.PLAIN,13));
		
		miVoorkennis = new MenuItem("Toon alle voorkennis");
		miVoorkennis.addActionListener(this);
		voorkennisPopupMenu.add(miVoorkennis);

		diVoorkennis = new MenuItem("Toon directe voorkennis");
		diVoorkennis.addActionListener(this);
		voorkennisPopupMenu.add(diVoorkennis);
		
		add(voorkennisPopupMenu);
		
	}

	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(modelJustSet) {
			zoomFit();
			modelJustSet = false;
		}
		super.paintComponent(g);
		
		if(voorkennisArea) {
			g.setFont(new Font("SansSerif", Font.BOLD,16));//"SansSerif", Font.BOLD,(int)(120*factor)));
			FontMetrics fm = g.getFontMetrics();
			//g.setStroke(new BasicStroke(2f*(float)factor));
			g.setColor(LeerdomeinGraphPanel.colorBlue3);//new Color(222, 229, 240));
			g.drawRect(1, 1, getWidth()-2, getHeight()/4-20);
			g.fillRect(1, 1, getWidth()-2, 26);
			g.drawRect(1, getHeight()/4, getWidth()-2, 3*getHeight()/4-1);
			g.fillRect(1, getHeight()/4, getWidth()-2, 26);
			String label = "Voorkennis";
			if(voorkennisTree)
				label += ": "+voorkennisPopupNode.getDescription();
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(Color.white);
			g.drawString(label, getWidth()/2-textLength/2, 5*textHeight/4);//getHeight()/8+textHeight/2);
		}
		else if(voorkennisTree) {
			g.setFont(new Font("SansSerif", Font.BOLD,16));//"SansSerif", Font.BOLD,(int)(120*factor)));
			FontMetrics fm = g.getFontMetrics();
			//g.setStroke(new BasicStroke(2f*(float)factor));
			g.setColor(LeerdomeinGraphPanel.colorBlue3);//new Color(222, 229, 240));
			g.drawRect(1, 1, getWidth()-2, getHeight()-2);
			g.fillRect(1, 1, getWidth()-2, 26);
			String label = "Voorkennis: "+voorkennisPopupNode.getDescription();
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(Color.white);
			g.drawString(label, getWidth()/2-textLength/2, 5*textHeight/4);//getHeight()/8+textHeight/2);
		}
		else {
			g.setColor(LeerdomeinGraphPanel.colorBlue3);//new Color(222, 229, 240));
			g.drawRect(1, 1, getWidth()-2, getHeight()-2);
			g.fillRect(1, 1, getWidth()-2, 26);
			
		}
		if(factor<0.20) {
			for(int i=0 ; i<bookNodes.size() ; i++) {
				bookNodes.get(i).paint(g, origin, factor);
			}
			for(int i=0 ; i<bookEdges.size() ; i++) {
				bookEdges.get(i).paint(g, origin, factor);
			}
		}
		if(factor<0.05) 
			return;
		
		if(selectedChapter==null)
			for(int i=0 ; i<chapterNodes.size() ; i++) {
				chapterNodes.get(i).paint(g, origin, factor);
			}
		for(int i=0 ; i<chapterEdges.size() ; i++) {
			ChapterGraphEdge edge = chapterEdges.get(i);
			ChapterGraphNode source = edge.getSource();
			ChapterGraphNode target = edge.getTarget();
			if(source.getBookCode().equals(target.getBookCode()))
				chapterEdges.get(i).paint(g, origin, factor);
			
		}
		if(factor<0.15) {
			return;
		}
		
		for (int i = 0; i < graphEdges.size(); i++) {
			GraphEdge edge = graphEdges.get(i);
			GNode source = edge.getSource();
			GNode target = edge.getTarget();
//			Point p = makeTempLocation(source, target);
//			if(source.getTempLocation()==null)
//				source.setTempLocation(makeTempLocation(source, target));
//			if(edge.getLength() < 600)
//				source.setTempLocation(null);
			if(voorkennisArea || GraphNode.hasSameChapterCode(source, target, selectedMethod) || edge.isVoorkennisTree())
			{
				edge.paint(g, origin, factor);
				if (!edge.isVoorkennisTree()) {
				int size[] = new int[1];
				GNode s = visibleParent(source, size);
				int   ssize = size[0];
				size[0] = 0;
				GNode t = visibleParent(target, size);
				int tsize = size[0];
				int msize = Math.min(tsize, ssize);
				int maxsize = Math.max(tsize, ssize);
				if (s != null && t != null && s != t && maxsize > 0) {
					edge = new GraphEdge(s, t, msize + 1);
					edge.paint(g, origin, factor);
				}
				}
				
			}
		}
		
		
		for (int i = 0; i < graphNodes.size(); i++) {
			if (!graphNodes.get(i).getBlur() && graphNodes.get(i).isVisible() && graphNodes.get(i).getTempLocation()==null) {
			  for (String  code: graphNodes.get(i).getVisibleSet()) {
			  
			    Rectangle rn = graphNodes.get(i).getTextBB(code);
				if (rn.width == 0) {
					graphNodes.get(i).paint(g, origin, factor);
					rn = graphNodes.get(i).getTextBB(code);
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
				}}
			}
			graphNodes.get(i).paint(g, origin, factor, false);
			//graphNodes.get(i).setTempLocation(null);
		}
		//zoomFitButton.paint(g);
	}
	
	private GNode visibleParent(GNode source, int[] size) {
		while (source != null && !source.isVisible()) {
			String id = idOf(source);
			NodeVector nv = parentById.get(id);
			if (nv == null) return null;
			source = folderById.get(nv.getInfo().getId());
			size[0]++;
		}
		return source;
	}


	private boolean onPanel(GraphNode node) {
	  for (String code : node.getVisibleSet()) {
		Point p = node.getLocationOnPanel(code, origin, factor);
		if(p!=null && p.x > 0 && p.x < getWidth() && p.y > 0 && p.y < getHeight())
			return true;
	  }
	  return false;
	}
	
//	private Point makeTempLocation (GraphNode source, GraphNode target) {
//		Point pTemp = null;
//		if(source==null)
//			return null;
//		if(!onPanel(source) && onPanel(target)) {
//			int x = 0;
//			int y = 0;
//			int w = getWidth();
//			int h = getHeight();
//			String code = source.getVisibleSet().iterator().next();
//			int a = source.getLocationOnPanel(code, origin, factor).x;
//			int b = source.getLocationOnPanel(code, origin, factor).y;
//			code = target.getVisibleSet().iterator().next();
//			int c = target.getLocationOnPanel(code, origin, factor).x;
//			int d = target.getLocationOnPanel(code, origin, factor).y;
//			Point py0 = new Point((int)(a+(double)(0-b)/(double)(d-b)*(c-a)), 0);
//			if(py0.x > 0 && py0.x < w)
//				return py0;
//			Point px0 = new Point(0, (int)(b+(double)(0-a)/(double)(c-a)*(d-b)));
//			if(px0.y > 0 && px0.y < h)
//				return px0;
//			return pTemp;
//		}
//		
//		
//		return pTemp;
//	}

	public void setGraphNodes(List<GNode> list) {
		if (list != this.graphNodes) {
			this.graphNodes.clear();
			this.graphNodes.addAll(list);
			
		}
	}

	public void setGraphEdges(ArrayList<GraphEdge> graphEdges) {
		if (graphEdges != this.graphEdges) {
			this.graphEdges.clear();
			this.graphEdges.addAll(graphEdges);
			
			chapterNodes.clear();
			Map<String,ChapterGraphNode> chapters = new TreeMap<>();
			ChapterGraphNode.hfstDescriptionsMap = (MethodsProperties.instance().getDescriptionsMap(activeMethod));
			for(String hfstCodes : ChapterGraphNode.hfstDescriptionsMap.keySet() ) {
				ChapterGraphNode cn = new ChapterGraphNode(hfstCodes, graphNodes, graphEdges, activeMethod);
                chapterNodes.add(cn);
				chapters.put(hfstCodes, cn);
			}
			
			chapterEdges.clear();
			
//			for(ChapterGraphNode cSourceNode : chapterNodes ) {
//				for(ChapterGraphNode cTargetNode : chapterNodes ) {
//					if(cSourceNode!=cTargetNode && cSourceNode.getMethodCode().equals(cTargetNode.getMethodCode())) {
//						for(GraphEdge edge : graphEdges) {
//							if(edge.getSource().hasChapterCode(cSourceNode.getHfstCode()) && edge.getTarget().hasChapterCode(cTargetNode.getHfstCode()) && cSourceNode.getBookCode().compareTo(cTargetNode.getBookCode())==0) {
//								chapterEdges.add(new ChapterGraphEdge(cSourceNode, cTargetNode));
//								break;
//							}
//						}
//					}
//				}
//			}
			Set<ChapterGraphEdge> chapterEdgeSet = new HashSet<>();
			for(GraphEdge edge: graphEdges) {
			  GraphNode from = (GraphNode) edge.getSource();
			  GraphNode to   = (GraphNode) edge.getTarget();
		 	  Collection<DomStudentModelMethodInfo> fromChapters = from.getMethodeInfos();
		 	  Collection<DomStudentModelMethodInfo> orgFromChapters = fromChapters;
		 	  if (fromChapters == null) continue;
		 	  fromChapters = new ArrayList<>(fromChapters);
		 	  Collection<DomStudentModelMethodInfo> toChapters = to.getMethodeInfos();
			  if (toChapters == null) continue;
			  toChapters = new ArrayList<>(toChapters);
			  
			  
			  Iterator<DomStudentModelMethodInfo> iterator = fromChapters.iterator();
			  while(iterator.hasNext()) {
			    DomStudentModelMethodInfo info = iterator.next();
			    if (info.getMethod().equals(activeRow.key())) {
    			    for(DomStudentModelMethodInfo toc: toChapters) {
    			      if(info.getBook().equals(toc.getBook()) && info.getMethod().equals(toc.getMethod()) && info.getChapter().equals(toc.getChapter())){
    			        iterator.remove();
    			        break;
    			      }
    			    } 
    			    
			    } else {
			      iterator.remove();
			    }
			  }
// EN ook even andersom
			  iterator = toChapters.iterator();
              while(iterator.hasNext()) {                
                DomStudentModelMethodInfo info = iterator.next();
                if (info.getMethod().equals(activeRow.key())) {
                  for(DomStudentModelMethodInfo toc: orgFromChapters) {
                    if(info.getBook().equals(toc.getBook()) && info.getMethod().equals(toc.getMethod()) && info.getChapter().equals(toc.getChapter())){
                      iterator.remove();
                      break;
                    }
                  }
                } else {
                  iterator.remove();
                }
              }
			  
			  
			  
			  for (DomStudentModelMethodInfo froc: fromChapters) {
			    for (DomStudentModelMethodInfo toc: toChapters) {
			      if (froc.getBook().equals(toc.getBook()) && froc.getMethod().equals(toc.getMethod()) && ! froc.getChapter().equals(toc.getChapter())) {
			        ChapterGraphNode fn = chapters.get(froc.key());
			        ChapterGraphNode tn = chapters.get(toc.key());
//			        if (froc.getChapter().intValue() == 1) {
//			          System.out.print(froc.key());System.out.print("->");
//			          System.out.print(toc.key());
//			          System.out.println();
//			        }
			        
			        if (fn != null && tn != null)
			          chapterEdgeSet.add(new ChapterGraphEdge(fn, tn));
			      }
			      
			    }
			  }
		 	  
			}
			chapterEdges.addAll(chapterEdgeSet);
			
			
			
//			//hfst 1
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(0), chapterNodes.get(3)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(0), chapterNodes.get(6)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(0), chapterNodes.get(8)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(3), chapterNodes.get(6)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(1), chapterNodes.get(2)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(1), chapterNodes.get(4)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(1), chapterNodes.get(5)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(1), chapterNodes.get(7)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(5), chapterNodes.get(7)));
//			//hfst 2
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(9), chapterNodes.get(11)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(9), chapterNodes.get(12)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(9), chapterNodes.get(15)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(10), chapterNodes.get(13)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(10), chapterNodes.get(16)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(11), chapterNodes.get(15)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(12), chapterNodes.get(15)));
//			
//			//hfst 1-2
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(4), chapterNodes.get(9)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(7), chapterNodes.get(9)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(6), chapterNodes.get(10)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(8), chapterNodes.get(10)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(12), chapterNodes.get(13)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(4), chapterNodes.get(14)));
//			
//			//hfst 3
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(17), chapterNodes.get(21)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(17), chapterNodes.get(19)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(19), chapterNodes.get(21)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(20), chapterNodes.get(25)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(18), chapterNodes.get(23)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(21), chapterNodes.get(22)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(17), chapterNodes.get(24)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(19), chapterNodes.get(24)));
//			
//			//hfst 2-3
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(9), chapterNodes.get(22)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(11), chapterNodes.get(17)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(15), chapterNodes.get(19)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(13), chapterNodes.get(18)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(14), chapterNodes.get(20)));
//			chapterEdges.add(new ChapterGraphEdge(chapterNodes.get(16), chapterNodes.get(18)));
//			
			
			bookNodes.clear();
			BookGraphNode.bookDescriptionsMap = MethodsProperties.instance().getBookDescriptionsMap(activeMethod);
			for(String bookCode: BookGraphNode.bookDescriptionsMap.keySet()) {
				bookNodes.add(new BookGraphNode(bookCode, chapterNodes, graphEdges, activeMethod));
			}
			
			bookEdges.clear();
			int[][] edges = MethodsProperties.instance().getBookEdges(activeMethod);
			for(int[] edge : edges) {
				bookEdges.add(new BookGraphEdge(bookNodes.get(edge[0]), bookNodes.get(edge[1])));
			}
// FIXME install edges			
//			bookEdges.add(new BookGraphEdge(bookNodes.get(0), bookNodes.get(1)));
//			bookEdges.add(new BookGraphEdge(bookNodes.get(1), bookNodes.get(2)));
//          bookEdges.add(new BookGraphEdge(bookNodes.get(1), bookNodes.get(3)));
			
//			//bookEdges.add(new BookGraphEdge(bookNodes.get(0), bookNodes.get(2)));
			
			
		}
		
		
		
	}
	
	private List<List<GNode>> getVoorkennisNodes(GNode voorkennisPopupNode2, Optional<Set<String>> deselections) {
		ArrayList<GNode> startList= new ArrayList<>();
		startList.add(voorkennisPopupNode2);
		ArrayList<List<GNode>> resultList = new ArrayList<>();
		resultList.add(startList);
		List<List<GNode>> voorkennisNodes = getVoorkennisNodes(startList, resultList);
		if(deselections.isPresent()) {
			for( List<GNode> row :  voorkennisNodes) {
				Iterator<GNode> iter = row.iterator();
				while (iter.hasNext()) {
					GNode n = iter.next();
					if (deselections.get().contains(n.getID())) iter.remove();
				}
			}
		}
		return voorkennisNodes;
	}
	
	private List<List<GNode>> getVoorkennisNodes(List<GNode> graphNodes, List<List<GNode>> voorkennisNodes) {
		ArrayList<GNode> vkNodes = new ArrayList<>();
		for(GNode graphNode : graphNodes) {
			for(GraphEdge edge : graphEdges) {
				if(edge.getTarget() == graphNode) {
					if(!vkNodes.contains(edge.getSource()))
						vkNodes.add(edge.getSource());
					edge.setVoorkennisTree(true);
				}
			}
		}
		voorkennisNodes.add(vkNodes);
		if(vkNodes.size()>0)
			return(getVoorkennisNodes(vkNodes, voorkennisNodes));
		return voorkennisNodes;
	}
	
	private void cleanVoorkennisNodes(List<List<GNode>> voorkennisNodes) {
		for(int i=voorkennisNodes.size()-1 ; i>-1 ; i--) {
			List<GNode> list = voorkennisNodes.get(i);
			for(int j=0 ; j<list.size() ;  j++) {
				GNode node = list.get(j);
				for(int k=0 ; k<i ; k++) {
					List<GNode> otherList = voorkennisNodes.get(k);
					otherList.remove(node);
				}
					
			}
		}
	}
	
	private void remove(GraphNode graphNode, ArrayList<ArrayList<GraphNode>> listlist) {
		for(ArrayList<GraphNode> list: listlist)
			list.remove(graphNode);
	}
	
	private boolean containsNode(GraphNode graphNode, ArrayList<ArrayList<GraphNode>> listlist) {
		for(ArrayList<GraphNode> list: listlist)
			if(list.contains(graphNode))
				return true;
		return false;
	}
	
	/**
	 * Utility to stip a collection of ids from /... suffices.
	 * Returns a copy
	 * @param set the collection
	 * @return stripped set
	 */
	public static Set<String> strip(Collection<String> set) {
		return StudentModelUtil.strip(set);
	}
	
	private void plaatsVoorkennisTree(GNode voorkennisPopupNode2, String variant) {
		factor = 0.75;
		for(GNode gn : graphNodes) {
			gn.setVisible(false);
		}
		String id = voorkennisPopupNode2.getID();
		// find variant structures of id.
		NodeLeaf leaf = mapById.get(id);
		List<DomStudentModelVariant> variants = leaf.getInfo().getVariants();
		if (variants == null)
			variants = Collections.emptyList();
		Optional<Set<String>> deselections = variants.stream()
				.filter(v -> Objects.equals(variant, v.getName()))
				.findAny()
				.map(DomStudentModelVariant::getDeselections)
				.map(StudentModelUtil::strip); // FIXME sommige leerdoelen ids hebben een /... suffix
		
		List<List<GNode>> voorkennisNodes = getVoorkennisNodes(voorkennisPopupNode2, deselections);
		cleanVoorkennisNodes(voorkennisNodes);
		for(int i=0 ; i<voorkennisNodes.size() ; i++) {
			List<GNode> gnList = voorkennisNodes.get(i);
			for(int j=0 ; j<gnList.size() ; j++) {
				GNode gn = gnList.get(j);
				if(selectedMethod!=null)
					gn.setVisible(selectedMethod,true);
				else
					gn.setVisible(true);
				int x = -20+i%2*40 + 100 + (j+1)*(getWidth()-200)/(gnList.size()+1);
				int y = -7*gnList.size()+15*j + (voorkennisNodes.size() - (i))*(getHeight()-50)/(voorkennisNodes.size());
				gn.setTempLocation(new Point(x,y));
			}
		}
		voorkennisTree = true;
		topPanel.setBounds(0, getHeight(), getWidth(), 26);
		voorkennisWegButton.setVisible(true);
		voorkennisButton.setVisible(false);
		repaint();
		produceAction("filter");
	}
		
	

	public List<GNode> getGraphNodes() {
		return graphNodes;
	}

	public ArrayList<GraphEdge> getGraphEdges() {
		return graphEdges;
	}
	
	public ArrayList<ChapterGraphNode> getChapterNodes() {
		return chapterNodes;
	}

	public ArrayList<ChapterGraphEdge> getChapterEdges() {
		return chapterEdges;
	}

	public ArrayList<BookGraphNode> getBookNodes() {
		return bookNodes;
	}

	public ArrayList<BookGraphEdge> getBookEdges() {
		return bookEdges;
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

//	public void deselectMethode() {
//		deselectMethode(true);
//	}
//	
//	public void deselectMethode(boolean b) {
//		selectedChapter = null;
//		selectedBook = null;
//		selectedMethod = null;
//		for(int i=0 ; i<graphNodes.size() ; i++) {
//			graphNodes.get(i).setVisible(true);
//			graphNodes.get(i).setTempLocation(null);
//		}
//		if(b)
//			produceAction("filter");
//		zoomFit();
//		for(int i=0 ; i<chapterNodes.size() ; i++) {
//			chapterNodes.get(i).makeLocation(graphNodes);
//		}
//		for(int i=0 ; i<bookNodes.size() ; i++) {
//			bookNodes.get(i).makeLocation(chapterNodes);
//		}
//		verbergVoorkennis(b);
//		methodeLabel.setText("Alle leerdoelen");
//		tussenLabel1.setText("");
//		bookLabel.setText("");
//		tussenLabel2.setText("");
//		chapterLabel.setText("");
//		voorkennisButton.setVisible(false);
//	}
	

	public void selectMethode(String methodeCode) { 
		selectMethode(methodeCode, true); 
	}

	public void setMethode(String methodeCode) {
	  selectedChapter = null;
	  selectedBook = null;
	  selectedMethod = methodeCode;
	  filter = Collections.singletonMap(methodeCode, Collections.emptyMap());
	}
	
	public void selectMethode(String methodeCode, boolean b) {

	  setMethode(methodeCode);
      methodeLabel.setText(methodeLabels.get(methodeCode));
	  methodeCode = String.valueOf(methodeCode);
	  for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setTempLocation(null);
			if(!graphNodes.get(i).hasMethodCode(methodeCode))
				graphNodes.get(i).setVisible(false);
			else
				if (graphNodes.get(i) instanceof GraphNode)
					graphNodes.get(i).setVisible(methodeCode, true);
		}
		if (b) produceAction("filter");
		zoomFit();
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		for(int i=0 ; i<bookNodes.size() ; i++) {
			bookNodes.get(i).makeLocation(chapterNodes);
		}
		verbergVoorkennis(b);
		tussenLabel1.setText("");
		bookLabel.setText("");
		tussenLabel2.setText("");
		chapterLabel.setText("");
		voorkennisButton.setVisible(false);
	}
	
	public void selectBook(String bookCode) {
		selectBook(bookCode,true);
	}
	
	public void setBook(String bookCode) {
      selectedChapter = null;
      selectedBook = bookCode;
      selectedMethod = bookCode.substring(0, bookCode.indexOf("-"));
      String boek = selectedBook = bookCode.substring(bookCode.indexOf('-')+1);
      filter = Collections.singletonMap(selectedMethod, Collections.singletonMap(boek, Collections.emptySet()));
	}
	
	public void selectBook(String bookCode, boolean b) {
		verbergVoorkennis(b);
		setBook(bookCode);
		//bookSelected = true;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setTempLocation(null);
			if(!graphNodes.get(i).hasBookCode(bookCode))
				graphNodes.get(i).setVisible(false);
			else
				graphNodes.get(i).setVisible(bookCode, true);
		}
		if(b)
			produceAction("filter");
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		for(int i=0 ; i<bookNodes.size() ; i++) {
			bookNodes.get(i).makeLocation(chapterNodes);
		}
//		for(int i=0 ; i<chapterNodes.size() ; i++) {
//			if(!chapterNodes.get(i).getBookCode().equals(cNode.getBookCode()))
//				chapterNodes.get(i).setVisible(false);
//		}
		
		zoomFit();
		methodeLabel.setText(methodeLabels.get(selectedMethod));
		tussenLabel1.setText(">");
		bookLabel.setText(BookGraphNode.getBookDescription(bookCode));
		tussenLabel2.setText("");
		chapterLabel.setText("");
		voorkennisButton.setVisible(false);
	}
	
	public void selectChapter(String hfstCode) {
		selectChapter(hfstCode, true);
	}
	
	public void selectChapters(String methode, Map<String,DomStudentModelMethodInfo> filterInfo) {
		selectedChapter = null;
		selectedBook = null;
		selectedMethod = methode;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).hasChapterCode(filterInfo))
				graphNodes.get(i).setVisible(false);
			else
				graphNodes.get(i).setVisible(filterInfo, true);
		}
		zoomFit();
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		methodeLabel.setText(methode);
		tussenLabel1.setText("");
		tussenLabel2.setText("");
		chapterLabel.setText("");
		bookLabel.setText("");
		voorkennisButton.setVisible(false);
	}
	
	public void selectChapters(Map<String,DomStudentModelMethodInfo> filterInfo) {
		selectedChapter = null;
		selectedBook = null;
		selectedMethod = null;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).hasChapterCode(filterInfo))
				graphNodes.get(i).setVisible(false);
			else
				graphNodes.get(i).setVisible(filterInfo, true);
		}
		zoomFit();
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		for(int i=0 ; i<bookNodes.size() ; i++) {
			bookNodes.get(i).makeLocation(chapterNodes);
		}
		methodeLabel.setText("Alle leerdoelen");
		tussenLabel1.setText("");
		tussenLabel2.setText("");
		chapterLabel.setText("");
		bookLabel.setText("");
		voorkennisButton.setVisible(false);
	}
	
	public void selectChapters(String methode, String book, Map<String,DomStudentModelMethodInfo> filterInfo) {
		selectedChapter = null;
		selectedBook = methode + "-" + book;
		selectedMethod = methode;
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).hasChapterCode(filterInfo))
				graphNodes.get(i).setVisible(false);
			else
				graphNodes.get(i).setVisible(filterInfo, true);
		}
		zoomFit();
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		for(int i=0 ; i<bookNodes.size() ; i++) {
			bookNodes.get(i).makeLocation(chapterNodes);
		}
		methodeLabel.setText(methodeLabels.get(selectedMethod));
		tussenLabel1.setText(">");
		tussenLabel2.setText("");
		chapterLabel.setText("");
		bookLabel.setText(BookGraphNode.getBookDescription(methode + "-" + book));
		voorkennisButton.setVisible(false);
	}
			
	public void setChapter(String hfstCode) {
      selectedChapter = hfstCode;
      selectedBook = hfstCode.substring(0, hfstCode.lastIndexOf("-"));
      selectedMethod = hfstCode.substring(0, hfstCode.indexOf("-"));
      String boek = selectedBook.substring(selectedMethod.length()+1);
      String hfstk = selectedChapter.substring(selectedBook.length()+1);
      filter = Collections.singletonMap(selectedMethod, Collections.singletonMap(boek, Collections.singleton(Integer.valueOf(hfstk))));
	}
	
	
	public void selectChapter(String hfstCode, boolean b) {
	    setChapter(hfstCode);
	  
		for(int i=0 ; i<graphNodes.size() ; i++) {
			graphNodes.get(i).setVisible(false);
		}
		for(int i=0 ; i<graphNodes.size() ; i++) {
			if(!graphNodes.get(i).hasChapterCode(hfstCode))
				graphNodes.get(i).setVisible(false);
			else
				graphNodes.get(i).setVisible(hfstCode, true);
		}
		if(b)
			produceAction("filter");
		zoomFit();
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			chapterNodes.get(i).makeLocation(graphNodes);
		}
		for(int i=0 ; i<bookNodes.size() ; i++) {
			bookNodes.get(i).makeLocation(chapterNodes);
		}
		methodeLabel.setText(methodeLabels.get(selectedMethod));
		tussenLabel1.setText(">");
		tussenLabel2.setText(">");
		chapterLabel.setText(ChapterGraphNode.getChapterDescription(hfstCode));
		bookLabel.setText(BookGraphNode.getBookDescription(hfstCode.substring(0, hfstCode.lastIndexOf("-"))));
		voorkennisButton.setVisible(true);
	}
	
	public void selectVoorkennis(String hfstCode) {
		setVoorkennisArea(true);
		zoomFit(getHeight()/4);
		ArrayList<GraphNode> voorkennisNodes = getVoorkennisNodes(hfstCode);
		ArrayList<Point> pos = maakVoorkennisPosities();
		for(int i = 0 ; i<Math.min(voorkennisNodes.size(),pos.size()) ; i++) {
			GraphNode vkNode = voorkennisNodes.get(i);
			vkNode.setVisible(true);
			vkNode.setTempLocation(new Point(pos.get(i).x, pos.get(i).y));
		}
		produceAction("filter");
		voorkennisWegButton.setVisible(true);
	}
	
	public void verbergVoorkennisTree() {
		voorkennisTree = false;
		for(int i = 0 ; i<graphNodes.size() ; i++) {
			GNode node = graphNodes.get(i);
			node.setTempLocation(null);
		}
		for(int i = 0 ; i<graphEdges.size() ; i++) {
			GraphEdge edge = graphEdges.get(i);
			edge.setVoorkennisTree(false);
		}
		if(selectedChapter!=null)
			selectChapter(selectedChapter);
		else if(selectedBook!=null)
			selectBook(selectedBook);
		else if(selectedMethod!=null)
			selectMethode(selectedMethod);
		else
			selectMethode(activeRow.key());
	}
	
	public void verbergVoorkennis(boolean b) {
		setVoorkennisArea(false);
		if(selectedChapter!=null) {
			ArrayList<GraphNode> voorkennisNodes = getVoorkennisNodes(selectedChapter);
			for(int i = 0 ; i<voorkennisNodes.size() ; i++) {
				GraphNode vkNode = voorkennisNodes.get(i);
				vkNode.setVisible(false);
				vkNode.setTempLocation(null);
			}
		}
		if (b) produceAction("filter");
		voorkennisWegButton.setVisible(false);
		zoomFit();
	}
	
	public ArrayList<GraphNode> getVoorkennisNodes(String hfstCode) {
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			if(hfstCode.equals(chapterNodes.get(i).getHfstCode()))
				return chapterNodes.get(i).getVoorkennisNodes();
		}
		return new ArrayList<GraphNode>();
	}
	
		
	@Override
	public void setBounds(int x, int y, int width, int height) {
		int oldWidth = getWidth();
		int oldHeight = getHeight();
		super.setBounds(x, y, width, height);
		topPanel.setBounds(0, 0, getWidth(), 26);
		//methodeChoice.setBounds(20, 2, 20, 24);
		zoomFitButton.setBounds(getWidth() - 35, 35, 30, 30);
		zoomInButton.setBounds(getWidth() - 35, 70, 30, 30);
		zoomOutButton.setBounds(getWidth() - 35, 105, 30, 30);
		voorkennisButton.setBounds(getWidth() - 165, 35, 120, 24);
		voorkennisWegButton.setBounds(getWidth() - 200, 1, 200, 24);
		if(voorkennisArea) {
			topPanel.setBounds(0, getHeight()/4, getWidth(), 26);
			//methodeChoice.setBounds(20, getHeight()/4+2, 20, 24);
			zoomFitButton.setBounds(getWidth() - 35, getHeight()/4+35, 30, 30);
			zoomInButton.setBounds(getWidth() - 35, getHeight()/4+70, 30, 30);
			zoomOutButton.setBounds(getWidth() - 35, getHeight()/4+105, 30, 30);
		}
		if(voorkennisTree) {
			topPanel.setBounds(0, getHeight(), getWidth(), 26);
			//methodeChoice.setBounds(20, getHeight()/4+2, 20, 24);
			zoomFitButton.setBounds(getWidth() - 35, getHeight()/4+35, 30, 30);
			zoomInButton.setBounds(getWidth() - 35, getHeight()/4+70, 30, 30);
			zoomOutButton.setBounds(getWidth() - 35, getHeight()/4+105, 30, 30);
		}
		
		origin.x += (getWidth() - oldWidth)/2;
		origin.y += (getHeight() - oldHeight)/2;
		
		
		repaint();
	}
	
	public GNode findNode(int x, int y) {
		GNode node = null;
		int ex = (int) ((x-origin.x)/factor);
		int ey = (int) ((y-origin.y)/factor);
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).contains(ex, ey) || graphNodes.get(i).getTempLocation()!=null && graphNodes.get(i).contains(x, y)) {
				node = graphNodes.get(i);
				break;
			}
		}
		return node;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (crash)
			crash = false;
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
		GNode mouseOverNode = null;
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).contains(ex, ey)) {
				mouseOverNode = graphNodes.get(i);
				break;
			}
		}

		if (mouseOverNode != null) {
			{
				//blurVoorkennis(mouseOverNode);
				crash = false;
			}
		} else if (!crash) {
			for (int i = 0; i < graphNodes.size(); i++) {
				graphNodes.get(i).setBlur(false);
			}
			for (int i = 0; i < graphEdges.size(); i++) {
				graphEdges.get(i).setBlur(false);
			}
			repaint();
		}

	}


  protected void blurVoorkennis(GNode voorkennisPopupNode2) {
    for (int i = 0; i < graphNodes.size(); i++) {
    	if (graphNodes.get(i) != voorkennisPopupNode2) {
    		graphNodes.get(i).setBlur(true);

    	}
    }
    for (int i = 0; i < graphEdges.size(); i++) {
    	if (graphEdges.get(i).getTarget() != voorkennisPopupNode2) {
    		graphEdges.get(i).setBlur(true);
    	} else {
    		graphEdges.get(i).getSource().setBlur(false);
    	}
    }
    repaint();
  }

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		crash = false;
		startX = e.getX();
		startY = e.getY();
		
		if(e.getSource()==bookLabel)
			selectBook(selectedBook);
		
		else if(e.getSource()==methodeLabel)
			selectMethode(selectedMethod);
		
		if(e.getModifiers()== InputEvent.BUTTON3_MASK || e.isControlDown()) {
			voorkennisPopupNode = findNode(e.getX(),e.getY());
			if(voorkennisPopupNode!=null) {
				int ex = (int) ((e.getX()-origin.x)/factor);
				int ey = (int) ((e.getY()-origin.y)/factor);

				String code = voorkennisPopupNode.search(ex, ey);
				voorkennisPopupVariant = voorkennisPopupNode.getVariant(code);
				voorkennisPopupMenu.show(this, e.getX(), e.getY());
				return;
			}
		}
		
			//selectMethode((String)(methodeChoice.getSelectedItem()));
		
		pressedX = e.getX();
		pressedY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    if (e.getSource() == bookLabel || e.getSource() == methodeLabel) return;
		if(Math.abs(pressedX-e.getX())>2 || Math.abs(pressedY-e.getY())>2)
			return;
		int ex = (int) ((e.getX()-origin.x)/factor);
		int ey = (int) ((e.getY()-origin.y)/factor);
		GNode node = null;
		String nodeVariant = "";
		for(int i=graphNodes.size()-1 ; i>= 0 ; i--) {
			GNode tmpnode = graphNodes.get(i);
			if(tmpnode.contains(ex, ey) || tmpnode.contains(e.getX(), e.getY())) {
				node = tmpnode;
				String code = node.search(ex, ey);
				String v = node.getVariant(code);
				if (v != null) nodeVariant = "/" + v;
				break;
			}
		}
		if(node!=null) {
			produceAction(node.getID() + nodeVariant);
			return;
		}
		
		ChapterGraphNode cNode = null;
//		if(!voorkennisArea) {
//			selectedChapterTitle = "";
//		}
		for(int i=0 ; i<chapterNodes.size() ; i++) {
			if(chapterNodes.get(i).contains(ex, ey)) {
				cNode = chapterNodes.get(i);
				break;
			}
		}
		
		if(cNode!=null) {
			selectChapter(cNode.getHfstCode());
			return;
		}
		
		BookGraphNode bNode = null;
		for(int i=0 ; i<bookNodes.size() ; i++) {
			if(factor<0.2 && bookNodes.get(i).contains(ex, ey, factor)) {
				bNode = bookNodes.get(i);
				break;
			}
		}
		
		if(bNode!=null && !voorkennisArea) {
			selectBook(bNode.getBookCode());
			return;
		}

	}
	
	public ArrayList<Point> maakVoorkennisPosities() {
		ArrayList<Point> posities = new ArrayList<Point>();
		posities.add(new Point(getWidth()/2, getHeight()/8));
		
		posities.add(new Point(250, getHeight()/8-40));
		posities.add(new Point(getWidth()-250, getHeight()/8+40));
		posities.add(new Point(200, getHeight()/8+40));
		posities.add(new Point(getWidth()-200, getHeight()/8-40));
		
		posities.add(new Point(280, getHeight()/8-20));
		posities.add(new Point(getWidth()-280, getHeight()/8+20));
		posities.add(new Point(230, getHeight()/8+20));
		posities.add(new Point(getWidth()-230, getHeight()/8-20));
		
		posities.add(new Point(310, getHeight()/8-60));
		posities.add(new Point(getWidth()-310, getHeight()/8+60));
		posities.add(new Point(310, getHeight()/8+60));
		posities.add(new Point(getWidth()-310, getHeight()/8-60));
		return posities;
		
	}
	

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	Map<String, NodeLeaf> mapById;
	Map<String, FolderNode> folderById = new HashMap<>();
	Map<String, NodeVector> parentById = new HashMap<>();
	
	public void setModel(TreeModel model, Map<String, Map<String, Set<Integer>>> filter, PersistenceId activeMethod) {
	    Map<String, GraphNode> graphMap = new LinkedHashMap<>();
	    this.activeMethod = activeMethod;
	    this.activeRow = MethodsProperties.instance().getMethod(activeMethod);
	    methodeLabels.putAll(MethodsProperties.instance().stream().collect(Collectors.toMap(DomMethod::key, DomMethod::getMethod)));
	    
		List<NodeLeaf> leaves = new ArrayList<>();
		List<NodeVector> folders = new ArrayList<>();
		ArrayList<GraphEdge> edges = new ArrayList<>();
		searchNodes(model, model.getRoot(), graphMap, "", leaves, folders);
		ArrayList<GNode> values = new ArrayList<>(graphMap.values());
		folderById.clear();
		int cnt = 0;
		if (activeMethod == null)
		for(NodeVector v : folders) {
			Collection<String> ids = v.stream()
					.map(o -> (o instanceof NodeLeaf) ?((NodeLeaf) o).getId() : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			Collection<GraphNode> nodes = ids.stream().map(id -> graphMap.get(id)).filter(Objects::nonNull)
					.filter( n -> null != n.getLocation(GNode.NULLKEY))
					.collect(Collectors.toList()); 
			if (!nodes.isEmpty())
			{
				FolderNode fn = new FolderNode(nodes);
				fn.description = v.title;
				fn.ID = v.info.getId();
				fn.subdomein = "";
				folderById.put(fn.ID, fn);
				NodeVector nv = parentById.get(fn.ID);
				if (nv != null) {
					FolderNode pn = folderById.get(idOf(nv));
					if (pn != null) pn.add(fn);
					else {
						pn = new FolderNode(new ArrayList<>()); pn.add(fn);
						pn.description = nv.title;
						pn.ID = nv.info.getId();
						pn.subdomein = "";
						folderById.put(pn.ID, pn);
// pn zit niet zelf in een ppn
						cnt = grantparents(pn, cnt, values);
						values.add(cnt++,pn);
					}
				}
			    fn.expand();
				values.add(cnt++,fn);
			}
		}
		for (FolderNode fn: folderById.values()) { fn.calculateHull(GNode.NULLKEY); }
		setGraphNodes(values);
		searchEdges(leaves, graphMap, edges);
		setGraphEdges(edges);
		mapById = leaves.stream().collect(Collectors.toMap(NodeLeaf::getId, Function.identity()));
		modelJustSet = true;
		setVoorkennisArea(false);
		if(voorkennisTree)
			verbergVoorkennisTree();

		Map<String, DomStudentModelMethodInfo> filterInfo = new HashMap<>();
		if(filter!=null) {
			for (String methodeName : filter.keySet()) {
				if(methodeName!=null) {
					Map<String,Set<Integer>> leerjaren = filter.get(methodeName);
					for (String leerjaarName : leerjaren.keySet()){
						Set<Integer> hoofdstukken = 	leerjaren.get(leerjaarName);
						for (Integer i : hoofdstukken){
							final DomStudentModelMethodInfo info = new DomStudentModelMethodInfo(methodeName, leerjaarName,i);
							filterInfo.put(info.key(), info);
						}
					}
				}
			}
		}
		if (filter != null && filter.size() == 1) {
			
			String methode = filter.keySet().iterator().next();
			Map<String, Set<Integer>> methodeMap = filter.values().iterator().next();
			if(methodeMap!=null && methodeMap.size()==1) {
				String book = methodeMap.keySet().iterator().next();
				Set<Integer> bookMap = methodeMap.values().iterator().next();
				if(bookMap!=null && bookMap.size()==1) {
					Integer chapter =  bookMap.iterator().next();
					selectChapter(methode + "-" + book + "-" + chapter, false);
				}
				else if(bookMap!=null && bookMap.size()>1){
					selectChapters(methode, book, filterInfo);
				}
			}
			else if (methode != null && methodeMap.size()>1) {
					selectChapters(methode, filterInfo);
			}
        }
		else if(filter!=null && filter.size() > 1) {
			selectChapters(filterInfo);
		}
		else {
			selectMethode(activeRow.key(), false);
		}
		//System.out.println("filter: "+filter);	
		//System.out.println("filterInfo: "+filterInfo.keySet());
		painter.repaint();
	}

	private int grantparents(FolderNode fn, int cnt, ArrayList<GNode> values) {
		NodeVector nv = parentById.get(fn.ID);
		if (nv != null) {
			FolderNode pn = folderById.get(idOf(nv));
			if (pn != null) pn.add(fn);
			else {
				pn = new FolderNode(new ArrayList<>()); pn.add(fn);
				pn.description = nv.title;
				pn.ID = nv.info.getId();
				pn.subdomein = "";
				folderById.put(pn.ID, pn);
//pn zit niet zelf in een ppn
				cnt = grantparents(pn, cnt, values);
				values.add(cnt++,pn);
			}
			
		}
		return cnt;
	}


	private void searchEdges(List<NodeLeaf> leaves, Map<String, GraphNode> graphMap, ArrayList<GraphEdge> edges) {
		for (NodeLeaf leaf : leaves) {
			String dest = leaf.getId();
			GraphNode gnd = graphMap.get(dest);
			List<String> sources = leaf.getVoorkennis();
			if (sources != null)
				for (String source : sources) {
					System.out.println("source = "+ source);
				    //source = source.split("/")[0];
				    String[] sourceStrings = source.split("/");
				    source = sourceStrings[0];
				   
				    float vkfactor = 1;
				    if(sourceStrings.length>1) {
				   		System.out.println("vkFactor = "+ vkfactor);
				   		vkfactor = Float.parseFloat(sourceStrings[1]);
				    }
				    GraphNode gns = graphMap.get(source);
					if (gns != null) {
						GraphEdge edge = new GraphEdge(gns, gnd, vkfactor);
						edges.add(edge);
					}
				}
		}
	}

	private void searchNodes(TreeModel model, Object node, Map<String, GraphNode> graphMap, String parent, 	List<NodeLeaf> leaves, List<NodeVector> folders) {
		if (model.isLeaf(node)) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) node;
			boolean visible = true;
			boolean active = false;
			if (model instanceof InvisibleTreeModel) active = ((InvisibleTreeModel) model).isActivatedFilter();
			if (active && object instanceof InvisibleNode )
			  visible = ((InvisibleNode) object).isVisible();
			node = object.getUserObject();
			if (node instanceof NodeLeaf) {
				NodeLeaf leaf = (NodeLeaf) node;
				String id = leaf.getId();
				Integer x = leaf.getX();
				Integer y = leaf.getY();
                GraphNode g = new GraphNode(id, parent, leaf.toString());
                g.setMethodeInfo(leaf.getMethode());
                g.setSize(leaf.getInfo().getNodeSize());
				if(x==null || y==null) {
                    List<DomStudentModelMethodInfo> infos = leaf.getMethodeInfos();
                    g.setMethodeInfos(infos);
				} else {
					List<DomStudentModelMethodInfo> glist = new ArrayList<>();
					DomStudentModelMethodInfo nul = new DomStudentModelMethodInfo();
					nul.setX(x);
					nul.setY(y);
                    glist.add(nul);
					List<DomStudentModelMethodInfo> infos = leaf.getMethodeInfos();
                    if(infos != null) glist.addAll(infos);
                    g.setMethodeInfos(glist);
  				}
                String code = String.valueOf(activeRow.key());
                g.setVisible(code, visible);
                graphMap.put(id, g);
                leaves.add(leaf);               
				
				//System.out.println("Methode: "+leaf.getMethode().get("Getal&Ruimte"));
			}
			return;
		}
		// Non leaf
		parent = node.toString(); // Of zo iets
		int col = parent.indexOf(':');
		if (col < 0)
			parent = "";
		else
			parent = parent.substring(0, col);
		Object o = ((DefaultMutableTreeNode) node).getUserObject();
		NodeVector nv = null;
		if (o instanceof NodeVector)
		{ 
			nv = (NodeVector) o;
			folders.add(nv);
		}
		Enumeration<?> objects = ((TreeNode)node).children();
		while (objects.hasMoreElements()) {
			Object child = objects.nextElement();
			parentById.put( idOf(child), nv);
			searchNodes(model, child, graphMap, parent, leaves, folders);
		}
	}

	public static String idOf(Object child) {
		if (child instanceof DefaultMutableTreeNode)
		   child = ((DefaultMutableTreeNode) child).getUserObject();
		if (child instanceof NodeLeaf) return ((NodeLeaf) child).getId();
		if (child instanceof NodeVector) return ((NodeVector) child).getInfo().getId();
		if (child instanceof GNode) return ((GNode) child).getID();
		return null;
	}


	public void updateModel(TreeModel model) {
		Map<String, GNode> graphMap = new HashMap<>();
		Map<String, Set<String>> edgeMap = new HashMap<>();
		for (GraphEdge edge : graphEdges) {
			String source = edge.getSource().getID();
			String dest = edge.getTarget().getID();
			Set<String> sources = edgeMap.computeIfAbsent(dest, k -> new TreeSet<>());
			sources.add(source);
		}
		for (GNode node : graphNodes) {
			graphMap.put(node.getID(), node);
		}
		updateNodes(model, model.getRoot(), graphMap, edgeMap);
	}

	private void updateNodes(TreeModel model, Object node, Map<String, GNode> graphMap,
			Map<String, Set<String>> edgeMap) {
		if (model.isLeaf(node)) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) node;
			node = object.getUserObject();
			if (node instanceof NodeLeaf) {
				NodeLeaf leaf = (NodeLeaf) node;
				String id = leaf.getId();
				GNode gn = graphMap.get(id);
				if (gn != null && gn.getLocation(GraphNode.NULLKEY)!=null) {
					leaf.setX(gn.getLocation(GraphNode.NULLKEY).x);
					leaf.setY(gn.getLocation(GraphNode.NULLKEY).y);
				}
				else if(gn!=null) {
					leaf.setX(null);
					leaf.setY(null);
				}
				if (gn != null) {
					gn.mergeMethodeInfos(leaf.getMethodeInfos());
				}
                leaf.setMethodeInfos(gn != null ? gn.getMethodeInfos():null);
				List<String> voorkennis = leaf.getVoorkennis();
				if (voorkennis == null)
					voorkennis = new ArrayList<>();
				Set<String> toRemove = new TreeSet<>(graphMap.keySet());
                Set<String> toAdd = new TreeSet<>(edgeMap.getOrDefault(id, Collections.emptySet()));
                Set<String> exists = voorkennis.stream().map(s -> s.split("/")[0]).collect(Collectors.toSet());
                toRemove.removeAll(toAdd);
                toRemove.retainAll(exists);
                toAdd.removeAll(exists);
                voorkennis.removeIf(s -> toRemove.contains(s.split("/")[0]));
                voorkennis.addAll(toAdd);
				leaf.setVoorkennis(voorkennis);
			}
			return;
		}
		Enumeration<?> objects = ((TreeNode)node).children();
		while (objects.hasMoreElements()) {
			Object child = objects.nextElement();
			updateNodes(model, child, graphMap, edgeMap);
		}

	}
	
	public void setVoorkennisArea(boolean b) {
		voorkennisArea = b;
		if(b) {
			topPanel.setBounds(0, getHeight()/4, getWidth(), 26);
			//methodeChoice.setBounds(20, getHeight()/4+2, 20, 24);
			zoomFitButton.setBounds(getWidth() - 35, getHeight()/4+35, 30, 30);
			zoomInButton.setBounds(getWidth() - 35, getHeight()/4+70, 30, 30);
			zoomOutButton.setBounds(getWidth() - 35, getHeight()/4+105, 30, 30);
			repaint();
		}
		else {
			topPanel.setBounds(0, 0, getWidth(), 26);
			//methodeChoice.setBounds(20, 2, 20, 24);
			zoomFitButton.setBounds(getWidth() - 35, 35, 30, 30);
			zoomInButton.setBounds(getWidth() - 35, 70, 30, 30);
			zoomOutButton.setBounds(getWidth() - 35, 105, 30, 30);
			repaint();
		}
	}

	
	public boolean crash; // slowdown restore blurring
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==zoomInButton && factor<1) {
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
			if(voorkennisArea)
				zoomFit(getHeight()/4);
			else
				zoomFit();
		}
		if(e.getSource()==voorkennisButton) {
			voorkennisWegButton.setVisible(true);
			voorkennisButton.setVisible(false);
			selectVoorkennis(selectedChapter);
		}
		if (e.getSource() == diVoorkennis) {
		  blurVoorkennis(voorkennisPopupNode);
		  crash = true;
		}
		if(e.getSource()==voorkennisWegButton) {
			voorkennisWegButton.setVisible(false);
			voorkennisButton.setVisible(true);
			verbergVoorkennis(true);
			if(voorkennisTree)
				verbergVoorkennisTree();
		}
		if(e.getSource()==miVoorkennis) {
			if(voorkennisArea)
				verbergVoorkennis(true);
			plaatsVoorkennisTree(voorkennisPopupNode, voorkennisPopupVariant);
		}
		
	}
	
	public void zoomFit() {
		zoomFit(0);
	}
	
	public void zoomFit(int vkHeight) {
		if(graphNodes.size()<1)
			return;
		
		int xMax = -10000;//graphNodes.get(0).getLocation().x;
		int yMax = -10000;//graphNodes.get(0).getLocation().y;
		int xMin = 10000;//graphNodes.get(0).getLocation().x;
		int yMin = 10000;//graphNodes.get(0).getLocation().y;
		
		for (int i = 0; i < graphNodes.size(); i++) {
			//graphNodes.get(i).setTempLocation(null);
			GNode node = graphNodes.get(i);
            if(node.isVisible() && node.getTempLocation()==null) {
              for (String code: node.getVisibleSet()) {
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
		factor = Math.min((float)(getWidth()-240)/(float)(xMax-xMin), (float)(getHeight()-80-vkHeight)/(float)(yMax-yMin));
		if(factor<0 || factor>1)
			factor=1;
		int ruimteX = getWidth() - (int)((xMax-xMin)*factor);
		int ruimteY = getHeight() - (int)((yMax-yMin)*factor) - (int)(vkHeight);
		origin.x = ruimteX/2 + (int)(-xMin*factor);
		origin.y = (int)(vkHeight) + ruimteY/2 + (int)(-yMin*factor);
		if (voorkennisArea)
			origin.y +=10;
		repaint();
	}
	
	//ActionProducer
	private ActionListener actionListener = null;
	private boolean editMode;

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

  public boolean isFiltered() {
    return selectedMethod != null;
  }

public void syncFolderNodes(JTree tree) {
	if (graphNodes.get(0) instanceof FolderNode) {
//		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
//		Enumeration e = root.depthFirstEnumeration();
//		while (e.hasMoreElements()) {
//			DefaultMutableTreeNode object = (DefaultMutableTreeNode) e.nextElement();
//			TreePath path = new TreePath(object.getPath());
//			if (tree.isExpanded(path)) {
//				String id = ((NodeVector) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getInfo().getId();
//				FolderNode fn = folderById.get(id);
//				fn.expand();
//			} else {
//				Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
//				if (o instanceof NodeVector) {
//					String id = ((NodeVector) o).getInfo().getId();
//					FolderNode fn = folderById.get(id);
//					fn.collapse();
//				}
//			}
//						
//		}
//	}
		int cnt = tree.getRowCount();
		for (int i = 0; i < cnt; i++) {
			TreePath path = tree.getPathForRow(i);
		if (tree.isExpanded(path)) {
			String id = ((NodeVector) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getInfo().getId();
			FolderNode fn = folderById.get(id);
			if (fn != null) fn.expand();
		} else {
			Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			if (o instanceof NodeVector) {
				String id = ((NodeVector) o).getInfo().getId();
				FolderNode fn = folderById.get(id);
				if (fn != null) fn.collapse();
			}
		}
	}}
}
  
  
  
public void setModel(JTree tree, Map<String, Map<String, Set<Integer>>> filter2, PersistenceId activeMethod2) {
	TreeModel model = tree.getModel();
	setModel(model, filter2, activeMethod2);
	tree.removeTreeExpansionListener(this);
	if (activeMethod2 == null)
	{
		TreeUI ui2 = tree.getUI();
		tree.updateUI();
		tree.setUI(ui2); // Wat doet dit precies!
		tree.addTreeExpansionListener(this);
		syncFolderNodes(tree);
	}
}


@Override
public void treeExpanded(TreeExpansionEvent event) {
	if (editMode) return;	
	JTree tree = (JTree) event.getSource();
	syncFolderNodes(tree);
	TreePath path = event.getPath();
	Object o = path.getLastPathComponent();
	o = ((DefaultMutableTreeNode) o).getUserObject();
	if (o instanceof NodeVector) {
		NodeVector v = (NodeVector) o;
		String id  = v.info.getId();
		FolderNode fn = folderById.get(id);
		if (fn != null) {
			fn.expand();
			showParentPath(path);			
			repaint();
		}
	}
}


private void showParentPath(TreePath path) {
	Object o;
	NodeVector v;
	String id;
	FolderNode fn;
	path = path.getParentPath();
	if(path != null) {
		o = path.getPathComponent(0);
		v = (NodeVector) ((DefaultMutableTreeNode) o).getUserObject();
		id = v.info.getId();
		fn = folderById.get(id);
		if (fn != null)
			fn.showParent();
	}
}


@Override
public void treeCollapsed(TreeExpansionEvent event) {
	if (editMode) return;
	Object o = event.getPath().getLastPathComponent();
	o = ((DefaultMutableTreeNode) o).getUserObject();
	if (o instanceof NodeVector) {
		NodeVector v = (NodeVector) o;
		String id  = v.info.getId();
		FolderNode fn = folderById.get(id);
		if (fn != null) {
			fn.collapse();
			showParentPath(event.getPath());
			repaint();
		}
	}
}


public void setEditMode(boolean b) {
	this.editMode = b;
	if (b) {
		for (GNode n: graphNodes) {
			if (n instanceof FolderNode) 
				((FolderNode) n).expand();
		}
	}
	
}

	


}
