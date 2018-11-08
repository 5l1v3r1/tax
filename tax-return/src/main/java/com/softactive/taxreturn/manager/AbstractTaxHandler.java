package com.softactive.taxreturn.manager;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;

import com.softactive.core.exception.MyException;
import com.softactive.core.manager.AbstractXmlHandler;
import com.softactive.taxreturn.object.TaxReturnConstants;

public abstract class AbstractTaxHandler<P>  extends AbstractXmlHandler<P> implements TaxReturnConstants{
	public AbstractTaxHandler() {
		super(new HashMap<String, Object>());
	}

	private static final long serialVersionUID = 456533834758113624L;
	public static final String VERSION = "versiyon";
	public static final String CODE = "kod";
	public static final String START = "yayimTarihi";
	public static final String END = "kaldirilisTarihi";
	public static final String NAME = "adi";
	@Override
	protected LocalDate resolveDateFromCustomFormat(String dateString) throws MyException {
		String[] dateArray = dateString.split("\\.");
		if(dateArray.length != 3) {
			throw new MyException("Cannot adjusted the date string: '" + dateString +
					"with splitter: '.'"); 
		}
		String adjustedString = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
		return resolveValidDate(adjustedString);
	}
	
	@Override
	protected boolean hasNext(Map<String, Object> metaMap) {
		return false;
	}
}
