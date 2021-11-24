package nl.numworx.uploadwidgetgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploaderConstants;
import gwtupload.client.SingleUploader;

class UploadPanel extends Composite implements ClickHandler, OnFinishUploaderHandler {
	
	FeedPanel feed;
	

	UploadPanel() {
		
		// Basic sample
		FlowPanel flow = new FlowPanel();
		
		
		SingleUploader uploader = new SingleUploader();
		// options:
		uploader.setAutoSubmit(false);
		uploader.setAvoidRepeatFiles(true);
		uploader.setMultipleSelection(false);
		uploader.addOnFinishUploadHandler(this);
		UploaderConstants strs = GWT.create(I18NConstants.class);
		uploader.setI18Constants(strs);
		
		feed = new FeedPanel();		
		
		Button btn = new Button("refresh"); btn.addClickHandler(this);

		flow.add(btn);
		flow.add(feed);
		flow.add(uploader);

		initWidget(flow);
	}


	@Override
	public void onClick(ClickEvent event) {
		feed.doRequest();
	}


	@Override
	public void onFinish(IUploader uploader) {
		feed.doRequest();
	}

}
