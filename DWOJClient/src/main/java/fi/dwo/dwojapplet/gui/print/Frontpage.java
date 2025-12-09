package fi.dwo.dwojapplet.gui.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.dwojapplet.domain.Sco;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

public class Frontpage extends JPanel implements Printable {

	private JLabel title;
	private JComponent description;
	private JLabel person;
	private JLabel results;
	private JLabel date;
	private SCORM12APIInterface api;
	private final String NOW = new Date().toString();
		
	public Frontpage(Sco sco) {
		super(null);
		setOpaque(false);
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		this.title = new JLabel(sco.getScoName());
		this.date  = new JLabel(NOW);
		this.results = new JLabel("Score:");
		this.person  = new JLabel("anoniem");
		JTextArea ta;
		this.description = ta = new JTextArea(sco.getDescription());
		ta.setLineWrap(true);
		api = sco;
		add(this.title);
		add(this.person);
		add(this.results);
		add(this.date);
		add(Box.createVerticalStrut(8));
		add(this.description);	
	}

	public void setPerson(String person)
	{
		this.person.setText(person);
	}
	
	public void setScore(String score) 
	{
		this.results.setText(score); // I18N
	}
	
	private void setDate(Date date) {
		if (date != null) {
			this.date.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, getLocale()).format(date));
		} else
			this.date.setText(NOW);
	}
	
	public void setDescription(String description) {
		remove(this.description);
		JTextArea ta;
		this.description = ta = new JTextArea(description);
		ta.setLineWrap(true);
		add(this.description);
	}
	
	public void setDescription(JComponent description) {
		remove(this.description);
		this.description = description;
		add(description);
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if(pageIndex == 0) {
			//String id = api.LMSGetValue("cmi.learner_id");
			String name = api.LMSGetValue("cmi.learner_name");
			String klas = api.LMSGetValue("dme.team");
			String score = api.LMSGetValue("cmi.score.raw");
			String time  = api.LMSGetValue("cmi.total_time");
			String timestamp = api.LMSGetValue("cmi.comments_from_lms.0.timestamp");
			Date stamp = null;
				try {
					SimpleDateFormat f = new SimpleDateFormat(RestyDateTimeFormat.RESTY_DATETIME_FORMAT);
					if (!timestamp.isEmpty())
						stamp = f.parse(timestamp);
				} catch(Exception oops) {
					//oops.printStackTrace();
					System.err.println(oops);
					stamp = null;
				}
			setDate(stamp);
			setPerson(name + "; " + klas);
 // FIXME I18N
			setScore(MessageFormat.format("Score: {0}, tijd: {1}",  score , time)); 
			
			int x  = (int) pageFormat.getImageableX();
			int y  = (int) pageFormat.getImageableY();
			graphics.translate(x, y);
			int w = (int) pageFormat.getImageableWidth();
			int h = (int) pageFormat.getImageableHeight();
			setSize(w,h);
			doLayout();
			print(graphics);
			return PAGE_EXISTS;
		}
		return NO_SUCH_PAGE;
	}

}
