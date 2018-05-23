package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.utils.HasHide;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class CorrectieView extends Composite implements HasHide {

  private static final String REVIEW_SCORE_CORRECTIE = "reviewScoreCorrectie";
  public static final String REVIEW_INTERACTIE_DATA = "reviewInteractieData";
  
  public static Provider<Map<String,Object>> addCorrection(Map<String,Object> map, InteractionView iv, int scoreMax) {
    final Widget w = Widget.asWidgetOrNull(iv);
    w.addStyleName("correctie");
    return new Provider<Map<String, Object>>() {

      Map<String, Object> result = new HashMap<>();
      PopupPanel popup;
      @Override
      public Map<String, Object> get() {
        if(result.isEmpty())
          return null;
        return result;
      }

      {
        ObjectMap h = JSONUtilities.wrapMap(map);
        h = h.getObjectMap(REVIEW_INTERACTIE_DATA);
        if(h != null) {
          if (h.containsKey(REVIEW_SCORE_CORRECTIE))
            result.put(REVIEW_SCORE_CORRECTIE, h.getInt(REVIEW_SCORE_CORRECTIE));
        }
        w.addDomHandler(event -> {
          int x = event.getRelativeX(w.getElement());
          int y = event.getRelativeY(w.getElement());
          int width = w.getOffsetWidth() - 16;
          int height = w.getOffsetHeight() - 16;
          if (x > width && y > height) {
            if(popup != null) {
              popup.showRelativeTo(w);
            } else {
              //iv.kijkNa();iv.getState(); // wat is nodig voor score?????? FIXME
              popup = startCorrection(result, w, iv.getScore(), scoreMax);
            }
          }
        }, MouseUpEvent.getType());
      }
    };

  }
  
  
  
  private static PopupPanel startCorrection(Map<String, Object> map, Widget w, int score, int scoreMax) {
      CorrectieView view = new CorrectieView();
      view.setObject(map);      
      Object correctie = map.getOrDefault(REVIEW_SCORE_CORRECTIE,"0");
      view.correctie.setText(correctie.toString());
      view.max.setText(Integer.toString(scoreMax));
      view.score.setText(Integer.toString(score));
      PopupPanel popup = new PopupPanel();
      popup.setWidget(view);
      view.setPopup(popup);      
      popup.showRelativeTo(w);
      return popup;
  }



  private Map<String,Object> object;

  private void setObject(Map<String, Object> map) {
    this.object = map;
  }



  private PopupPanel popup;



  private void setPopup(PopupPanel popup) {
    this.popup = popup;
    PopupFacade.addPopup(this);
    
  }



  private static CorrectieViewUiBinder uiBinder = GWT.create(CorrectieViewUiBinder.class);

  interface CorrectieViewUiBinder extends UiBinder<Widget, CorrectieView> {}

  @UiField HasText max, score;
  @UiField TextBox correctie;

  public CorrectieView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("ok")
  void onOk(ClickEvent e) {
    String result = correctie.getText();
    int n = Integer.parseInt(result);
    object.put(REVIEW_SCORE_CORRECTIE, (n));
    hide();
  }

  @UiHandler("cancel")
  void onCancel(ClickEvent e) {
    Object n = object.getOrDefault(REVIEW_SCORE_CORRECTIE, "0");
    correctie.setText(n.toString());
    hide();
  }

  @Override
  public void hide() {
    popup.hide();
  }
 
}
