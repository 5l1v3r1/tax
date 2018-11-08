package com.softactive.taxreturn.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.softactive.core.exception.MyException;
import com.softactive.core.exception.MySheetNotFoundException;
import com.softactive.core.manager.ExcelWriter;
import com.softactive.core.object.ExcelEntry;
import com.softactive.service.CriteriumService;
import com.softactive.service.IndicatorParentNameStandardService;
import com.softactive.taxreturn.object.IndicatorParentStandard;
import com.softactive.taxreturn.object.TaxReturnEntry;

@Component
public class TaxReturnHandler extends AbstractTaxHandler<TaxReturnEntry>{
	private static final long serialVersionUID = 705861168932568047L;
	private TaxExcelWriter writer = new TaxExcelWriter();
	@Autowired
	private CriteriumService cs;
	@Autowired
	CodeMatcher matcher;
	private CriteriumChecker crChecker;
	public static final String VERSION = "kodVer";
	public static final String TAX_RETURN = "beyanname";
	public static final String SIGN = "sign";

	// tags to filter nodes to match from scheme
	public static final String CODE = "kod";
	public static final String TYPE = "tip";
	public static final String KIND = "turu";
	public static final String GENRE = "tur";
	public static final String FIRM_KIND = "isyeriTuru";
	public static final String ELSE = "diğer";
	public static final String[] TAGS_KEY = new String[] {
			CODE,
			TYPE,
			KIND,
			GENRE,
			FIRM_KIND,
			ELSE
	};



	public static final String PERIOD_CURRENT = "cd";
	public static final String PERIOD_PAST = "od";

	// regexes to search in tags to understand if the value in the node
	// precedent period.
	private static final String[] PAST_VALUE_REGEXES = new String[] {
			"oncekiDonem(.*)",
			PERIOD_PAST
	};

	private static final String[] CURRENT_VALUE_REGEXES = new String[] {
			"cariDonem(.*)",
			PERIOD_CURRENT
	};

	public static final String PERIOD = "donem";
	public static final String FREQUENCY = "tip";
	public static final String YEAR = "yil";


	private static final String NO_DATE = "no_date";
	private static final String MULTI_PERIOD = "multi_period";
	private static final String MULTI_INDICATOR = "multi_indicator";

	// Tags for single value node to prevent crossing codes with indicator name
	// If we notice such tags in a node, we will not change the unique code of indicator
	// if not means such node has just one code but multiple indicators lay inside in it
	// so we generate new unique codes from original code as base and the name of node in which
	// the value is written as suffix
	private static final String[] SINGLE_VALUE_TAGS = new String[] {
			"tutari",
			"tutar",
			"zarar",
			"isyeriSayisi"
	};

	private static final String[] INDEXED_TITLE_REGEXES = new String[] {
			"(.*)BKK",
			"(.*)Bolge",
			"(.*)Turu",
			"ilIlce",
			"ulke"
	};

	//	private static final String[] FILTERED_PARENT_NAMES = new String[] {
	//			"idari"
	//	};

	private static final String[] COMMON_SCHEME_NODE_NAMES = new String[] {
			"ulke",
			"ilIlce"
	};

	public static final String COMMON_CODES_SCHEME_PATH = "src/main/resources/version/Kodlar.xml";

	//	private LocalDate lastDate;
	//	private String frq;

	@Override
	protected List<Element> inputBody(Document r) {
		return resolveAllNodes(r);
	}

