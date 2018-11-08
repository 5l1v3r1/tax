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
public class Price extends Base{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6462327896892739619L;
	@JdbcColumn(field = "id")
	private Integer id;
	@JdbcColumn(field = "data_date", keyValue = JdbcColumn.KEY)
	private Date dataDate;
	@JdbcColumn(field = "risk_factor_id", keyValue = JdbcColumn.KEY)
	private Integer riskFactorId;
	@JdbcColumn(field = "price", keyValue = JdbcColumn.VALUE)
	private Double price;
}
