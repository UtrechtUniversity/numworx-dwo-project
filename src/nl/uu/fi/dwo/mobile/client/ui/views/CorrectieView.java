package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.pointerevents.client.PointerUpEvent;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.DWOPopupPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.PopupButton;
import nl.uu.fi.dwo.mobile.utils.HasHide;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class CorrectieView extends Composite implements HasHide {

  public static final String REVIEW_SCORE_CORRECTIE = "reviewScoreCorrectie";
  public static final String REVIEW_SCORE_COMMENT   = "reviewScoreComment";
  public static final String REVIEW_INTERACTIE_DATA = "reviewInteractieData";
  
  public static final String CORRECTIE = DWOplayer.DWO_BUNDLE.dwoplayercss().correctie();
  public static final String CORRECTED = DWOplayer.DWO_BUNDLE.dwoplayercss().corrected();
  
  public static Provider<Map<String,Object>> addCorrection(Map<String,Object> map, InteractionView iv, final Widget widget, int scoreMax, OpdrNavIF comRoot) {
    widget.addStyleName(CORRECTIE);
    ObjectMap h = JSONUtilities.wrapMap(map);
    h = h.getObjectMap(REVIEW_INTERACTIE_DATA);
    if ( h != null && h.containsKey(REVIEW_SCORE_CORRECTIE)) {
      widget.setStyleName(CORRECTED, h.getInt(REVIEW_SCORE_CORRECTIE)!=0);
    }
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
          if (h.containsKey(REVIEW_SCORE_COMMENT))
        	result.put(REVIEW_SCORE_COMMENT, h.getString(REVIEW_SCORE_COMMENT));
        }
        widget.addDomHandler(event -> {
          int x = event.getRelativeX(widget.getElement());
          int y = event.getRelativeY(widget.getElement());
          int width = widget.getOffsetWidth() - 16;
          int height = widget.getOffsetHeight() - 16;
          if (x > width && y > height) {
            if(popup != null) {
              popup.showRelativeTo(widget);
            } else {
              //iv.kijkNa();iv.getState(); // wat is nodig voor score?????? FIXME
              popup = startCorrection(result, widget, iv.getScore(), scoreMax, comRoot);
            }
          }
        }, MouseUpEvent.getType());
        widget.addDomHandler(event -> {
            int x = event.getRelativeX(widget.getElement());
            int y = event.getRelativeY(widget.getElement());
            int width = widget.getOffsetWidth() - 16;
            int height = widget.getOffsetHeight() - 16;
            if (x > width && y > height) {
              if(popup != null) {
                popup.showRelativeTo(widget);
              } else {
                //iv.kijkNa();iv.getState(); // wat is nodig voor score?????? FIXME
                popup = startCorrection(result, widget, iv.getScore(), scoreMax, comRoot);
              }
            }
          }, PointerUpEvent.getType());
      }
    };

  }
  
  int minCor,maxCor;
  
  private static PopupPanel startCorrection(Map<String, Object> map, Widget w, int score, int scoreMax, OpdrNavIF comRoot) {
      CorrectieView view = new CorrectieView(w, comRoot);
      view.setObject(map);      
      Object correctie = map.getOrDefault(REVIEW_SCORE_CORRECTIE,"0");
      Object comment   = map.getOrDefault(REVIEW_SCORE_COMMENT, "");
      view.maxCor = scoreMax - score;
      view.minCor = -score;
      view.correctie.setText(correctie.toString());
      view.max.setText(Integer.toString(scoreMax));
      view.score.setText(Integer.toString(score));
      view.area.setText(String.valueOf(comment));
//      PopupPanel popup = new PopupPanel();
//      popup.setWidget(view);
//      view.setPopup(popup);      
//      popup.showRelativeTo(w);
//      return popup;

		DWOPopupPanel panel = new DWOPopupPanel(Text.constants.docentCorrectieTitle(), PopupButton.NOVIEW_LISTENER);
		panel.addContent(view);
		view.setPopup(panel);
		panel.showRelativeTo(w);
     return panel;
      
      
      
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
  @UiField MLTextBox area;
  private final Widget parent;

  private CorrectieView(Widget w, OpdrNavIF comRoot) {
    parent = w;
    initWidget(uiBinder.createAndBindUi(this));
    area.setCommunicationRoot(comRoot);
  }

  @UiHandler("ok")
  void onOk(ClickEvent e) {
    String result = correctie.getText();
    int n = Integer.parseInt(result);
    n = Math.max(minCor, Math.min(maxCor, n));
    object.put(REVIEW_SCORE_CORRECTIE, (n));
    String comment = area.getText();
    object.put(REVIEW_SCORE_COMMENT, comment);
    if(parent != null) {
      parent.setStyleName(CORRECTED, n!=0);
    }
    hide();
  }

  @UiHandler("cancel")
  void onCancel(ClickEvent e) {
    Object n = object.getOrDefault(REVIEW_SCORE_CORRECTIE, "0");
    correctie.setText(n.toString());
    Object comment = object.getOrDefault(REVIEW_SCORE_COMMENT, "");
    area.setText(comment.toString());
    hide();
  }

  @Override
  public void hide() {
    popup.hide();
  }
 
  @UiHandler("correctie")
  void onChange(ValueChangeEvent<String> e) {
	  String v = e.getValue();
	  try {
		int n = Integer.parseInt(v);
		  if (n > maxCor) correctie.setValue(Integer.toString(maxCor),false);
		  if (n < minCor) correctie.setValue(Integer.toString(minCor),false);
	} catch (NumberFormatException e1) {
		
	}
  }
  
}
