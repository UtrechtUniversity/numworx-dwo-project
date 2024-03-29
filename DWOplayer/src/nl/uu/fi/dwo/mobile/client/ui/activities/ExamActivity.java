package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.Exam;

public class ExamActivity extends AbstractActivity {

  @Inject ExamActivity() {  }
  @Inject ClientFactory clientFactory;
  @Inject PlaceController placeController;
  @Inject RPCHandler rpc;
  
  boolean legal(String base) {
    RegExp r = RegExp.compile("^/[a-z]+(/[a-z]+)*/$");
    return r.test(base);
  }
  
  
  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    String id = ((Exam) placeController.getWhere()).getToken();
    String token = "";
    if (!id.isEmpty()) token = "?id=" + token;
    panel.setWidget(new Label());
    String base = Location.getParameter("base");
    if (base == null || !legal(base)) base = "";
    final String exam = base + "exam/" + token;
    final Promise<String> exampromise = rpc.getClassCourseURL(id, base).recover((p)->exam);
    exampromise.onResolve(() -> {
    
    Actions.EXAM.execute();
    clientFactory.logout().onResolve(() -> {
    Timer t = new Timer() {

		@Override
		public void run() {
		    gotoExam(exampromise.getValue());			
		} };
	t.schedule(100);
    });
    });
  }

  private static native void gotoExam(String ref) /*-{
    top.location.href = ref;
  }-*/;

}
