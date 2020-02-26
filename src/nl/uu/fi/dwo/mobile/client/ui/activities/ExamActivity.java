package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.mobile.client.ui.Actions;

public class ExamActivity extends AbstractActivity {

  @Inject public ExamActivity() {  }
  
  boolean legal(String base) {
    RegExp r = RegExp.compile("^/[a-z]+(/[a-z]+)*/$");
    return r.test(base);
  }
  
  
  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    panel.setWidget(new Label());
    Actions.EXAM.execute();
    String base = Location.getParameter("base");
    if (base == null || !legal(base)) base = "";
    gotoExam(base + "exam/");
  }

  private static native void gotoExam(String ref) /*-{
    top.location.href = ref;
  }-*/;

}
