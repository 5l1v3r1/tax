package com.softactive.taxreturn.object;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Indicator extends Base{
	private static final long serialVersionUID = -5209374511044393085L;
	
	@JdbcColumn(field = "id")
	private Integer id;
	@JdbcColumn(field = "code", keyValue = JdbcColumn.VALUE)
	private Integer code;
	@JdbcColumn(field = "description", keyValue = JdbcColumn.VALUE)
	private String description;
	@JdbcColumn(field = "sign", keyValue = JdbcColumn.VALUE)
	private Integer sign;
	@JdbcColumn(field = "parentName", keyValue = JdbcColumn.VALUE)
	private String parentName;
}
