package com.softactive.taxreturn.manager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.softactive.core.exception.MyException;
import com.softactive.service.TaxReturnSubjectService;
import com.softactive.taxreturn.object.TaxReturnSubject;

@Component
public class TaxReturnSubjectHandler extends AbstractTaxReturnSubjectHandler<TaxReturnSubject>{

	private static final long serialVersionUID = -2509824641639037303L;
	@Autowired
	TaxReturnSubjectService trss;

	@Override
	protected TaxReturnSubject outputComponent(Element o) throws MyException {
		TaxReturnSubject trs = new TaxReturnSubject();
		String code = o.getElementsByTagName(CODE).item(0).getTextContent();
		String name = o.getElementsByTagName(NAME).item(0).getTextContent();
		trs.setCode(code);
		trs.setName(name);
		trss.save(trs);
		return trs;
	}

	@Override
	protected boolean isOutputInvalid(List<TaxReturnSubject> output) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void mapMetaData(Document r) throws MyException {
		// TODO Auto-generated method stub
		
	}
}
