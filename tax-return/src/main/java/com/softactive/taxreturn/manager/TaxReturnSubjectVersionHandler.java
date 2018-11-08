package com.softactive.taxreturn.manager;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.softactive.core.exception.MyException;
import com.softactive.service.TaxReturnSubjectVersionService;
import com.softactive.taxreturn.object.TaxReturnSubjectVersion;

@Component
public class TaxReturnSubjectVersionHandler extends AbstractTaxReturnSubjectHandler<TaxReturnSubjectVersion>{
	private static final long serialVersionUID = 9135615696018616440L;
	@Autowired
	TaxReturnSubjectVersionService trsvs;

	@Override
	protected TaxReturnSubjectVersion outputComponent(Element o) throws MyException {
		TaxReturnSubjectVersion trsv = new TaxReturnSubjectVersion();
		String code = o.getElementsByTagName(CODE).item(0).getTextContent();
		String version = o.getElementsByTagName(VERSION).item(0).getTextContent();
		String startString = o.getElementsByTagName(START).item(0).getTextContent();
		String endString = o.getElementsByTagName(END).item(0).getTextContent();
		LocalDate start = resolveDateFromCustomFormat(startString);
		try {
			LocalDate end = resolveDateFromCustomFormat(endString);
			trsv.setEnd(end);
		} catch (MyException e) {
			//Do nothing, version is still valid
		}
		trsv.setCode(code);
		trsv.setStart(start);
		trsv.setVersion(resolveValidInteger(version));
		trsvs.save(trsv);
		System.out.println("xml element for version: " + o);
		System.out.println("code= " + code);
		System.out.println("version= " + version);
		return trsv;
	}

	@Override
	protected void mapMetaData(Document arg0) throws MyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isOutputInvalid(List<TaxReturnSubjectVersion> output) {
		// TODO Auto-generated method stub
		return false;
	}

}
