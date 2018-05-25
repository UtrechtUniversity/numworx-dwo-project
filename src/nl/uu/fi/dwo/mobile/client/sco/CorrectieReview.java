package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import javax.inject.Provider;
import javax.swing.text.LayeredHighlighter.LayerPainter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;

public class CorrectieReview extends CorrectieFacade {
    private Provider<?> correctie;


    CorrectieReview() {
    }

    private CorrectieReview(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore) {
      correctie = CorrectieView.addCorrection(h, iv, widget, maxScore);
    }

    @Override
    protected CorrectieFacade create(Map<String, Object> h, InteractionView iv, Widget widget, int maxScore) {
      if(Memento.instance().isReview() && Memento.instance().isEindtoetsVerzegeld() && maxScore > 0) {
          return new CorrectieReview(h, iv, widget, maxScore);
      }
      return super.create(h, iv, widget, maxScore);
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
