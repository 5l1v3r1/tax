package com.softactive.taxreturn.object;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.LocalDate;

import com.softactive.core.exception.MyException;
import com.softactive.core.object.ExcelCellPointer;


public class TaxExcelPointer extends ExcelCellPointer {	

	public TaxExcelPointer(Sheet sheet, int rowIndex, String columnLetter) {
		super(sheet, rowIndex, columnLetter);
	}

	public TaxExcelPointer(Sheet sheet, String rowName, String columnName) {
		super(sheet, rowName, columnName);
	}
	
	public LocalDate getPeriod() throws MyException {
		try {
			return LocalDate.parse(getColumnName());
		} catch (IllegalArgumentException e) {
			// throw column name as message of exception to handle
			// non periodic column situations more easily
			throw new MyException(getColumnName());
		}
	}
	
	@Override
	protected String uniqueColumnName() {
		return TaxReturnEntry.CODE;
	}
}
