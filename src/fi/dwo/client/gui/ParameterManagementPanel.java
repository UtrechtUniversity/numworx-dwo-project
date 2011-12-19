// Source file:
// C:\\parameters\\fi\\dwo\\client\\gui\\ParameterManagementPanel.java

package fi.dwo.client.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

import fi.beans.appletutil.AppletUtil;
import fi.beans.base64code.StringCodeObject;
import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormAppletIF;
import fi.beans.scorm.ScormEditComponentIF;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.ScoEditor;
import fi.dwo.client.gui.action.ImportScorm;
import fi.dwo.client.gui.action.Save2004Action;
import fi.dwo.client.gui.action.SaveAppletAction;
import fi.dwo.client.persistence.MapperCreator;
//import fi.dwo.client.system.Collections;
import fi.dwo.client.system.TextMapper;
import fi.dwo.parameters.domain.ConvertorCreator;
import fi.dwo.parameters.domain.ConvertorIF;
import fi.dwo.parameters.gui.MainParameterComponent;
import fi.dwo.parameters.gui.ParameterComponent;

/**
 * This class is a panel for editing the parameters of a SCO.
 * If the SCO has an edit-mode, a dialog with the editmode is showed.<br> 
 * The editmode is showed in a dialog, because of the possible size of the editmode.<br>
 * If the SCO has not an edit-mode, a MainParameterComponent is showed with the parameters of the sco.<br>
 * @author M.J.B. Kupers
 *
 */
public class ParameterManagementPanel extends JPanel implements CenterSubPanel, ActionListener, WindowListener, ScoEditor {
    static boolean POPUP = true; // FIXME in productie true

	private CenterPanel center;

    private ScormEditComponentIF editComponent;
    private Parameter[] parameters;
    private ParameterComponent parameterComponent;

    private boolean editMode;
    
    private Sco sco;
    
    private JButton previewButton, previewBtn;
    private JButton saveButton, saveBtn;
    private JButton resetButton;
    private JButton closeButton,stopBtn;
    private JButton importScormButton;
    private JButton exportScormButton;
    private JButton exportAppletButton;
    
    private JLabel noParamLabel;
    
    private JScrollPane scrollPanel;
    
    private JDialog editModeDialog;
    private ScoDialog scoDialog;
    

