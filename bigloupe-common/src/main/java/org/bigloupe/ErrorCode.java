package org.bigloupe;

/**
 * Contains code and key to retrieve I18 msg 
 */
public enum ErrorCode {
	// Chart
	CHART_MUST_BE_NOT_NULL(1, "CHART_MUST_BE_NOT_NULL"),
	CHART_MUST_HAVE_ID_AND_KEY(2,
			"CHART_MUST_HAVE_ID_AND_KEY"),
	CHART_CANNOT_BE_SAVED(3, "CHART_CANNOT_BE_SAVED"),
	CHART_ONLY_JSON_FORMAT_SUPPORTED(4, "CHART_ONLY_JSON_FORMAT_SUPPORTED"),
	
	// Series
	UNKNOWN_TYPE_SERIES(10, "UNKNOWN_TYPE_SERIES"),
	SERIES_NOT_FOUND(11, "SERIES_NOT_FOUND"),
	SERIES_AND_DATA_NOT_MATCHED(12, "SERIES_AND_DATA_NOT_MATCHED"),
	SERIES_EXPRESSION_MALFORMED(13, "SERIES_EXPRESSION_MALFORMED"),
	
	// Function
	UNKNOWN_FUNCTION(100, "UNKNOWN_FUNCTION"),
	FUNCTION_ERROR(101, "FUNCTION_ERROR");

	private final int code;
	private final String key;

	private ErrorCode(int code, String key) {
		this.code = code;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + " : " + key;
	}
}
