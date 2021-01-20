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
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
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
    JCheckBox rest = new JCheckBox("Niet geclassificeerde leerdoelen");
    

    KoppelingGRPanel genrtab = genr.getTab();
    KoppelingGRPanel mwtab = mw.getTab();

    Map<String,Map<String,Set<Integer>>> filter = Collections.emptyMap();

    @Override
    public void actionPerformed(ActionEvent e) {
      ConfirmDialog dialog = new ConfirmDialog(owner, getValue(NAME).toString());
      Box tabs = Box.createVerticalBox();
      dialog.getContentPane().setLayout(new BorderLayout());
      Border margin = BorderFactory.createEmptyBorder(0, 20, 0, 0);
      JLabel l = new JLabel(genr.getName());
      l.setBorder(margin);
      tabs.add(l);
      tabs.add(genrtab);
      l = new JLabel(mw.getName());
      l.setBorder(margin);
      tabs.add(l);
      tabs.add( mwtab);
      l = new JLabel("Alle leerdoelen");
      l.setBorder(margin);
      tabs.add(l);
      rest.setBorder(margin);
      tabs.add(rest);
      
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
        if (!mwmap.isEmpty())   filter.put(mw.getName(), mwmap);
        if (!genrmap.isEmpty()) filter.put(genr.getName(), genrmap);
        if (rest.isSelected())  filter.put(null,null);
        consumer.accept(filter);
      }
    }
    
    public void doFilter() {
      consumer.accept(filter);
    }
  }