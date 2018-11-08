package com.softactive.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.softactive.taxreturn.object.Company;
import com.softactive.taxreturn.object.RiskFactor;
import com.softactive.taxreturn.object.TaxReturnSubject;

@Repository
public class RiskFactorService extends BaseDataRepository<RiskFactor>{
	private final String BASE_SQL = initQuery() + " where (source_code='";

	@Autowired
	private PriceService ps;
	
	@Override
	public String tableName() {
		return "risk_factor";
	}

	public RiskFactor findRiskFactorBySubjectAndCompany(Integer sub, Integer com) {
		String sql = initQuery() + " where (subject_id=" + sub + " and company_id=" + com + ")" ;
		List<RiskFactor> answer = query(sql);
		if (answer!=null && answer.size() == 1) {
			return answer.get(0);
		} else if(answer.size()!=0) {
			for(RiskFactor toDel:answer) {
				delete(toDel.getId());
			}
		}
		return null;
	}
	
	@Override
	public RiskFactor findUnique(RiskFactor rf) {
		return findRiskFactorBySubjectAndCompany(rf.getSubjectId(), rf.getCompanyId());
	}

	public List<RiskFactor> findBySourceAndFrequency(String src, String frq) {
		String sql = BASE_SQL + src + "' and frequency_code='" + frq + "')";
		return query(sql);
	}
	
//	public RiskFactor pickRiskFactorBySourceAndFrequency(String src, String frq) {
//		String sql = BASE_SQL + src + "' and frequency_code='" + frq + 
//				"' and (update_date <= '" + thresholdDate(frq) + "' or update_date is null ) ) LIMIT 1";
//		List<RiskFactor> answer = query(sql);
//		if(answer!=null && answer.size()==1) {
//			return answer.get(0);
//		} else {
//			return null;
//		}
//	}
	
//	public List<RiskFactor> findBySourceForUpdate(String src){
//		List<RiskFactor> answer =new ArrayList<RiskFactor>();
//		for(String frq:FREQUENCIES) {
//			answer.addAll(findBySourceAndFrequency(src, frq));
//		}
//		return answer;
//	}
//	
//	private Date thresholdDate(String frq) {
//		Calendar c = Calendar.getInstance();
//		switch(frq) {
//		case FREQUENCY_5_YEAR:
//			c.add(Calendar.YEAR, -5);
//			break;
//		case FREQUENCY_ANNUAL:
//			c.add(Calendar.YEAR, -1);
//			break;
//		case FREQUENCY_QUARTERLY:
//			c.add(Calendar.MONTH, -3);
//			break;
//		case FREQUENCY_MONTHLY:
//			c.add(Calendar.MONTH, -1);
//			break;
//		case FREQUENCY_WEEKLY:
//			c.add(Calendar.WEEK_OF_YEAR, -1);
//			break;
//		case FREQUENCY_DAILY:
//			c.add(Calendar.DAY_OF_YEAR, -1);
//			break;
//		}
//		return date(c);
//	}
	
	private Date date(Calendar c) {
		return date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}
	
	private Date date(int year, int month, int day) {
		return Date.valueOf(year + "-" + month + "-" + day);
	}
	
//	public List<RiskFactor> findBySourceAndFrequencyForUpdate(String src, String frq) {
//		String sql = BASE_SQL + src + "' and frequency_code='" + frq + 
//				"' and update_date > '" + getThresholdForFrequency(frq) + "')";
//		return query(sql);
//	}
	
	public List<RiskFactor> findBySourceAndFrequencyForHistorical(String src, String frq) {
		String sql = BASE_SQL + src + "' and frequency_code='" + frq + 
				"' and update_date is null)";
		return query(sql);
	}
	
//	private String getThresholdForFrequency(String frq) {
//		Calendar c = Calendar.getInstance();
//		switch(frq) {
//		case FREQUENCY_5_YEAR:
//			c.add(Calendar.YEAR, -5);
//			break;
//		case FREQUENCY_ANNUAL:
//			c.add(Calendar.YEAR, -1);
//			break;
//		case FREQUENCY_QUARTERLY:
//			c.add(Calendar.MONTH, -3);
//			break;
//		case FREQUENCY_MONTHLY:
//			c.add(Calendar.MONTH, -1);
//			break;
//		case FREQUENCY_WEEKLY:
//			c.add(Calendar.WEEK_OF_YEAR, -1);
//			break;
//		case FREQUENCY_DAILY:
//			c.add(Calendar.DAY_OF_YEAR, -1);
//			break;
//		}
//		return getDateString(c);
//	}
	
	private String getDateString(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	public List<RiskFactor> findBySource(String src) {
		String sql = BASE_SQL + src + "')";
		return query(sql);
	}

//	public List<RiskFactor> findByFrequency(String frq) {
//		String sql = initQuery() + " where frequency_code='" + frq + "'";
//		return query(sql);
//	}

	public void deleteForCompanies(List<Company> companies) {
		List<RiskFactor> toDelete = findByCompanies(companies);
		if(toDelete==null || toDelete.size()==0) {
			return;
		}
		ps.deleteByRiskFactor(toDelete);
		String sql = initQuery() + getWhereStatementForCompanies(companies);
		delete(query(sql));
	}
	
	public void deleteForRegionIdAndSource(String regionId, String src) {
		List<RiskFactor> toDelete = findByRegionIdAndSource(regionId, src);
		if(toDelete==null || toDelete.size()==0) {
			return;
		}
		ps.deleteByRiskFactor(toDelete);
		deleteByRiskFactors(toDelete);
	}
	
	private void deleteByRiskFactors(List<RiskFactor> rfs) {
		for(RiskFactor rf:rfs) {
			delete(rf.getId());
		}
	}
	
	public void deleteForRegions(List<Company> regions) {
		List<RiskFactor> toDelete = findByRegions(regions);
		ps.deleteByRiskFactor(toDelete);
		String sql = initQuery() + getWhereStatementForCompanies(regions);
		delete(query(sql));
	}
	
	public void deleteForSubjects(List<TaxReturnSubject> subjects) {
		List<RiskFactor> toDelete = findBySubjects(subjects);
		ps.deleteByRiskFactor(toDelete);
		for(RiskFactor rf:toDelete) {
			delete(rf.getId());
		}
	}

	private List<RiskFactor> findByCompanies(List<Company> companies){
		String sql = initQuery() + getWhereStatementForCompanies(companies);
		return query(sql);
	}
	
	private List<RiskFactor> findByRegionIdAndSource(String regionId, String src){
		String sql = initQuery() + " where region_id='" + regionId +
				"' and source_code='" + src + "'";
		return query(sql);
	}
	
	private List<RiskFactor> findByRegions(List<Company> companies){
		String sql = initQuery() + getWhereStatementForCompanies(companies);
		return query(sql);
	}

	private List<RiskFactor> findBySubjects(List<TaxReturnSubject> subjects){
		String sql = initQuery() + getWhereStatementForSubjects(subjects);
		return query(sql);
	}


	private String getWhereStatementForCompanies(List<Company> companies) {
		String sql = " where company_id in (";
		for(Company c:companies) {
			sql += c.getId() + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += ")";
		return sql;
	}

	private String getWhereStatementForSubjects(List<TaxReturnSubject> subjects) {
		String sql = " where subject_id in (";
		for(TaxReturnSubject s:subjects) {
			sql += s.getId() + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += ")";
		return sql;
	}
}
