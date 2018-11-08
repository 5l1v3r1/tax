package com.softactive.taxreturn.manager;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.manager.AbstractBaseRiskFactorGenerator;
import com.softactive.service.CompanyService;
import com.softactive.service.RiskFactorService;
import com.softactive.service.TaxReturnSubjectService;
import com.softactive.taxreturn.object.Company;
import com.softactive.taxreturn.object.RiskFactor;
import com.softactive.taxreturn.object.TaxReturnSubject;

@Component
public class TaxReturnRiskFactorGenerator extends AbstractBaseRiskFactorGenerator<Company, TaxReturnSubject, RiskFactor>{
	private static final long serialVersionUID = 5177967987644403879L;
	@Autowired
	private TaxReturnSubjectService trss;
	@Autowired
	private CompanyService cs;
	@Autowired
	private RiskFactorService rfs;
	
	@PostConstruct
	public void init() {
		is = trss;
		rs = cs;
		super.rfs = this.rfs;
	}

	@Override
	protected RiskFactor getRiskFactor(Company r, TaxReturnSubject i) {
		RiskFactor rf = new RiskFactor();
		rf.setCompanyId(r.getId());
		rf.setSubjectId(i.getId());
		return rf;
	}

}
