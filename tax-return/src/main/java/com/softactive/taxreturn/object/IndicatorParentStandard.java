package com.softactive.taxreturn.object;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class IndicatorParentStandard extends Base{
	private static final long serialVersionUID = 7886126440670140663L;
	@JdbcColumn(field = "id")
	private Integer id;
	@JdbcColumn(field = "indicator_id")
	private Integer indicatorId;
	@JdbcColumn(field = "name", keyValue = JdbcColumn.VALUE)
	private String name;
}
