package com.softactive.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.softactive.taxreturn.object.Price;
import com.softactive.taxreturn.object.RiskFactor;

@Repository
public class PriceService extends BaseDataRepository<Price> {

	@Override
	public String tableName() {
		return "prc_price";
	}
	
	@Override
	public Price findUnique(Price p) {
		return findByRiskFactorIdAndDate(p.getRiskFactorId(), p.getDataDate());
	}
	
	public Price findByRiskFactorIdAndDate(Integer rfId, Date date) {
		String sql = initQuery() + " where risk_factor_id=" + rfId +
				" and data_date='" + date + "'";
		List<Price> answer = query(sql);
		if (answer.size() == 1) {
			return answer.get(0);
		}
		return null;
	}
	
	public void deleteByRiskFactor(List<RiskFactor> riskFactors) {
		String sql = initQuery() + " where risk_factor_id " + getWhereStatementForRiskFactors(riskFactors);
		for(Price p:query(sql)) {
			delete(p.getId());
		}
	}
	
	private String getWhereStatementForRiskFactors(List<RiskFactor> riskFactors) {
		String sql = " in ('";
		for(RiskFactor rf:riskFactors) {
			sql += rf.getId() + "','";
		}
		sql = sql.substring(0,sql.length()-2);
		sql += ")";
		return sql;
	}
}
