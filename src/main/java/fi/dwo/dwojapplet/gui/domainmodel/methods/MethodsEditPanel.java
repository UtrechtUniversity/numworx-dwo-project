package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class MethodsEditPanel extends JPanel {

  private static final Font font = Constants.FONT12;

  private JTextField txtField;
  private JButton ok, cancel;
  private ChapterSettings settings;

  MethodsEditPanel() {
    super(new BorderLayout());
    JLabel header = new JLabel(TextMapper.getText(TextMapper.GUIS_LBL_METHODS));
    Box box = Box.createHorizontalBox();
    box.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    box.setOpaque(true);
    box.setBackground(Constants.COLOR15);
    box.setAlignmentX(0);
    header.setForeground(Constants.COLOR20);
    header.setFont(font.deriveFont(24f));
    header.setHorizontalAlignment(JLabel.CENTER);
    box.add(Box.createHorizontalGlue());
    box.add(header);
    box.add(Box.createHorizontalGlue());
    JLabel title = new JLabel(TextMapper.getText(TextMapper.GUISM_NAME));
    title.setFont(Constants.FONT13/*.deriveFont(18f)*/);
    title.setHorizontalAlignment(JLabel.LEADING);
    Border left = BorderFactory.createMatteBorder(10, 40, 0, 0, getBackground());
    title.setBorder(left);
    title.setForeground(Constants.COLOR15);
    title.setAlignmentX(0);
    ok = new JButton(TextMapper.getText(TextMapper.BTN_OK));
    ok.setPreferredSize(new Dimension(100, 24));
    ok.setBackground(Constants.COLOR15);
    ok.setForeground(Constants.COLOR20);
    cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));    
    cancel.setPreferredSize(new Dimension(100, 24));
    cancel.setBackground(Constants.COLOR15);
    cancel.setForeground(Constants.COLOR20);
    settings = new ChapterSettings(TextMapper.getText(TextMapper.GUISM_CHAPTER), TextMapper.getText(TextMapper.GUISM_YEAR));
    settings.makeTextFields();
    settings.makeGUI(0,0);
    settings.setBackground(getBackground());
    txtField = new JTextField();
    txtField.setAlignmentX(0);
    txtField.setColumns(40);
    Border b = txtField.getBorder();
    txtField.setBorder(BorderFactory.createCompoundBorder(left, b));
    txtField.setMaximumSize(txtField.getPreferredSize());

    Box south = Box.createHorizontalBox();
    south.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    south.setOpaque(true);
    south.setBackground(Constants.COLOR21);
    south.add(Box.createHorizontalGlue());
    south.add(ok);
    south.add(Box.createHorizontalStrut(20));
    south.add(cancel);
    south.add(Box.createHorizontalGlue());
    add(south, BorderLayout.SOUTH);
    
    Box vbox = Box.createVerticalBox();
    vbox.add(box);
    vbox.add(Box.createVerticalStrut(20)); 
    vbox.add(title);
    vbox.add(txtField);
    add(vbox, BorderLayout.NORTH);
    
    add(settings, BorderLayout.CENTER);
  }
  
  int showDialog(JComponent parent) {
    ConfirmDialog dialog = new ConfirmDialog(parent, "");
    dialog.setContentPane(this);
    ok.addActionListener(method.standard? dialog::cancel : dialog::ok);
    cancel.addActionListener(dialog::cancel);
    dialog.pack();
    dialog.center();
    dialog.setVisible(true);
    return dialog.getOption();
  }
  
  private DomMethod method;
  public void setMethod(DomMethod rowSet) {
    method = rowSet;
    
    txtField.setText(rowSet.method);
    txtField.setEnabled(!rowSet.standard);
    settings.setBooks(rowSet.books.toArray(new String[rowSet.books.size()]));
    String[][] chapters = new String[rowSet.chapters.size()][];
    for (int i = 0; i < chapters.length; i++) {
      List<String> list = rowSet.chapters.get(i);
      chapters[i] = list.toArray(new String[list.size()]);
    }
    settings.setChapters(chapters);
    settings.makeGUI();
    settings.setReadonly(rowSet.standard);    
  }

  public DomMethod getMethod() {
    DomMethod rowSet = method;

    rowSet.method = txtField.getText();
    settings.makeObjects();
    rowSet.books = Arrays.asList(settings.getBooks());
    String[][] chapters = settings.getChapters();
    rowSet.chapters = new ArrayList<>(chapters.length);
    for (int i = 0; i < chapters.length; i++) {
      String[] strings = chapters[i];
      rowSet.chapters.add(Arrays.asList(strings));
    }   
    return rowSet;
  }
  
}
