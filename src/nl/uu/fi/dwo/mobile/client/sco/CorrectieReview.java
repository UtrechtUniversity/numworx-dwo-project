package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import javax.inject.Provider;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;

public class CorrectieReview extends CorrectieFacade {
    private Provider<?> correctie;

    CorrectieReview() {
    }

    private CorrectieReview(Map<String, Object> h, InteractionView iv, int maxScore) {
      correctie = CorrectieView.addCorrection(h, iv, maxScore);
    }

    @Override
    protected CorrectieFacade create(Map<String, Object> h, InteractionView iv, int maxScore) {
      if(!Memento.instance().isReview()) {
        return super.create(h, iv, maxScore);
      }
      return new CorrectieReview(h, iv, maxScore);
    }

    @Override
    public void correctie(Map<String, Object> h) {
      Object o = correctie.get();
      if (o != null) {
        h.put(CorrectieView.REVIEW_INTERACTIE_DATA, o);
      }
   }
}
