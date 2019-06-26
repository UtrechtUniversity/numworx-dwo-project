package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public class Util {
  private Util() {}

  interface Template extends SafeHtmlTemplates
  {
  	@SafeHtmlTemplates.Template("<div class='score-template'><div class='score-incorrect' style='width:{0}%' ></div><div class='score-correct' style='width:{1}%'></div></div>")
  	SafeHtml content(float incorrect, float correct);

    @SafeHtmlTemplates.Template("<div class='score-treeItem' style='margin-right:{4}em'><span class='score-title'>{0}</span><span class='score-perc'>{1}%</span><div class='score-template'><div class='score-incorrect' style='width:{2}%'></div><div class='score-correct' style='width:{3}%'></div></div></div>")
    SafeHtml treeItem(String test, int perc, float incorrect, float correct, int margin);
  }
  
  static private Template scoreTemplate = GWT.create(Template.class);

  static SafeHtml percentageBar(int perc) {
    float red = 0.5f, green = 0.5f;
    	if (perc > 50) { red = 0; green = perc-50;
    	} else if (perc < 50) {
    		red = 50 - perc; green = 0;
    	}
    	SafeHtml sh = scoreTemplate.content(red, green);
    return sh;
  }
  
  
  static final int MAX_LEVEL = 3;
  static SafeHtml treeItem (String title, int perc, int level) {
    float red = 0.5f, green = 0.5f;
    if (perc > 50) { red = 0; green = perc-50;
    } else if (perc < 50) {
        red = 50 - perc; green = 0;
    }
    level = Math.max(MAX_LEVEL-level, 0);
    return scoreTemplate.treeItem(title, perc, red, green, level);
  }

}
