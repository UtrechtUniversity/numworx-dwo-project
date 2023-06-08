package fi.dwo.dwojapplet.gui.domainmodel.graph;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

public class GraphNodeTest {

  private GraphNode graph;

  @Before
  public void setUp() throws Exception {
     graph = new GraphNode(0, 0);
     Map<String, Map<String, Set<Integer>>> map = new TreeMap<String, Map<String,Set<Integer>>>();
     Map<String, Set<Integer>> mapa = new TreeMap<>();
     mapa.put("a", Collections.singleton(1));
     mapa.put("b", Collections.singleton(2));
     map.put("a", mapa);
     Map<String, Set<Integer>> mapb;
     mapb = new HashMap<String, Set<Integer>>();
     mapb.put("b", Collections.singleton(2));
     map.put("b", mapb);
     graph.setMethodeInfo(map);
  }

  @Test
  public void testSetMethodeInfos() {
    DomStudentModelMethodInfo a,b;
    a = new DomStudentModelMethodInfo("a", "a", 1);
    b = new DomStudentModelMethodInfo("a", "b", 2);
    a.setX(123);
    a.setY(321);
    graph.setMethodeInfos(Arrays.asList(a,b));
    assertEquals(123, b.getX().intValue());
    assertEquals(321, b.getY().intValue());
    a = new DomStudentModelMethodInfo("a", "a", 1);
    graph.setMethodeInfos(Arrays.asList(a,b));
    assertEquals(123, a.getX().intValue());
    assertEquals(321, a.getY().intValue());
   
    
    b = new DomStudentModelMethodInfo("b", "b", 2);
    graph.setMethodeInfos(Arrays.asList(a,b));
    assertNull(b.getX());
  
  }

}
