package org.baeldung.spring;

public class Test {
	static String[] regexs = new String[] {
			"09(.*)",
			"(.*)98(.*)",
			"(.*)09",
			"09809",
			"0980"
	};

//	public static void main(String[] args) {
//		String test = "09809";
//		for(String regex:regexs) {
//			System.out.println("String '" + test + "' matches with regex: '" + regex + "': " + test.matches(regex));
//		}
//	}

}
