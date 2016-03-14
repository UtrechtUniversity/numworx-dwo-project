package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.commons.system.TextMapper;
import java.awt.Color;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * This panel allows one to manage and switch between SchoolLogins.
 *
 */
public class SchoolClassRegistrationAskKeyJPanel extends JPanel implements ActionListener, FocusListener {

    private Color STYLE_COLOUR;
    private final Color WARN_COLOR = Color.RED;

    // componentList
//    JButton okBtn = new JButton(TextMapper.getText(TextMapper.BTN_OK));
//    JButton cancelBtn = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
    private Boolean okSelected = false;
    
    private JLabel classLabel;
    private JTextField classTextField;
//	private JCheckBox treeCB;
//	private JCheckBox classKeyCB;
    private JLabel registrationKeyLabel;
    private JTextField registrationKeyTextField;

    // propertyList
    private String className;
    // private String classKeyTextName;
    private String registrationKey; // not null means it is set
    private boolean iconizer;
//    private JDialog parentDialog;

    public SchoolClassRegistrationAskKeyJPanel() {
        init();
    }

    public void init() {
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //this.setBackground(GuiConstants.MAIN_BACKGROUND);
//        this.setAlignmentX(LEFT_ALIGNMENT);
//        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Constructing Pane
        classLabel = new JLabel(TextMapper.getText("klasnaam"));

        classTextField = new JTextField(25);
        classTextField.setEditable(false);
        classTextField.addFocusListener(this);
        classTextField.requestFocusInWindow();// request focus for typing

        registrationKeyLabel = new JLabel(
                TextMapper.getText("registratiesleutel"));
        registrationKeyLabel.setVisible(true);

        registrationKeyTextField = new JTextField(25);

        STYLE_COLOUR = registrationKeyTextField.getForeground();
        registrationKeyTextField
                .setToolTipText(TextMapper
                        .getText("Geef de registratiesleutel op om te registreen."));
        registrationKeyTextField.addFocusListener(this);
        registrationKeyTextField.setVisible(true);
//        okBtn.addActionListener(this);
//        cancelBtn.addActionListener(this);
        // intialize a random password when the registration key is enabled and
        // the length is less than 5.

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        // local layout creates space for invisible objects
        layout.setHonorsVisibility(false);
        // link horizontal size of class and classKey textfields for prettier
        // layout
        layout.linkSize(SwingConstants.HORIZONTAL, classTextField,
                registrationKeyTextField);

        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.TRAILING)
                        .addComponent(classLabel)
                        .addComponent(registrationKeyLabel)
//                        .addComponent(okBtn)
                )
                .addContainerGap()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING)
                        .addComponent(classTextField)
                        .addComponent(registrationKeyTextField))
//                .addComponent(cancelBtn)
        );

        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                        .addComponent(classLabel)
                        .addComponent(classTextField))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                        .addComponent(registrationKeyLabel)
                        .addComponent(registrationKeyTextField))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
//                        .addComponent(okBtn)
//                        .addComponent(cancelBtn)
                )
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//    if (e.getSource() == okBtn) {
//            okSelected = true;
//            parentDialog.dispose();
//        } else if (e.getSource() == cancelBtn) {
//            okSelected = false;
//            parentDialog.dispose();
//        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() == classTextField) {
            setClassName(classTextField.getText());
        } else if (e.getSource() == registrationKeyTextField) {
            setRegistrationKey(registrationKeyTextField.getText());
//            if (getRegistrationKey() == null){                
//                setRegistrationKey("");
//            } else {
//                setRegistrationKey(registrationKeyTextField.getText());
//            }
//            if (getRegistrationKey().length() < 5) {
//                registrationKeyTextField.setForeground(WARN_COLOR);
//            } else {
//                registrationKeyTextField.setForeground(STYLE_COLOUR);
//            }
        }
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
        this.classTextField.setText(className);
    }

    /**
     * @return the registrationKey
     */
    public String getRegistrationKey() {
        return registrationKey;
    }

    /**
     * @param registrationKey the registrationKey to set
     */
    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
        this.registrationKeyTextField.setText(registrationKey);
    }

    /**
     * Sets the panel attributes according to the values of
     * DomSchoolClass4Teacher
     *
     * @param sc the SchoolClass
     */
    public void setSchoolClass(DomNewSchoolClass4Student sc) {
        setClassName(sc.getSchoolClassName());
        setRegistrationKey(sc.getRegistrationKey());
    }

    /**
     * Sets the fields of the parameter.
     *
     * @param sc the SchoolClass
     */
    public void updateSchoolClass(DomNewSchoolClass4Student sc) {
        sc.setSchoolClassName(className);
        sc.setRegistrationKey(registrationKey);
    }

//    public void setParent(JDialog parent){
//        parentDialog = parent;
//    }
//
//    /**
//     * @return the okSelected
//     */
//    public Boolean getOkSelected() {
//        return okSelected;
//    }
//
}
