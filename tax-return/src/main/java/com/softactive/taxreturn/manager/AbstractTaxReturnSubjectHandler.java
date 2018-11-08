package com.softactive.taxreturn.manager;

import java.util.HashMap;
import java.util.Map;

import com.softactive.core.object.Base;

public abstract class AbstractTaxReturnSubjectHandler<P extends Base>  extends AbstractTaxHandler<P>{

	private static final long serialVersionUID = -440185393339969161L;
	public static final String[] TAGS = new String[] {
			TAX_RETURN
	};

	@Override
	protected String[] getArrayTags() {
		return TAGS;
	}
}
