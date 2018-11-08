package com.softactive.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.object.Base;
import com.softactive.service.BaseDataRepository;

@Lazy
@Component
public class DataServiceUtils {

	@Autowired
	List<BaseDataRepository<?>> dataServices;

	public BaseDataRepository<?> getDataServiceFromJdbcObject(Class<?> type) {
		for (BaseDataRepository<? extends Base> service : dataServices) {
			if (type.getName().equalsIgnoreCase(service.getParametrizedTypeOfT().getName())) {
				return service;
			}
		}
		return null;
	}

}
