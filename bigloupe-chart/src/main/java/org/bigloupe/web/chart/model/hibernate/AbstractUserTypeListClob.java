package org.bigloupe.web.chart.model.hibernate;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/**
 * List format will be = "|val1|val2|...|valN|" Retro compatibility : "val1|val2|...|valN" is also readable
 * 
 * 
 */
public abstract class AbstractUserTypeListClob implements UserType {
    
    /** Delimiter for string representation */
    public final static String DELIM = "|";
    private final static String DELIM_FOR_SPLIT = "\\" + DELIM;
    
    /** String value to encode nulls */
    private static final String NULL_STR = "null";
    
    /** SQL_TYPE */
    public static final int SQL_TYPE = Types.CLOB;
    
    /** SQL_TYPES */
    public static final int[] SQL_TYPES = { SQL_TYPE };
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return true;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class returnedClass() {
        return List.class;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        
        String encodedObjects = (String) cached;
        
        return this.decodeString(encodedObjects);
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        
        List<Object> objectList = (List<Object>) value;
        String encodedMap = this.encodeCollection(objectList);
        
        return encodedMap;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object arg0) throws HibernateException {
        
        if (arg0 == null) {
            return null;
        }
        
        List<Object> list = (List<Object>) arg0;
        List<Object> copy = new ArrayList<Object>();
        copy.addAll(list);
        
        return copy;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        
        if (original == null) {
            return null;
        }
        
        List<Object> liste = (List<Object>) original;
        List<Object> copy = new ArrayList<Object>(liste.size());
        
        for (Object e : liste) {
            copy.add(e);
        }
        
        return copy;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object arg0, Object arg1) throws HibernateException {
        
        if (arg0 == null && arg1 == null) {
            return true;
            
        } else if (arg0 == null || arg1 == null) {
            return false;
        }
        
        return arg0.equals(arg1);
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object arg0) throws HibernateException {
        
        if (arg0 == null) {
            return 0;
        }
        
        return arg0.hashCode();
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
     */
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException {
    	
        StringBuilder result = new StringBuilder();
        // First we get the stream
        Reader reader = resultSet.getCharacterStream(names[0]);
        if (reader == null) {
            return new ArrayList<Object>();
        }
        try {
            char[] charbuf = new char[4096];
            for (int i = reader.read(charbuf); i > 0; i = reader.read(charbuf)) {
                result.append(charbuf, 0, i);
            }
        } catch (IOException e) {
            throw new SQLException(e.getMessage());
        }
        return this.decodeString(result.toString());
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if (value != null) {
            List<Object> liste = (List<Object>) value;
            String string = this.encodeCollection(liste);
            StringReader reader = new StringReader(string);
            st.setCharacterStream(index, reader, string.length());
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }
    
    /**
     * Transforms a collection to encoded String.
     * 
     * A delimiter is put between collection items.
     * 
     * @param pCollection
     *            The specified collection
     * @return The String value
     */
    public String encodeCollection(List<Object> pCollection) {
        
        StringBuilder values = new StringBuilder();
        
        for (int i = 0; i < pCollection.size(); i++) {
            
            Object value = pCollection.get(i);
            String s = NULL_STR;
            if (value != null) {
                s = objectToString(value);
            }
            
            values.append(DELIM);
            values.append(s);
        }
        if (pCollection.size() > 0) {
            // add last delim
            values.append(DELIM);
        }
        
        return values.toString();
    }
    
    /**
     * Transforms an encoded string to a List.
     * 
     * @param objectValues
     *            The specified String value
     * @return A Collection
     */
    public List<Object> decodeString(String objectValues) {
        List<Object> objectList = new ArrayList<Object>();
        if (StringUtils.isNotEmpty(objectValues)) {
            // remove first and and last position delim if present:
            int pos = 0;
            int len = StringUtils.length(objectValues);
            if (StringUtils.startsWith(objectValues, DELIM)) {
                // skip the first pos delim
                pos += StringUtils.length(DELIM);
            }
            if (objectValues.length() > StringUtils.length(DELIM) && StringUtils.endsWith(objectValues, DELIM)) {
                // skip also the last DELIM
                len -= StringUtils.length(DELIM);
            }
            String trimObjVals = StringUtils.mid(objectValues, pos, len);
            String[] values = trimObjVals.split(DELIM_FOR_SPLIT);
            for (String value : values) {
                Object e = null;
                if (value != null && !NULL_STR.equals(value)) {
                    e = stringToObject(value);
                }
                objectList.add(e);
            }
        }
        
        return objectList;
    }
    
    /**
     * Gets a String view of the given value
     * 
     * @param e
     *            The specified object
     * @return The String value
     */
    protected abstract String objectToString(Object e);
    
    /**
     * Gets an value of the given String value
     * 
     * @param s
     *            The specified String
     * @return The value
     */
    protected abstract Object stringToObject(String s);
    
}