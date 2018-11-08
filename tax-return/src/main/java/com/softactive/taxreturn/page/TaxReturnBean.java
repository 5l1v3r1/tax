package com.softactive.taxreturn.page;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.softactive.taxreturn.manager.TaxReturnHandler;
import com.softactive.taxreturn.object.TaxReturnConstants;

@Component
public class TaxReturnBean implements TaxReturnConstants{
	UploadedFile file = null;

	@Autowired
	private TaxReturnHandler taxReturnHandler;

	private List<String> fileNames = new ArrayList<String>();



	//	public static final String TAX_RETURN_PATH = "//src/main/resources/taxreturns/";
	//	public static final String TAX_RETURN_PATH = "//";


	//	private String destination="D:\\tmp\\beyannameler\\";

	private String destination="";


	private int index;

	private PostTask onComplete = new PostTask() {

		@Override
		public void onPost(Map<String, Object> sharedParams) {
			index++;
			System.out.println("index: " + index);
			if (index < fileNames.size()) {
				next();					
			} else {
				taxReturnHandler.checkCriteria();
				index = 0;
				FacesMessage message = new FacesMessage("xmls are parsed, excel is created");
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
		}
	};

	@PostConstruct
	public void init() {
		index = 0;
		taxReturnHandler.setPost(onComplete);
	}

	public void handleFileUpload(FileUploadEvent event) {
		try {
			copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileNames.add(event.getFile().getFileName());
	}

	public void copyFile(String fileName, InputStream in) {
		try {


			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(new File(destination + fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void next() {
		FacesMessage message = new FacesMessage("starting to handle xmls");
		FacesContext.getCurrentInstance().addMessage(null, message);
		taxReturnHandler.handle(fileNames.get(index));
	}
}