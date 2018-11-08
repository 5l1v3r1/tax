package com.softactive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.softactive.taxreturn.object.TaxReturnSubject;

@Repository
public class TaxReturnSubjectService extends BaseDataRepository<TaxReturnSubject> {
	
	@Override
	public String tableName() {
		return "cmn_subject";
	}
	
	@Override
	public TaxReturnSubject findUnique(TaxReturnSubject trs) {
		if(trs.getId()!=null) {
			return find(trs.getId());
		}
		return findByCode(trs.getCode());
	}
	
	public static Map<String, Object> getMap(TaxReturnSubject i){
		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("id", i.getId());
		answer.put("code", i.getCode());
		answer.put("name", i.getName());
		return answer;
	}
	
	public TaxReturnSubject findByCode(String code) {
		code = escapeChars(code);
		String sql = initQuery() + " where code='" + code + "' limit 1";
		List<TaxReturnSubject> answer = query(sql);
		if(answer!=null && answer.size()==0) {
			return null;
		}
		return answer.get(0);
	}
	
	private String escapeChars(String original) {
		return original.replaceAll("'", "\\\\'");
	}
	
//	public void delete(List<TaxReturnSubject> list) {
//		String sql = getDeleteSql() + getWhereStatement(list);
//		getJdbcTemplate().execute(sql);
//		rfs.deleteForTaxReturnSubjects(list);
//	}
	
	protected String getWhereStatement(List<TaxReturnSubject> list) {
		String sql = " where id in ";
		for (TaxReturnSubject i : list) {
			sql += i.getId() + ", ";
		}
		sql = sql.substring(0, sql.length() - 2) + ")";
		return sql;
	}

	public TaxReturnSubject findTaxReturnSubjectById(Integer id) {
		String sql = initQuery() + " where id=" + id;
		List<TaxReturnSubject> answer = query(sql);
		if (answer.size() == 1) {
			return answer.get(0);
		}
		return null;
	}

	public List<TaxReturnSubject> getListFrom(String src, Integer last) {
		String sql = initQuery() + " where (source_code='" + src + "' and id > " + last + ")";
		return query(sql);
	}

	public List<TaxReturnSubject> getListFrom(String src, String frq, Integer last) {
		String sql = initQuery() + " where (source_code='" + src + "' and frequency_code='" + frq + "' and id > "
				+ last + ")";
		return query(sql);
	}

	public List<TaxReturnSubject> getTaxReturnSubjectsBySource(String src) {
		String sql = initQuery() + " where source_code='" + src + "'";
		return query(sql);
	}

	public List<TaxReturnSubject> getTaxReturnSubjectsBySourceCodeAndFrequency(String src, String frq) {
		String sql = initQuery() + " where (source_code='" + src + "' and frequency_code='" + frq + "')";
		return query(sql);
	}
}
