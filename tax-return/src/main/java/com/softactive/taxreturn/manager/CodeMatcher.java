package com.softactive.taxreturn.manager;

import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.softactive.core.exception.MyException;
import com.softactive.core.object.CoreConstants;
import com.softactive.core.utils.ParserUtils;
import com.softactive.service.IndicatorParentNameStandardService;
import com.softactive.taxreturn.object.IndicatorParentStandard;
import com.softactive.taxreturn.object.TaxReturnEntry;

@Component
public class CodeMatcher implements CoreConstants{

	public static final String DESCRIPTION = "aciklama";
	public static final String NAME = "ad";

	
	private static final String[] TAGS_FOR_CONCAT = new String[] {
			"isletmeTuru"
	};

	private static final String[] SUFFIX_TO_RETAIN = new String[] {
			"BKK",
			"Bolge"
	};
	
	public static final String[] NAME_TAGS_FROM_SCHEME = new String[] {
			DESCRIPTION,
			NAME
	};
	
	
	private Document doc;
	@Autowired
	private IndicatorParentNameStandardService ipss;
	
	private void fillBasicsOfEntry(TaxReturnEntry entry, Node e, String code) throws MyException {
		entry.setCode(code);
		TreeMap<String, String> values = ParserUtils.getAsMap(e);
		String name = null;
		for(String tag:NAME_TAGS_FROM_SCHEME) {
			name = values.get(tag);
			if(name != null) {
				break;
			}
		}
		if(name!=null) {
			entry.setName(name);
		}
		throw new MyException("couldn't detect name tag from scheme node. The value map:\n" + values);
	}
	
	private Node commonNode(String name) {
		Node parentNode = null;
		NodeList tempList = doc.getElementsByTagName(name);
		Node tempNode = null;
		if(tempList != null && tempList.getLength()>0){
			tempNode = tempList.item(0);
		} else {
			List<IndicatorParentStandard> list = ipss.findAlternativesByName(name);
			if(list!=null) {
				for(IndicatorParentStandard ips:list) {
					parentNode = doc.getElementsByTagName(ips.getName()).item(0);
					if(parentNode!=null) {
						break;
					}
				}
			}
		}
		if(tempNode != null) {
			parentNode = tempNode.getParentNode();
		}
		return parentNode;
	}

	private Node parentNodeByKeyOrAlternatives(String parentName) {
		Node parentNode = doc.getElementsByTagName(parentName).item(0);
		if(parentNode!=null) {
			return parentNode;
		}
		List<IndicatorParentStandard> list = ipss.findAlternativesByName(parentName);
		if(list!=null) {
			for(IndicatorParentStandard ips:list) {
				parentNode = (Element) doc.getElementsByTagName(ips.getName()).item(0);
				if(parentNode!=null) {
					return parentNode;
				}
			}
		}
		return null;
	}
	
	private Node parentNode(Document doc, String parentName, String name, String code, boolean isCommonScheme) throws MyException {
		this.doc = doc;
		Node parentNode = null;

		// try for just if common scheme is used
		if(isCommonScheme) {
			parentNode = commonNode(name);
		}

		// common scheme or not, continue until detect true parent node!
		if(parentNode == null) {
			parentNode = parentNodeByKeyOrAlternatives(parentName);
		}

		if(parentNode == null) {
			for(String suffix:SUFFIX_TO_RETAIN) {
				// some keys has e pre-part like yatirimBolge but we need just the suffix
				// which can be match from the common scheme. so try;
				if(name.matches(REGEX_SUFFIX + suffix)) {
					String tmpParentName = suffix.toLowerCase();
					parentNode = parentNodeByKeyOrAlternatives(tmpParentName).getParentNode();
					break;
				}
			}
		}
		if(parentNode == null) {
			for(String tag:TAGS_FOR_CONCAT) {
				// we detect such patern by node's own key name
				// but use its parent name
				if(name.equals(tag)) {
					// some keys are not defined by node name but better defined in its parent node
					// so lets get it to manipulate
					parentNode = parentNodeFromParentName(parentName, name, code, tag);
					break;
				}
			}
		}
		if(parentNode == null) {
			throw new MyException("couldn't find any parent node.\nparent name: " + parentName + "\nname: " + name +
					"\ncode: " + code);
			//					TaxReturnEntry ent = getWhiteEntry(name);
			//					ent.setParentName(parentName);
			//					LocalDate current = (LocalDate) sharedParams.get(PARAM_DATE);
			//					ent.addPrice(current.toString(), code);
			//					return ent;			
		}
		return parentNode;
	}

	private Node parentNodeFromParentName(String parentName, String name, String code, String tag) {
		String tmpParentName = parentName;

		// some pre-parts are cut in code scheme like yurtDisi
		// so we also cut our parent name 
		if(parentName.matches("yurtDisi(.*)")) {
			tmpParentName = tmpParentName.substring("yurtdisi".length());
		}

		// just the first word of remaining parent name is inside the code scheme
		// lets pick the first word
		String firstWord = ParserUtils.pickTheFirstWordFromCamelCase(tmpParentName);
		if(firstWord != null) {
			tmpParentName = firstWord;
		}

		// concatenating calculated base key from parent name
		// with detected tag, will generate child node name from code scheme
		String firstChar = String.valueOf(tag.charAt(0)).toUpperCase();
		tag = firstChar + tag.substring(1);
		tmpParentName = tmpParentName + tag;

		// generated child node name will find a child node
		// but we need the parent to iterate over.
		// so we can find matching code between them
		Node tmpNode =  parentNodeByKeyOrAlternatives(tmpParentName);
		if(tmpNode!=null) {
			return tmpNode.getParentNode();
		}
		return null;
	}

	public void fillBasicsOfEntry(Document doc, TaxReturnEntry en, Node n, String key, String value, boolean isCommonScheme) throws MyException {
		Node pNode = parentNode(doc, en.getParentName(), key, value, isCommonScheme);
		fillBaseEntry(en, pNode, key, value);
	}
	
	private void fillBaseEntry(TaxReturnEntry en, Node parentNode, String name, String code) throws MyException {
		NodeList list = parentNode.getChildNodes();
		String[] codeTitles = new String[] {
				TaxReturnEntry.CODE,
				name
		};
		for(String title:codeTitles) {
			for(int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if(n instanceof Element) {
					Element e = (Element) list.item(i);
					Element eCode = (Element) e.getElementsByTagName(title).item(0);
					if(eCode == null) {
						break;
					}
					if(eCode.getTextContent().equals(code)) {
						fillBasicsOfEntry(en, e, name, code);
					}
				}
			}
		}
		throw new MyException("cannot find the code in parent node.\n" + 
				"parent node name: " + parentNode.getNodeName() + "\nname: " + name + "\ncode:" + code);
	}
	private void fillBasicsOfEntry(TaxReturnEntry entry, Element e, String parentName, String code) throws MyException {
		entry.setCode(code);
		TreeMap<String, String> values = ParserUtils.getAsMap(e);
		String name = null;
		for(String tag:NAME_TAGS_FROM_SCHEME) {
			name = values.get(tag);
			if(name != null) {
				break;
			}
		}
		if(name!=null) {
			entry.setName(name);
			entry.setParentName(parentName);
		}
		throw new MyException("couldn't detect name tag from scheme node. The value map:\n" + values);
	}
}
