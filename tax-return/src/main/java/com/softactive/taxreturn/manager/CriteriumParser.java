package com.softactive.taxreturn.manager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.exception.MyError;
import com.softactive.core.exception.MyException;
import com.softactive.core.manager.AbstractExcelHandler;
import com.softactive.core.object.ExcelCellPointer;
import com.softactive.service.CriteriumService;
import com.softactive.taxreturn.object.Criterium;
import com.softactive.taxreturn.object.TaxExcelPointer;
import com.softactive.taxreturn.object.TaxReturnConstants;

@Component
public class CriteriumParser extends AbstractExcelHandler<Criterium> implements TaxReturnConstants{
	private static final long serialVersionUID = 9107145398620520329L;

	private static final String SHEET_NAME = "Kriterler";
	
	public CriteriumParser() {
		super(new HashMap<String, Object>());
	}
	
	@Override
	protected int getArrayStartIndex() {
		// Skip the header row
		return 1;
	}

	@Override
	protected void mapMetaData(Workbook r) throws MyException {
		sharedParams.put(PARAM_WORKBOOK, r);
		sharedParams.put(PARAM_DATE, getLastDate(r));
		sharedParams.put(PARAM_TAX_RETURN_TYPE, getTaxReturnType(r));
	}

	private LocalDate getLastDate(Workbook wb) {
		String dateString = wb.getSheet("idari").getRow(2).getCell(2).getStringCellValue();
		return LocalDate.parse(dateString);
	}

	private String getTaxReturnType(Workbook wb) {
		return wb.getSheet(SHEET_TAX_RETURN).getRow(1).getCell(0).getStringCellValue();
	}

	@Override
	protected String getSheetName() {
		return SHEET_NAME;
	}
	
	@Autowired
	private CriteriumService cs;

	private AbstractMap.SimpleEntry<String, Integer> getAddress(String cellAddress) throws MyException{
		int rowStart = getRowNumberStartIndex(cellAddress);
		String columnAddress = getColumnAddressName(cellAddress, rowStart);
		int rowNumber = getRowNumber(cellAddress, rowStart);
		return new AbstractMap.SimpleEntry<String, Integer>(columnAddress, rowNumber);
	}

	private int getRowNumber(String cellAddress, int start) {
		String numberString = cellAddress.substring(start);
		return Integer.valueOf(numberString) - 1;
	}

	private String getColumnAddressName(String cellAddress, int end) {
		return cellAddress.substring(0, end);
	}

	private int getRowNumberStartIndex(String cellAddress) throws MyException {
		for(int i = 1; i < cellAddress.length(); i++) {
			char c = cellAddress.charAt(i);
			try {
				Integer.valueOf(c);
				return i;
			} catch (NumberFormatException e) {
				continue;
			}
		}
		throw new MyException("no integer found in cell address (starting with char index:1): " + cellAddress);
	}

	private String getStandardizedAddress(String parentName, AbstractMap.SimpleEntry<String, Integer> address) {
		Workbook wb = (Workbook) sharedParams.get(PARAM_WORKBOOK);
		Sheet sheet = wb.getSheet(parentName);

		TaxExcelPointer ex = new TaxExcelPointer(sheet, address.getValue(), address.getKey());
		String sub = null;
		try {
			LocalDate period = ex.getPeriod();
			LocalDate lastPeriod = (LocalDate) sharedParams.get(PARAM_DATE);
			Years periodDiff = Years.yearsBetween(lastPeriod, period);
			int diff = periodDiff.getYears();
			sub = "[" + diff + "]";
		} catch (MyException e) {
			sub = "'" + e + "'";
		}
		String indicatorName = ex.getCode();
		return parentName + "!" + indicatorName + sub;
	}

	private String getParentName(String formula, int index) {
		return formula.substring(0, index);
	}

	private String getCellAdress(String formula, int index) {
		return formula.substring(index+1);
	}

	@Override
	protected boolean hasNext(Map<String, Object> metaMap) {
		return false;
	}
	
