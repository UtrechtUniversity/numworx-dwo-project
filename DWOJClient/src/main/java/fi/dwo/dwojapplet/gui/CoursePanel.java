// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CoursePanel.java
package fi.dwo.dwojapplet.gui;

import fi.beans.mathkit.JMathPane;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;
import fi.dwo.dwojapplet.domain.SchoolAdmin;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.UserResultList;
import fi.dwo.dwojapplet.gui.action.NullStrategy;
import fi.dwo.dwojapplet.gui.wiskopdr.LinkIF;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;
/**
 * This class is a panel witch shows a list of all the SCO's in the specified
 * Course.
 *
 * @author M.J.B. Kupers
 *
 */
public class CoursePanel extends JPanel implements CenterSubPanel,
        ActionListener, LinkIF //,Scrollable
        {

    /* (non-Javadoc)
   * @see fi.dwo.dwojapplet.gui.CenterSubPanel#getSubHeaderColor()
   */
  @Override
  public Color getSubHeaderColor() {
    return Constants.COLOR10;
  }

    private static final int MINWIDTH = 600;

    // if endy + bottom > MINHEIGT -> setBOUNDS(...)
    private static final int MINHEIGHT = (GuiConstants.getDwoProfile() == 51 || GuiConstants.getDwoProfile() == 27) ? 380 : 470, BOTTOM = 20;

    CourseContainer center;
    private String lessonMode = Sco.NORMAL;

    private Course course;

    private JLabel scoListHeader;

    private JTextArea courseDescription;
//    private WiskOpdrPanel wiskOpdrPanel;
    private JTextComponent courseDescriptionHTML;

    private JButton showResultsButton;

    private boolean courseView;

    private JLabel ip;

    private int startY;

    private boolean scoLoading;

    private Object jsObject;

	private WiskOpdrPanel wiskOpdrPanel;

    /**
     * Creates a new Course Panel. The CoursePanel shows an overview of all the
     * sco's of the specified Course.
     *
     * @param course The Course to show.
     */
    public CoursePanel(Course course) {
        this.course = course;
        this.setLayout(null);
        this.setBackground(Constants.COLOR10);
        this.setSize(MINWIDTH, MINHEIGHT);

        ScoLinkedLabel l;
        FontMetrics fm;

        startY = 30;
		String s = course.getDescription();
		courseDescription = new JTextArea();
		boolean htmlMode = false;
		
		if((s != null) && (!s.trim().equals(""))) 
		{
			if(s.startsWith("<html>"))
			{	htmlMode=true;
				courseDescriptionHTML = new JMathPane(DwoHelper.getServerUrlPath()); // was JLABEL
				courseDescriptionHTML.setText(s);
				courseDescriptionHTML.setEditable(false);
				add(courseDescriptionHTML);
				courseDescriptionHTML.setFont(new Font("SansSerif", Font.PLAIN, 13));
				courseDescriptionHTML.setOpaque(false);
				courseDescriptionHTML.setBounds(20,startY,550,110);
				courseDescriptionHTML.setSize(courseDescriptionHTML.getPreferredSize());
				courseDescriptionHTML.setForeground(GuiConstants.MAIN_FOREGROUND);
				startY += courseDescriptionHTML.getHeight() + 10;
			}
			else if(s.startsWith("H4sIAAAAAA"))
			{	htmlMode=true;
				wiskOpdrPanel = WiskOpdr.getWiskOpdrPanel(s, DwoHelper.getAu().getLocale()) ; //TODO java locale
				wiskOpdrPanel.setLocation(20,startY);
				wiskOpdrPanel.setJSObjectOwner(this);
				wiskOpdrPanel.setBackground(getBackground());
	        	add(wiskOpdrPanel);
	        	startY += wiskOpdrPanel.getHeight() + 10;
			}
			else
			{	add(courseDescription);
				htmlMode = course.isNotVisible();
				courseDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
				courseDescription.setOpaque(false);
				courseDescription.setLineWrap(true);
				courseDescription.setEditable(false);
				courseDescription.setWrapStyleWord(true);
				courseDescription.setBounds(20,startY,550,110);
				courseDescription.setText(s);
				courseDescription.setSize(courseDescription.getPreferredSize());
				courseDescription.setForeground(GuiConstants.MAIN_FOREGROUND);
				startY += courseDescription.getHeight() + 10;
			}
		}
		
		
        scoListHeader = new JLabel(TextMapper.getText(TextMapper.GUICO_SCO_LIST_TITLE));
        scoListHeader.setFont(GuiConstants.SCO_HEADER_TEXT);
        scoListHeader.setForeground(GuiConstants.MAIN_FOREGROUND);
        fm = scoListHeader.getFontMetrics(scoListHeader.getFont());
        scoListHeader.setSize(fm.stringWidth(scoListHeader.getText()) + 10, fm.getHeight());
        scoListHeader.setLocation(30, startY);
        if (!course.isNotVisible()) {
            add(scoListHeader);
            startY += scoListHeader.getSize().height + 10;
        }
        Image courseLogo = course.getCourseLogo();
        final ReducedImageIcon image = new ReducedImageIcon(courseLogo);
		ip = new JLabel(image);
        ip.setSize(image.getIconWidth(), image.getIconHeight());
        ip.setLocation(this.getSize().width - ip.getSize().width - 50, startY);
        if (!htmlMode) {
            add(ip);
        }

        int nextY = addScoLinkedLabels();

        /* If the user is a teacher (NOT A SCHOOLADMIN), show a button to go to the results */
        if (GuiCreator.instance().getUser() instanceof Teacher 
            && ! (GuiCreator.instance().getUser() instanceof SchoolAdmin)
            ) {
            showResultsButton = new JButton(TextMapper.getText(TextMapper.GUIMNU_RESULTS));
            showResultsButton.setLocation(30, nextY + 20);
            showResultsButton.addActionListener(this);
            this.add(showResultsButton);
            showResultsButton.setSize(showResultsButton.getPreferredSize());

            nextY += showResultsButton.getSize().height + 10;

        }
// resize panel.
        if (nextY + BOTTOM > MINHEIGHT) {
            setSize(MINWIDTH, nextY + BOTTOM);
        }

        setPreferredSize(getSize());
        //setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    /**
     * @return
     */
    private int addScoLinkedLabels() {
        ScoLinkedLabel l;
        FontMetrics fm;
        int nextY = startY;
        int i;
        if (!course.isNotVisible() && course.getScoList()!=null) {
            for (i = 0; i < course.getScoList().length; i++) {
                l = new ScoLinkedLabel(course.getScoList()[i]);
                l.setForeground(GuiConstants.MAIN_FOREGROUND);
                l.setFont(GuiConstants.SCO_TEXT);
                fm = l.getFontMetrics(l.getFont());
                l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
                l.setLocation(30, nextY);
                l.addActionListener(this);
                this.add(l);
                nextY += l.getSize().height + 3;
            }
        }
        return nextY;
    }

    public void setCourseView(boolean b) {
        // if(b) then deeplink, geen beheerknop?
        courseView = b;
    }

    public boolean getCourseView() {
        return courseView;
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {
        center.showClassList();
        removeButtons();
        if (wiskOpdrPanel != null) 
          wiskOpdrPanel.end();
    }

    /**
     * remove DwoButtons from the panel. refresh() will add them again.
     *
     * @see #refresh()
     */
    private void removeButtons() {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            if (comp instanceof ResultScoreButton) {
                remove(comp);
            }
        }
    }

    private void removeScos() {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            if (comp instanceof ScoLinkedLabel) {
                ((ScoLinkedLabel) comp).removeActionListener(this);
                remove(comp);
            }
        }
    }

    /**
     * Refresh the sco view. Update the buttons width scores.
     */
    private void refresh() {
//        if (wiskOpdrPanel != null) {
//            wiskOpdrPanel.setJSObjectOwner(this); // GLOBAL EVIL
//        }
        removeButtons();
        //ResultsModuleIF results = GuiCreator.instance().dwo.getUserResultsModule(course);
        ResultsModuleIF results = center.getUserResultsModule(course);
        UserResultList scoresults = null;
        if (results != null) {
            Vector v = results.getResults();
            if (v.size() > 0) {
                scoresults = (UserResultList) v.elementAt(0);
            }
        }
        if (scoresults == null) {
            return;
        }
        int ipx = ip.getLocation().x - 10 - 30;
        int nextY = startY;
        int i;
// TODO dit moet veel beter.....
        ScoLinkedLabel l;
        FontMetrics fm;
        if (!course.isNotVisible()) {
            for (i = 0; i < course.getScoList().length; i++) {
                Sco sco = course.getScoList()[i];
                l = new ScoLinkedLabel(sco);
                l.setForeground(GuiConstants.MAIN_FOREGROUND);
                l.setFont(GuiConstants.SCO_TEXT);
                fm = l.getFontMetrics(l.getFont());
                l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
                Component button = scoresults.getResultScore()[i].getGui();
                //System.out.println(i + ": " + scoresults.getResultScore()[i].getScore());
                button.setSize(fm.stringWidth("100 %") + 30, l.getSize().height);
                button.setLocation(ipx - button.getSize().width, nextY - 5);
                ((ResultScoreButton) button).setBarMode();
                button.setVisible(false && sco.isShowScore());
                button.setBackground(getSubHeaderColor());
                this.add(button);
                nextY += l.getSize().height + 3;
            }
        }
    }

    /**
     * Sets the centerpanel to communicate with.
     *
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CourseContainer centerPanel) {
        center = centerPanel;
        //center.hideClassList(); obsolete!
        refresh();
    }

    /**
     * Returns a Panel that can function as a header panel.
     *
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public JComponent getHeaderPanel() {

        HeaderPanel hp = new HeaderPanel(course.getName(), true);
        hp.setBackground(getSubHeaderColor());
//        if (false) {
//            hp.setHorizontalAlignment(SwingConstants.LEFT);
//            Image courseLogo;
//            courseLogo = course.getCourseLogo();
//            MediaTracker tr = new MediaTracker(this);
//            tr.addImage(courseLogo, 0);
//            try {
//                tr.waitForAll();
//            } catch (Exception e) {
//            }
//            courseLogo = courseLogo.getScaledInstance(courseLogo.getWidth(null) / 2, courseLogo.getHeight(null) / 2, Image.SCALE_SMOOTH);
//            tr.addImage(courseLogo, 0);
//            try {
//                tr.waitForAll();
//            } catch (Exception e) {
//            }
//            ImageIcon icon = new ReducedImageIcon(courseLogo);
//            hp.setIcon(icon);
//            hp.setIconTextGap(20);
//            Border oldBorder = hp.getBorder();
//            Border gapBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
//            hp.setBorder(BorderFactory.createCompoundBorder(oldBorder, gapBorder));
//        }
        if (!getCourseView()) {
            hp.setButtonBox(GuiCreator.instance().getButtonBox(this));
        }
        return hp;
    }

    static private final NullStrategy NULS = new NullStrategy();

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof ScoLinkedLabel && !scoLoading) {
            scoLoading = true;
            Sco sco = ((ScoLinkedLabel) e.getSource()).getSco();
            if (!scoLoading) {
                GuiCreator.instance().setWait();
            }
            GuiCreator.instance().getMainPanel().center.setStrategy(NULS);
            final Sco s = sco;
            // Java 1.6 minimum
                    CenterSubPanel csp = GuiCreator.instance().getHTML5ScoPanel(s);
                        if (csp != null) {
                            s.setLessonMode(getLessonMode());
                            center.loadTotal(csp);
                        }
                    synchronized (CoursePanel.this) {
                        GuiCreator.instance().getMainPanel().center.setStrategy(null);
                        GuiCreator.instance().setReady();
                        scoLoading = false;
                    }
        } else if (e.getSource() == showResultsButton) {
            GuiCreator.instance().dwo.clearResultsModule();
            CenterSubPanel cp = GuiCreator.instance().getResultPanel(course);
            center.loadCenter(cp);
        }
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * implements CenterSubPanel, uses {@link #setCenterPanel(CourseContainer)}.
     *
     * @see CenterSubPanel#setCenterPanel(CenterPanel)
     */
    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        CourseContainer cc = centerPanel;
        setCenterPanel(cc); // geen recursie
    }

    /**
     * @return the lessonMode
     */
    String getLessonMode() {
        return lessonMode;
    }

    /**
     * @param lessonMode the lessonMode to set
     */
    public void setLessonMode(String lessonMode) {
        this.lessonMode = lessonMode;
        if (showResultsButton != null) {
            showResultsButton.setVisible(lessonMode != Sco.BROWSE);
        }

    }

    @Override
    public Object getUserObject() {
        return course;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == course) {
            System.err.println("refresh course");
            removeButtons();
            removeScos();
            int nexty = addScoLinkedLabels();
            if (showResultsButton != null) {
                showResultsButton.setLocation(30, nexty + 20);
            }
            refresh();
            repaint();
        }

    }

    public Course getCourse() {
        return course;
    }

    public boolean gotoScoNr(String rest) {
        return "true".equals(Sco.gotoSco(rest, this, course, this));
    }

    public Object getJSObject() {
        if (jsObject == null) {
            setJSObject(null);
        }
        return jsObject;
    }

    public AppletContext getAppletContext() {
        return DwoHelper.getApplet().getAppletContext();
    }

    public void setJSObject(Object window) {
        jsObject = window;
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    private Dimension unit = new Dimension(1, 1);

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      if (orientation == SwingConstants.VERTICAL) {
        return unit.height; // 1 line
      } else {
        return unit.width;
      }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      if (orientation == SwingConstants.VERTICAL) {
        return Math.max(unit.height, visibleRect.height - unit.height); // 1 page
      } else {
        return visibleRect.width;
      }
    }

    public boolean getScrollableTracksViewportWidth() {
      if (wiskOpdrPanel != null) {
          return getParent().getWidth() > getPreferredSize().width;
      }
      return true; // platte tekst of HTML
    }

    public boolean getScrollableTracksViewportHeight() {
      return getParent().getHeight() > getPreferredSize().height;
    }
}
