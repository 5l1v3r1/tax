package com.softactive.taxreturn.page;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.service.CompanyService;
import com.softactive.service.TaxReturnSubjectService;
import com.softactive.taxreturn.manager.TaxReturnRiskFactorGenerator;
import com.softactive.taxreturn.object.TaxReturnConstants;

import lombok.Getter;
import lombok.Setter;

@Component
public class TaxRiskFactorBean implements TaxReturnConstants{
	@Autowired @Getter @Setter
	private TaxReturnRiskFactorGenerator generator;
	@Autowired
	private TaxReturnSubjectService trss;
	@Autowired CompanyService cs;

	@PostConstruct
	public void init() {
//		User u = (User)auth.getPrincipal();
//		String s = UserDetails.class.getName();
//		co = cs.findCompany(auth.getName());
	}

	public void generate() {
		generator.start(trss.listOfObject(), cs.listOfObject());
	}
}