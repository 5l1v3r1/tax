package com.softactive.taxreturn.object;

import java.sql.Date;

import com.softactive.core.object.Base;
import com.softactive.core.utils.JdbcColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class RiskFactor extends Base{
	private static final long serialVersionUID = -908150266455822668L;
	@JdbcColumn(field = "company_id", keyValue = JdbcColumn.KEY)
	private Integer companyId;
	@JdbcColumn(field = "subject_id", keyValue = JdbcColumn.KEY)
	private Integer subjectId;
	@JdbcColumn(field = "id")
	private Integer id;
	@JdbcColumn(field = "update_date", keyValue = JdbcColumn.VALUE)
	private Date updateDate;
}