	@Override
	protected void mapMetaData(Document r) throws MyException {
		Element main = (Element) r.getElementsByTagName(TAX_RETURN).item(0);

		//match code scheme xml with uploaded tax return xml document
		String name = main.getAttribute(VERSION);

		TreeMap<String, String> taxReturnInfo = new TreeMap<>();
		taxReturnInfo.put(TaxReturnEntry.CODE, "Beyanname Türü");

		taxReturnInfo.put("İsim", name.split("_")[0]);
		writer.write(taxReturnInfo, SHEET_TAX_RETURN, TAX_RETURN_COLUMNS);

		String filePath = "src/main/resources/version/" + name + "_Kodlar.xml";
		Document codeScheme = getCodeScheme(filePath);
		if(codeScheme==null) {
			throw new MyException("cannot find any attribute in meta data with name: " + VERSION +
					"\nmeta data element: " + main);
		} else {
			sharedParams.put(PARAM_VERSIONED_SCHEME_XML, codeScheme);
		}

		//period
		Element period = (Element) r.getElementsByTagName(PERIOD).item(0);

		//frequency
		String frq = getValue(period, FREQUENCY);
		sharedParams.put(PARAM_FREQUENCY_ID, frq);

		//date
		String yearString = getValue(period, YEAR);
		int y = resolveValidInteger(yearString);

		LocalDate lastDate = new LocalDate(y, 12, 31);
		sharedParams.put(PARAM_DATE, lastDate);

		Document commonsScheme = getCodeScheme(COMMON_CODES_SCHEME_PATH);
		sharedParams.put(PARAM_COMMONS_SCHEME_XML, commonsScheme);

		sharedParams.put(PARAM_WORKBOOK, writer.getWorkbook());

		writer.stampLastDate(lastDate);

		crChecker = new CriteriumChecker(sharedParams);
	}

	@Override
	public void onListSuccessfullyParsed(List<TaxReturnEntry> list) {
		System.out.println("one xml file is parsed and added into excel. " + list.size() + " entries are resolved");
	}



	public void checkCriteria() {
		sharedParams.put(PARAM_EXCEL_WRITER, writer);
		crChecker.handle(cs);
	}

	private Document getCodeScheme(String filePath) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(filePath);
			doc.normalize();
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Document codeScheme(String key, boolean isCommonScheme) {
		Document codeScheme = null;
		for(String filter:COMMON_SCHEME_NODE_NAMES) {
			if(key.equals(filter)) {
				codeScheme = (Document) sharedParams.get(PARAM_COMMONS_SCHEME_XML);
				isCommonScheme = true;
			}
		}
		if(codeScheme==null) {
			codeScheme = (Document) sharedParams.get(PARAM_VERSIONED_SCHEME_XML);
		}
		return codeScheme;
	}

	

	private void fillBaseEntry(TaxReturnEntry en, Node n, String name, String code)  throws MyException{
		String parentName = en.getParentName();
		boolean isCommonScheme = false;
		// isCommonScheme will be updated as true if detected scheme is the common one
		// and not the one for version and type of uploaded xml
		Document codeScheme = codeScheme(name, isCommonScheme);

		// scheme is detected
		// now try to detect parent node for match code with its definiton
		matcher.fillBasicsOfEntry(codeScheme, en, n, name, code, isCommonScheme);
	}

	
	

