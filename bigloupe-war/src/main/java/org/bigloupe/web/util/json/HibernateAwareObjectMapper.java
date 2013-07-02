package org.bigloupe.web.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Mapper jackson 2.0 permettant de gérer les beans Hibernate avec lazy loading
 * 
 * 
 * @author bigloupe
 * 
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = -6831174208232436052L;
	
	public HibernateAwareObjectMapper() {
		Hibernate4Module hm = new Hibernate4Module();
		registerModule(hm);
	}



}
