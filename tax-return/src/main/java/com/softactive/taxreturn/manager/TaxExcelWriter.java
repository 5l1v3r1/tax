package com.softactive.taxreturn.manager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.joda.time.LocalDate;

import com.softactive.core.exception.MySheetNotFoundException;
import com.softactive.core.manager.ExcelWriter;
import com.softactive.taxreturn.object.TaxReturnConstants;

public class TaxExcelWriter extends ExcelWriter implements TaxReturnConstants{
	protected void setLastDate() {
		String dateString = workbook.getSheet("idari").getRow(2).getCell(2).getStringCellValue();
		try{
			LocalDate date = LocalDate.parse(dateString);
			stampLastDate(date);
		} catch(IllegalArgumentException e) {
			System.out.println("error while stamping last date to excel");
		}
	}
	@Override
	protected String fileName() {
		return FILE_NAME;
	}
	@Override
	protected void writeExtras() {
		XSSFSheet idari = workbook.getSheet("idari");
		Row r = idari.getRow(2);
		Cell name = r.getCell(0);
		name.setCellValue("Tarih");
		Cell date = r.getCell(2);
		if(date==null) {
			date = r.createCell(2);
		}
		date.setCellValue(lastDate.toString());
		try {
			getSheet(SHEET_FORMULAS);
		} catch (MySheetNotFoundException e) {
			createNewSheet(SHEET_FORMULAS, CRITERIA_COLUMNS);
		}
	}

}
