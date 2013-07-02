package org.bigloupe;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorMessage {

	// Decide which locale to use
	private static final Locale currentLocale = new Locale("en", "US");
	private static ResourceBundle messages;

	static {
		try {
			messages = ResourceBundle.getBundle("org/bigloupe/errorcode", currentLocale);
		} catch (Exception e) {
			e.printStackTrace();
			messages = null;
		}
	}

	public static String getMessage(String key, Object... args) {
		if (messages != null) {
			if (args != null)
				return MessageFormat.format(messages.getString(key), args);
			else
				return messages.getString(key);
		}
		else
			return key;
		
	}

}
