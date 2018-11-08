package com.softactive.taxreturn.object;

import java.util.TreeMap;

import org.joda.time.LocalDate;

import com.softactive.core.object.ExcelEntry;

import lombok.Getter;
import lombok.Setter;


public class TaxReturnEntry extends ExcelEntry {
	public static final String TABLE = "TABLO";
	public static final String NAME = "KALEM";
	public static final String DATE = "TARİH";

	public static final String CODE = "KOD";
	public static final String PERIOD = "DÖNEM";

	//pre columns by order
	@Getter @Setter
	private String parentName;
	@Getter @Setter
	protected String name;
	@Getter @Setter
	private LocalDate date;
	
	//post columns by order
	@Getter @Setter
	protected String code;
	@Getter @Setter
	private Integer period;

	protected TreeMap<String, String> preColumnsMap(){
		TreeMap<String, String> answer = new TreeMap<String, String>();
		answer.put(TABLE, parentName);
		answer.put(NAME, name);
		answer.put(DATE, date.toString());
		return answer;
	};
	protected void addPostColumns(TreeMap<String, String> entryMap){
		entryMap.put(CODE, code);
		entryMap.put(PERIOD, String.valueOf(period));
	};
}
