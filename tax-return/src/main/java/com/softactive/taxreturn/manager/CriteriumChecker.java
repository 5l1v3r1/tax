package com.softactive.taxreturn.manager;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.softactive.core.exception.MyError;
import com.softactive.core.exception.MyException;
import com.softactive.core.manager.AbstractHandler;
import com.softactive.core.manager.ExcelWriter;
import com.softactive.core.object.CoreConstants;
import com.softactive.core.object.ExcelCellPointer;
import com.softactive.core.object.ExcelEntry;
import com.softactive.service.CriteriumService;
import com.softactive.taxreturn.object.Criterium;
import com.softactive.taxreturn.object.TaxReturnConstants;

public class CriteriumChecker extends AbstractHandler<CriteriumService, List<Criterium>, List<Criterium>, Criterium, 
TreeMap<String, String>, List<TreeMap<String, String>>> implements TaxReturnConstants{
	
	public CriteriumChecker(Map<String, Object> sharedParams) {
		super(sharedParams);
	}

	private static final long serialVersionUID = 4534755617845373062L;

	private AbstractMap.SimpleEntry<String, String> getAddress(String pseudoAddress) throws MyException{
		int columnStart = getColumnNameStartIndex(pseudoAddress);
		String columnName = getColumnName(pseudoAddress, columnStart);
		String rowName = getRowName(pseudoAddress, columnStart);
		return new AbstractMap.SimpleEntry<String, String>(rowName, columnName);
	}

	private String getRowName(String cellAddress, int end) {
		return cellAddress.substring(0, end);
	}
	
	private String getColumnName(String cellAddress, int start) {
		String columnName = cellAddress.substring(start + 1, cellAddress.length() - 1);
		if(cellAddress.charAt(start)=='[') {
			int periodBefore = Integer.valueOf(columnName);
			return getLocalDate(periodBefore).toString();
		}
		return cellAddress.substring(start, cellAddress.length() - 1);
	}
	
	private LocalDate getLocalDate(int periodBefore) {
		LocalDate current = (LocalDate)sharedParams.get(PARAM_DATE);
		return current.plusYears(-1*periodBefore);
	}

	private int getColumnNameStartIndex(String cellAddress) throws MyException {
		for(int i = 1; i < cellAddress.length(); i++) {
			char c = cellAddress.charAt(i);
			if(c == '\'' || c =='[') {
				return i;
			}
		}
		throw new MyException("no column definer char found in cell address (starting with char index:1): " + cellAddress);
	}

	private String getParentName(String formula, int index) {
		return formula.substring(0, index);
	}

	private String getCellAdress(String formula, int index) {
		return formula.substring(index+1);
	}
	
	private List<String> splitFormula(Criterium cr) {
		String formula = cr.getFormula();
		List<String> components = new ArrayList<String>();
		String preText = "";
		String indicator = "";
		int index = 0;
		for(int i = 0; i < formula.length(); i++) {
			try {
				char op = returnIfOperator(formula.charAt(i));
				preText += String.valueOf(op);
				if(indicator.length()>0) {
					components.add(indicator);
					indicator = "";
				}
			} catch (MyException e) {
				indicator += e.getMsg();
				if(preText.length()>0) {
					sharedParams.put(String.valueOf(index), preText);
					index++;
					preText = "";
				}
				if(index==0) {
					index = 1;
				}
			}
			if(i == formula.length()-1) {
				if(indicator.length() > 0) {
					components.add(indicator);
					indicator = "";
				} else if(preText.length() > 0) {
					sharedParams.put(String.valueOf(index), preText);
					preText = "";
				}
			}
		}
		return components;
	}
	
	private String getCellAddress(String s) throws MyException {
		int index = s.indexOf('!');
		String parentName = getParentName(s, index);
		String pseudeCellAddress = getCellAdress(s, index);
		SimpleEntry<String, String> address = getAddress(pseudeCellAddress);
		Workbook wb = (Workbook) sharedParams.get(PARAM_WORKBOOK);
		Sheet sheet = wb.getSheet(parentName);
		ExcelCellPointer pointer = new ExcelCellPointer(sheet, address.getKey(), address.getValue());
		return pointer.getFullAddress();
	}
	
	private TreeMap<String, String> getMappedCriterium(Criterium cr, String formula) {
		TreeMap<String, String> answer = new TreeMap<String, String>();
		answer.put(COLUMN_FORMULA, formula);
		answer.put(COLUMN_CEIL, String.valueOf(cr.getCeil()));
		answer.put(COLUMN_CEIL_POINT, String.valueOf(cr.getCeilPoint()));
		answer.put(COLUMN_FLOOR, String.valueOf(cr.getFloor()));
		answer.put(COLUMN_FLOOR_POINT, String.valueOf(cr.getFloorPoint()));
		answer.put(COLUMN_NAME, cr.getName());
		answer.put(COLUMN_COEF, String.valueOf(cr.getCoef()));
//		answer.put(ExcelEntry.CODE, String.valueOf(cr.getCode()));
		double a = cr.getA();
		double b = cr.getB();
		String point= "=(" + FORMULA_DEFINER + COLUMN_FORMULA + "-" + b + "-2*" + a + ")/(" + 
		FORMULA_DEFINER + COLUMN_FORMULA + "-" + b + "-" + a + ")";
		answer.put(COLUMN_POINT, point);
		answer.put(COLUMN_WEIGHTED_POINT, "=" + FORMULA_DEFINER + COLUMN_POINT + "*" + FORMULA_DEFINER + COLUMN_COEF);
		answer.put(COLUMN_CODE, String.valueOf(cr.getCode()));
		return answer;
	}
	
	@Override
	protected List<Criterium> parsedInput(CriteriumService rowInput) throws MyException {
		return rowInput.listOfObject();
	}

	@Override
	protected void mapMetaData(List<Criterium> r) throws MyException {
		sharedParams.put(PARAM_LAST_INDEX, 0);
		sharedParams.put("0", "");
	}

	@Override
	protected boolean hasNext(Map<String, Object> metaMap) {
		return false;
	}
	
	@Override
	protected List<Criterium> inputBody(List<Criterium> criteria) {
		return criteria;
	}

	@Override
	protected TreeMap<String, String> outputComponent(Criterium cr) throws MyException {
		List<String> splittedCriterium = splitFormula(cr);
		String formula = "=" + (String) sharedParams.get("0");
		for(int i = 0; i < splittedCriterium.size(); i++) {
			String address = null;
			try {
				address = getCellAddress(splittedCriterium.get(i));
			} catch (MyException e) {
				System.out.println(e);
			}
			formula += address;
			String end = (String) sharedParams.get(String.valueOf(i+1));
			if(end != null) {
				formula += end;
			}
		}
		return getMappedCriterium(cr, formula);
	}
	
	@Override
	protected List<TreeMap<String, String>> output(List<Criterium> array) {
		List<TreeMap<String, String>> answer = new ArrayList<>();
		for(Criterium cr:array) {
			try {
				answer.add(outputComponent(cr));
			} catch (MyException e) {
				e.printStackTrace();
			}
		}
		return answer;
	}
	
	@Override
	protected boolean isOutputInvalid(List<TreeMap<String, String>> output) {
		if(output.size()==0) {
			MyError er = new MyError(1,"couldnt parse any entry component from criteria");
			sharedParams.put(PARAM_ERROR, er);
			return true;
		}
		for(TreeMap<String, String> component:output) {
			if(component==null) {
				MyError er = new MyError(1,"there is a null entry in output entries");
				sharedParams.put(PARAM_ERROR, er);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onListSuccessfullyParsed(List<TreeMap<String, String>> entries) {
		super.onListSuccessfullyParsed(entries);
		ExcelWriter writer = (ExcelWriter) sharedParams.get(PARAM_EXCEL_WRITER);
		for(TreeMap<String, String> entry:entries) {
			try {
				ExcelCellPointer pointer = writer.write(entry, SHEET_REPORT, REPORT_COLUMNS);
			} catch (MyException e1) {
				System.out.println(e1);
			}
			try {
				writer.flush(FILE_NAME);
			} catch (MyException e) {
				System.out.println(e);
			}
		}
	}
}
