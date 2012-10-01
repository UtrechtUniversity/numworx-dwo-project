package fi.dwo.client.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fi.beans.base64code.StringCodeObject;
import fi.beans.mathkit.JMathPane;
import fi.beans.mathkit.JMathToolTip;
import fi.beans.mathkit.MathKit;
import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm2xml.Scorm2Xml;
import fi.beans.stringutils.StringUtils;
import fi.dwo.client.domain.ResultScore;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.ResultsModulePanel.ResultsModel;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class  ResultLogger extends JPanel implements ActionListener {

	private static final UserComparator USER_COMPARATOR = new UserComparator();

	private static final long serialVersionUID = 1L;
	private static final String SAVE = "bewaar";
	private static final String SUSPEND_DATA = "cmi.suspend_data";
	private static final String BUTTON_REFRESH = "refresh";
	private static final String BUTTON_LOGANSWERS = "log answers";
	private static final String BUTTON_LOGSCORES = "log scores";
	private static final String BUTTON_LOGERRORCOUNT = "log errors";
	private static final String BUTTON_LOGATTEMPTSCOUNT = "log attempts count";
	private static final String BUTTON_LOGATTEMPTS = "log attempts";
	
	private static final String[] LOG_BUTTONS = {
							BUTTON_LOGSCORES,
							BUTTON_LOGANSWERS,
							BUTTON_LOGERRORCOUNT,
							BUTTON_LOGATTEMPTSCOUNT,
							BUTTON_LOGATTEMPTS
	};
	private static final String TAB = "\t";
	
	private static final String LOGKEY_ANSWER = "logAnswer";
	private static final String LOGKEY_SCORE = "logScore";
	private static final String LOGKEY_MAXSCORE = "logMaxScore";
	private static final String LOGKEY_ERRORCOUNT = "logErrorCount";
	private static final String LOGKEY_ATTEMPTSCOUNT = "logAttemptsCount";
	private static final String LOGKEY_ATTEMPTS = "logAttempts";
	
	private static final String[] LOGKEYS = {
					LOGKEY_SCORE,
					LOGKEY_ANSWER,
					LOGKEY_ERRORCOUNT,
					LOGKEY_ATTEMPTSCOUNT,
					LOGKEY_ATTEMPTS
	};

	protected static final String RAWDATA = "RAWDATA", PARTIAL="PARTIAL";
	
	private LogTable[] table = new LogTable[LOGKEYS.length];
	private JTable   cmiTable, leerlingTable, partialTable;
	private JViewport leerlingView;
	//private Map itemScores;
	//private Map logAnswers;
	//private Map logScores;
	private DefaultTableModel model, cmiModel, leerlingModel, partialModel;
	private User[] leerlingen;
	
	private Sco sco;
	private SchoolClass schoolClass;
	private List keys = new ArrayList();
	
	private String logModeKey = LOGKEY_SCORE;
	private JPanel contentPane;
	
	private Box b;

	private ArrayList cmiKeys = new ArrayList();

	private JScrollPane[] scrollPane;

	private JTabbedPane tabpane;
	
	
	public void alert(Throwable t) {
		JOptionPane.showMessageDialog(this, t.toString());
		t.printStackTrace();
		throw new RuntimeException(t);
	}
		
	public static void showLogs(Sco sco, SchoolClass schoolClass) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new ResultLogger(sco, schoolClass));
        frame.setTitle(TextMapper.getText("Overzicht Logs"));
        frame.pack();
        frame.setSize(800,600);
        frame.show();        
	}
	
	boolean fuse;
	synchronized void setOldMode() {
		if(!fuse)
		{
			tabpane.remove(1);
			tabpane.remove(1);
			tabpane.remove(1);
			tabpane.remove(1);
			LogTable over = table[0];
			table = new LogTable[1];
			table[0] = over;
			fuse = true;		// one shot
		}
	}

	JTable getTable(int index) {
		if(index < table.length)
			return table[index];
		return cmiTable;
	}
	
	public ResultLogger(Sco sco, SchoolClass schoolClass) {
		this.sco = sco;
		this.schoolClass = schoolClass;
		Box v = Box.createVerticalBox();
		b = Box.createHorizontalBox();
		
		JButton btn = null;
		b.add(Box.createHorizontalStrut(30));
		btn = new JButton(BUTTON_REFRESH);
		btn.setActionCommand(BUTTON_REFRESH);
		btn.addActionListener(this);
		b.add(btn);
		
		b.add(Box.createHorizontalStrut(30));		
		b.add(Box.createHorizontalGlue());		
		v.add(Box.createVerticalStrut(10));
		v.add(b);
		
		model = new DefaultTableModel(1, 1);
		scrollPane = new JScrollPane[table.length+2];
		ChangeListener changeListener = new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				int y2 = ((JViewport) e.getSource()).getViewPosition().y;
				leerlingView.setViewPosition(new Point(0, y2));
			} 
	};
		for(int i = 0; i < table.length; i++) {
			contentPane = new JPanel(new BorderLayout());
			table[i] = new LogTable(model);
			table[i].setEnabled(false);
			table[i].setLogMode(LOGKEYS[i]);
			contentPane.add(table[i],BorderLayout.CENTER);
			contentPane.add(table[i].getTableHeader(),BorderLayout.NORTH);
			scrollPane[i]= new JScrollPane(contentPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane[i].getViewport().addChangeListener(changeListener);}
		
		btn = new JButton("Bewaar");
		btn.setActionCommand(SAVE);
		btn.addActionListener(this);
		
		setLayout(new BorderLayout(5,5));
		add(v, BorderLayout.NORTH);
		tabpane = new JTabbedPane();
		for(int i = 0; i < table.length; i++)
		tabpane.add(scrollPane[i], LOG_BUTTONS[i]);
		ChangeListener l = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e) {
				int index = tabpane.getSelectedIndex();
				if(index < table.length)
					logModeKey = table[index].logModeKey;
				else if(index == table.length)
					logModeKey = RAWDATA;
				else 
					logModeKey = PARTIAL;
				if(index < scrollPane.length) {
					//resizeTable();
					int y = scrollPane[index].getViewport().getViewPosition().y;
					leerlingView.setViewPosition(new Point(0,y));
					leerlingTable.setRowHeight(getTable(index).getRowHeight());
				}			
			}

		};
		tabpane.addChangeListener(l);
		tabpane.validate();
		JSplitPane split;
		leerlingModel = new DefaultTableModel(1,1);
		leerlingTable = new JTable(leerlingModel);
		leerlingTable.setFont(GuiConstants.NORMAL_TEXT);
		cmiModel = new DefaultTableModel(1,1);
		cmiTable = new JTable(cmiModel);
		cmiTable.setDefaultRenderer(Object.class, new CMIRenderer());
		JPanel pane2 = new JPanel(new BorderLayout());
		pane2.add(cmiTable.getTableHeader(), BorderLayout.NORTH);
		pane2.add(cmiTable, BorderLayout.CENTER);
		scrollPane[table.length] = new JScrollPane(pane2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane[table.length].getViewport().addChangeListener(changeListener);
		tabpane.add(scrollPane[table.length], "log data");
		partialModel = new DefaultTableModel(1,1);
		partialTable = new JTable(partialModel);
		pane2 = new JPanel(new BorderLayout());
		pane2.add(partialTable.getTableHeader(), BorderLayout.NORTH);
		pane2.add(partialTable, BorderLayout.CENTER);
		scrollPane[table.length+1] = new JScrollPane(pane2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane[table.length+1].getViewport().addChangeListener(changeListener);
		tabpane.add(scrollPane[table.length+1], "deel-scores");
		
		
		
		
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(100);
		Box vbox = Box.createVerticalBox();
		vbox.add(Box.createVerticalStrut(26)); // FIXME: height of tabs!  experimental value
		Box vbox2 = Box.createVerticalBox();
		vbox2.add(leerlingTable.getTableHeader());
		vbox2.add(leerlingTable);
		leerlingView = new JViewport();
		leerlingView.setView(vbox2);
		vbox.add(leerlingView);
		vbox.add(Box.createVerticalStrut(20)); // FIXME height of scrollbar
		split.add(vbox);
		
		split.add(tabpane);
		
		add(split, BorderLayout.CENTER);
		add(btn, BorderLayout.SOUTH);
		
		requestLog();
		requestCMIData();
		requestPartialScore();
		resizeTable();
	}
	
	private void requestPartialScore() {
		leerlingen = schoolClass.getStudents();
		Arrays.sort(leerlingen, USER_COMPARATOR);
		partialModel.setRowCount(leerlingen.length+1);
		SortedSet pages = new TreeSet();
		Map[] lists = new Map[leerlingen.length];
		sco.dwo = GuiCreator.instance().getDWO();
		for(int i = 0; i < leerlingen.length; i++) {
			User u = leerlingen[i];
			sco.setUser(u);
			List list = sco.getPartialScoreIF().getScoreMapList(sco);
			lists[i] = new HashMap();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Map object = (Map) iterator.next();
				Object page = object.get(PartialScoreIF.LOCATION);
				pages.add(page);
				lists[i].put(page, object);
			}
		}
		sco.setUser(User.getCurrentUser());
		partialModel.setColumnCount(pages.size()*2);
		for (int i = 0; i < leerlingen.length; i++) {
			int j = 0;
			for (Iterator iterator = pages.iterator(); iterator.hasNext();j+=2) {
				Object object = iterator.next();
				Map map = (Map)lists[i].get(object);
				if(map == null)
					continue;
				
				partialModel.setValueAt(map.get(PartialScoreIF.SCORE_RAW), i+1, j);
				Object o = map.get(PartialScoreIF.SCORE_MAX);
				if(o != null) {
					partialModel.setValueAt(o, 0, j);
				}
				o = map.get(PartialScoreIF.DESCRIPTION);
				if(o != null) {
					partialModel.setValueAt(o, 0, j+1);
				}
				o = map.get(PartialScoreIF.SESSION_TIME);
				partialModel.setValueAt(o, i+1, j+1);
			}
			
		}
		TableColumnModel columnModel = partialTable.getColumnModel();
		int j = 0;
		for (Iterator iterator = pages.iterator(); iterator.hasNext();j+=2) {
			Object object = iterator.next();
			columnModel.getColumn(j).setHeaderValue(object + " score");
			columnModel.getColumn(j+1).setHeaderValue("tijdsduur");
		}
		
	}

	public void resizeTable()
	{
		//if(true)return;
		
		for(int i = 0; i < table.length; i++)
		{
			JTable tablei = table[i];
			TableUtil.setJTableSizes(tablei);
		}
		TableUtil.setJTableSizes(cmiTable);
		TableUtil.setJTableSizes(partialTable);
		leerlingTable.setRowHeight(getTable(0).getRowHeight());
		leerlingTable.setRowMargin(cmiTable.getRowMargin());
		cmiTable.setRowHeight(cmiTable.getRowHeight());
		cmiTable.setRowMargin(cmiTable.getRowMargin());
		contentPane.revalidate();
	}

	public void requestLog() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			leerlingen = schoolClass.getStudents();
			Arrays.sort(leerlingen, USER_COMPARATOR);
			model.setRowCount(leerlingen.length+1);
			leerlingModel.setRowCount(leerlingen.length+1);
			//model.setValueAt("Naam", 0, 0);
			
			SortedSet set = new TreeSet();
			keys.clear();
			Map[] data = new Map[leerlingen.length];
			for(int i = 0; i < leerlingen.length; i++) {
				User leerling = leerlingen[i];
				String suspendData = getSuspendData(leerling, sco);
				Map strings = getLog(suspendData);
				data[i] = strings;
				if(strings == null)	continue;
				set.addAll(strings.keySet());
			}
			
			keys.addAll(set);
			model.setColumnCount(Math.max(keys.size(),1));
			
			for(int j = 0; j < table.length; j++) {
				JTable tablei = table[j];
				TableColumnModel columnModel = tablei.getColumnModel();
				if(keys.isEmpty())
					columnModel.getColumn(0).setHeaderValue("N/A"); // A is zo slordig.
				else
				for (int i = 0; i < keys.size(); i++) {	
					columnModel.getColumn(i).setHeaderValue(keys.get(i));
				}
			}
			TableColumnModel columnModel = leerlingTable.getColumnModel();
			columnModel.getColumn(0).setHeaderValue("Naam");
			
			
			for (int i = 0; i < leerlingen.length; i++) {
				User leerling = leerlingen[i];
				int i1 = i+1;
				leerlingModel.setValueAt(leerling.getName(), i1, 0);
				Map strings = data[i];
				if(strings == null)
					continue;
				Iterator iterator;
				iterator = strings.entrySet().iterator();
				for (int j = 0; j < keys.size(); j++) {
					model.setValueAt(null, i1, j);
				}
				while (iterator.hasNext()) {
					Entry object = (Entry) iterator.next();
					Object key = object.getKey();
					Object value = object.getValue();
					int index = getIndex(key);
					model.setValueAt(value, i1, index);
					if(value instanceof Hashtable && ((Hashtable)value).containsKey(LOGKEY_MAXSCORE))model.setValueAt(value, 0, index);
				}
			}
		} catch (Exception e1) {
			alert(e1);
		}
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	// Rauwe data display
	public void requestCMIData() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			leerlingen = schoolClass.getStudents();
			Arrays.sort(leerlingen, USER_COMPARATOR);
			cmiModel.setRowCount(leerlingen.length+1);
			
			SortedSet set = new TreeSet();
			cmiKeys.clear();
			Map[] data = new Map[leerlingen.length];
			for(int i = 0; i < leerlingen.length; i++) {
				User leerling = leerlingen[i];
				Properties CMIData = getCMIData(leerling, sco);
				data[i] = CMIData;
				if(CMIData == null)	continue;
				set.addAll(CMIData.keySet());
// deze is twijfelachtig.....			
				if(CMIData.containsKey("cmi.interactions.0.id"))
				{
					setAttempts(CMIData, i+1);
				}
				
			}
			
			cmiKeys.addAll(set);
			cmiModel.setColumnCount(cmiKeys.size());
			
				TableColumnModel columnModel = cmiTable.getColumnModel();
				for (int i = 0; i < cmiKeys.size(); i++) {	
					columnModel.getColumn(i).setHeaderValue(cmiKeys.get(i));
				}
			
			
			for (int i = 0; i < leerlingen.length; i++) {
				User leerling = leerlingen[i];
				int i1 = i+1;
				Map strings = data[i];
				if(strings == null)
					continue;
				Iterator iterator;
				iterator = strings.entrySet().iterator();
				for (int j = 0; j < cmiKeys.size(); j++) {
					cmiModel.setValueAt(null, i1, j);
				}
				while (iterator.hasNext()) {
					Entry object = (Entry) iterator.next();
					Object key = object.getKey();
					Object value = object.getValue();
					int index = cmiKeys.indexOf(key);
					cmiModel.setValueAt(value, i1, index);
				}
			}
		} catch (Exception e1) {
			alert(e1);
		}
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	
	
	
	private void setAttempts(Properties data, int i) {
		for(int j = 0; true; j++)
		{
			String prefix = "cmi.interactions." + j + ".";
			String key = data.getProperty(prefix + "id");
			if(key == null)
				break;
			int z = keys.indexOf(key);
			Map map = (Map) model.getValueAt(i, z);
			List v = (List)map.get(LOGKEY_ATTEMPTS);
			if(v == null){
				v = new Vector();
				map.put(LOGKEY_ATTEMPTS, v);
			}
			v.add(data.getProperty(prefix + "learner_response")+ ";");
		}
	}

	private Properties getCMIData(User u, Sco s) throws PersistenceException {
		final PersistenceFacade instance = PersistenceFacade.instance();
		String r = instance.LMSGetValue(s, u, Scorm2Xml.COCD);
		final Scorm2Xml xml = new Scorm2Xml(r);
		String extra; // extra 
		String key;
		
		key = "cmi.core.score.raw";
		extra = instance.LMSGetValue(s, u, key);
		if(extra.length()>0)xml.setValue(key, extra);
		
		key = "cmi.core.total_time";
		extra = instance.LMSGetValue(s, u, key);
		if(extra.length()>0)xml.setValue(key, extra);

		key = "cmi.core.session_time"; //????
		if(extra.length()>0)extra = instance.LMSGetValue(s, u, key);
		xml.setValue(key, extra);
		
		
		
		return xml.toProperties();
	}

	public void actionPerformed(ActionEvent e) {
		if(BUTTON_REFRESH == e.getActionCommand()){
			requestLog();
			requestCMIData();
			requestPartialScore();
			resizeTable();
		}
		else if(e.getActionCommand() == SAVE)
		{
			JFileChooser chooser = new JFileChooser();
			int result = chooser.showSaveDialog(this);
			if(result == JFileChooser.APPROVE_OPTION)
			{
		        File f = chooser.getSelectedFile();
		        if(logModeKey.equals(PARTIAL))
		        	saveTable(f, partialTable);
		        else if(logModeKey.equals(RAWDATA))
				{
					saveTable(f, cmiTable);
					
				} else	
				if(logModeKey.equals(LOGKEY_ATTEMPTS)) {
                    try {
                        PrintWriter out = new PrintWriter(new FileWriter(f));
                        int len = model.getRowCount();
                        int width = model.getColumnCount();
                        for(int i = 0; i < len; i++) {
                        	for(int j = 0; j < 33 &&i==0; j++) {
                                if(j==0){
                                	out.print("Naam");
                                	out.print(TAB);
                                }
                                else if(j==1){
                                	out.print("LogID");
                                	out.print(TAB);
                                }
                                else if(j==2){
                                	out.print("Answer");
                                	out.print(TAB);
                                }
                                else if(j==3){
                                	out.print("Correct");
                                	out.print(TAB);
                                }
                                else if(j==4){
                                	out.print("Errors");
                                	out.print(TAB);
                                }
                                else if(j==5){
                                	out.print("Att. Answer");
                                	out.print(TAB);
                                }
                                else if(j==6){
                                	out.print("Att. StepNr");
                                	out.print(TAB);
                                }
                                else if(j==7){
                                	out.print("Att. Correct");
                                	out.print(TAB);
                                }
                                else if(j==8){
                                	out.print("Att. Score");
                                	out.print(TAB);
                                }
                                else if(j==9){
                                	out.print("Att. Date");
                                	out.print(TAB);
                                }
                                else if(j==10){
                                	out.print("Att. Feedback");
                                	out.print(TAB);
                                }
                                //else {
                                //	out.print("Attempt " + (j-4));
	                            //	out.print(TAB);
	                            //}
                                
                            }
                        	out.println();
                        	out.println();
                        	
                        	Object value = leerlingModel.getValueAt(i, 0);
                            if(value == null) value = "";
                            out.println(value);
                            out.print("");
                            out.print(TAB);
                            
                            
                            for(int j = 0; j < width; j++) {
                                TableColumnModel columnModel = table[0].getColumnModel();
                                value = columnModel.getColumn(j).getHeaderValue();
                                out.print(value);
                                out.print(TAB);
                                
                                value = model.getValueAt(i, j);
                                if(value!=null && value instanceof Hashtable) { 
	                                
                                	String answer = (String)((Hashtable)value).get(LOGKEY_ANSWER);
	                                if(answer == null) answer = "";
	                                out.print(answer);
                                    out.print(TAB);
                                    
                                    Integer score = (Integer)((Hashtable)value).get(LOGKEY_SCORE);
	                                if(score == null) score = new Integer(0);
	                                
	                                Integer maxScore = (Integer)((Hashtable)value).get(LOGKEY_MAXSCORE);
	                                if(maxScore == null) maxScore = new Integer(0);
	                                
	                                boolean correct = score.intValue()>0 && score.intValue()==maxScore.intValue();
	                                out.print(correct);
                                    out.print(TAB);
                                    
                                    Integer errors = (Integer)((Hashtable)value).get(LOGKEY_ERRORCOUNT);
	                                if(errors == null) errors = new Integer(0);
	                                out.print(errors);
	                                out.print(TAB);
                                }
                                
                                if(value!=null && value instanceof Hashtable) { 
	                                value = ((Hashtable)value).get(logModeKey);
                                }
                                if(value == null) value = new Vector();
                                Vector v = (Vector)value;
                                for(int k = 0; k < v.size(); k++) {
                                    String s = (String)v.get(k);
                                    s = s.replace('\n', ' ');
                                    String[] strings = StringUtils.split(s, ";");
                                    for(int m = 0; m < strings.length; m++) {
                                    	out.print(strings[m]);
                                        out.print(TAB);
                                    }
                                    out.println();
                                    for(int m = 0; m < 5; m++) {
                                    	out.print(TAB);
                                    }
                                }
                                out.println();
                                out.print("");
                                out.print(TAB);
                            }
                            out.println();
                        }
                        out.close();
                    } 
                    catch (IOException e1) {
                        alert(e1);
                    }
			    }
			    else {
	                try {
	                    PrintWriter out = new PrintWriter(new FileWriter(f));
	                    int len = model.getRowCount();
	                    int width = model.getColumnCount();
	                    out.print("Naam");
                        for(int j = 0; j < width; j++) {
                            TableColumnModel columnModel = table[0].getColumnModel();
                            Object value = columnModel.getColumn(j).getHeaderValue();
                            out.print(TAB);
                            out.print(value);
                        }
                        out.println();
                        for(int i = 0; i < len; i++) {
	                    	Object name = leerlingModel.getValueAt(i, 0);
	                    	out.print(name==null?"":name.toString());
	                        for(int j = 0; j < width; j++) {
	                            out.print(TAB);
	                            Object value = model.getValueAt(i, j);
	                            if(value == null)
	                                value = "";
	                            if(value instanceof Hashtable) { 
	                                value = ((Hashtable)value).get(logModeKey);
	                            }
	                            out.print(value);
	                        }
	                        out.println();
	                    }
	                    out.close();
	                } 
	                catch (IOException e1) {
	                    alert(e1);
	                }
			    }
			}			
		}
	}

	private void saveTable(File f, JTable table) {
		TableModel m = table.getModel();
		try {
		    PrintWriter out = new PrintWriter(new FileWriter(f));
			int len = m.getRowCount();
		    int width = m.getColumnCount();
		    out.print(leerlingTable.getColumnModel().getColumn(0).getHeaderValue());
		    for(int j = 0; j<width ; j++)
		    {
		    	out.print(TAB);
		    	out.print(table.getColumnModel().getColumn(j).getHeaderValue());
		    }
		    out.println();
		    for(int i = 0; i < len; i++) {
		    	Object name = leerlingModel.getValueAt(i, 0);
		    	out.print(name==null?"":name.toString());
		        for(int j = 0; j < width; j++) {
		            out.print(TAB);
		            Object value = m.getValueAt(i, j);
		            if(value == null)
		                value = "";
		            out.print(value);
		        }
		        out.println();
		    }
		    out.close();
		} catch(Exception ex)
		{
			
		}
	}

	
	private int getIndex(Object key) {
		return keys.indexOf(key);
	}

	private Map getLog(String suspendData) {
		Object o = StringCodeObject.decodeStringToObject(suspendData);
		Hashtable h = (Hashtable)o;
		Hashtable log = new Hashtable();
		if(h!=null && h.containsKey("log"))
		{	log = (Hashtable)h.get("log");
		}
		return log;
	}
	

	private String getSuspendData(User u, Sco s) throws PersistenceException {
		return PersistenceFacade.instance().LMSGetValue(s, u, SUSPEND_DATA);
	}

	static class UserComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			User l1 = (User)o1;
			User l2 = (User)o2;			
			return l1.getUsername().compareTo(l2.getUsername());
		}

	}
	
	public class LogTable extends JTable {
		
		private String logModeKey = LOGKEY_SCORE;
		private LogRenderer renderer = new LogRenderer();
		private LogEditor editor = new LogEditor();
		
		public JToolTip createToolTip() {
			return new JMathToolTip();
		}

		public LogTable(TableModel model) {	
			super(model);
		}
		
		public void setLogMode(String logModeKey) {	
			this.logModeKey = logModeKey;
			renderer.setLogMode(logModeKey);
			repaint();
		}
		
		public TableCellRenderer getCellRenderer(int row, int column) {
			if(column>=0)
				return renderer;
			return super.getCellRenderer(row, column);
		}
		
		public TableCellEditor getCellEditor(int row, int column) {
			if(column>=0)
				return editor;
			return super.getCellEditor(row, column);
		}
	}
	
	public static class CMIRenderer implements TableCellRenderer
	{
		
		JLabel defaultPane;
		JMathPane   mathPane;
		CMIRenderer() {
			super();
			defaultPane = new JLabel();
			//defaultPane.setEditable(false);
			defaultPane.setOpaque(true);
			mathPane = new JMathPane();
			mathPane.setEditable(false);
		}

		protected Component setCell(Object value) {
			if(String.valueOf(value).startsWith("<math"))
			{
				value = "<html><p>" + value + "</p></html>";
				mathPane.setText(value.toString());
				return mathPane;
			} else {
				defaultPane.setText(value == null ? " " : value.toString() );
				return defaultPane;
			}
			
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component result = setCell(value);
			result.setFont(table.getFont());
			if(isSelected)
				result.setBackground(table.getSelectionBackground());
			else
				result.setBackground(table.getBackground());
			return result;
		}
		
	}
	
	public class LogRenderer extends JLabel implements TableCellRenderer {

		private String logModeKey = "logScore";
		private JMathPane mathpane = new JMathPane();
		public LogRenderer() {
			super();
			setOpaque(true);
			setHorizontalAlignment(CENTER);

		}
		
		public void setLogMode(String logModeKey) {	
			this.logModeKey = logModeKey;
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int col) {
			setFont(GuiConstants.NORMAL_TEXT);
			 {
				setText(" ");
				if(selected)
					setBackground(table.getSelectionBackground());
				else
					setBackground(table.getBackground());
				setToolTipText(null);
			} 
			 if(value!=null) {
				setToolTipText(null);

		        int red = 255;
		        int green = 255;
		        int blue = 0;
		        if(value instanceof Hashtable) {	
		        	Hashtable logMap = (Hashtable)value;
		        	
		        	String answer = " ";
		        	int score = 0;
			        int maxScore = 10;
			        int errorCount = 0;
			        int attemptsCount = 0;
			        Vector attempts = new Vector();
			        
			        boolean hasAnswer = true;
			        boolean hasScore = true;
			        boolean hasMaxScore = true;
			        boolean hasErrorCount = true;
			        boolean hasAttemptsCount = true;
			        boolean hasAttempts = true;
					
					if(logMap.containsKey(LOGKEY_ANSWER))answer = (String)logMap.get(LOGKEY_ANSWER); else hasAnswer = false;
					if(logMap.containsKey(LOGKEY_SCORE))score = ((Integer)logMap.get(LOGKEY_SCORE)).intValue(); else hasScore = false;
					if(logMap.containsKey(LOGKEY_MAXSCORE))maxScore = ((Integer)logMap.get(LOGKEY_MAXSCORE)).intValue(); else hasMaxScore = false;
					if(logMap.containsKey(LOGKEY_ERRORCOUNT))errorCount = ((Integer)logMap.get(LOGKEY_ERRORCOUNT)).intValue(); else hasErrorCount = false;
					if(logMap.containsKey(LOGKEY_ATTEMPTSCOUNT))attemptsCount = ((Integer)logMap.get(LOGKEY_ATTEMPTSCOUNT)).intValue(); else hasAttemptsCount = false;
					if(logMap.containsKey(LOGKEY_ATTEMPTS))attempts = (Vector)logMap.get(LOGKEY_ATTEMPTS); else hasAttempts = false;
					
					float f = 0;
					if(maxScore!=0) f = (float)score/(float)maxScore;
					
			        if (f > 1.0001) {
			            red = 0;
			        } else {
				        if (f < 0.5) {
				            green = (int) (green * (f / 0.5));
				        } else {
				            red = (int) (red * (1 - (f - 0.5) / 0.5));
				        }
			        }
			        
			        if(red>255)red=255;
			        if(green>255)green=255;
			        if(blue>255)blue=255;
			        if(red<0)red=0;
			        if(green<0)green=0;
			        if(blue<0)blue=0;
			        
			        red = red + (255 - red)/2;
			        green = green + (255 - green)/2;
			        blue = blue + (255 - blue)/2;
			        
			        if(logModeKey == LOGKEY_SCORE && hasScore) {
				        if(row==0) {
				        	setFont(new Font("SansSerif",Font.BOLD,12));
				        	setText(""+maxScore);
				        }
				        else {
				        	 //if(!hasAttemptsCount || attemptsCount>0) 
				        		 setBackground(new Color(red, green, blue));
				        	setText(""+score);
				        }
				    }
			        if(logModeKey == LOGKEY_ANSWER && hasAnswer) {
			        	if(row==0) {
				        	
				        }
				        else {
				        	//if(hasScore && (!hasAttemptsCount || attemptsCount>0))
				        		setBackground(new Color(red, green, blue));
				        	setText(answer);
				        	if(hasAttempts)
				        	{
						    	String text = "<html><p>";
								for(int i=0 ; i<attempts.size() ; i++)
								{	String newText = (String)attempts.elementAt(i);
									text = text + newText.substring(0, newText.indexOf(";")) + "<br>";
								}
								text = text + "<p></html>";
								setToolTipText(text);
				        	}
				  // override <math stuff      	
				        	if(answer.startsWith("<math"))
				        	{
				        		mathpane.setText("<html><p style='text-align: center'>"+ answer + "</p></html>");
				        		mathpane.setToolTipText(getToolTipText());
				        		mathpane.setBackground(getBackground());
				        		return mathpane;
				        	}
				        }
			        }
			        if(logModeKey == LOGKEY_ERRORCOUNT && hasErrorCount) {
				       	if(row==0) {
					        	
					    }
					    else {
					    	if(hasScore && (!hasAttemptsCount || attemptsCount>0))setBackground(new Color(red, green, blue));
					        setText(""+errorCount);
				        }
				    }
			        if(logModeKey == LOGKEY_ATTEMPTSCOUNT && hasAttemptsCount) {
				       	if(row==0) {
					        	
					    }
					    else {
					    	if(hasScore)setBackground(new Color(red, green, blue));
					        setText(""+attemptsCount);
				        }
				    }
			        if(logModeKey == LOGKEY_ATTEMPTS && hasAttempts) {
				       	if(row==0) {
					        	
					    }
					    else if(attempts!=null){
					    	if(hasScore)setBackground(new Color(red, green, blue));
					    	String text = "<html>";
							for(int i=0 ; i<attempts.size() ; i++)
							{	String newText = (String)attempts.elementAt(i);
								text = text + newText.substring(0, newText.indexOf(";")) + "<BR>";
							}
							text = text + "</html>";
							setText(text);
							/*if(attempts.size()>4)
							{
								JPanel panel = new JPanel();
								JScrollPane scrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
								//panel.setMaximumSize(new Dimension(80,50));
								panel.add(scrollPane);
								scrollPane.setPreferredSize(new Dimension(120,50));
								scrollPane.revalidate();
								return panel;
							}*/
						}
				    }
		        }
		        else if(value instanceof String) {
		            setText((String)value);
		            //setOldMode(); // support of old-style logging
		        }
				
			}
			
			//setRowHeights(table, row, col);
			
			return this;
		}

		
		private void setRowHeights(JTable table, int row, int column ) {
		    // This line was very important to get it working with JDK1.4
		    TableColumnModel columnModel = table.getColumnModel();
		    setSize(columnModel.getColumn(column).getWidth(), 100000);
		    int height_wanted = (int) getPreferredSize().getHeight();
		    addSize(table, row, column, height_wanted);
		    height_wanted = findTotalMaximumRowSize(table, row);
		    if (height_wanted != table.getRowHeight(row) && height_wanted > 2) {
		      table.setRowHeight(row, height_wanted);
		    }
		}
		
		private final Map cellSizes = new HashMap();

		private void addSize(JTable table, int row, int column,
                  int height) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null) {
				cellSizes.put(table, rows = new HashMap());
			}
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null) {
				rows.put(new Integer(row), rowheights = new HashMap());
			}
			rowheights.put(new Integer(column), new Integer(height));
		}
		  private int findTotalMaximumRowSize(JTable table, int row) {
			    int maximum_height = 0;
			    Enumeration columns = table.getColumnModel().getColumns();
			    while (columns.hasMoreElements()) {
			      TableColumn tc = (TableColumn) columns.nextElement();
			      TableCellRenderer cellRenderer = tc.getCellRenderer();
			      if (cellRenderer instanceof LogRenderer) {
			        LogRenderer tar = (LogRenderer) cellRenderer;
			        maximum_height = Math.max(maximum_height,
			            tar.findMaximumRowSize(table, row));
			      }
			    }
			    return maximum_height;
			  }

			  private int findMaximumRowSize(JTable table, int row) {
			    Map rows = (Map) cellSizes.get(table);
			    if (rows == null) return 0;
			    Map rowheights = (Map) rows.get(new Integer(row));
			    if (rowheights == null) return 0;
			    int maximum_height = 0;
			    for (Iterator it = rowheights.entrySet().iterator();
			         it.hasNext();) {
			      Map.Entry entry = (Map.Entry) it.next();
			      int cellHeight = ((Integer) entry.getValue()).intValue();
			      maximum_height = Math.max(maximum_height, cellHeight);
			    }
			    return maximum_height;
			  }

		
	}
	
	public class LogEditor extends AbstractCellEditor implements  TableCellEditor {
		
		private JButton button = new JButton();
		private Object value;
		private ResultScore domain;
		private ResultsModel model;
		
		public LogEditor() {
			super();
		}

		public Object getCellEditorValue() {
			if(value!=null && value instanceof Hashtable)
			{	int score = ((Integer)((Hashtable)value).get("logScore")).intValue();
				return new Integer(score);
			}
			return value;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.value = value;
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = renderer.getTableCellRendererComponent(table, value, true, true, row, column);
			return component;
		}
		
	}
}
