package org.bigloupe.web.listener;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

    private static final String ATTRIBUTE_NAME = SessionListener.class.getName();
    private Map<HttpSession, String> sessions = new ConcurrentHashMap<HttpSession, String>();

	@Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(ATTRIBUTE_NAME, this);
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        HttpSession session = request.getSession();
        if (session.isNew()) {
            sessions.put(session, request.getRemoteAddr());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        sessions.remove(event.getSession());
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // NOOP. Useless since we can't obtain IP here.
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        // NOOP. No logic needed.
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP. No logic needed. Maybe some future cleanup?
    }

    public static SessionListener getInstance(ServletContext context) {
        return (SessionListener) context.getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Number of objects per ip address
     * @param remoteAddr
     * @return
     */
    public int getCount(String remoteAddr) {
        return Collections.frequency(sessions.values(), remoteAddr);
    }
    
    public Map<HttpSession, String> getSessions() {
		return sessions;
	}

}

