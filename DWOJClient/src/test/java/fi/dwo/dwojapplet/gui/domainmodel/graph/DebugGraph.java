package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.util.ArrayList;

public class DebugGraph extends Graph {

  public DebugGraph() {
    makeExample();
    
    setAsScoreGraph(true);
    graphNodes.get(12).setSuccesFailScore(100.0);
    graphNodes.get(7).setSuccesFailScore(100.0);
    graphNodes.get(6).setSuccesFailScore(100.0);
    graphNodes.get(9).setSuccesFailScore(100.0);
    graphNodes.get(5).setSuccesFailScore(100.0);
    graphNodes.get(19).setSuccesFailScore(100.0);
    graphNodes.get(15).setSuccesFailScore(100.0);
    graphNodes.get(14).setSuccesFailScore(100.0);
    graphNodes.get(28).setSuccesFailScore(0.0);
    graphNodes.get(23).setSuccesFailScore(0.0);
    graphNodes.get(25).setSuccesFailScore(0.0);
  }
  private ArrayList<GraphNode> graphNodes = new ArrayList<>();
  
  public void makeExample() {
  	graphNodes.add(new GraphNode("0", "", "", -1000,-1000));
  	graphNodes.add(new GraphNode("1", "D1.2a", "Roosterpapier", -769, 190));
  	graphNodes.add(new GraphNode("2", "D1.2b", "Geodriehoek", -378, 507));
  	graphNodes.add(new GraphNode("3", "D2.2.2a", "Uitslag", 7, -71));
  	graphNodes.add(new GraphNode("4", "D2.2.2b", "Kijklijnen gebruiken", -833, 451));
  	graphNodes.add(new GraphNode("5", "D2.3.1a", "Vlakke figuren", 291, 638));
  	graphNodes.add(new GraphNode("6", "D2.3.1b", "Veelhoek", 142, 486));
  	graphNodes.add(new GraphNode("7", "D2.3.1c", "Vierkant, rechthoek",-129, 331));
  	graphNodes.add(new GraphNode("8", "D2.3.1d", "Cirkel", 486, 480));
  	graphNodes.add(new GraphNode("9", "D2.3.2a", "Ruimtefiguur", 744,524));
  	graphNodes.add(new GraphNode("10", "D2.3.2b", "Balk, kubus", 75, 138));
  	graphNodes.add(new GraphNode("11", "D2.3.2c", "Kubusbouwsel", 687, 85));
  	graphNodes.add(new GraphNode("12", "D2.3.2d", "Prisma", 292, 44));
  	graphNodes.add(new GraphNode("13", "D2.3.2e", "Piramide", 473, -12));
  	graphNodes.add(new GraphNode("14", "D2.4.1a", "Halve lijn, lijn, lijnstuk", -392, 703));
  	graphNodes.add(new GraphNode("15", "D2.4.1b", "Snijdende/evenwijdige lijnen", -315, 603));
  	graphNodes.add(new GraphNode("16", "D2.4.1c", "Kijklijn", -765, 589));
  	graphNodes.add(new GraphNode("17", "D2.4.1d", "Loodlijn", -358, 375));
  	graphNodes.add(new GraphNode("18", "D2.4.1e", "Diagonaal", 291,421));
  	graphNodes.add(new GraphNode("19", "D2.4.2a","Rechte hoek", -252, 452));
  	graphNodes.add(new GraphNode("20", "D2.5.1a", "Loodlijn tekenen", -534, 262));
  	graphNodes.add(new GraphNode("21", "D2.5.1b", "Evenwijdige lijnen tekenen", -683,340));
  	graphNodes.add(new GraphNode("22", "D2.5.1c", "Cirkel tekenen", 311, 326));
  	graphNodes.add(new GraphNode("23", "D2.5.1d", "Driehoek met gegeven zijden tekenen", -365, 34));
  	graphNodes.add(new GraphNode("24", "D2.5.2a", "Uitslag balk tekenen", -673,-334));
  	graphNodes.add(new GraphNode("25", "D2.5.2b", "Uitslag piramide tekenen",-338,-488));
  	graphNodes.add(new GraphNode("26", "D2.5.2d", "Tekening kubus of balk afmaken", -535, -124));
  	graphNodes.add(new GraphNode("27", "D2.5.2e", "Tekening piramide afmaken",-220, -257));
  	graphNodes.add(new GraphNode("28", "D2.5.2f", "Tekening prisma afmaken", 87, -337));
  	
  	setGraphNodes(transformFromGephi(graphNodes));
  	
  	graphEdges.add(new GraphEdge(graphNodes.get(2), graphNodes.get(20)));
  	graphEdges.add(new GraphEdge(graphNodes.get(2), graphNodes.get(21)));
  	graphEdges.add(new GraphEdge(graphNodes.get(14), graphNodes.get(16)));
  	graphEdges.add(new GraphEdge(graphNodes.get(14), graphNodes.get(15)));
  	graphEdges.add(new GraphEdge(graphNodes.get(19), graphNodes.get(17)));
  	graphEdges.add(new GraphEdge(graphNodes.get(17), graphNodes.get(20)));
  	graphEdges.add(new GraphEdge(graphNodes.get(15), graphNodes.get(19)));
  	graphEdges.add(new GraphEdge(graphNodes.get(5), graphNodes.get(8)));
  	graphEdges.add(new GraphEdge(graphNodes.get(8), graphNodes.get(22)));
  	graphEdges.add(new GraphEdge(graphNodes.get(5), graphNodes.get(6)));
  	graphEdges.add(new GraphEdge(graphNodes.get(6), graphNodes.get(7)));
  	graphEdges.add(new GraphEdge(graphNodes.get(19), graphNodes.get(7)));
  	graphEdges.add(new GraphEdge(graphNodes.get(9), graphNodes.get(10)));
  	graphEdges.add(new GraphEdge(graphNodes.get(7), graphNodes.get(10)));
  	graphEdges.add(new GraphEdge(graphNodes.get(22), graphNodes.get(23)));
  	graphEdges.add(new GraphEdge(graphNodes.get(6), graphNodes.get(23)));
  	graphEdges.add(new GraphEdge(graphNodes.get(10), graphNodes.get(11)));
  	graphEdges.add(new GraphEdge(graphNodes.get(10), graphNodes.get(24)));
  	graphEdges.add(new GraphEdge(graphNodes.get(9), graphNodes.get(12)));
  	graphEdges.add(new GraphEdge(graphNodes.get(9), graphNodes.get(13)));
  	graphEdges.add(new GraphEdge(graphNodes.get(16), graphNodes.get(4)));
  	graphEdges.add(new GraphEdge(graphNodes.get(6), graphNodes.get(18)));
  	graphEdges.add(new GraphEdge(graphNodes.get(10), graphNodes.get(26)));
  	graphEdges.add(new GraphEdge(graphNodes.get(13), graphNodes.get(25)));
  	graphEdges.add(new GraphEdge(graphNodes.get(13), graphNodes.get(27)));
  	graphEdges.add(new GraphEdge(graphNodes.get(12), graphNodes.get(28)));
  	graphEdges.add(new GraphEdge(graphNodes.get(12), graphNodes.get(3)));
  	graphEdges.add(new GraphEdge(graphNodes.get(13), graphNodes.get(3)));
  	graphEdges.add(new GraphEdge(graphNodes.get(10), graphNodes.get(3)));
  	graphEdges.add(new GraphEdge(graphNodes.get(3), graphNodes.get(24)));
  	graphEdges.add(new GraphEdge(graphNodes.get(3), graphNodes.get(25)));
  	graphEdges.add(new GraphEdge(graphNodes.get(6), graphNodes.get(12)));
  	graphEdges.add(new GraphEdge(graphNodes.get(6), graphNodes.get(13)));
  	graphEdges.add(new GraphEdge(graphNodes.get(7), graphNodes.get(12)));
  	graphEdges.add(new GraphEdge(graphNodes.get(1), graphNodes.get(26)));
  	graphEdges.add(new GraphEdge(graphNodes.get(1), graphNodes.get(27)));
  	graphEdges.add(new GraphEdge(graphNodes.get(1), graphNodes.get(28)));
  	graphEdges.add(new GraphEdge(graphNodes.get(23), graphNodes.get(25)));
  	graphEdges.add(new GraphEdge(graphNodes.get(15), graphNodes.get(21)));
  }

  public ArrayList<GNode> transformFromGephi(ArrayList<GraphNode> graphNodes) {
  	ArrayList<GNode> graphNodesNew = new ArrayList<>();
  	for(int i=0 ; i<graphNodes.size() ; i++) {
  		GraphNode gn = graphNodes.get(i);
  		double factor = 0.5;
  		int x = (int)(factor*(gn.getLocation(GraphNode.NULLKEY).x+1200));
  		int y = (int)(factor*(-gn.getLocation(GraphNode.NULLKEY).y+900));
  		graphNodesNew.add(new GraphNode(gn.getID(), gn.getSubdomein(), gn.getDescription(), x, y));
  	}
  	return graphNodesNew;
  }

}
