package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class CorrectieReview extends CorrectieFacade {
    private Provider<?> correctie;


    CorrectieReview() {
    }

    private CorrectieReview(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot, Logging logging, ActivityComponent a) {
      correctie = CorrectieView.addCorrection(h, iv, widget, maxScore, comRoot, a);
      this.comRoot = comRoot;
      this.iv = iv;
      this.maxScore = maxScore;
      this.logging = logging;
      ObjectMap hh = JSONUtilities.wrapMap(h);
      hh = hh.getObjectMap(CorrectieView.REVIEW_INTERACTIE_DATA);
      if ( hh != null && hh.containsKey(CorrectieView.REVIEW_SCORE_CORRECTIE)) {
        this.lastcorr = hh.getInt(CorrectieView.REVIEW_SCORE_CORRECTIE);
      } 
    }

    @Override
    protected CorrectieFacade create(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot, Logging logging, ActivityComponent a) {
      if(
    	  a.isPremium() &&   		  
    	  Memento.instance().isReview() && Memento.instance().isEindtoetsVerzegeld() && maxScore > 0) {
          return new CorrectieReview(h, iv, widget, maxScore, comRoot, logging, a);
      }
      return super.create(h, iv, widget, maxScore, comRoot, logging, a);
    }


	@Override
    public void correctie(Map<String, Object> h) {
      if(correctie == null) return;
      Object o = correctie.get();
      if (o != null) {
        h.put(CorrectieView.REVIEW_INTERACTIE_DATA, o);
        sendCorrectieStatement(o);
      }
   }
	
	private OpdrNavIF comRoot;
	private InteractionView iv;
	private int maxScore, lastcorr;
	private Logging logging;
	
	// send een correctie logentry naar de logger
	private void sendCorrectieStatement(Object o) {
	    ObjectMap map = JSONUtilities.wrapMap( (Map) o);
	    int corr = map.getInt(CorrectieView.REVIEW_SCORE_CORRECTIE);
	    if(corr == lastcorr || logging == null) return;
	    lastcorr = corr;
        int raw = iv.getScore() + corr;
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put("score", Collections.singletonMap("raw", raw));
	    if (raw == maxScore) 
	      parameters.put("success", Boolean.TRUE);
	    else if (corr < 0) 
	      parameters.put("success", Boolean.FALSE);
	    parameters.put("verb", SMLogger.CORRECTED);
	    logging.updateLog(parameters);    
	}
	
	
	
	
}
