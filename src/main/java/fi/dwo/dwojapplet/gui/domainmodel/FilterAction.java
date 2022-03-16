package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
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
      this.filter = filter;
    }
  }