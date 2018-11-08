package com.softactive.taxreturn.object;

import org.joda.time.LocalDate;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class TaxReturnSubjectVersion extends Base{
	private static final long serialVersionUID = -7361416798413056837L;
	@JdbcColumn(field = "id")
	protected Integer id;
	@JdbcColumn(field = "code", keyValue = JdbcColumn.VALUE)
	protected String code;
	@JdbcColumn(field = "version", keyValue = JdbcColumn.VALUE)
	protected Integer version;
	@JdbcColumn(field = "start", keyValue = JdbcColumn.VALUE)
	protected LocalDate start;
	@JdbcColumn(field = "end", keyValue = JdbcColumn.VALUE)
	protected LocalDate end;
}
