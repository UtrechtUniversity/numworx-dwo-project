package nl.uu.fi.dwo.account.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class StudentModelPanel extends Composite implements HasText {

	static private Logger LOG = Logger.getLogger("StudentModelPanel");
	
	private static StudentModelPanelUiBinder uiBinder = GWT.create(StudentModelPanelUiBinder.class);

	interface StudentModelPanelUiBinder extends UiBinder<Widget, StudentModelPanel> {
	}

	public StudentModelPanel(DomUserFull domUserFull, DomSchoolRoleAndClassV2 role) {
		initWidget(uiBinder.createAndBindUi(this));
		controller = new StudentModelController(this, domUserFull, role);
		controller.init();
	}

	@UiField
	Button button;
	
	@UiField ListBox selectBox;
	@UiField TextArea area;
	
	private PopupPanel popup;
	private StudentModelController controller;
	
	@UiHandler("button")
	void onClick(ClickEvent e) {
		popup.hide();
	}

	@UiHandler("selectBox")
	void onChange(ChangeEvent e) {
		int s = selectBox.getSelectedIndex();
		if( s == 0) {
			LOG.info("deselect all");
			area.setText("");
		} else {
			String value = selectBox.getSelectedValue();
			controller.select(value);
		}
	}
	
	
	public void setText(String text) {
		area.setText(text);
	}

	public String getText() {
		return area.getText();
	}

	public void setPopup(PopupPanel popup) {
		this.popup = popup;;	
	}

	public void updateModels(Collection<String> keySet) {
		selectBox.clear();
		selectBox.addItem("-- kies model --");
		for(String s: keySet) {
			selectBox.addItem(s);
		}
		
	}

	public void updateStructure(DomStudentModelStructure modelStructure,
			DomStudentModelStructureScore modelStructureScore, String locale) {
		StringBuilder sb = new StringBuilder(256);
		List<DomStudentModelCategory> categories = modelStructure.getCategories();
		List<DomStudentModelCategoryScore> cscore = modelStructureScore.getCategories();
		int len = Math.min(categories.size(), cscore.size());
		for(int i = 0; i < len; i++) {
			DomStudentModelCategory cat = categories.get(i);
			DomStudentModelCategoryScore catScore = cscore.get(i);
			String title = cat.getInfo().getTitle().get(locale);
			String description = cat.getInfo().getDescription().get(locale);
			double score = catScore.getScore();
			long    count = catScore.getCount();
			
			sb.append(title).append(" ").append(score) .append("/").append(count)
				.append("=").append(score/count)
			.append("\n");
			sb.append(description).append("\n");
			List<DomStudentModelObj> objectives = cat.getObjectives();
			List<DomStudentModelObjectiveScore> oscores = catScore.getObjectives();
			int olen = Math.min(objectives.size(), oscores.size());
			for(int j = 0; j < olen; j++) {
				DomStudentModelObj obj = objectives.get(j);
				DomStudentModelObjectiveScore oscore = oscores.get(j);
				
				String otitle = obj.getInfo().getTitle().get(locale);
				String odescr = obj.getInfo().getDescription().get(locale);
				double scr = oscore.getScore();
				double cnt = oscore.getCount();
				sb.append(" - ").append(otitle).append(" ").append(scr) .append("/").append(cnt)
					.append("=").append(scr/cnt)
				.	append("\n");
					sb.append("   ").append(odescr).append("\n");
			
				
			}
			
		}
		setText(sb.toString());
	}

}
