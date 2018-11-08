package com.softactive.taxreturn.page;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.manager.PostTask;
import com.softactive.taxreturn.manager.CriteriumParser;
import com.softactive.taxreturn.object.TaxReturnConstants;

@Component
public class TaxCriteriaBean implements TaxReturnConstants{
	
	@Autowired
	private CriteriumParser criteriumParser;

	private PostTask onComplete = new PostTask() {

		@Override
		public void onPost(Map<String, Object> sharedParams) {
			System.out.println("completed");
		}
	};

	@PostConstruct
	public void init() {
		criteriumParser.setPost(onComplete);
	}

	public void handleFileUpload(FileUploadEvent event) {
		try {
			copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		criteriumParser.handle(event.getFile().getFileName());
	}

	public void copyFile(String fileName, InputStream in) {
		try {


			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(new File(fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

			System.out.println("New file created!");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}