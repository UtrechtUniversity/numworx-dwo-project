package nl.uu.fi.dwo.mobile.client.sco;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView;
import nl.uu.fi.dwo.mobile.client.ui.views.DocentCorrectie;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.DWOPopupPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.PopupButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class CorrectieFacade {
    private static CorrectieFacade instance = GWT.create(CorrectieFacade.class);
    private static CorrectieFacade NULL = new CorrectieFacade();
    
//    @Deprecated
//    public static CorrectieFacade get(Map<String,Object> h, InteractionView view, int maxScore, OpdrNavIF comRoot) {
//        return instance.create(h, view, view.asWidget(), maxScore, comRoot, null, null);
//    }
 
    public static Widget wrap(Widget view, ActivityInterface activity) {
        return instance.prepareWidget(view, activity);
    }
    
    static class CorrectieLayer extends LayoutPanel implements AcceptsOneWidget {
    
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

	@Override
	public void setWidget(IsWidget tail) {
		add(tail);
        setWidgetBottomHeight(tail, 0, Unit.PX, 16, Unit.PX);
        setWidgetRightWidth(tail, 0, Unit.PX, 16, Unit.PX);		
	}
  }
    
  protected Widget prepareWidget(Widget view, ActivityInterface activity) {
    if(activity.isEindtoetsVerzegeld() && activity.isPremium()) 
      return new CorrectieLayer(view);
    return (view);
  }

  @Deprecated
  public static CorrectieFacade get(Map<String,Object> h, InteractionView view, Widget widget, int maxScore, OpdrNavIF comRoot) {
	  //if (comRoot.getMode() != OpdrNavIF.EINDTOETS) return NULL; // alleen EINDTOETS, nee
      return instance.create(h, view, widget, maxScore, comRoot, null, null, false);
  }

    protected CorrectieFacade create(Map<String, Object> h, InteractionView view, Widget widget, int maxScore, OpdrNavIF comRoot, Logging logging, ActivityInterface a, boolean checkDocent) {
      return NULL;
    }
    
    public void correctie(Map<String,Object> state) {}

    @Deprecated
    public static void showReview(Map<String,Object> h0, IsWidget w, ActivityComponent a) {
    	if(h0 == null || a.memento().getLessonMode() != LessonMode.browse) return;
    	ObjectMap h = JSONUtilities.wrapMap(h0);
    	h = h.getObjectMap("reviewInteractieData");
    	if(h != null && h.containsKey("reviewScoreCorrectie") && h.getInt("reviewScoreCorrectie")!=0) {
    		w.asWidget().addStyleName(CorrectieView.CORRECTIE);
    	}
    }
    
    public static void showReview(Map<String,Object> h0, AcceptsOneWidget p, InteractionView view, int maxScore, ActivityInterface a) {
    	if(h0 == null || a.getLessonMode() != LessonMode.browse) return;
    	ObjectMap h = JSONUtilities.wrapMap(h0);
    	h = h.getObjectMap(CorrectieView.REVIEW_INTERACTIE_DATA);
    	if (h == null) return;
		final int scoreCorrectie = h.containsKey(CorrectieView.REVIEW_SCORE_CORRECTIE) ? h.getInt(CorrectieView.REVIEW_SCORE_CORRECTIE):0;
		final String scoreComment = h.containsKey(CorrectieView.REVIEW_SCORE_COMMENT) ? h.getString(CorrectieView.REVIEW_SCORE_COMMENT): "";
 	
    	if ( 
    			scoreCorrectie != 0 ||
    			(scoreComment != null && !scoreComment.trim().isEmpty())
    	   ) { 

    		PushButton pb = new PushButton();
    		pb.addStyleName(CorrectieView.CORRECTIE);
    	
// pushbutton style
    		pb.setPixelSize(16, 16);
    		Style style = pb.getElement().getStyle();
    		style.setMargin(0, Unit.PX);
    		style.setPadding(0, Unit.PX);
    		style.setPosition(Position.ABSOLUTE);
    		style.setBottom(0, Unit.PX);
    		style.setRight(0, Unit.PX);
    		style.setBorderStyle(BorderStyle.NONE);
    		style.setPropertyPx("borderRadius", 0);
    		style.setPropertyPx("outlineWidth", 0);
    		style.setProperty("pointerEvents", "auto");
   		pb.addClickHandler(ev -> { 
    			DocentCorrectie correctie = new DocentCorrectie(maxScore, view.getScore(), scoreCorrectie, scoreComment);
    			DWOPopupPanel panel = new DWOPopupPanel(Text.constants.docentCorrectieTitle(), PopupButton.NOVIEW_LISTENER);
    			panel.addContent(correctie);
    			panel.showRelativeTo(pb);
    			PopupFacade.addPopup(panel::hide);
    		});
    		p.setWidget(pb);
    	}
    }
    public static CorrectieFacade get(HashMap<String, Object> h, InteractionView view,
        int scoreMax, OpdrNavIF comRoot, Logging logging, ActivityComponent a) {
      return instance.create(h, view, view.asWidget(), scoreMax, comRoot, logging, a, false);
    }
    public static CorrectieFacade get(HashMap<String, Object> h, InteractionView view, IsWidget w,
        int scoreMax, OpdrNavIF comRoot, Logging logging, ActivityComponent a) {
      return instance.create(h, view, w.asWidget(), scoreMax, comRoot, logging, a, false);
    }

	public static CorrectieFacade get(HashMap<String, Object> h, TextEditor view, int scoreMax, OpdrNavIF comRoot,
			Logging logging, ActivityInterface activity, boolean checkDocent) {
		return instance.create(h, view, view.asWidget(), scoreMax, comRoot, logging, activity, checkDocent);
	}

	public static CorrectieFacade get(HashMap<String, Object> h, StubView view, Widget widget, int scoreMax,
			OpdrNavIF comRoot, Logging logging, ActivityComponent activity, boolean checkDocent) {
		return instance.create(h, view, widget, scoreMax, comRoot, logging, activity, checkDocent);
	}
   
    
    
    
}
