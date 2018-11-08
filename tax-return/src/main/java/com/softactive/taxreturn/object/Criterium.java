package com.softactive.taxreturn.object;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Criterium extends Base{
	private static final long serialVersionUID = -8518928435692974357L;
	@JdbcColumn(field = "id")
	private Integer id;
	@JdbcColumn(field = "formula", keyValue = JdbcColumn.VALUE)
	private String formula;
	@JdbcColumn(field = "floor", keyValue = JdbcColumn.VALUE)
	private Double floor;
	@JdbcColumn(field = "floor_point", keyValue = JdbcColumn.VALUE)
	private Double floorPoint;
	@JdbcColumn(field = "ceil", keyValue = JdbcColumn.VALUE)
	private Double ceil;
	@JdbcColumn(field = "ceil_point", keyValue = JdbcColumn.VALUE)
	private Double ceilPoint;
	@JdbcColumn(field = "code", keyValue = JdbcColumn.VALUE)
	private Integer code;
	@JdbcColumn(field = "tax_return", keyValue = JdbcColumn.VALUE)
	private String taxReturn;
	@JdbcColumn(field = "coef", keyValue = JdbcColumn.VALUE)
	private Double coef;
	@JdbcColumn(field = "a", keyValue = JdbcColumn.VALUE)
	private Double a;
	@JdbcColumn(field = "b", keyValue = JdbcColumn.VALUE)
	private Double b;
	@JdbcColumn(field = "name", keyValue = JdbcColumn.VALUE)
	private String name;
}
