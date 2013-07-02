package org.bigloupe.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringUtils {
	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '\"';

	public static String shellQuote(String s, char quoteCh) {
		StringBuffer buf = new StringBuffer(s.length() + 2);

		buf.append(quoteCh);
		for (int i = 0; i < s.length(); i++) {
			final char ch = s.charAt(i);
			if (ch == quoteCh) {
				buf.append('\\');
			}
			buf.append(ch);
		}
		buf.append(quoteCh);

		return buf.toString();
	}

	public static String getCompatibleSQLTableName(String s) {
		s = s.replace("-", "_");
		s = s.replace(".", "_");
		return s;
	}

	public static String urlEncode(String value)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(value, "UTF-8");
	}

}
