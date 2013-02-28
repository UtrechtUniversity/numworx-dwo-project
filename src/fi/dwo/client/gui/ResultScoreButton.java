//Source file: N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ResultScoreButton.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import fi.beans.scorm2xml.Scorm2Xml;
import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;
import fi.dwo.client.domain.ResultScoreIF;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * Shows a panel with the color representing the score. It also shows the score.
 */
public class ResultScoreButton extends JPanel implements
        ActionListener {
    private float score;

    private ResultScoreIF domain;

    //private ResultsModulePanel resultsModulePanel;
    
    private String toolTip;
    
    private boolean active = true;

    /**
     * Creates a new ResultScoreButton. This represents a ResultScore, and shows
     * a color and the score.
     * 
     * @param score The score to show.
     * @param rs The ResultScore linked to this button.
     */
    public ResultScoreButton(float score, ResultScoreIF rs) {
        super(new BorderLayout());
        
        domain = rs;
        this.score = score;
        int red = 255;
        int green = 255;
        int blue = 0;
        JComponent l;
        if (score != 0) {
            if(score == -1) { //it is -1 he did the course but has no score
                score = 0;
            }
            if (score > 100) {
                red = 0;
            } else {
	            if (score < 50) {
	                green = (int) (green * (score / 50));
	            } else {
	                red = (int) (red * (1 - (score - 50) / 50));
	            }
            }
            if(red>255)red=255;
	        if(green>255)green=255;
	        if(blue>255)blue=255;
	        if(red<0)red=0;
	        if(green<0)green=0;
	        if(blue<0)blue=0;
	        
            this.setBackground(new Color(red, green, blue));

            if (domain.isDeepest()) {
                LinkedLabel ll = new LinkedLabel(((int)Math.round(score)) + " %");
                ll.addActionListener(this);
                ll.setMouseoverColor(Color.black);
                String[] arguments = new String[2];
                arguments[0] = domain.getUserGroup().getName();
                arguments[1] = domain.getLessonGroup().getToolTip();
                String s = TextMapper.getText(TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON);
                s = MessageFormat.format(s, arguments);
                this.setToolTipText(s);
                ll.setToolTipText(s);
                l = ll;
                setBackground(new Color(230,230,230));
            } else {
                l = new JLabel(((int) Math.round(score)) + " %");
                setBackground(new Color(230,230,230));
                //addMouseListener(this);
                           }
        } else {
        	l = new JLabel(" ");
        	setBackground(new Color(230,230,230));
        	//addMouseListener(this);
        	 
        }
            center(l);
            FontMetrics fm;
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(getText(l)) + 10, fm.getHeight());

            this.setSize(l.getSize().width + 5, l.getSize().height + 5);
            this.add(l, BorderLayout.CENTER);

            

    }
    
    /**
     * De text van label of button
     * @param l label of button
     * @return
     */
	private String getText(JComponent l) {
		if(l instanceof JLabel)
			return ((JLabel) l).getText();
		if(l instanceof AbstractButton)
			return ((AbstractButton)l).getText();
		return "";
	}
/**
 * setHorizontalAlignment voor labels en buttons.
 * Helaas geen common class/interface.
 * @param l button of label
 */
	private void center(JComponent l) {
		if(l instanceof AbstractButton)
			((AbstractButton) l).setHorizontalAlignment(SwingConstants.CENTER);
		if(l instanceof JLabel)
			((JLabel) l).setHorizontalAlignment(SwingConstants.CENTER);
	}
    
    public void setBarMode()
    {	setBackground(GuiConstants.MAIN_BACKGROUND);
    	JPanel p = new JPanel();
    	p.setLayout(null);
    	JPanel bar = new JPanel();
    	p.setBounds(0,0,(int)(getSize().width*score/100),getSize().height/3);
    	p.setPreferredSize(p.getSize());
    	p.setBackground(new Color(200,220,240));
    	bar.setBounds(0,0,(int)(getSize().width*score/100),getSize().height/3);
    	bar.setPreferredSize(getSize());
    	bar.setBackground(new Color(0,180,0));
    	
    	add(p,BorderLayout.SOUTH);
    	p.add(bar);
    	active = false;
    }

//	private DefaultCategoryDataset addDataset(Scorm2Xml xml, String series, DefaultCategoryDataset set) {
//		int count = Integer.parseInt(xml.getValue("cmi.objectives._count"));
//        for(int i = 0; i < count; i++) {
//        	String id = xml.getValue("cmi.objectives."+i+".id");
//        	String raw = xml.getValue("cmi.objectives."+i+".score.raw");
//        	double score = Double.parseDouble(raw); // een percentage... tenzij score.max != 100
//        	set.addValue(score, series, id);
//        }
//        return set;
//	}
//    /**
//     * Creates a sample chart.
//     *
//     * @param dataset  the dataset.
//     *
//     * @return The chart.
//     */
//    private JFreeChart createChart(CategoryDataset dataset) {
//        SpiderWebPlot plot = new SpiderWebPlot(dataset);
//       // plot.setStartAngle(54);
//      //  plot.setInteriorGap(0.40);
//        plot.setToolTipGenerator(new StandardCategoryToolTipGenerator());
//        JFreeChart chart = new JFreeChart("Objectives",
//                GuiConstants.NORMAL_TEXT, plot, false);
//        //chart.setBackgroundPaint(getBackground());
//        //ChartUtilities.applyCurrentTheme(chart);
//        return chart;
//    }

    
    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(active)domain.showResult();
        else if (false) // TODO parameter voor testing.... false is productie!
        {
//        	if(score == 0)
//        		return;
//        	Sco sco = (Sco) domain.getLessonGroup();
//        	User user = (User) domain.getUserGroup();
//			String scoName = sco.getScoName();
//			Box content = Box.createVerticalBox();
//			String cocd = GuiCreator.instance().dwo.LMSGetValue(sco, user, "cocd");
//			content.add(new JLabel("Score " + Math.max(score,0)));
//			Scorm2Xml xml = new Scorm2Xml(cocd);
//			String time = "Total time " + xml.getValue("cmi.core.total_time");
//			content.add(new JLabel(time));
//			DefaultCategoryDataset set = addDataset(xml, user.getName(), new DefaultCategoryDataset());
//			set.addValue(score, user.getName(), "score");
//			JFreeChart chart = createChart(set);
//			ChartPanel panel = new ChartPanel(chart, false, false, false, false, true);
//
//			content.add(panel);
//        	JOptionPane.showMessageDialog(this, content,  scoName, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Returns this component.
     * @return This component.
     * @see fi.beans.tooltip.ToolTipIF#getComponent()
     */
    public Component getComponent() {
        return this;
    }

}