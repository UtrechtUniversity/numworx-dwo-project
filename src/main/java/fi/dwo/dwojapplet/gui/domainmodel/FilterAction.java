package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

class FilterAction extends AbstractAction {

    final Component owner;
    final Consumer<Map<String,Map<String,Set<Integer>>>> consumer;
    
    FilterAction( Component owner, Consumer<Map<String,Map<String,Set<Integer>>>> consumer) {
      super("Filter leerdoelen");
      this.owner = owner;
      this.consumer = consumer;
      setEnabled(false);
//      mw.setTree(tree);
//      genr.setTree(tree);
    }

    FilterPanel p;
    
    PersistenceId activeMethod;
    
    
    Map<String,Map<String,Set<Integer>>> filter = Collections.emptyMap();

    @Override
    public void actionPerformed(ActionEvent e) {
      ConfirmDialog dialog = new ConfirmDialog(owner, getValue(NAME).toString());
      FilterPanel p;
      p = new FilterPanel(activeMethod);
      p.setFilter(filter);
      dialog.getContentPane().setLayout(new BorderLayout());
      
      dialog.getContentPane().add(p, BorderLayout.CENTER);
      
      JButton ok = new JButton(TextMapper.getText(TextMapper.BTN_OK));
      ok.addActionListener(dialog::ok);
      JPanel south = new JPanel(new FlowLayout());
      south.setBackground(Constants.COLOR21);
      south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      south.add(ok);
      dialog.getContentPane().add(south, BorderLayout.SOUTH);
      
      dialog.pack();
      dialog.show();
      if (JOptionPane.OK_OPTION == dialog.getOption()) {
        filter = p.getFilter();
        consumer.accept(filter);
      }
    }
    
    public void doFilter() {
      consumer.accept(filter);
    }

    public void unsetFilter() {
      unset();
      doFilter();
    }
    
    public PersistenceId getActiveMethod() {
      return activeMethod;
    }

    public void setActiveMethod(PersistenceId activeMethod) {
      if (!Objects.equals(activeMethod, this.activeMethod)) {
        this.activeMethod = activeMethod;
        filter = Collections.emptyMap();
        setEnabled(activeMethod != null);
      }
    }

    void unset() {
      filter = Collections.emptyMap();
    }

    Map<String, Map<String, Set<Integer>>> getFilter() {
      return filter;
    }

    void setFilter(Map<String, Map<String, Set<Integer>>> filter) {
      DomMethod dm;
      dm = MethodsProperties.instance().getMethod(activeMethod);
      Map<String, Set<Integer>> method = filter.getOrDefault(dm.key(), Collections.emptyMap());
      if (method.size() == 1) {
        Entry<String, Set<Integer>> entry = method.entrySet().iterator().next();
        if (entry.getValue().isEmpty()) {
          String key = entry.getKey();
          int i = dm.books.indexOf(key);
          int aantalHoofdstukken = dm.chapters.get(i).size();
          Set<Integer> set;
          filter = new HashMap<>(filter);
          set = (IntStream.range(1, 1+aantalHoofdstukken).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
          filter.put(dm.key(), Collections.singletonMap(key, set));
        }
      }
      this.filter = filter;
    }
  }