package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import javax.inject.Provider;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;

public class CorrectieReview extends CorrectieFacade {
    private Provider<?> correctie;


    CorrectieReview() {
    }

    private CorrectieReview(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot) {
      correctie = CorrectieView.addCorrection(h, iv, widget, maxScore, comRoot);
    }

    @Override
    protected CorrectieFacade create(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore, OpdrNavIF comRoot) {
      if(
    	  DWOplayer.isPremium() &&   		  
    	  Memento.instance().isReview() && Memento.instance().isEindtoetsVerzegeld() && maxScore > 0) {
          return new CorrectieReview(h, iv, widget, maxScore, comRoot);
      }
      return super.create(h, iv, widget, maxScore, comRoot);
    }


	@Override
    public void correctie(Map<String, Object> h) {
      if(correctie == null) return;
      Object o = correctie.get();
      if (o != null) {
        h.put(CorrectieView.REVIEW_INTERACTIE_DATA, o);
      }
   }
}
