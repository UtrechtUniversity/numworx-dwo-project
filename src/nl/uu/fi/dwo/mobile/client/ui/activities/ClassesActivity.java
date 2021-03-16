package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.CoursesOfClasToSelectItems;
import nl.uu.fi.dwo.mobile.DWO2player.InsertSelectItems;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.ClassesViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class ClassesActivity extends AbstractActivity implements ValueChangeHandler<DomSchoolClass> {
	

	@Inject ClassesActivity() {
	}

	@Inject DwoGlobalVars vars;
	@Inject RPCHandler rpc;
	@Inject ClassesViewImpl view;
	@Inject NavigationViewNumworx navigation;
	@Inject HeaderView header;

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		navigation.setBeheer(false);
		navigation.showCells();
		navigation.setCells(SelectModuleItemHolder.getItems());
		view.setSchoolClasses(Collections.singletonList(vars.getActiveSchoolRoleAndClass().getSchoolClass()));
		rpc.getStudentsSchoolClasses().then(p -> {
			view.setSchoolClasses(p.getValue());
			view.setActiveSchoolClass(vars.getActiveSchoolRoleAndClass().getSchoolClass());
			eventBus.addHandlerToSource(ValueChangeEvent.getType(), view, this);
			header.show();
			panel.setWidget(view);
			return null;});
	}

	@Override
	public void onValueChange(ValueChangeEvent<DomSchoolClass> event) {
		DomSchoolClass value = event.getValue();
		SelectModuleItemHolder.clear();
		Promise<DomCoursesOfSchoolClass> promise = rpc.setActiveSchoolClass(value).filter(Boolean::booleanValue)
		.then(p -> rpc.getCoursesClass(value));
		Promise<List<SelectModuleItem>> list = promise
				.map( t -> { 
					t.getSchoolClass().setIconizer(Boolean.FALSE);
					return t;})
				.map(new CoursesOfClasToSelectItems());
		list.then(new InsertSelectItems(false, RoleType.STUDENT))
		
		.onResolve( 
			() -> {
				if (promise.getFailure() != null) {
					navigation.setCells(Collections.emptyList());	
				} else {
					List<nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem> items = SelectModuleItemHolder.getItems();
					navigation.setCells(items);
 				}
				
			});
	}
}
