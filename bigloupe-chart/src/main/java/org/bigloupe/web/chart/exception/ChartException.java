package org.bigloupe.web.chart.exception;

import org.bigloupe.ErrorCode;
import org.bigloupe.ErrorMessage;
import org.bigloupe.web.chart.model.Series;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=ChartExceptionSerialize.class)
public class ChartException extends RuntimeException {

	private static final long serialVersionUID = 1977196367917679181L;
	
	@JsonProperty
	int code;

	@JsonProperty
	String reason;

//	public ChartException(int code, String reason) {
//		super(reason);
//		this.reason = reason;
//		this.code = code;
//	}
	
	public ChartException(Exception e) {
		super(e.getMessage());
		this.reason = e.getMessage();
		this.code = 500;
	}
	
	public ChartException(ErrorCode errorCode) {
		super(ErrorMessage.getMessage(errorCode.getKey()));
		this.reason = ErrorMessage.getMessage(errorCode.getKey());
		this.code = errorCode.getCode();
	}
	
	public ChartException(ErrorCode errorCode,
			Object... args) {
		super(ErrorMessage.getMessage(errorCode.getKey(), args));
		this.reason = ErrorMessage.getMessage(errorCode.getKey(), args);
		this.code = errorCode.getCode();
	}
	

}
