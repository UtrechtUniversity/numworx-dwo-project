package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.DWOPopupPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.PopupButton;
import nl.uu.fi.dwo.mobile.utils.HasHide;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;

public class CorrectieView extends Composite implements HasHide, PopupListener {

  private static final String CHECK_DOCENT = "checkDocent";
  public static final String REVIEW_SCORE_CORRECTIE = "reviewScoreCorrectie";
  public static final String REVIEW_SCORE_COMMENT   = "reviewScoreComment";
  public static final String REVIEW_INTERACTIE_DATA = "reviewInteractieData";
  public static final String REVIEW_STUDENTMODELSET = "reviewStudentModelSet";
  
  public static final String CORRECTIE = DWOplayer.DWO_BUNDLE.dwoplayercss().correctie();
  public static final String CORRECTED = DWOplayer.DWO_BUNDLE.dwoplayercss().corrected();
  
  public static Provider<Map<String,Object>> addCorrection(Map<String,Object> map, InteractionView iv, final Widget widget, int scoreMax, OpdrNavIF comRoot, ActivityInterface a, boolean checkDocent, Logging logger) {
    widget.addStyleName(CORRECTIE);
    ObjectMap h = JSONUtilities.wrapMap(map);
    h = h.getObjectMap(REVIEW_INTERACTIE_DATA);
    if ( h != null && h.containsKey(REVIEW_SCORE_CORRECTIE)) {
      widget.setStyleName(CORRECTED, h.getInt(REVIEW_SCORE_CORRECTIE)!=0);
    }
    return new Provider<Map<String, Object>>() {

      Map<String, Object> result = new HashMap<>();
      { String logID = logger.getLogID();
        if (logID != null && !logID.isEmpty()) result.put("logID", logID);
      }
      PopupPanel popup;
      @Override
      public Map<String, Object> get() {
        if(result.isEmpty())
        {
        	if (checkDocent) return Collections.singletonMap(CHECK_DOCENT, Boolean.TRUE);
        	return null;
        }
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
          if (h.containsKey(CHECK_DOCENT))
        	  result.put(CHECK_DOCENT, h.getBoolean(CHECK_DOCENT, checkDocent));
          if (h.containsKey(REVIEW_STUDENTMODELSET))
        	  result.put(REVIEW_STUDENTMODELSET, h.getString(REVIEW_STUDENTMODELSET));
        }
        widget.addDomHandler(event -> {
          int x = event.getRelativeX(widget.getElement());
          int y = event.getRelativeY(widget.getElement());
          int width = widget.getOffsetWidth() - 16;
          int height = widget.getOffsetHeight() - 16;
          if (x > width && y > height) {
            if(popup != null) {            
            	popup.center();
            } else {
              //iv.kijkNa();iv.getState(); // wat is nodig voor score?????? FIXME
              popup = startCorrection(result, widget, iv.getScore(), scoreMax, comRoot, a, checkDocent, logger);
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
                popup.center();
              } else {
                //iv.kijkNa();iv.getState(); // wat is nodig voor score?????? FIXME
                popup = startCorrection(result, widget, iv.getScore(), scoreMax, comRoot, a, checkDocent, logger);
              }
            }
          }, PointerUpEvent.getType());
      }
    };

  }
  
  int minCor,maxCor;
