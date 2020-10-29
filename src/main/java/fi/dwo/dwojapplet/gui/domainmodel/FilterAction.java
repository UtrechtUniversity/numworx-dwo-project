package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JTabbedPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;

class FilterAction extends AbstractAction {

    final Component owner;
    final Consumer<Map<String,Map<String,Set<Integer>>>> consumer;
    
    FilterAction( Component owner, Consumer<Map<String,Map<String,Set<Integer>>>> consumer) {
      super("Filter leerdoelen");
      this.owner = owner;
      this.consumer = consumer;
//      mw.setTree(tree);
//      genr.setTree(tree);
    }

    MWAction mw = new MWAction();
    GenRAction genr = new GenRAction();
    

    KoppelingGRPanel genrtab = genr.getTab();
    KoppelingGRPanel mwtab = mw.getTab();

    Map<String,Map<String,Set<Integer>>> filter = Collections.emptyMap();

    @Override
    public void actionPerformed(ActionEvent e) {
      ConfirmDialog dialog = new ConfirmDialog(owner, getValue(NAME).toString());
      JTabbedPane tabs = new JTabbedPane();
      dialog.getContentPane().setLayout(new BorderLayout());
      tabs.addTab(genr.getName(), genrtab);
      tabs.addTab(mw.getName(), mwtab);
      
      dialog.getContentPane().add(tabs, BorderLayout.CENTER);
      
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
        Map<String, Set<Integer>> mwmap = mw.getMethodMap(mwtab);
        Map<String, Set<Integer>> genrmap = genr.getMethodMap(genrtab);
        filter = new HashMap<>();
        if (!mwmap.isEmpty()) filter.put(mw.getName(), mwmap);
        if (!genrmap.isEmpty()) filter.put(genr.getName(), genrmap);
        consumer.accept(filter);
      }
    }
    
    public void doFilter() {
      consumer.accept(filter);
    }
  }