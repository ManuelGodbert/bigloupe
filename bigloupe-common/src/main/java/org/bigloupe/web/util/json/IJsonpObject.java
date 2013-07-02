package org.bigloupe.web.util.json;

/**
 * All object in JSONP must implement this interface
 * 
 */
public interface IJsonpObject {

	/**
	 * Javascript callback function 
	 */
	String getJsonCallback();

}
