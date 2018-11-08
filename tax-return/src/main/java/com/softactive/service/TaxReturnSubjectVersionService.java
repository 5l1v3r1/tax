package com.softactive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.softactive.taxreturn.object.TaxReturnSubjectVersion;

@Repository
public class TaxReturnSubjectVersionService extends BaseDataRepository<TaxReturnSubjectVersion> {
	
	@Override
	public String tableName() {
		return "subject_version";
	}
	
	@Override
	public TaxReturnSubjectVersion findUnique(TaxReturnSubjectVersion trsv) {
		if(trsv.getId()!=null) {
			return find(trsv.getId());
		}
		return findByCodeAndVersion(trsv.getCode(), trsv.getVersion());
	}
	
	public static Map<String, Object> getMap(TaxReturnSubjectVersion i){
		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("id", i.getId());
		answer.put("code", i.getCode());
		answer.put("start", i.getStart());
		answer.put("end", i.getEnd());
		answer.put("version", i.getVersion());
		return answer;
	}
	
	public TaxReturnSubjectVersion findByCodeAndVersion(String code, Integer version) {
		code = escapeChars(code);
		String sql = initQuery() + " where code='" + code + "' and version=" + version + " limit 1";
		List<TaxReturnSubjectVersion> answer = query(sql);
		if(answer!=null && answer.size()==0) {
			return null;
		}
		return answer.get(0);
	}
	
	private String escapeChars(String original) {
		return original.replaceAll("'", "\\\\'");
	}
	
//	public void delete(List<TaxReturnSubjectVersion> list) {
//		String sql = getDeleteSql() + getWhereStatement(list);
//		getJdbcTemplate().execute(sql);
//		rfs.deleteForTaxReturnSubjectVersions(list);
//	}
	
	protected String getWhereStatement(List<TaxReturnSubjectVersion> list) {
		String sql = " where id in ";
		for (TaxReturnSubjectVersion i : list) {
			sql += i.getId() + ", ";
		}
		sql = sql.substring(0, sql.length() - 2) + ")";
		return sql;
	}

	public TaxReturnSubjectVersion findTaxReturnSubjectVersionById(Integer id) {
		String sql = initQuery() + " where id=" + id;
		List<TaxReturnSubjectVersion> answer = query(sql);
		if (answer.size() == 1) {
			return answer.get(0);
		}
		return null;
	}

	public List<TaxReturnSubjectVersion> getListFrom(String src, Integer last) {
		String sql = initQuery() + " where (source_code='" + src + "' and id > " + last + ")";
		return query(sql);
	}

	public List<TaxReturnSubjectVersion> getListFrom(String src, String frq, Integer last) {
		String sql = initQuery() + " where (source_code='" + src + "' and frequency_code='" + frq + "' and id > "
				+ last + ")";
		return query(sql);
	}

	public List<TaxReturnSubjectVersion> getTaxReturnSubjectVersionsBySource(String src) {
		String sql = initQuery() + " where source_code='" + src + "'";
		return query(sql);
	}

	public List<TaxReturnSubjectVersion> getTaxReturnSubjectVersionsBySourceCodeAndFrequency(String src, String frq) {
		String sql = initQuery() + " where (source_code='" + src + "' and frequency_code='" + frq + "')";
		return query(sql);
	}
}
