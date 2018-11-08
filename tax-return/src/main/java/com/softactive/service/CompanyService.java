package com.softactive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.softactive.persistence.model.User;
import com.softactive.taxreturn.object.Company;

@Repository
public class CompanyService extends BaseDataRepository<Company>{
	@Autowired
	private UserService us;
	
	@Override
	public Company findUnique(Company c) {
		return find(c.getId());
	}
	public Company findCompany(String mailAddress) {
		User u = us.findUserByEmail(mailAddress);
		Integer companyId = u.getCompanyId();
		return find(companyId);
	}

	@Override
	public String tableName() {
		return "cmn_company";
	}
}
