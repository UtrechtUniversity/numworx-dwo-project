// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoManagementPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.SAXException;

import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.gui.SchoolPanel.ImageButtonEditor;
import fi.dwo.client.gui.SchoolPanel.ImageRenderer;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;
import fi.dwo.server.form.DWOFile;
import fi.dwo.server.persistence.DwoXmlRpcException;

/**
 * This class is a panel containing a list of SCO's to edit, delete or add.
 * It is used for SCO-management.
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *
 */
public class ScoManagementPanel extends Panel implements CenterSubPanel, ActionListener {

	private CenterPanel center;

    private DwoButton addScoButton, exportCourseButton, importScosButton;
    private ImageButton courseLogoButton; 

    private Image removeImage, editImage, courseImage, parametersImage, upImage, downImage;

    private Course course;
    
    private Label label;
    
    private Label noScosLabel;
	private FileDialog saveDial, openDial;

    /**
     * @param course
     */
    public ScoManagementPanel(Course course) {
        super(null);
        this.course = course;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(620, 485);
        course.loadScos();
        Image logo = course.getCourseLogo();
        /* Add Remove-course image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.REMOVE_SCO_IMAGE);
        editImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.EDIT_SCO_IMAGE);
        courseImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.COURSE_SCO_IMAGE);
        parametersImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.PARAMETERS_SCO_IMAGE);
        
        upImage = DwoHelper.getImage(GuiConstants.RESOURCES
        		+ GuiConstants.UP_SCO_IMAGE);
        downImage = DwoHelper.getImage(GuiConstants.RESOURCES
        		+ GuiConstants.DOWN_SCO_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(editImage, 1);
        tr.addImage(courseImage, 2);
        tr.addImage(parametersImage, 3);
        tr.addImage(upImage, 0);
        tr.addImage(downImage, 0);
        tr.addImage(logo, 4);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        FontMetrics fm;

        addScoButton = new DwoButton(TextMapper
                .getText(TextMapper.GUIS_ADD_SCO));
        fm = addScoButton.getFontMetrics(addScoButton.getFont());
        addScoButton.setSize(
                fm.stringWidth(addScoButton.getLabel()) + 20,
                fm.getHeight() + 10);
        addScoButton.addActionListener(this);
        addScoButton.setLocation(30, 10);
        addScoButton.setVisible(false);
        this.add(addScoButton);
        addScoButton.setVisible(true);

        exportCourseButton = new DwoButton("Backup module");
        fm = exportCourseButton.getFontMetrics(exportCourseButton.getFont());
        exportCourseButton.setSize(
                fm.stringWidth(exportCourseButton.getLabel()) + 20,
                fm.getHeight() + 10);
        exportCourseButton.addActionListener(this);
        exportCourseButton.setLocation(100+addScoButton.getWidth()+10, 10);
        exportCourseButton.setVisible(false);
        this.add(exportCourseButton);
        if(DwoHelper.isApplication())
        	exportCourseButton.setVisible(true);
        
        importScosButton = new DwoButton("Maak activiteiten vanuit backup");
        fm = importScosButton.getFontMetrics(importScosButton.getFont());
        importScosButton.setSize(
                fm.stringWidth(importScosButton.getLabel()) + 20,
                fm.getHeight() + 10);
        importScosButton.addActionListener(this);
        importScosButton.setLocation(exportCourseButton.getX()+exportCourseButton.getWidth()+10, 10);
        importScosButton.setVisible(false);
        this.add(importScosButton);
        
        courseLogoButton = new ImageButton(logo);
 
        if(DwoHelper.isApplication())
        {	importScosButton.setVisible(true);
            courseLogoButton.addActionListener(this);
        }
        
        String[] arguments = new String[1];
        arguments[0] = course.getName();
        label = new Label(MessageFormat.format(TextMapper.getText(TextMapper.GUIS_LBL_SCO_OF_COURSE), arguments));
        
        label.setFont(GuiConstants.SCO_TEXT);
        fm = label.getFontMetrics(label.getFont());
        label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
        label.setLocation(30, 50);
        this.add(label);
        courseLogoButton.setLocation(520, label.getLocation().y);
        this.add(courseLogoButton);
        arguments = new String[1];
        arguments[0] = course.getName();
        String s = TextMapper.getText(TextMapper.GUIS_NO_SCOS);
        noScosLabel = new Label(MessageFormat.format(s, arguments));
        noScosLabel.setFont(GuiConstants.SCO_TEXT);
        fm = noScosLabel.getFontMetrics(noScosLabel.getFont());
        noScosLabel.setSize(fm.stringWidth(noScosLabel.getText()) + 10, fm.getHeight());
        noScosLabel.setLocation((this.getSize().width/2) - (noScosLabel.getSize().width/2), 100);
        this.add(noScosLabel);
        
        buildJTable();
        if(DwoHelper.isApplication())
        {
        	final Frame topFrame = DwoHelper.getFrameForComponent(null);		
        	saveDial = new FileDialog(topFrame, exportCourseButton.getLabel(), FileDialog.SAVE);
        	saveDial.setDirectory(System.getProperty("user.dir","."));
        	openDial = new FileDialog(topFrame, importScosButton.getLabel(), FileDialog.LOAD);
        	openDial.setDirectory(System.getProperty("user.dir","."));
        }
    }

    
    JScrollPane jtbl;
	public class ImageRenderer extends JLabel implements TableCellRenderer {

		private ImageIcon icon = new ImageIcon();

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			Image image = (Image)value;
			if(image != null) {
				icon.setImage(image);
				setIcon(icon);
			} else {
				setIcon(null);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
			Object[] arguments = new Object[]  { table.getValueAt(row, 0) };
			switch(col) {
			case 1:	String s = TextMapper.getText(TextMapper.GUIS_TLTP_COURSE_SCO);
	    			setToolTipText(MessageFormat.format(s, arguments));
	    			break;
			case 2: setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_EDIT_SCO));
				break;
			case 3: setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_PARAMETERS_SCO));
			break;
			case 7: String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCO);
					setToolTipText(MessageFormat.format(format, arguments));
				break;
			default:
				setToolTipText(null); // TODO ....
			}
			if(selected)
			{
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}
			return this;
		}

	}
    public class ImageButtonEditor extends AbstractCellEditor implements
	TableCellEditor, ActionListener {

    	Object value;
    	ScoModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (ScoModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
            Sco s = course.getScoList()[row];
    		if (value == courseImage) {
                /* Show the Course Panel */
                center.loadCenter(GuiCreator.instance().getCourseManagementPanel());
    		} else
    		if (value == editImage) {
                if (ScoNameDialog.editSco(s)) {
                    model.fireTableCellUpdated(row, 0);
                }
    		} else if (value == removeImage) {
                /* Delete the course */
                String message;
                message = TextMapper.getText(TextMapper.GUIS_MSG_SCO_DELETE);
                if (JOptionPane.showConfirmDialog(ScoManagementPanel.this, message, TextMapper.getText(TextMapper.GUIS_MSG_TTL_SCO_DELETE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (GuiCreator.instance().deleteSco(s)) {
                    	model.fireTableRowsDeleted(row, row);
                    }
                }
    		} else if (value == parametersImage) {
    			GuiCreator.instance().loadParameterManagementPanel(s);
    		} else if (value == upImage) {
    			Sco s2 = course.getScoList()[row-1];
    			swapSco(s, s2);
    			model.fireTableRowsUpdated(row-1, row);
    		} else if (value == downImage) {
    			Sco s2 = course.getScoList()[row+1];
    			swapSco(s, s2);
    			model.fireTableRowsUpdated(row, row+1);
    		}
    		fireEditingStopped();
    	}

}