private boolean checkDocent;
  
  private static PopupPanel startCorrection(Map<String, Object> map, Widget w, int score, int scoreMax, OpdrNavIF comRoot, ActivityInterface a, boolean checkDocent, Logging logger) {
      CorrectieView view = new CorrectieView(a, w, comRoot, logger);
      view.setObject(map);      
      Object correctie = map.getOrDefault(REVIEW_SCORE_CORRECTIE,"0");
      Object comment   = map.getOrDefault(REVIEW_SCORE_COMMENT, "");
      Object studentmodelSet = map.get(REVIEW_STUDENTMODELSET);
      view.maxCor = scoreMax - score;
      view.minCor = -score;
      view.correctie.setText(correctie.toString());
      view.max.setText(Integer.toString(scoreMax));
      view.score.setText(Integer.toString(score));
      view.area.setText(String.valueOf(comment));
      view.checkDocent = checkDocent;
      view.studentmodelSet = Objects.toString(studentmodelSet,"");
      DWOPopupPanel panel = new DWOPopupPanel(Text.constants.docentCorrectieTitle(), view);
      panel.addContent(view);
      view.setPopup(panel);
      panel.center();
      return panel;
  }

  private Map<String,Object> object;

  private void setObject(Map<String, Object> map) {
    this.object = map;
  }

  private PopupPanel popup, leerdoelenPopup;

  private void setPopup(PopupPanel popup) {
    this.popup = popup;
    PopupFacade.addPopup(this);
    
  }



  private static CorrectieViewUiBinder uiBinder = GWT.create(CorrectieViewUiBinder.class);

  interface CorrectieViewUiBinder extends UiBinder<Widget, CorrectieView> {}

  @UiField HasText max, score;
  @UiField TextBox correctie;
  @UiField(provided=true) MLTextBox area;
  @UiField Button leerdoelen;
  private final Widget parent;
  private final OpdrNavIF comroot;
  private final Scorm2004IF api;
  private String[] smObjectives;
  private String studentmodelSet = ""; // never null, "" means NULL
  
  private native static void closeWindow(CorrectieView view) /*-{
  	$wnd.closeWindow = function() {
  		view.@nl.uu.fi.dwo.mobile.client.ui.views.CorrectieView::onCloseView()()
  	}
  }-*/;
  

  private void onCloseView() {
	  String set = api.GetValue("dme.studentmodelset");
	  onHide();
	  studentmodelSet = set;
  }
  
  private CorrectieView(ActivityInterface a, Widget w, OpdrNavIF comRoot, Logging logger) {
    parent = w;
    area = new MLTextBox(a);
    api = a.api();
    initWidget(uiBinder.createAndBindUi(this));
    if (logger != null) smObjectives = logger.getSMObjectives();
	leerdoelen.setVisible(a.isTest() && smObjectives != null);
    area.setCommunicationRoot(comRoot);
    comroot = comRoot;
  }

	protected boolean isTest(ActivityComponent a) {
		return a.isTest();
	}

  @UiHandler("ok")
  void onOk(ClickEvent e) {
    String result = correctie.getText();
    int n = Integer.parseInt(result);
    n = Math.max(minCor, Math.min(maxCor, n));
    object.put(REVIEW_SCORE_CORRECTIE, (n));
    String comment = area.getText();
    object.put(REVIEW_SCORE_COMMENT, comment);
    if(checkDocent)
    	object.put(CHECK_DOCENT, Boolean.FALSE);
    if(parent != null) {
      parent.setStyleName(CORRECTED, n!=0);
    }
    if (studentmodelSet.isEmpty())
    	object.remove(REVIEW_STUDENTMODELSET);
    else
    	object.put(REVIEW_STUDENTMODELSET, studentmodelSet);
    hide();
    comroot.setChanged(false); // checkpoint???????
  }

  @UiHandler("cancel")
  void onCancel(ClickEvent e) {
    Object n = object.getOrDefault(REVIEW_SCORE_CORRECTIE, "0");
    correctie.setText(n.toString());
    Object comment = object.getOrDefault(REVIEW_SCORE_COMMENT, "");
    area.setText(comment.toString());
    studentmodelSet = Objects.toString(object.get(REVIEW_STUDENTMODELSET), "");
    
    hide();
  }

  @UiHandler("leerdoelen")
  void onLeerdoelen(ClickEvent e) {
	  String value = JSONUtilities.toJSONArray(smObjectives).toString();
	  api.SetValue("dme.studentmodelitems", value);
	  api.SetValue("dme.studentmodelset", studentmodelSet);
	  DWOPopupPanel popup = new DWOPopupPanel("Leerdoelen", PopupButton.NOVIEW_LISTENER);
	  LeerdoelenView view = new LeerdoelenView();
	  popup.addContent(view);
	  leerdoelenPopup = popup;
	  closeWindow(this);
	  popup.center();
  }
  
  @Override
  public void hide() {
    popup.hide();
    if (leerdoelenPopup != null) leerdoelenPopup.hide();
  }
 
  @UiHandler("correctie") 
  void onMouseDown(MouseDownEvent ev) {
	  comroot.getKeyboard().blur();
	  correctie.setFocus(true);
	  ev.stopPropagation();
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

	@Override
	public void onShow() {
	}
	
	@Override
	public void onHide() {
	    if (leerdoelenPopup != null) leerdoelenPopup.hide();
	}
  
}
