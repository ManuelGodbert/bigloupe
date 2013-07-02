package org.bigloupe.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * A POJO containing the status of an action and a {@link List} of messages.
 * This is mainly used as a DTO for the presentation layer (Json format)
 */
public class ResponseStatus {

	private Boolean success;
	private List<String> message;

	public ResponseStatus() {
		this.message = new ArrayList<String>();
	}

	public ResponseStatus(Boolean success) {
		super();
		this.success = success;
		this.message = new ArrayList<String>();
	}

	public ResponseStatus(Boolean success, String message) {
		super();
		this.success = success;
		this.message = new ArrayList<String>();
		this.message.add(message);
	}

	public ResponseStatus(Boolean success, List<String> message) {
		super();
		this.success = success;
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message.add(message);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String mess : message) {
			sb.append(mess + ", ");
		}

		return "ResponseStatus [success=" + success + ", message="
				+ sb.toString() + "]";
	}
}