    class ScoModel extends AbstractTableModel {

		public int getColumnCount() {
			return 7;
		}

		public int getRowCount() {
			return course.getScoList().length;
		}

		public Class getColumnClass(int col) {
			if(col > 0)
				return Image.class;
			return super.getColumnClass(col);
		}

		public boolean isCellEditable(int row, int col) {
			if(col == 4)
				return row != 0;
			if(col == 7)
				return row != getRowCount()-1;
			return col > 0;
		}

		public Object getValueAt(int row, int col) {
			switch(col)
			{
			case 0: return course.getScoList()[row].getScoName();
			case 1: return courseImage;
			case 2: return editImage;
			case 3: return parametersImage;
			case 6: return removeImage;
			
			case 4: if(row != 0)
						return upImage;
					break;
			case 5: if(row != getRowCount()-1)
						return downImage;
					break;
			}
			return null;
		}
    	
    }
    
    
    
    private void buildJTable() {
    	if(jtbl != null)
    	{
    		remove(jtbl);
    		jtbl = null;
    	}
        Sco[] scos = course.getScoList();
        if(scos == null || scos.length == 0) {
            noScosLabel.setVisible(true);
            label.setVisible(false);
            return;
        } else {
            noScosLabel.setVisible(false);            
            label.setVisible(true);
        }
        JTable table = new JTable(new ScoModel());
    	TableUtil.setDefaults(table, false, new ImageRenderer(), new ImageButtonEditor());

    	TableUtil.setJTableSizes(table);
    	jtbl = new JScrollPane(table);
    	TableUtil.setBorder(jtbl);
        jtbl.setLocation(30, label.getSize().height
                + label.getLocation().y+10);
        TableUtil.shrinkToFit(table, jtbl, 520-30, 405);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    	
    }
    
    
 
    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Panel getHeaderPanel() {
        Panel p = new BorderedPanel(null);
        if(GuiConstants.GUI_IMAGE_BG) {
        	p = new BorderedPanel(null,0)
            {  	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE),-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        };
        }
        p.setBackground(GuiConstants.MAIN_BACKGROUND);
        p.setBounds(181, 20, 449, 71);
        this.add(p);

