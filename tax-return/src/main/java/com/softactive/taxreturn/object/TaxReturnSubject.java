package com.softactive.taxreturn.object;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class TaxReturnSubject extends Base{
	private static final long serialVersionUID = -689981111293289903L;
	@JdbcColumn(field = "name", keyValue = JdbcColumn.VALUE)
	protected String name;
	@JdbcColumn(field = "id")
	protected Integer id;
	@JdbcColumn(field = "code", keyValue = JdbcColumn.KEY)
	protected String code;
}
