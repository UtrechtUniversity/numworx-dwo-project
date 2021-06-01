package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import fi.beans.numworxlf.JCheckBox;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class FilterPanel extends JPanel {

  MethodeAction mw = new AnyMethodAction().init(2);
  MethodeAction genr = new AnyMethodAction().init(1);
  
  AnyMethodAction m;
  JCheckBox rest = new JCheckBox("Niet geclassificeerde leerdoelen");
  

  KoppelingGRPanel genrtab = genr.getTab();
  KoppelingGRPanel mwtab = mw.getTab();
  KoppelingGRPanel mtab;

  
  
  FilterPanel(PersistenceId activeMethod) {
    super(null);
    BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
    setLayout(layout);
    Border margin = BorderFactory.createEmptyBorder(0, 20, 0, 0);
    JLabel l;
    if (activeMethod != null) {
      m = new AnyMethodAction();
      m.setMethode(MethodsProperties.instance().getMethod(activeMethod));
      l = new JLabel(m.getName());
      l.setBorder(margin);
      add(l);
      add(mtab = m.getTab());
    } else {
      l = new JLabel(genr.getName());
      l.setBorder(margin);
      add(l);
      add(genrtab);
      l = new JLabel(mw.getName());
      l.setBorder(margin);
      add(l);
      add( mwtab);
    }
    l = new JLabel("Alle leerdoelen");
    l.setBorder(margin);
    add(l);
    rest.setBorder(margin);
    add(rest);
    
  }

    Map<String,Map<String,Set<Integer>>> getFilter() {
      Map<String, Set<Integer>> mwmap = mw.getMethodMap(mwtab);
      Map<String, Set<Integer>> genrmap = genr.getMethodMap(genrtab);
      Map<String,Map<String,Set<Integer>>> filter = new HashMap<>();
      if (m != null) {
        mwmap = m.getMethodMap(mtab);
        if (!mwmap.isEmpty()) filter.put(m.getKey(), mwmap);
      } else {
        if (!mwmap.isEmpty())   filter.put(mw.getKey(), mwmap);
        if (!genrmap.isEmpty()) filter.put(genr.getKey(), genrmap);
      }
      if (rest.isSelected()) filter.put(null,null);
      return filter;
    }
  
    void setFilter(Map<String,Map<String,Set<Integer>>> filter) {
      rest.setSelected(filter.containsKey(null));
      if (m != null) {
        Map<String, Set<Integer>> mwmap = filter.getOrDefault(m.getKey(), Collections.emptyMap());
        m.setMethodMap(mtab, mwmap);
      } else {
        Map<String, Set<Integer>> mwmap = filter.getOrDefault(mw.getKey(), Collections.emptyMap());
        mw.setMethodMap(mwtab, mwmap);
        Map<String, Set<Integer>> genrmap = filter.getOrDefault(genr.getKey(), Collections.emptyMap());
        genr.setMethodMap(genrtab, genrmap);
      }
    }
}
