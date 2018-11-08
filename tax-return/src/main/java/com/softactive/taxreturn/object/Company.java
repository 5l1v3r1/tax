package com.softactive.taxreturn.object;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Company extends Base{
	private static final long serialVersionUID = 3950538959007526541L;
	
	@JdbcColumn(field = "name", keyValue = JdbcColumn.VALUE)
	protected String name;
	@JdbcColumn(field = "id")
	protected Integer id;

}
