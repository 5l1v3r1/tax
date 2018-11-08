package com.softactive.taxreturn.object;

import com.softactive.core.object.CoreConstants;

public interface TaxReturnConstants{
	
	public static final String TAX_RETURN = "beyanname";

	
	public static String FILE_NAME = "beyanname.xls";

	public static final String SHEET_TAX_RETURN = "Beyanname";
	public static final String SHEET_REPORT = "Rapor";
	public static final String SHEET_FORMULAS = "Kriterler";

	public static final String COLUMN_SOURCE_CODE = "source_code";

	public static final String PARAM_TABLE = "table";
	public static final String PARAM_VERSIONED_SCHEME_XML = "versioned_scheme_xml";
	public static final String PARAM_COMMONS_SCHEME_XML = "commons_scheme_xml";
	public static final String PARAM_CEIL = "ceil";
	public static final String PARAM_CEIL_POINT = "ceil_point";
	public static final String PARAM_FLOOR = "floor";
	public static final String PARAM_FLOOR_POINT = "floor_point";
	public static final String PARAM_CODE = "code";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_IMPORTANCE_COEF = "coef";
	public static final String PARAM_TAX_RETURN_TYPE = "tax_return";
	public static final String PARAM_EXCEL_WRITER = "writer";
	public static final String PARAM_WORKBOOK = "workbook";
	public static final String PARAM_CRITERIUM = "criterium";
	public static final String PARAM_LAST_INDEX = "last_index";





	public static final int ERROR_CONNECTION = 0;
	public static final int ERROR_INVALID_RESPONSE_FORMAT = 1;
	public static final int ERROR_INVALID_RESPONSE_BODY = 2;
	public static final int ERROR_INVALID_VALUE = 3;
	public static final int ERROR_EMPTY_OBJECT_LIST = 4;
	public static final int ERROR_INVALID_METADATA_FORMAT = 5;
	public static final int ERROR_PARSING_METADATA = 13;
	public static final int ERROR_INVALID_DATA_ARRAY_FORMAT = 6;
	public static final int ERROR_INVALID_JSON_OBJECT_FORMAT = 7;
	public static final int ERROR_NULL_JSON_ARRAY = 8;
	public static final int ERROR_EMPTY_JSON_ARRAY = 9;
	public static final int ERROR_INVALID_XML_NODE_TAG = 10;
	public static final int ERROR_PARSING_OBJECT_FROM_XML = 11;
	public static final int ERROR_WRITING_TO_EXCEL = 12;




	public static final String TITLE_DESCRIPTION = "KALEM";
	public static final String TITLE_CODE = "KOD";
	public static final String TITLE_PARENT = "GRUP";
	public static final String TITLE_ID = "ID";
	public static final String TITLE_SIGN = "İŞARET";


	public static final String REP_TAX_RETURN_SUBJECT = "trs_rep";
	public static final String REP_TAX_RETURN_SUBJECT_VERSION = "trsv_rep";
	public static final String REP_CRITERIUM = "criterium_rep";
	public static final String REP_COMPANY = "com_rep";
	public static final String REP_PARENT_ALT = "par_alt_rep";
	public static final String REP_PRICE = "price_rep";
	public static final String REP_RISK_FACTOR = "rf_rep";

	
	public static final String COLUMN_DAILY = "daily";
	public static final String COLUMN_MONTHLY = "monthly";
	public static final String COLUMN_QUARTERLY = "quarterly";
	public static final String COLUMN_ANNUAL = "annual";
	
	
	public static final String COLUMN_CODE = "Kod";
	public static final String COLUMN_NAME = "İsim";
	public static final String COLUMN_COEF = "Önem";
	public static final String COLUMN_FORMULA = "Formül";
	public static final String COLUMN_CEIL = "Alt Limit";
	public static final String COLUMN_FLOOR = "Üst Limit";
	public static final String COLUMN_CEIL_POINT = "Min Puan";
	public static final String COLUMN_FLOOR_POINT = "Max Puan";
	public static final String COLUMN_WEIGHTED_POINT = "Ağırlıklı Puan";
	public static final String COLUMN_POINT = "Puan";


	public static String[] REPORT_COLUMNS = new String[] {
			COLUMN_CODE,
			COLUMN_NAME,
			COLUMN_FORMULA,
			COLUMN_COEF,
			COLUMN_POINT,
			COLUMN_WEIGHTED_POINT
	};
	
	public static String[] CRITERIA_COLUMNS = new String[] {
			COLUMN_CODE,
			COLUMN_NAME,
			COLUMN_COEF,
			COLUMN_FORMULA,
			COLUMN_CEIL,
			COLUMN_FLOOR,
			COLUMN_CEIL_POINT,
			COLUMN_FLOOR_POINT
	};
	
	public static String[] TAX_RETURN_COLUMNS = new String[] {
			"KOD",
			COLUMN_NAME
	};

}