        /* My Profile-Label */
        JLabel l = new JLabel(TextMapper.getText(TextMapper.GUIS_SCO_MANAGEMENT));
        l.setOpaque(false);
        l.setFont(GuiConstants.HEADER_TEXT);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), (p.getSize().height / 2)
                - (l.getSize().height / 2));
        p.add(l);
        
        if(GuiConstants.GUI_IMAGE_BG) 
        {	p.remove(l);
        	Font f = l.getFont();
            l.setFont(new Font(f.getName(), f.getStyle(), 26));
	        fm = l.getFontMetrics(l.getFont());
	        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	        l.setLocation(60, 38);
	        p.add(l);
        }

        return p;
    }


    
    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == courseLogoButton)
    	{
    		try {
				importCourseLogo();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
    	}
      	if(e.getSource() == exportCourseButton)
    	{
    		try {
				export();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		return;
    	} else if(e.getSource() == importScosButton)
    	{
    		try { 
    			importScos();
    		} catch (Exception e2) {
    			e2.printStackTrace();			
    		}
    	}
        if(e.getSource() == addScoButton) {
        	Sco s = null;
        	
        	// speciaal voor de SAG en REV: er kan maar 1 soort appletConfig gebruikt worden, nl WiskOpdr
        	if(course.getDwoProfile()==15 || course.getDwoProfile()==22) {
        		try {
        		AppletConfig ac = (AppletConfig)(PersistenceFacade.instance().get(55,AppletConfig.class));
        		s = ScoNameDialog.addSco(this, course, ac);
	        	} catch (PersistenceException ex) {
	                JOptionPane.showMessageDialog(this, ex.getMessage());
  	            }
            }
        	//
        	
            else {
            	s = AddScoDialog.addSco(this, course);
            }
            if(s != null) {
	            Sco[] as = course.getScoList();
	            /* Create a larger array and add the item */
	            Sco[] tmp = new Sco[as.length + 1];
	            System.arraycopy(as, 0, tmp, 0, as.length);
	            tmp[tmp.length - 1] = s;
	            course.setScoList(tmp);
	            buildJTable();                
            }

        }
            
    }

    private void importCourseLogo() throws IOException 
    {
    	String naam; 
    	openDial.setTitle("Laad Modulelogo");
    	openDial.show();
    	naam = openDial.getFile();
    	if(naam != null)
    	{
    		ByteArrayOutputStream output = new ByteArrayOutputStream();
    		File dir = new File(openDial.getDirectory());
    		File file = new File(dir,naam);
    		BufferedImage img = ImageIO.read(file);
    		Image reduced;
    		if(img.getWidth()<=64 && img.getHeight()<=64)
    			reduced = img;
    		else
    			reduced = img.getScaledInstance(Math.min(64,img.getWidth()), Math.min(64,img.getHeight()), Image.SCALE_SMOOTH);
    		if(reduced instanceof BufferedImage)
    		{
    			img = (BufferedImage) reduced;
    		} else {
    			img = new BufferedImage(Math.min(64,img.getWidth()),Math.min(64,img.getHeight()),BufferedImage.TYPE_INT_ARGB);
    			img.createGraphics().drawImage(reduced, 0, 0, null);
    		}
    		ImageIO.write(img, "png", output);
    		output.close();
    		byte[] data = output.toByteArray();
    		reduced = Toolkit.getDefaultToolkit().createImage(data);
    		
    		course.setCourseLogo(reduced);
    		courseLogoButton.setImage(reduced);
// TODO omzetten in PersistenceFacade!
    		try {
				DbAccessCreator.instance().setLogo(course.getID(), data);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	}
    }
    
    
    private void importScos() throws ParserConfigurationException, SAXException, IOException, DwoXmlRpcException, XmlRpcException, SQLException {
    	String naam;
    	openDial.setTitle(importScosButton.getLabel());
		openDial.show();
		naam = openDial.getFile();
		if(naam!=null)
		{	
			File dir = new File(openDial.getDirectory());
			File file = new File(dir, naam);
			FileInputStream input = new FileInputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			Hashtable result = zipper.inputIMSManifest(input);
			Sco[] scoList = course.getScoList();
			int offset = scoList.length;
			Set names = new HashSet();
			for (int i = 0; i < offset; i++) {
				String name = scoList[i].getScoName();
				names.add(name);
			}
			Vector scos = (Vector)result.get("sco");
			Enumeration elements = scos.elements();
			while (elements.hasMoreElements()) {
				Hashtable sco = (Hashtable) elements.nextElement();
				String title = (String) sco.get("sconame");
				title = CourseManagementPanel.replaceDuplicate(title, names);
				names.add(title);
				sco.put("sconame", title);
			}
			zipper.appendCourse(course.getID(), offset, result);
			course.loadScos();
			buildJTable();
		}
	}

	private void export() throws ParserConfigurationException, TransformerException, SQLException, IOException, XmlRpcException {
    	String naam;
    	
		saveDial.show();
		naam = saveDial.getFile();
		if(naam!=null)
		{	
			File dir = new File(saveDial.getDirectory());
			File file = new File(dir, naam);
			FileOutputStream out = new FileOutputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			zipper.createIMSManifest(course.getID(), -1, out);
			
		}
	}

	private void swapSco(Sco s1, Sco s2) {
    	if (GuiCreator.instance().swapSco(s1, s2))
    		/*buildJTable()*/;
	}

	/**
     * Returns the current object, as the object to add to a gui.
     * 
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    public Component getComponent() {
        return this;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.CenterSubPanel#end()
     */
    public void end() {
        // TODO Auto-generated method stub
        
    }

}