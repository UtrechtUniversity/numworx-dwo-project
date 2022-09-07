package nl.uu.fi.dwo.mobile.client.sco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Provider;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.rest.dom.xapi.Extensions;

public class CorrectieReview extends CorrectieFacade {
    private Provider<?> correctie;


    CorrectieReview() {
    }

    private CorrectieReview(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot, Logging logging, ActivityInterface a, boolean checkDocent) {
      correctie = CorrectieView.addCorrection(h, iv, widget, maxScore, comRoot, a, checkDocent, logging);
      this.comRoot = comRoot;
      this.iv = iv;
      this.maxScore = maxScore;
      this.logging = logging;
      ObjectMap hh = JSONUtilities.wrapMap(h);
      hh = hh.getObjectMap(CorrectieView.REVIEW_INTERACTIE_DATA);
      if ( hh != null && hh.containsKey(CorrectieView.REVIEW_SCORE_CORRECTIE)) {
        this.lastcorr = hh.getInt(CorrectieView.REVIEW_SCORE_CORRECTIE);
      } 
      if ( hh != null && hh.containsKey(CorrectieView.REVIEW_STUDENTMODELSET)) {
          this.lastSet = hh.getString(CorrectieView.REVIEW_STUDENTMODELSET);
      } 
    }

    @Override
    protected CorrectieFacade create(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot, Logging logging, ActivityInterface a, boolean checkDocent) {
      if(
    	  a.isPremium() &&   		  
    	  a.isReview() && a.isEindtoetsVerzegeld() && maxScore > 0) {
          return new CorrectieReview(h, iv, widget, maxScore, comRoot, logging, a, checkDocent);
      }
      return super.create(h, iv, widget, maxScore, comRoot, logging, a, checkDocent);
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
	private String lastSet;
	private Logging logging;
	
	// send een correctie logentry naar de logger
	private void sendCorrectieStatement(Object o) {
	    ObjectMap map = JSONUtilities.wrapMap( (Map) o);
	    int corr = map.containsKey(CorrectieView.REVIEW_SCORE_CORRECTIE) ?
	    			map.getInt(CorrectieView.REVIEW_SCORE_CORRECTIE) :
	    			0;
	    String set = map.getString(CorrectieView.REVIEW_STUDENTMODELSET);
	    if(corr == lastcorr && Objects.equals(set, lastSet)|| logging == null) return;
	    lastcorr = corr;
	    lastSet  = set;
        int raw = iv.getScore() + corr;
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put("score", Collections.singletonMap("raw", raw));
	    if (raw == maxScore) 
	      parameters.put("success", Boolean.TRUE);
	    else if (corr < 0) 
	      parameters.put("success", Boolean.FALSE);
	    parameters.put("verb", SMLogger.CORRECTED);
	    if (set != null) {
	    	JSONArray value = JSONParser.parseStrict(set).isArray();
	    	if (value != null) {
	    		int len = value.size();
	    		ArrayList<String> list = new ArrayList<>(len);
	    		for (int i = 0; i < len; i++ ) list.add(value.get(i).isString().stringValue());
		    	parameters.put(Extensions.OBJECTIVES, list); // is JSON string, should be List<String>, JSONArray toegestaan?
	    	}
	    }
	    logging.updateLog(parameters);    
	}
	
	
	
	
}
