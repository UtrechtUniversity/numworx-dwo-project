package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;

public class CorrectieFacade {
    private static CorrectieFacade instance = GWT.create(CorrectieFacade.class);
    private static CorrectieFacade NULL = new CorrectieFacade();
    
    private static CorrectieFacade get(Map<String,Object> h, InteractionView view, int maxScore) {
        return instance.create(h, view, view.asWidget(), maxScore);
    }
    public static Widget wrap(Widget view) {
        return instance.prepareWidget(view);
    }
    
    static class CorrectieLayer extends LayoutPanel {
    
      private CorrectieLayer(Widget content) {
         // this.content = content;
          Label tail = new Label();
          add(content);
          add(tail);
          setWidgetLeftRight(content, 0, Unit.PX, 0, Unit.PX);
          setWidgetTopBottom(content, 0, Unit.PX, 0, Unit.PX);
          setWidgetBottomHeight(tail, 0, Unit.PX, 16, Unit.PX);
          setWidgetRightWidth(tail, 0, Unit.PX, 16, Unit.PX);
          tail.setStylePrimaryName(DWOplayer.DWO_BUNDLE.dwoplayercss().correctieTail());
      }
  }
    
  protected Widget prepareWidget(Widget view) {
    if(Memento.instance() != null && Memento.instance().isEindtoetsVerzegeld() && DWOplayer.isPremium()) 
      return new CorrectieLayer(view);
    return (view);
  }

    public static CorrectieFacade get(Map<String,Object> h, InteractionView view, Widget widget, int maxScore, int mode) {
	  if (mode != OpdrNavIF.EINDTOETS) return NULL; // alleen EINDTOETS
      return instance.create(h, view, widget, maxScore);
  }

    protected CorrectieFacade create(Map<String, Object> h, InteractionView view, Widget widget, int maxScore) {
      return NULL;
    }
    
    public void correctie(Map<String,Object> state) {}
    public static  void showReview(Map<String,Object> h0, IsWidget w) {
    	if(h0 == null) return;
    	ObjectMap h = JSONUtilities.wrapMap(h0);
    	h = h.getObjectMap("reviewInteractieData");
    	if(h != null && h.containsKey("reviewScoreCorrectie") && h.getInt("reviewScoreCorrectie")!=0) {
    		w.asWidget().addStyleName(CorrectieView.CORRECTIE);
    	}
    }
	public static CorrectieFacade get(Map<String, Object> h, InteractionView view, int scoreMax,
			int mode) {
		if (mode != OpdrNavIF.EINDTOETS) return NULL; // alleen EINDTOETS
		return get(h,view, scoreMax);
	}
}