	private String adjustFormula(String rawFormula) {
		List<AbstractMap.SimpleEntry<String, String>> splitted = splitFormula(rawFormula);
		return adjustFormula(splitted);
	}
	
	public static final String ENTRY_INDICATOR = "indicator";
	public static final String ENTRY_TEXT = "text";

	private List<AbstractMap.SimpleEntry<String, String>> splitFormula(String r) {
		List<AbstractMap.SimpleEntry<String, String>> components = new ArrayList<>();
		String preText = "";
		String indicator = "";
		for(int i = 0; i < r.length(); i++) {
			try {
				char op = returnIfOperator(r.charAt(i));
				preText += String.valueOf(op);
				if(indicator.length()>0) {
					AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<>(ENTRY_INDICATOR, indicator);
					components.add(entry);
					indicator = "";
				}
			} catch (MyException e) {
				indicator += e.getMsg();
				if(preText.length()>0) {
					AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<>(ENTRY_TEXT, preText);
					components.add(entry);
					preText = "";
				}
			}
			if(i == r.length()-1) {
				if(indicator.length() > 0) {
					AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<>(ENTRY_INDICATOR, indicator);
					components.add(entry);
					indicator = "";
				} else if(preText.length() > 0) {
					AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<>(ENTRY_TEXT, preText);
					components.add(entry);
					preText = "";
				}
			}
		}
		return components;
	}

	private String adjustFormula(List<AbstractMap.SimpleEntry<String, String>> splitted) {
		String formula = "";
		for(int i = 0; i < splitted.size(); i++) {
			AbstractMap.SimpleEntry<String, String> entry = splitted.get(i);
			if(entry.getKey().equals(ENTRY_INDICATOR)) {
				String address = null;
				try {
					address = outputComponent2(entry.getValue());
				} catch (MyException e) {
					System.out.println(e);
				}
				formula += address;
			} else {
				formula += entry.getValue();
			}
		}
		return formula;
	}

	private void setByProducts(Criterium cr){
		double a = 
				(
						cr.getCeil() - 
						cr.getFloor()
						)
				/
				(
						(
								2-cr.getCeilPoint()
								)
						/
						(
								1-cr.getCeilPoint()
								)
						- 
						(
								2-cr.getFloorPoint()
								)
						/
						(
								1-cr.getFloorPoint()
								)				
						);
		double b = cr.getFloor() - ( a / (1 - cr.getFloorPoint()) ) - a;
		cr.setA(a);
		cr.setB(b);
	}

	private String outputComponent2(String o) throws MyException {
		int index = o.indexOf('!');
		String parentName = getParentName(o, index);
		String cellAddress = getCellAdress(o, index);
		AbstractMap.SimpleEntry<String, Integer> address = getAddress(cellAddress);
		String answer = getStandardizedAddress(parentName, address);
		return answer;
	}
	
	@Override
	public void onListSuccessfullyParsed(List<Criterium> list) {
		super.onListSuccessfullyParsed(list);
		cs.save(list);
	}

	@Override
	protected Criterium outputComponent(Row o) throws MyException {
		Criterium cr = new Criterium();
		cr.setCode((int) o.getCell(0).getNumericCellValue());
		cr.setName(o.getCell(1).getStringCellValue());
		cr.setCoef(o.getCell(2).getNumericCellValue());
		String formula = o.getCell(3).getCellFormula();
		cr.setFormula(adjustFormula(formula));
		cr.setFloor(o.getCell(4).getNumericCellValue());
		cr.setCeil(o.getCell(5).getNumericCellValue());
		cr.setFloorPoint(o.getCell(6).getNumericCellValue());
		cr.setCeilPoint(o.getCell(7).getNumericCellValue());
		cr.setTaxReturn((String)sharedParams.get(PARAM_TAX_RETURN_TYPE));
		setByProducts(cr);
		return cr;
	}

	@Override
	protected boolean isOutputInvalid(List<Criterium> output) {
		// TODO Auto-generated method stub
		return false;
	}

}