    /**
     * Creates a new ParameterManagementPanel
     * If the sco has an edit-mode, a dialog with the editmode is showed.
     * If the sco has not an edit-mode, a MainParameterComponent is showed with the parameters of the sco.<br>
     * @param sco The SCO wherefrom the parameters must be changed.
     */
    public ParameterManagementPanel(Sco sco) {
        super(new BorderLayout());
        
// in tree mode never popup.
        if(CenterPanel.isIconizer())
        	POPUP = false;
        
// nodig, maar nog niet gezet.
        setCenterPanel(GuiCreator.instance().getMainPanel().getCenter());
        center.end(); // idempotent!
        GuiCreator.instance().setWait();

        this.sco = sco;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        JPanel buttonPanel;
		JPanel mainPanel;  // import van awt componenten
        buttonPanel = new JPanel(new FlowLayout()/*,BorderedPanel.SOUTH*/);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        buttonPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        if(!CenterPanel.isIconizer())
        	this.add(buttonPanel, BorderLayout.NORTH);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(mainPanel, BorderLayout.CENTER);
        
		ScormAppletIF applet = null;
        Applet editableApplet = sco.getApplet();
        if(editableApplet instanceof ScormAppletIF)
        {
        	applet = (ScormAppletIF) editableApplet;
            editMode = applet.hasEditMode();
        } else 
        	editMode = false;
        
        if(editMode) {
            Hashtable launchData = sco.getLaunchdata();
            launchData.put("language", TextMapper.getLanguage());
        	editComponent = applet.getEditComponent(launchData);
            this.setSize(800, 620);
            this.setPreferredSize(getSize());
            this.setMinimumSize(getSize());
            
            String title = TextMapper.getText(TextMapper.GUIPA_DLG_TTL);
            String[] tmp = {sco.getScoName()};
            title = MessageFormat.format(title, tmp);
            
            editModeDialog = new JDialog(DwoHelper.getFrameForComponent(DwoHelper.getApplet()), title, false);
           
            editModeDialog.setSize(800, 620);
            
            Dimension parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension size = editModeDialog.getSize();
            int x = (parentSize.width - size.width) / 2;
            int y = (parentSize.height - size.height) / 2;
            
            if(x < 0) {
                x = 0;
            }
            if(y < 0) {
                y = 0;
            }

            editModeDialog.setLocation(x, y);
            editModeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            editModeDialog.addWindowListener(this);
            
            // keuze, embedded of popup
            	if(!POPUP)
            	{	
            		editModeDialog = null;
            	} else {
            		editModeDialog.setContentPane(this);
            	}
        	} else {
        		if(applet != null)
        			parameters = applet.getEditableParameters();
        		else 
        			parameters = null;
        		if(parameters == null)parameters = new Parameter[0];
        		this.setSize(627, 485);
        		this.setPreferredSize(getPreferredSize());
        		editModeDialog = null;
        }
        
        
        FontMetrics fm;

        previewButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_PREVIEW));
        previewButton.addActionListener(this);
        buttonPanel.add(previewButton);
        
        if(sco.getAppletID()==12) previewButton.setEnabled(false);// geen preview mogelijk bij popupurlapplet
        
        saveButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_SAVE));
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);

        resetButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_RESET));
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        closeButton = new JButton(TextMapper.getText(TextMapper.GUIPA_BTN_CANCEL));
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);
// school 190 264 385 heeft scorm export recht     
        if(DwoHelper.isApplication())
        {	if( DwoHelper.isScormExportLoggedIn() ) 
        	{   importScormButton = new JButton(new ImportScorm(sco));
		        buttonPanel.add(importScormButton);
		        
		        exportScormButton = new JButton(new Save2004Action(sco));
		        buttonPanel.add(exportScormButton);
        	}
/*
 * Let op alleen 13,20,27,46 en 51 voor recht 'a' of ADMIN
 *  (sco.getCourse().getDwoProfile()==13 
   || sco.getCourse().getDwoProfile()==20 
   || sco.getCourse().getDwoProfile()==27 
   || sco.getCourse().getDwoProfile()==46 
   || sco.getCourse().getDwoProfile()==51)
*/
	        if ( DwoHelper.isAppletExportLoggedIn() ) 
	    	{   exportAppletButton = new JButton(new SaveAppletAction(sco));
		        buttonPanel.add(exportAppletButton);
	    	}
			
			sco.setEditor(this);
			
        }
        
                
        scrollPanel = new JScrollPane();
        scrollPanel.setBorder(null);
        scrollPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        scrollPanel.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        mainPanel.add(scrollPanel);
        
           
        if(!editMode) {
            Hashtable tmp = sco.getLaunchdata();
            ConvertorIF convertor = ConvertorCreator.createConverter(ConvertorCreator.CONV_LAUNCHDATA);
            tmp = (Hashtable) convertor.convertHashtable(tmp, parameters);
            parameterComponent = new MainParameterComponent(parameters, tmp);
            JPanel hulp = new JPanel(new FlowLayout(FlowLayout.LEADING), false); hulp.add(parameterComponent);
            hulp.setOpaque(false);
            scrollPanel.setViewportView(hulp);//parameterComponent);
        } else {
            scrollPanel.setSize(getSize());
            scrollPanel.setViewportView(editComponent.getComponent());
            scrollPanel.validate();
            System.out.println(editComponent.getComponent());
            
        }
        
        noParamLabel = new JLabel(TextMapper.getText(TextMapper.GUIPA_NO_PARAMS));
        noParamLabel.setFont(GuiConstants.SCO_TEXT);
        fm = noParamLabel.getFontMetrics(noParamLabel.getFont());
        noParamLabel.setSize(fm.stringWidth(noParamLabel.getText()) + 10, fm.getHeight());
        noParamLabel.setLocation((this.getSize().width/2) - (noParamLabel.getSize().width/2), 100);
        noParamLabel.setHorizontalAlignment(JLabel.CENTER);
        if(parameters != null && parameters.length == 0)scrollPanel.setViewportView(noParamLabel);
        doLayout();
        GuiCreator.instance().setReady();
        
        if(editMode) {
        	if(POPUP)
        		editModeDialog.setVisible(true);
        	else
        		if(CenterPanel.isIconizer())
        			center.loadCenter(this);
        		else
        			center.loadTotal(this);
        } else {
            center.loadCenter(this);            
        }
    }

    boolean done;

	Hashtable tmp;

	private String scormTitel;
    public void end() {
    	if(!done)
    	{	done = true;
    		saveSco();
    	}	
    	sco.setEditor(null);
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {
    	HeaderPanel hp = new HeaderPanel(TextMapper.getText(TextMapper.GUIPA_SCO_EDIT));
    	Box box = Box.createHorizontalBox();
    	box.add(stopBtn = new JButton(TextMapper.getText(TextMapper.GUIH_STOP_EDIT)));
    	box.add(Box.createHorizontalStrut(10));
    	box.add(saveBtn = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_SAVE)));    	
    	box.add(Box.createHorizontalStrut(10));
    	box.add(previewBtn = new JButton("Preview"));
    	//box.setBorder(BorderFactory.createLineBorder(Color.red));
		hp.setButtonBox(GuiCreator.instance().fx(box));
		
		stopBtn.addActionListener(this);
		previewBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		return hp;
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
     * @return java.awt.Component
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == stopBtn)
        {
        	int x = saveSco();
        	if(x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION)
        		return;
        	done = true;
        	if(editMode)
        		editComponent.end();
        	center.setStrategy(null);
        	center.select(sco);
        	return;
        }
        if(src == previewBtn) {
        	Hashtable tmp = getLaunchdata();
        	this.tmp = sco.getLaunchdata(); //????
        	done = true;
        	sco.setLaunchdata(tmp);
        	ScoPanel sp = GuiCreator.instance().previewSco(sco);
        	sp.tmp = this;
        	center.loadCenter(sp);
        	return;
        }
 		if(src == closeButton) {
        	int x = saveSco();
        	if(x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION)
        		return;
        	done = true;
        	if(editMode)
        		editComponent.end();
        	
            if(editMode&&POPUP) {
                editModeDialog.setVisible(false);
            } else {
                center.loadCenter(GuiCreator.instance().getScoManagementPanel(sco.getCourse()));
            }
        } else if(src == resetButton) {
            if(editMode) {
                editComponent.reset();
            } else {
                parameterComponent.reset();
            }
        } else if(src == previewButton) {
            Hashtable tmp, old;
            tmp = getLaunchdata();
            old = sco.getLaunchdata();
            sco.setLaunchdata(tmp);
            ScoPanel sp = GuiCreator.instance().previewSco(sco);
            // vreembde fout: preview gaat de eerste keer niet goed, tweede keer wel
            // Ik maak dus nogmaal een scoPanel aan.
            sco.setLaunchdata(tmp);
            sp = GuiCreator.instance().previewSco(sco);
            
            //ScoDialog.showScoPreview(this, sp);
            scoDialog = new ScoDialog(this, sp.getSco().getScoName(), "", false, sp);
            scoDialog.show();
            //
            sco.setLaunchdata(old);
            
            
        } else if (src == saveButton||src == saveBtn) {
        	int x = saveSco();
        	done = false;
//        	if(editMode&&POPUP) {
//                editModeDialog.setVisible(false);
//            }
           
        } 
        else if(src == exportScormButton) {
        	//save2004();
        } else if(src == importScormButton) {
	    	//open();
        } else if(src == exportAppletButton) {
        	//saveApplet();
        }
    }

    // TODO dit is getEditLaunchdata() van Sco
	public Hashtable getLaunchdata() {
		Hashtable tmp;
		if(editMode) {
		    tmp = editComponent.getLaunchData();
		} else {
		    tmp = (Hashtable) sco.getLaunchdata();
		    ConvertorIF convertor = ConvertorCreator.createConverter(ConvertorCreator.CONV_LAUNCHDATA);
		    tmp = (Hashtable) convertor.convertHashtable(tmp, parameters);
		    
		    parameterComponent.addParameters(tmp);
		    tmp = (Hashtable) convertor.createHashtable(tmp, parameters);
		}
		return tmp;
	}
    	
    private int saveSco() {
    	String message;
        Hashtable tmp;
        tmp = getLaunchdata();
        Hashtable old = sco.getLaunchdata();
    	old.remove("language");old.remove("bgcolor");
    	tmp.remove("language");tmp.remove("bgcolor");
    	
    	
    	
        message = TextMapper.getText(TextMapper.GUIPA_MSG_PARAM_SAVE);
        int result = JOptionPane.NO_OPTION;
// Deze tekst is m.i. niet helemaal lekker geformuleerd. Wim
        if (
        		!(compareMap(tmp, old)) &&
        		(result = JOptionPane.showConfirmDialog(this, message, TextMapper.getText(TextMapper.GUIPA_MSG_TTL_PARAM_SAVE), JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {
        	final GuiCreator instance = GuiCreator.instance();
			instance.setWait();setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			sco.setLaunchdata(tmp);
            instance.updateSco(sco);
            MapperCreator.instance(Applet.class).removeObject(sco.getAppletID());
            instance.setReady();setCursor(Cursor.getDefaultCursor());
            return JOptionPane.YES_OPTION;
        }
        forcepaint();
        return result; // NO, CANCEL, CLOSED
    }

    private void forcepaint() {
    	Component c = this;
		Graphics g = c.getGraphics();
		if(g != null) c.paint(g);
	}

	// map equals map, 
    // serialized object not not equal others.
    
	private boolean compareMap(Map tmp, Map old) {
		boolean equals = old.equals(tmp); // dit zou genoeg moeten zijn!
		return equals;
//		if(equals) return true;
//		equals = old.size() == tmp.size();
//		if(!equals) return false;
//
//		Iterator iter = old.keySet().iterator();
//    	while (iter.hasNext()) {
//			Object object = (Object) iter.next();
//			Object v1 = old.get(object);
//			Object v2 = tmp.get(object);
//			if(v1.equals(v2))
//			{
//				// all's well
//			} else {
//				if(v1 instanceof String && v2 instanceof String)
//				{
//// base64 differ, objects may be not!
//					Object vv1 = StringCodeObject.decodeStringToObject(v1.toString());
//					Object vv2 = StringCodeObject.decodeStringToObject(v2.toString());				
//					if(vv1 instanceof Map && vv2 instanceof Map)
//					{
//						equals = compareMap((Map)vv1, (Map)vv2);
//					} else 
//					{
//						equals = vv1 != null && vv1.equals(vv2);
//					}
//					if(!equals) 
//						return false;
//				} else
//				if(v2 != null && v1.getClass().isArray() && v2.getClass().isArray())
//				{
//					Class c1 = v1.getClass();
//					Class c2 = v2.getClass();
//// int[], etc. ????? via Class.getComponentClass() TODO if (! v1.getClass().getComponentClass().isPrimitive() ) ...
//					if( !c1.getComponentType().isPrimitive() && !c2.getComponentType().isPrimitive())
//					{
//						Object[] vva1 = (Object[]) v1; Object[] vva2 = (Object[]) v2;					
//						equals = Arrays.equals(vva1, vva2);
//						if(equals || vva1.length != vva2.length)
//							return equals;
//						// unequal, why
//						for (int i = 0; i < vva2.length; i++) {
//							Object o1 = vva1[i]; Object o2 = vva2[i];
//							if( o1 instanceof Map && o2 instanceof Map ) 
//							{	if( ! compareMap((Map)o1, (Map)o2))
//									return false;
//							} else 
//								if( ! (o1 != null) && ! o1.equals(o2))
//									return false;
//								if( o1 == null && o2 != null)
//									return false;
//						}
//						return true;
//					} else {
//// er zijn 7 types: byte, char, short, int, long, float, double
//						if(v1 instanceof byte[] && v2 instanceof byte[] )
//						{
//							return Arrays.equals( (byte[])v1, (byte[])v2);
//						}
//						if(v1 instanceof int[] && v2 instanceof int[] )
//						{
//							return Arrays.equals( (byte[])v1, (byte[])v2);
//						}
//						// to difficult...
//						return false;
//					}
//				} else
//					return false;
//			}
//		}
//    	return true;
	}
    

	public Object getUserObject() {
		return sco;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		ActionEvent event = new ActionEvent(closeButton, ActionEvent.ACTION_PERFORMED, closeButton.getActionCommand() );
		actionPerformed(event);
		if(!e.getWindow().isShowing())
			e.getWindow().dispose();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setLaunchdata(Hashtable params) {
		if(editMode)
			editComponent.setState(params);
		else 
		{
			sco.setLaunchdata(params);
			// TODO .....  parametercomponent???
		}
		
	}
  
    
}