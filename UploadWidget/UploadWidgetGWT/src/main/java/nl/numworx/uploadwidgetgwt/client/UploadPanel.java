package nl.numworx.uploadwidgetgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineHTML;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.ISession;
import gwtupload.client.ISession.Session;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.UploaderConstants;
import gwtupload.client.SingleUploader;
import gwtupload.client.Uploader;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

class UploadPanel extends Composite implements ClickHandler, OnFinishUploaderHandler, Constants {
	
	FeedPanel feed;
	private OpdrNavIF comRoot;
	private Uploader uploader;
	private LessonMode lessonMode;
	private Role role;

	UploadPanel() {
		
		// Basic sample
		FlowPanel flow = new FlowPanel();

		uploader = new SingleUploader();
		// options:
		uploader.setAutoSubmit(false);
		uploader.avoidRepeatFiles(true);
		uploader.setMultipleSelection(false);
		uploader.addOnFinishUploadHandler(this);
		UploaderConstants strs = GWT.create(I18NConstants.class);
		uploader.setI18Constants(strs);
		uploader.setServletPath("/dwo/dav/upload/servlet.gupld");
		uploader.getElement().getStyle().setBottom(0, Style.Unit.PX);
		uploader.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		
		feed = new FeedPanel();		
		
//		InlineHTML btn = new InlineHTML("<i class=\"fa fa-refresh\"></i>"); btn.addClickHandler(this);
//		btn.getElement().getStyle().setFloat(Style.Float.RIGHT);
//		flow.add(btn);
		flow.add(feed);
		flow.add(uploader);

		initWidget(flow);
	}


	void setValidExtensions(String ext) {
		uploader.setValidExtensions(ext);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		feed.doRequest();
	}


	@Override
	public void onFinish(IUploader uploader) {
		feed.doRequest();
	}

	OpdrNavIF getComRoot() {
		return comRoot;
	}


	void setComRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		this.lessonMode = comRoot.getLessonMode();
		this.role = comRoot.getRole();
		UploadSession.setComRoot(comRoot);
		if (lessonMode == LessonMode.normal && role == Role.Learner) {
			ObjectMap context = comRoot.getContext();
			Hidden registration = new Hidden("registration", context.getString("registration"));
			uploader.add(registration, 0);
			Hidden uuid = new Hidden("uuid", comRoot.getUUID());
			uploader.add(uuid, 1);
			Hidden learner = new Hidden("learnerId", comRoot.getLearnerId());
			uploader.add(learner,2);
		} else {
			uploader.setEnabled(false);
		}
		feed.setComRoot(comRoot);
	}


	void setFileInputType(FileInputType type) {
		uploader.setFileInput(type.getInstance());
	}

	void setAutoSubmit(boolean b) {
		uploader.setAutoSubmit(b);
	}

	void setItemsMax(int max) {
		feed.setItemsMax(max);
		uploader.setMultipleSelection(max > 1);
		if (max <= 0) uploader.removeFromParent();
	}


	public void setFileInputModel(ObjectList model) {
		feed.setFileInputModel(model);
	}

}