	@Override
	protected String[] getArrayTags() {
		return TAGS_KEY;
	}

//	private LocalDate getPeriodBefore(LocalDate current, String frq) throws MyException {
//		if(current == null) {
//			throw new MyException("current date object is null");
//		}
//		LocalDate past = current.minusYears(1);
//		return past;
//	}

//	private String getDate(Node n) throws MyException {
//		String dateString = n.getTextContent();
//		LocalDate date = resolveValidDate(dateString + "-12-31");
//		return date.toString();
//	}

//	private void writeAsDiffSheet(Node parent) throws MyException{
//		NodeList list = parent.getChildNodes();
//		TreeMap<String, TaxReturnEntry> entries = new TreeMap<String, TaxReturnEntry>();
//		TreeMap<String, String> extras = new TreeMap<>();
//		String singleDateFromChild = NO_DATE;
//		TaxReturnEntry baseEntry = null;
//		String parentName = parent.getNodeName();
//		if(parentName.length()>31) {
//			parentName = parentName.substring(0, 31);
//		}
//		for(int i = 0; i < list.getLength(); i++) {
//			try {
//				Node child = list.item(i);
//				String name = child.getNodeName();
//				if(name.startsWith("#")) {
//					continue;
//				}
//				if(child.getChildNodes().getLength()>1) {
//					continue;
//				}
//				String value = child.getTextContent();
//				boolean keyNode = false;
//				for(String key:TAGS_KEY) {
//					if(name.equals(key)) {
//						parentName = parent.getParentNode().getNodeName();
//						try {
//							fillBaseEntry(baseEntry, name, value);
//							if(name == CODE) {
//								baseEntry.setCode(null);
//							}
//						} catch (MyException e) {
//							System.out.println(e);
//						}
//						if(parentName.length()>31) {
//							parentName = parentName.substring(0, 31);
//						}
//						keyNode = true;
//						break;
//					}
//				}
//				if(keyNode) {
//					continue;
//				}
//				if(name.equals(YEAR)) {
//					singleDateFromChild = getDate(child);
//					for(String entryKey: entries.keySet()) {
//						TaxReturnEntry existingEntry = entries.get(entryKey);
//						for(String priceKey: existingEntry.getPriceKeys()) {
//							String existingValue = existingEntry.getPrice(priceKey);
//							existingEntry.addPrice(singleDateFromChild, existingValue);
//							existingEntry.removePrice(priceKey);
//						}
//					}
//					continue;
//				}
//				boolean indexedIndicatorNode = false;
//				for(int t = 1; t < 3; t++) {
//					if(name.matches(REGEX_SUFFIX + t)) {
//						TaxReturnEntry newEntry = entries.get(String.valueOf(t));
//						if(newEntry == null) {
//							newEntry = getWhiteEntry(parentName);
//						}
//						for(String regex:PAST_VALUE_REGEXES) {
//							if(name.matches(regex)) {
//								String entryName = name.substring(regex.length());
//								newEntry.setName(entryName);
//								newEntry.setCode(entryName);
//								String frq = (String) sharedParams.get(PARAM_FREQUENCY_ID);
//								LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE); 
//								LocalDate past = null;
//								past = getPeriodBefore(current, frq);
//								String pricePast = null;
//								pricePast = resolveValidString(value, null);
//								newEntry.addPrice(past.toString(), pricePast);
//								entries.put(String.valueOf(t), newEntry);
//								indexedIndicatorNode = true;
//								break;
//							}
//						}
//						if(indexedIndicatorNode) {
//							break;
//						}
//						for(String regex:CURRENT_VALUE_REGEXES) {
//							if(name.matches(regex)) {
//								String entryName = name.substring(regex.length());
//								newEntry.setName(entryName);
//								newEntry.setCode(entryName);
//								LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE);
//								newEntry.addPrice(current.toString(), resolveValidString(value, null));
//								entries.put(String.valueOf(t), newEntry);
//								indexedIndicatorNode = true;
//								break;
//							}
//						}
//					}
//				}
//				if(indexedIndicatorNode) {
//					continue;
//				}
//				boolean pastValueNode = false;
//				for(String regex:PAST_VALUE_REGEXES) {
//					if(name.matches(regex)) {
//						TaxReturnEntry newEntry = entries.get(MULTI_PERIOD);
//						if(newEntry == null) {
//							newEntry = getWhiteEntry(parentName);
//						}
//						String frq = (String) sharedParams.get(PARAM_FREQUENCY_ID);
//						LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE); 
//						LocalDate past = null;
//						past = getPeriodBefore(current, frq);
//						String pricePast = resolveValidString(value, null);
//						newEntry.addPrice(past.toString(), pricePast);
//						entries.put(MULTI_PERIOD, newEntry);
//						pastValueNode = true;
//						break;
//					}
//				}
//				if(pastValueNode) {
//					continue;
//				}
//				boolean currentValueNode = false;
//				for(String regex:CURRENT_VALUE_REGEXES) {
//					if(name.matches(regex)) {
//						TaxReturnEntry newEntry = entries.get(MULTI_PERIOD);
//						if(newEntry == null) {
//							newEntry = getWhiteEntry(parentName);
//						}
//						LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE);
//						newEntry.addPrice(current.toString(), resolveValidString(value, null));
//						entries.put(MULTI_PERIOD, newEntry);
//						currentValueNode = true;
//						break;
//					}
//				}
//				if(currentValueNode) {
//					continue;
//				}
//				boolean indexedTitleNode = false;
//				for(String regex: INDEXED_TITLE_REGEXES) {
//					if(name.matches(regex)) {
//						TaxReturnEntry tempEntry = null;
//						try {
//							fillBaseEntry(tempEntry, name, value);
//						} catch (MyException e) {
//							System.out.println(e);
//							continue;
//						}
//						extras.put(name, tempEntry.getName());
//						indexedTitleNode = true;
//						break;
//					}
//				}
//				if(indexedTitleNode) {
//					continue;
//				}
//				String price = resolveValidString(value, null);
//				TaxReturnEntry newEntry = getWhiteEntry(parentName);
//				newEntry.setCode(name);
//				newEntry.setName(name);
//				newEntry.addPrice(singleDateFromChild, price);
//				entries.put(MULTI_INDICATOR + name, newEntry);
//				continue;
//			} catch (MyException e) {
//				continue;
//			}
//		}
//		adjustAndWrite(entries, extras, baseEntry, parentName, singleDateFromChild, sharedParams);
//	}

//	private TaxReturnEntry getWhiteEntry(String parentName) {
//		TaxReturnEntry whiteEntry = new TaxReturnEntry();
//		whiteEntry.setCode(parentName);
//		whiteEntry.setName(parentName);
//		whiteEntry.setParentName(parentName);
//		return whiteEntry;
//	}

//	private void adjustAndWrite(TreeMap<String, TaxReturnEntry> entries, TreeMap<String, String> extras, TaxReturnEntry baseEntry, String parentName, String singleDateFromChild, Map<String, Object> sharedParams) throws MyException{
//		if(entries.size()==0) {
//			entries.put(parentName, getWhiteEntry(parentName));
//		}
//		for(String entryKey:entries.keySet()) {
//			TaxReturnEntry en = entries.get(entryKey);
//			if(entryKey.equals(MULTI_PERIOD)) {
//				if(baseEntry!=null) {
//					en.setCode(baseEntry.getCode());
//					en.setName(baseEntry.getName());
//				}
//			}
//			if(entryKey.startsWith(MULTI_INDICATOR)) {
//				if(baseEntry!=null) {
//					en.setCode(baseEntry.getCode() + "_" + en.getCode());
//					en.setName(baseEntry.getName());
//				} else {
//					String name = entryKey.substring(MULTI_INDICATOR.length());
//					en.setCode(name);
//					en.setName(name);
//				}
//			}
//			for(String priceKey:en.getPriceKeys()) {
//				if(priceKey == NO_DATE) {
//					String existingValue = en.getPrice(NO_DATE);
//					en.removePrice(NO_DATE);
//					String dateString = null;
//					if(!singleDateFromChild.equals(NO_DATE)) {
//						dateString = singleDateFromChild;
//					} else {
//						dateString = getCurrentDateString(sharedParams);
//					}
//					en.addPrice(dateString, existingValue);
//				}
//				en.setExtras(extras);
//			}
//			try {
//				writer.write(en.getValuesAsMap(), parentName, TAX_RETURN_COLUMNS);
//			} catch (MyException e) {
//				System.out.println(e);
//			}
//		}
//	}

//	private String getCurrentDateString(Map<String, Object> sharedParams) {
//		LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE);
//		return current.toString();
//	}

	@Override
	protected TaxReturnEntry outputComponent(Element e) throws MyException {
		String tableName = e.getParentNode().getNodeName();
		TaxReturnEntry en = new TaxReturnEntry();
		en.setParentName(tableName);
		fillEntry(en, e);
		//		writeAsDiffSheet(e);
		return en;
	}

	private void fillEntry(TaxReturnEntry en, Node n) {
		NodeList list = n.getChildNodes();
		for(int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			String name = child.getNodeName();
			if(name.startsWith("#")) {
				continue;
			}
			if(child.getChildNodes().getLength()>1) {
				continue;
			}
			appendToEntry(en, child);
		}
	}

	private void appendToEntry(TaxReturnEntry en, Node n) {
		String key = n.getNodeName();
		String value = n.getTextContent();
		for(String name:TAGS_KEY) {
			if(name.equals(key)) {
				try {
					fillBaseEntry(en, n, name, value);
					if(name == CODE) {
						en.setCode(null);
					}
				} catch (MyException e) {
					System.out.println(e);
				}
				break;
			}
		}
	}


	@Override
	protected boolean isOutputInvalid(List<TaxReturnEntry> output) {
		// TODO Auto-generated method stub
		return false;
	}
}
