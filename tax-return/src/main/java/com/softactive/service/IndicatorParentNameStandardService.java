package com.softactive.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.softactive.taxreturn.object.IndicatorParentStandard;

@Repository
public class IndicatorParentNameStandardService extends BaseDataRepository<IndicatorParentStandard>{

	@Override
	public String tableName() {
		return "indicator_parent_standard";
	}
	
	public List<IndicatorParentStandard> findAlternativesByName(String name) {
		IndicatorParentStandard ips = findByName(name);
		if(ips!=null) {
			return findByIndicatorId(ips.getIndicatorId(), ips.getId());
		}
		return null;
	}
	
	public IndicatorParentStandard findByName(String name) {
		String sql = initQuery() + " where name='" + name + "' limit 1";
		List<IndicatorParentStandard> list = query(sql);
		if (list!=null && list.size()==1) {
			return list.get(0);
		}
		return null;
	}
	
	public List<IndicatorParentStandard> findByIndicatorId(Integer indicatorId, Integer idFilter){
		String sql = initQuery() + " where indicator_id=" + indicatorId + " and id != " + idFilter;
		return query(sql);
	}
	
	@Override
	public IndicatorParentStandard findUnique(IndicatorParentStandard t) {
		return findByName(t.getName());
	}
}
