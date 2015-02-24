// Source file:
// C:\\parameters\\fi\\dwo\\parameters\\gui\\SingleLevelTreeParameterComponent.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JButton;

import fi.beans.scorm.TreeParameter;
import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class SingleLevelTreeParameterComponent extends ParameterComponent implements ParameterComponentIF,
        ActionListener {

    private TabPane tabPane;

    private TreeSheetCreator tabSheetCreator;

    private JButton button;

    private Component last;

    private JButton addButton; //with no elements, we show a button

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     */
    public SingleLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);

    }

    public SingleLevelTreeParameterComponent(ParameterComponentIF parent,
            TreeParameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        //setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setOpaque(false);
        Hashtable tmp = defaultValue;
        if (defaultValue.containsKey(parameter.getName())) {
            tmp = (Hashtable) defaultValue.get(parameter.getName());
        }
        tabSheetCreator = new TreeSheetCreator(this, parameter, tmp);

        String[] arguments = new String[1];
        arguments[0] = "" + tabSheetCreator.getNrItems();
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button = new JButton(MessageFormat.format(s, arguments));//, parent.getColor());
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setSize(button.getPreferredSize());
        //fm.stringWidth(button.getLabel()) + 20,
        //        fm.getHeight() + 10);
        button.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        button.addActionListener(this);

        arguments[0] = parameter.getItemLabel();
        s = TextMapper.getText(TextMapper.BTN_TREE_ADD_ITEM);

        addButton = new JButton(MessageFormat.format(s, arguments));
        fm = addButton.getFontMetrics(addButton.getFont());
        addButton.setSize(addButton.getPreferredSize());
        // fm.stringWidth(addButton.getLabel()) + 20,
        //        fm.getHeight() + 10);
        addButton.setLocation(preLabel.getLocation().x + preLabel.getSize().width
                + 10, 1);
        addButton.addActionListener(this);

        if (tabSheetCreator.getNrItems() == 0) {
            addButton.setVisible(false);
            this.add(addButton);
            addButton.setVisible(true);
            last = generatePostItems(addButton);

        } else {
            button.setVisible(false);
            this.add(button);
            button.setVisible(true);
            last = generatePostItems(button);
        }

        this.setSize(last.getLocation().x + last.getSize().width + 5, button
                .getSize().height + 2);

    }

    /**
     * With a tree element, there is no help button so generate nothing and
     * return <code>after</code>
     *
     * @param after
     * @return 
     * @see
     * fi.dwo.parameters.gui.ParameterComponent#generatePostItems(java.awt.Component)
     */
    @Override
    protected Component generatePostItems(Component after) {
        postLabel = new FixedLabel("(" + TextMapper.getText(TextMapper.LBL_NO_ITEMS) + ")");
        postLabel.setFont(ParameterConstants.LABEL_FONT);
        FontMetrics fm = postLabel.getFontMetrics(postLabel.getFont());
        postLabel.setSize(fm.stringWidth(postLabel.getText()) + 10, fm.getHeight());
        postLabel.setLocation(after.getLocation().x + after.getSize().width + 10, 1);

        if (tabSheetCreator.getNrItems() == 0) {
            this.add(postLabel);
            after = postLabel;
        }
        return after;
    }

    private void createTabPane() {
        if (tabPane == null) {
            tabPane = new TabPane();
            tabPane.setBackground(parent.getColor());
            /* The color is the other color than the parent */
            if (parent.getColor() == SELECTED_COLOR_1) {
                tabPane.setTabColor(SELECTED_COLOR_2);
            } else {
                tabPane.setTabColor(SELECTED_COLOR_1);
            }
            tabPane.setTabSheetCreator(tabSheetCreator);
            tabSheetCreator.setTabPane(tabPane);
            tabSheetCreator.createTabs();

            tabPane.setLocation(preLabel.getLocation());
            tabPane.addComponentListener(this);
        }
    }

    public void noItems() {
        add(preLabel);
        add(addButton);
        postLabel.setLocation(addButton.getLocation().x + addButton.getSize().width + 10, 1);
        add(postLabel);
        if (tabPane != null) {
            remove(tabPane);
        }
        remove(button);
        parent.isFocussed(this);

        this.setSize(postLabel.getLocation().x + postLabel.getSize().width + 5, addButton
                .getSize().height + 2);

    }

    /**
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            createTabPane();

            add(tabPane);
            remove(preLabel);
            remove(button);

            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            validate();
        } else if (e.getSource() == addButton) {
            createTabPane();
            tabSheetCreator.addTab();
            add(tabPane);
            remove(preLabel);
            remove(postLabel);
            remove(addButton);
            parent.isFocussed(this);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);

        }

    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#unFocus()
     */
    @Override
    public void unFocus() {
        if (tabPane != null) {
            this.remove(tabPane);
        }
        String[] arguments = new String[1];
        arguments[0] = "" + tabSheetCreator.getNrItems();
        String s = TextMapper.getText(TextMapper.BTN_TREE_NR_ITEMS);

        button.setLabel(MessageFormat.format(s, arguments));

        if (tabSheetCreator.getNrItems() != 0) {
            this.add(preLabel);
            this.add(button);
        } else {
        }

        this.setSize(last.getLocation().x + last.getSize().width + 5, button
                .getSize().height + 2);

    }
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */

    @Override
    public void componentResized(ComponentEvent e) {
        if (false && e.getSource() == tabPane) {
            this.setVisible(false);
            this.setSize(tabPane.getLocation().x + tabPane.getSize().width, tabPane.getLocation().y + tabPane.getSize().height);
            this.setVisible(true);
            validate();
        }
    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */

    @Override
    public void reset() {
        if (tabPane != null) {
            this.remove(tabPane);
        }
        tabPane = null;
        Hashtable tmp = defaultValue;
        if (defaultValue.containsKey(parameter.getName())) {
            tmp = (Hashtable) defaultValue.get(parameter.getName());
        }
        int oldNrItems = tabSheetCreator.getNrItems();

        tabSheetCreator = new TreeSheetCreator(this, (TreeParameter) parameter, tmp);
        if (tabSheetCreator.getNrItems() == 0) {
            noItems();
        }
        if ((oldNrItems == 0) && (tabSheetCreator.getNrItems() != 0)) {
            //we showed the no items button, but we must remove it.
            remove(preLabel);
            remove(postLabel);
            remove(addButton);
        }
        unFocus();
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    @Override
    public String getSequenceString(ParameterComponentIF component) {
        if (parent != null) {
            return parent.getSequenceString(this);
        } else {
            return super.getSequenceString(component);
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    @Override
    public void addParameters(Hashtable parameters) {
        tabSheetCreator.addParameters(parameters);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        super.setBounds(x, y, width, height);
    }

    @Override
    public Dimension getPreferredSize() {
        if (getLayout() != null) {
            Dimension size = getLayout().preferredLayoutSize(this);
            System.out.println("single pref=" + size + " now=" + getSize());
            return size;
        }

        if (tabPane != null && tabPane.isShowing()) {
            return new Dimension(tabPane.getLocation().x + tabPane.getPreferredSize().width, tabPane.getLocation().y + tabPane.getPreferredSize().height);
        } else {
            return new Dimension(last.getLocation().x + last.getSize().width + 5, button
                    .getSize().height + 2);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
