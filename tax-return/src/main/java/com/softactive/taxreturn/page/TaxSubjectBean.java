package com.softactive.taxreturn.page;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.manager.PostTask;
import com.softactive.taxreturn.manager.TaxReturnSubjectHandler;
import com.softactive.taxreturn.manager.TaxReturnSubjectVersionHandler;
import com.softactive.taxreturn.object.TaxReturnConstants;

@Component
public class TaxSubjectBean implements TaxReturnConstants{
	UploadedFile file = null;

	@Autowired
	private TaxReturnSubjectVersionHandler versionHandler;
	@Autowired
	private TaxReturnSubjectHandler subjectHandler;

//	private PostTask startHandleSubjects = new PostTask() {
//
//		@Override
//		public void onPost() {
//			subjectHandler.handle(file, subjectParams);
//		}
//
//	};

	private PostTask onComplete = new PostTask() {

		@Override
		public void onPost(Map<String, Object> sharedParams) {
			FacesMessage message = new FacesMessage("Succesful, the xml for tax subjects and their versions, is stored in DB.");
			FacesContext.getCurrentInstance().addMessage(null, message);		}
	};

	@PostConstruct
	public void init() {
//		versionHandler.setPost(startHandleSubjects);
		subjectHandler.setPost(onComplete);
	}

	public void handleFileUpload(FileUploadEvent event) {
		System.out.println("upload finished");
		file = event.getFile();
//		versionHandler.handle(file, versionParams);
	}
}